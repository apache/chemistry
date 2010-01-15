/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Florent Guillaume, Nuxeo
 *     Amelie Avramo, EntropySoft
 *     Florian Roth, In-integrierte Informationssysteme
 */
package org.apache.chemistry.atompub.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.FOMBuilder;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMEntry;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.util.EntityTag;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.ObjectNotFoundException;
import org.apache.chemistry.Property;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.abdera.ObjectElement;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.util.GregorianCalendar;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ctc.wstx.sr.BasicStreamReader;
import com.ctc.wstx.sr.NsInputElementStack;

/**
 * CMIS Collection for object entries.
 */
public abstract class CMISObjectsCollection extends CMISCollection<ObjectEntry> {

    private static final Log log = LogFactory.getLog(CMISObjectsCollection.class);

    public static final String COLTYPE_PATH = "path";

    public static final String COLTYPE_CHILDREN = "children";

    public static final String COLTYPE_DESCENDANTS = "descendants";

    public static final String COLTYPE_FOLDER_TREE = "foldertree";

    public static final TargetType TARGET_TYPE_CMIS_DESCENDANTS = TargetType.get(
            "CMISDESCENDANTS", true);

    public static final TargetType TARGET_TYPE_CMIS_FOLDER_TREE = TargetType.get(
            "CMISFOLDERTREE", true);

    public CMISObjectsCollection(String type, String name, String id,
            Repository repository) {
        super(type, name, id, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    // called by AbstractProvider.process if unknown TargetType
    @Override
    public ResponseContext extensionRequest(RequestContext request) {
        TargetType type = request.getTarget().getType();
        if (type != TARGET_TYPE_CMIS_DESCENDANTS
                && type != TARGET_TYPE_CMIS_FOLDER_TREE) {
            return ProviderHelper.notsupported(request);
        }
        if (request.getMethod().equalsIgnoreCase("GET")) {
            return getFeed(request);
        } else if (request.getMethod().equalsIgnoreCase("DELETE")) {
            return deleteEntry(request);
        } else {
            // stupid signature prevents use of varargs...
            return ProviderHelper.notallowed(request, new String[] { "GET",
                    "DELETE" });
        }
    }

    @Override
    protected Feed createFeedBase(RequestContext request)
            throws ResponseContextException {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        feed.declareNS(CMIS.CMIS_NS, CMIS.CMIS_PREFIX);
        feed.setId(getId(request));
        feed.setTitle(getTitle(request));
        feed.addAuthor(getAuthor(request));
        feed.setUpdated(new Date()); // XXX
        feed.addLink(getServiceLink(request), AtomPub.LINK_SERVICE,
                AtomPub.MEDIA_TYPE_ATOM_SERVICE, null, null, -1);
        feed.addLink(getObjectLink(id, request), AtomPub.LINK_VIA,
                AtomPub.MEDIA_TYPE_ATOM_ENTRY, null, null, -1);
        return feed;
    }

    @Override
    public String getId(RequestContext request) {
        return "urn:x-children:" + id;
    }

    @Override
    public String getAuthor(RequestContext request) {
        return "system";
    }

    // don't add a source
    @Override
    protected ResponseContext buildGetEntryResponse(RequestContext request,
            Entry entry) throws ResponseContextException {
        Document<Entry> entryDoc = entry.getDocument();
        AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(
                entryDoc);
        rc.setEntityTag(ProviderHelper.calculateEntityTag(entry));
        return rc;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Entry getEntryFromRequest(RequestContext request)
            throws ResponseContextException {
        Parser parser = request.getAbdera().getParser();
        Document<Entry> entry;
        try {
            Document<Element> doc = request.getDocument(parser);
            fixMissingNamespace(doc);
            entry = (Document<Entry>) doc.clone();
        } catch (Exception e) {
            throw new ResponseContextException(500, e);
        }
        return entry == null ? null : entry.getRoot();
    }

    // attempt to fixup missing cmisra ns for buggy clients
    // (IBM Firefox plugin)
    private void fixMissingNamespace(Document<Element> doc) {
        try {
            FOMDocument<?> fomdoc = (FOMDocument<?>) doc;
            FOMEntry fomentry = (FOMEntry) fomdoc.getOMDocumentElement();
            FOMBuilder fombuilder = (FOMBuilder) fomentry.builder;

            // BasicStreamReader parser = (BasicStreamReader) builder.parser;
            Field parserField = StAXBuilder.class.getDeclaredField("parser");
            parserField.setAccessible(true);
            BasicStreamReader parser = (BasicStreamReader) parserField.get(fombuilder);

            // NsInputElementStack stack = (NsInputElementStack)
            // parser.mElementStack;
            Field stackField = BasicStreamReader.class.getDeclaredField("mElementStack");
            stackField.setAccessible(true);
            NsInputElementStack stack = (NsInputElementStack) stackField.get(parser);

            if (stack.getNamespaceURI(AtomPubCMIS.CMISRA_PREFIX) == null) {
                stack.addNsBinding(AtomPubCMIS.CMISRA_PREFIX,
                        AtomPubCMIS.CMISRA_NS);
            }
        } catch (Exception e) {
            throw new CMISRuntimeException(e);
        }
    }

    /*
     * ----- CollectionInfo -----
     */

    public String getTitle(RequestContext request) {
        return name + " collection";
    }

    /*
     * ----- AbstractEntityCollectionAdapter -----
     */

    @Override
    protected String addEntryDetails(RequestContext request, Entry entry,
            IRI feedIri, ObjectEntry object) throws ResponseContextException {
        Factory factory = request.getAbdera().getFactory();
        String oid = object.getId();
        String typeId = object.getTypeId();

        entry.declareNS(CMIS.CMIS_NS, CMIS.CMIS_PREFIX);

        entry.setId(getId(object));
        entry.setTitle(getTitle(object));
        entry.setUpdated(getUpdated(object));
        List<Person> authors = getAuthors(object, request);
        if (authors != null) {
            for (Person a : authors) {
                entry.addAuthor(a);
            }
        }
        Text t = getSummary(object, request);
        if (t != null) {
            entry.setSummaryElement(t);
        }

        entry.addLink(getServiceLink(request), AtomPub.LINK_SERVICE,
                AtomPub.MEDIA_TYPE_ATOM_SERVICE, null, null, -1);
        String link = getLink(object, feedIri, request);
        entry.addLink(link, AtomPub.LINK_SELF, AtomPub.MEDIA_TYPE_ATOM_ENTRY,
                null, null, -1);
        entry.addLink(link, AtomPub.LINK_EDIT, AtomPub.MEDIA_TYPE_ATOM_ENTRY,
                null, null, -1);
        // alternate is mandated by Atom when there is no atom:content
        entry.addLink(link, AtomPub.LINK_ALTERNATE,
                AtomPub.MEDIA_TYPE_ATOM_ENTRY, null, null, -1);

        entry.addLink(getTypeLink(typeId, request), AtomPub.LINK_DESCRIBED_BY,
                AtomPub.MEDIA_TYPE_ATOM_ENTRY, null, null, -1);

        BaseType baseType = object.getBaseType();
        if (baseType == BaseType.FOLDER) {
            String pid = (String) object.getValue(Property.PARENT_ID);
            if (pid != null) {
                entry.addLink(getObjectLink(pid, request), AtomPub.LINK_UP,
                        AtomPub.MEDIA_TYPE_ATOM_ENTRY, null, null, -1);
            }
            // down link always present to be able to add children
            entry.addLink(getChildrenLink(oid, request), AtomPub.LINK_DOWN,
                    AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);
            // TODO don't add descendants links if no children
            entry.addLink(getDescendantsLink(oid, request), AtomPub.LINK_DOWN,
                    AtomPubCMIS.MEDIA_TYPE_CMIS_TREE, null, null, -1);
            entry.addLink(getFolderTreeLink(oid, request),
                    AtomPubCMIS.LINK_FOLDER_TREE, AtomPub.MEDIA_TYPE_ATOM_FEED,
                    null, null, -1);
        } else if (baseType == BaseType.DOCUMENT) {
            // edit-media link always needed for setContentStream
            entry.addLink(getMediaLink(oid, request), AtomPub.LINK_EDIT_MEDIA);
            // TODO don't add link if no parents
            entry.addLink(getParentsLink(oid, request), AtomPub.LINK_UP,
                    AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);
        }
        // entry.addLink("XXX", CMIS.LINK_ALLOWABLE_ACTIONS);
        // entry.addLink("XXX", CMIS.LINK_RELATIONSHIPS);

        Type objectType = repository.getType(typeId);
        entry.addExtension(new ObjectElement(factory, object, objectType));

        return link;
    }

    // getEntries is abstract, must be implemented

    protected static class PropertiesAndStream {
        public Map<String, Serializable> properties;

        public InputStream stream;

        public String mimeType;
    }

    /**
     * Finds properties and stream from entry.
     *
     * @param typeId is null for a POST, existing type for a PUT
     */
    protected PropertiesAndStream extractCMISProperties(RequestContext request,
            String typeId) throws ResponseContextException {
        boolean isNew = typeId == null;
        Entry entry = getEntryFromRequest(request);
        if (entry == null) {
            throw new ResponseContextException("Missing entry", 400);
        }
        if (!ProviderHelper.isValidEntry(entry)) {
            List<String> errors = new LinkedList<String>();
            // attempt to fixup entries for buggy clients
            // (IBM Firefox plugin and others)
            if (entry.getId() == null
                    || entry.getId().toString().trim().length() == 0) {
                errors.add("missing atom:id");
            }
            if (entry.getUpdated() == null) {
                errors.add("missing atom:updated");
            }
            if (entry.getAuthor() == null
                    && (entry.getSource() != null && entry.getSource().getAuthor() == null)) {
                errors.add("missing atom:author");
            }
            Content content = entry.getContentElement();
            if (content == null) {
                if (entry.getAlternateLink() == null) {
                    errors.add("missing atom:link rel=alternate");
                }
            } else {
                if ((content.getSrc() != null || content.getContentType() == Content.Type.MEDIA)
                        && entry.getSummaryElement() == null) {
                    errors.add("missing atom:summary");
                }
            }
            if (!errors.isEmpty()) {
                log.error("Invalid entry: " + StringUtils.join(errors, ", "));
                // throw new ResponseContextException("Invalid entry", 400);
            }
        }

        // get properties and type from entry
        Element obb = entry.getFirstChild(AtomPubCMIS.OBJECT);
        if (obb == null) {
            // compat with buggy CMISSpacesAir
            obb = entry.getFirstChild(new QName(CMIS.CMIS_NS, "object"));
        }
        Map<String, Serializable> properties;
        if (obb == null) {
            // no CMIS object, basic AtomPub post/put
            properties = new HashMap<String, Serializable>();
            if (isNew) {
                typeId = BaseType.DOCUMENT.getId();
                properties.put(Property.TYPE_ID, typeId);
            }
        } else {
            ObjectElement objectElement = new ObjectElement(obb, repository);
            try {
                properties = objectElement.getProperties(typeId);
            } catch (Exception e) { // TODO proper exception
                throw new ResponseContextException(500, e);
            }
            // type
            String tid = (String) properties.get(Property.TYPE_ID);
            if (isNew) {
                // post
                typeId = tid;
            } else if (tid != null && !tid.equals(typeId)) {
                // mismatched types during put
                throw new ResponseContextException("Invalid type: " + tid, 500);
            }
        }
        Type type = repository.getType(typeId);
        if (type == null) {
            throw new ResponseContextException("Unknown type: " + typeId, 500);
        }

        // get stream and its mime type from entry
        InputStream stream;
        String mimeType;
        Element cmisContent = entry.getFirstChild(AtomPubCMIS.CONTENT);
        if (cmisContent != null) {
            // cmisra:content has precedence over atom:content
            Element el = cmisContent.getFirstChild(AtomPubCMIS.MEDIA_TYPE);
            if (el == null) {
                throw new ResponseContextException("missing cmisra:mediatype",
                        500);
            }
            mimeType = el.getText();
            el = cmisContent.getFirstChild(AtomPubCMIS.BASE64);
            if (el == null) {
                throw new ResponseContextException("missing cmisra:base64", 500);
            }
            byte[] b64 = el.getText().getBytes(); // no charset, pure ASCII
            stream = new ByteArrayInputStream(Base64.decodeBase64(b64));
        } else {
            Content content = entry.getContentElement();
            if (content != null) {
                org.apache.abdera.model.Content.Type ct = content.getContentType();
                switch (ct) {
                case TEXT:
                    mimeType = "text/plain;charset=UTF-8";
                    break;
                case HTML:
                    mimeType = "text/html;charset=UTF-8";
                    break;
                case XHTML:
                    mimeType = "application/xhtml+xml";
                    break;
                case XML:
                    mimeType = "application/xml";
                    break;
                case MEDIA:
                    mimeType = content.getMimeType().toString();
                    break;
                default:
                    throw new AssertionError(ct.toString());
                }
                try {
                    if (ct == org.apache.abdera.model.Content.Type.MEDIA) {
                        stream = content.getDataHandler().getInputStream();
                    } else {
                        String value = content.getValue();
                        if (ct == org.apache.abdera.model.Content.Type.TEXT
                                && "".equals(value)) {
                            // stupid Abdera generates an empty <content> by
                            // itself
                            stream = null;
                        } else {
                            stream = new ByteArrayInputStream(
                                    value.getBytes("UTF-8"));
                        }
                    }
                } catch (IOException e1) {
                    throw new ResponseContextException("cannot get stream", 500);
                }
            } else {
                stream = null;
                mimeType = null;
            }
        }

        // set Atom-defined properties into properties
        // title
        String title = entry.getTitle(); // Atom MUST
        properties.put(Property.NAME, title);
        // TODO summary
        if (isNew) {
            // updated
            // parse the date ourselves, as Abdera's AtomDate loses the timezone
            DateTime updatedElement = entry.getUpdatedElement(); // Atom MUST
            if (updatedElement != null) { // TODO XXX TCK
                Calendar updated = GregorianCalendar.fromAtomPub(updatedElement.getText());
                properties.put(Property.LAST_MODIFICATION_DATE, updated);
            }
        }

        PropertiesAndStream res = new PropertiesAndStream();
        res.properties = properties;
        res.stream = stream;
        res.mimeType = mimeType;
        return res;
    }

    @Override
    public ResponseContext postEntry(RequestContext request) {
        // TODO parameter versioningState
        SPI spi = repository.getSPI();
        try {
            PropertiesAndStream posted = extractCMISProperties(request, null);
            ObjectId folderId = spi.newObjectId(id);
            String sourceFolderId = request.getTarget().getParameter(
                    AtomPubCMIS.PARAM_SOURCE_FOLDER_ID);
            boolean isMove = sourceFolderId != null;
            ObjectId objectId;
            if (isMove) {
                if ("null".equals(sourceFolderId) || "".equals(sourceFolderId)) {
                    sourceFolderId = null;
                }
                String oid = (String) posted.properties.get(Property.ID);
                if (oid == null) {
                    throw new ResponseContextException("Missing id", 400);
                }
                objectId = spi.newObjectId(oid);
                ObjectId sourceFolder = sourceFolderId == null ? null
                        : spi.newObjectId(sourceFolderId);
                objectId = spi.moveObject(objectId, folderId, sourceFolder);
            } else {
                String typeId = (String) posted.properties.get(Property.TYPE_ID);
                BaseType baseType = repository.getType(typeId).getBaseType();
                switch (baseType) {
                case DOCUMENT:
                    String filename = (String) posted.properties.get(Property.CONTENT_STREAM_FILE_NAME);
                    if (filename == null) {
                        filename = (String) posted.properties.get(Property.NAME);
                    }
                    ContentStream contentStream = posted.stream == null ? null
                            : new SimpleContentStream(posted.stream,
                                    posted.mimeType, filename);
                    VersioningState versioningState = null; // TODO
                    objectId = spi.createDocument(posted.properties, folderId,
                            contentStream, versioningState);
                    break;
                case FOLDER:
                    objectId = spi.createFolder(posted.properties, folderId);
                    break;
                default:
                    throw new UnsupportedOperationException("not implemented: "
                            + baseType);
                }
            }

            // prepare the updated entry to return in the response
            // AbstractEntityCollectionAdapter#getEntryFromCollectionProvider is
            // package-private...
            Entry entry = request.getAbdera().getFactory().newEntry();
            ObjectEntry object = spi.getProperties(objectId, null);
            addEntryDetails(request, entry, null, object);
            if (isMediaEntry(object)) {
                addMediaContent(null, entry, object, request);
            } else {
                addContent(entry, object, request);
            }
            String link = getObjectLink(object.getId(), request);
            return buildCreateEntryResponse(link, entry);
        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        } catch (ConstraintViolationException e) {
            return createErrorResponse(new ResponseContextException(400, e));
        } catch (CMISRuntimeException e) {
            return createErrorResponse(new ResponseContextException(500, e));
        } catch (Exception e) {
            return createErrorResponse(new ResponseContextException(500, e));
        } finally {
            spi.close();
        }
    }

    // unused but abstract in parent...
    @Override
    public ObjectEntry postEntry(String title, IRI id, String summary,
            Date updated, List<Person> authors, Content content,
            RequestContext request) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        SPI spi = repository.getSPI();
        try {
            // existing object
            String id = getResourceName(request);
            ObjectEntry object = spi.getProperties(spi.newObjectId(id), null);
            if (object == null) {
                return new EmptyResponseContext(404);
            }

            PropertiesAndStream put = extractCMISProperties(request,
                    object.getTypeId());

            // update entry
            String changeToken = null; // TODO
            spi.updateProperties(object, changeToken, put.properties);
            if (put.stream != null) {
                String filename = (String) put.properties.get(Property.CONTENT_STREAM_FILE_NAME);
                if (filename == null) {
                    filename = (String) object.getValue(Property.CONTENT_STREAM_FILE_NAME);
                }
                ContentStream contentStream = put.stream == null ? null
                        : new SimpleContentStream(put.stream, put.mimeType,
                                filename);
                spi.setContentStream(object, contentStream, true);
            }

            // build response
            Entry entry = request.getAbdera().getFactory().newEntry();
            // refetch full object
            object = spi.getProperties(object, null);
            addEntryDetails(request, entry, null, object);
            if (isMediaEntry(object)) {
                addMediaContent(null, entry, object, request);
            } else {
                addContent(entry, object, request);
            }
            return buildGetEntryResponse(request, entry);

        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        } catch (CMISRuntimeException e) {
            return createErrorResponse(new ResponseContextException(500, e));
        } catch (Exception e) {
            return createErrorResponse(new ResponseContextException(500, e));
        } finally {
            spi.close();
        }
    }

    // unused but abstract in parent...
    @Override
    public void putEntry(ObjectEntry object, String title, Date updated,
            List<Person> authors, String summary, Content content,
            RequestContext request) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        ObjectId object;
        SPI spi = repository.getSPI();
        try {
            String oid = resourceName;
            object = spi.newObjectId(oid);
            if (COLTYPE_DESCENDANTS.equals(getType())
                    || COLTYPE_FOLDER_TREE.equals(getType())) {
                String unfile = request.getTarget().getParameter(
                        AtomPubCMIS.PARAM_UNFILE_OBJECTS);
                Unfiling unfileObjects = unfile == null ? null
                        : Unfiling.get(unfile);
                boolean continueOnFailure = getParameter(request,
                        AtomPubCMIS.PARAM_CONTINUE_ON_FAILURE, false);
                spi.deleteTree(object, unfileObjects, continueOnFailure);
            } else {
                // TODO XXX allVersions not in spec
                boolean allVersions = getParameter(request, "allVersions",
                        false);
                spi.deleteObject(object, allVersions);
            }
        } catch (ObjectNotFoundException e) {
            throw new ResponseContextException(404, e);
        } catch (ConstraintViolationException e) {
            throw new ResponseContextException(409, e);
        } catch (CMISRuntimeException e) {
            throw new ResponseContextException(500, e);
        } catch (Exception e) {
            throw new ResponseContextException(500, e);
        } finally {
            spi.close();
        }
    }

    @Override
    public List<Person> getAuthors(ObjectEntry object, RequestContext request) {
        String author = null;
        try {
            author = (String) object.getValue(Property.CREATED_BY);
        } catch (Exception e) {
            // no such property or bad type
        }
        if (author == null) {
            author = "system";
        }
        Person person = request.getAbdera().getFactory().newAuthor();
        person.setName(author);
        return Collections.singletonList(person);
    }

    @Override
    public ResponseContext getMedia(RequestContext request) {
        SPI spi = repository.getSPI();
        try {
            String id = getResourceName(request);
            ObjectEntry object = getEntry(id, request, spi);
            if (object == null) {
                return new EmptyResponseContext(404);
            }
            ContentStream contentStream = spi.getContentStream(object, null);
            if (contentStream == null) {
                return new EmptyResponseContext(409, "No content");
            }
            InputStream stream = contentStream.getStream();
            if (stream == null) {
                return new EmptyResponseContext(409, "No content");
            }
            Date updated = getUpdated(object);
            SizedMediaResponseContext ctx = new SizedMediaResponseContext(
                    stream, updated, 200);
            ctx.setSize(getContentSize(object));
            ctx.setContentType(getContentType(object));
            ctx.setEntityTag(EntityTag.generate(id, AtomDate.format(updated)));
            return ctx;
        } catch (ResponseContextException e) {
            return e.getResponseContext();
        } catch (ConstraintViolationException e) {
            return new EmptyResponseContext(409, "No content");
        } catch (IOException e) {
            return new EmptyResponseContext(500, e.toString());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new EmptyResponseContext(400);
        } finally {
            spi.close();
        }
    }

    @Override
    public boolean isMediaEntry(ObjectEntry object)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            return isMediaEntry(object, spi);
        } finally {
            spi.close();
        }
    }

    public boolean isMediaEntry(ObjectEntry object, SPI spi)
            throws ResponseContextException {
        return getContentType(object) != null && getContentSize(object) != -1
                && spi.hasContentStream(object);
    }

    @Override
    protected String addMediaContent(IRI feedIri, Entry entry,
            ObjectEntry object, RequestContext request)
            throws ResponseContextException {
        String mediaLink = getMediaLink(object.getId(), request);
        entry.setContent(new IRI(mediaLink), getContentType(object));
        // LINK_EDIT_MEDIA already added (always) by addEntryDetails
        return mediaLink;
    }

    // called when this is not a media entry
    @Override
    public Object getContent(ObjectEntry object, RequestContext request)
            throws ResponseContextException {
        // no content (content stream is exposed as media entry)
        // mandatory LINK_ALTERNATE already added by addEntryDetails
        return null;
    }

    public long getContentSize(ObjectEntry object) {
        try {
            Integer value = (Integer) object.getValue(Property.CONTENT_STREAM_LENGTH);
            return value == null ? -1 : value.longValue();
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    @Override
    public String getContentType(ObjectEntry object) {
        try {
            return (String) object.getValue(Property.CONTENT_STREAM_MIME_TYPE);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        SPI spi = repository.getSPI();
        try {
            String id = getResourceName(request);
            ObjectEntry object = getEntry(id, request, spi);
            if (object == null) {
                return new EmptyResponseContext(404);
            }
            Entry entry = request.getAbdera().getFactory().newEntry();
            IRI feedIri = new IRI(getFeedIriForEntry(object, request));
            addEntryDetails(request, entry, feedIri, object);
            if (isMediaEntry(object, spi)) {
                addMediaContent(feedIri, entry, object, request);
            } else {
                addContent(entry, object, request);
            }
            return buildGetEntryResponse(request, entry);
        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        } finally {
            spi.close();
        }
    }

    @Override
    public ObjectEntry getEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            return getEntry(resourceName, request, spi);
        } finally {
            spi.close();
        }
    }

    public ObjectEntry getEntry(String resourceName, RequestContext request,
            SPI spi) throws ResponseContextException {
        Target target = request.getTarget();
        String properties = target.getParameter(AtomPubCMIS.PARAM_FILTER);
        boolean allowableActions = getParameter(request,
                AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
        String incl = target.getParameter(AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS);
        RelationshipDirection relationships = RelationshipDirection.fromInclusion(incl);
        Inclusion inclusion = new Inclusion(properties, null, relationships,
                allowableActions, false, false);
        if (COLTYPE_PATH.equals(getType()) || "".equals(resourceName)) {
            String path;
            if (COLTYPE_PATH.equals(getType())) {
                path = resourceName;
            } else {
                path = target.getParameter(AtomPubCMIS.PARAM_PATH);
                if (path == null) {
                    throw new ResponseContextException("Missing id and path",
                            500);
                }
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return spi.getObjectByPath(path, inclusion);
        } else { // object
            String id = resourceName;
            return spi.getProperties(spi.newObjectId(id), inclusion);
        }
    }

    @Override
    public String getResourceName(RequestContext request) {
        String name;
        if (COLTYPE_PATH.equals(getType())) {
            name = "path";
        } else {
            name = "objectid";
        }
        String resourceName = request.getTarget().getParameter(name);
        return UrlEncoding.decode(resourceName);
    }

    @Override
    protected String getLink(ObjectEntry object, IRI feedIri,
            RequestContext request) {
        return getObjectLink(object.getId(), request);
    }

    @Override
    public String getName(ObjectEntry object) {
        throw new UnsupportedOperationException(); // unused
    }

    @Override
    public String getId(ObjectEntry object) {
        return "urn:uuid:" + object.getId();
    }

    @Override
    public String getTitle(ObjectEntry object) {
        return (String) object.getValue(Property.NAME);
    }

    @Override
    public Date getUpdated(ObjectEntry object) {
        Date date = null;
        try {
            Calendar calendar = (Calendar) object.getValue(Property.LAST_MODIFICATION_DATE);
            if (calendar != null) {
                date = calendar.getTime();
            }
        } catch (Exception e) {
            // no such property or bad type
        }
        if (date == null) {
            date = new Date();
        }
        return date;
    }

    @Override
    public Text getSummary(ObjectEntry object, RequestContext request) {
        String summary = null;
        try {
            summary = (String) object.getValue("description"); // TODO improve
        } catch (Exception e) {
            // no such property or bad type
        }
        if (summary == null) {
            summary = (String) object.getValue(Property.NAME);
        }
        // TODO summary not needed if there's a non-base64 inline content
        Text text = request.getAbdera().getFactory().newSummary();
        text.setValue(summary);
        return text;
    }

}
