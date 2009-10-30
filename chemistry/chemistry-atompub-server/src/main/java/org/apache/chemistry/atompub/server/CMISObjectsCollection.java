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
 */
package org.apache.chemistry.atompub.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.util.EntityTag;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.abdera.ObjectElement;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.util.GregorianCalendar;
import org.apache.commons.codec.binary.Base64;

/**
 * CMIS Collection for object entries.
 */
public abstract class CMISObjectsCollection extends CMISCollection<ObjectEntry> {

    public CMISObjectsCollection(String type, String name, String id,
            Repository repository) {
        super(type, name, id, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

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
        } else if (baseType == BaseType.DOCUMENT) {
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
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            throw new ResponseContextException(400);
        }

        // get properties and type from entry
        Map<String, Serializable> properties;
        Element obb = entry.getFirstChild(AtomPubCMIS.OBJECT);
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
                properties = objectElement.getProperties();
            } catch (Exception e) { // TODO proper exception
                throw new ResponseContextException(500, e);
            }
            // type
            String tid = (String) properties.get(Property.TYPE_ID);
            if (isNew) {
                // post
                typeId = tid;
            } else if (!typeId.equals(tid)) {
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
                        stream = new ByteArrayInputStream(
                                content.getValue().getBytes("UTF-8"));
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
        // TODO parameter sourceFolderId
        // TODO parameter versioningState
        try {
            PropertiesAndStream posted = extractCMISProperties(request, null);

            SPI spi = repository.getSPI(); // TODO XXX connection leak
            ObjectId folderId = spi.newObjectId(id);
            ObjectId objectId;
            String typeId = (String) posted.properties.get(Property.TYPE_ID);
            BaseType baseType = repository.getType(typeId).getBaseType();
            switch (baseType) {
            case DOCUMENT:
                String filename = (String) posted.properties.get(Property.CONTENT_STREAM_FILE_NAME);
                ContentStream contentStream;
                try {
                    contentStream = new SimpleContentStream(posted.stream,
                            posted.mimeType, filename);
                } catch (IOException e) {
                    throw new ResponseContextException(500, e);
                }
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

            // prepare the updated entry to return in the response
            // AbstractEntityCollectionAdapter#getEntryFromCollectionProvider is
            // package-private...
            Entry entry = request.getAbdera().getFactory().newEntry();
            ObjectEntry object = spi.getProperties(objectId, null, false, false);
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
        try {
            // existing object
            String id = getResourceName(request);
            SPI spi = repository.getSPI(); // TODO XXX connection leak
            ObjectEntry object = spi.getProperties(spi.newObjectId(id), null,
                    false, false);
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
                ContentStream contentStream;
                try {
                    contentStream = new SimpleContentStream(put.stream,
                            put.mimeType, filename);
                } catch (IOException e) {
                    throw new ResponseContextException(500, e);
                }
                spi.setContentStream(object, true, contentStream);
            }

            // build response
            Entry entry = request.getAbdera().getFactory().newEntry();
            // refetch full object
            object = spi.getProperties(object, null, false, false);
            addEntryDetails(request, entry, null, object);
            if (isMediaEntry(object)) {
                addMediaContent(null, entry, object, request);
            } else {
                addContent(entry, object, request);
            }
            return buildGetEntryResponse(request, entry);

        } catch (ResponseContextException e) {
            return createErrorResponse(e);
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
        throw new UnsupportedOperationException();
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
    public boolean isMediaEntry(ObjectEntry object)
            throws ResponseContextException {
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        return getContentType(object) != null && getContentSize(object) != -1
                && spi.hasContentStream(object);
    }

    @Override
    protected String addMediaContent(IRI feedIri, Entry entry,
            ObjectEntry object, RequestContext request)
            throws ResponseContextException {
        String mediaLink = getMediaLink(object.getId(), request);
        entry.setContent(new IRI(mediaLink), getContentType(object));
        entry.addLink(mediaLink, AtomPub.LINK_EDIT_MEDIA,
                getContentType(object), null, null, getContentSize(object));
        return mediaLink;
    }

    // called when this is not a media entry
    @Override
    public Object getContent(ObjectEntry object, RequestContext request)
            throws ResponseContextException {
        Factory factory = request.getAbdera().getFactory();
        Content content = factory.newContent();
        content.setContentType(null);
        String mediaLink = getMediaLink(object.getId(), request);
        content.setSrc(mediaLink);
        return content;
    }

    public long getContentSize(ObjectEntry object) {
        Integer value = (Integer) object.getValue(Property.CONTENT_STREAM_LENGTH);
        return value == null ? -1 : value.longValue();
    }

    @Override
    public String getContentType(ObjectEntry object) {
        try {
            return (String) object.getValue(Property.CONTENT_STREAM_MIME_TYPE);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ObjectEntry getEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        if ("path".equals(getType())) {
            String path = resourceName;
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return spi.getObjectByPath(path, null, false, false);
        } else { // object
            String id = resourceName;
            return spi.getProperties(spi.newObjectId(id), null, false, false);
        }
    }

    @Override
    public String getResourceName(RequestContext request) {
        String name;
        if ("path".equals(getType())) {
            name = "path";
        } else {
            name = "objectid";
        }
        String resourceName = request.getTarget().getParameter(name);
        // TODO decode properly
        resourceName = resourceName.replace("%3a", ":");
        resourceName = resourceName.replace("%3A", ":");
        resourceName = resourceName.replace("%20", " ");
        resourceName = resourceName.replace("%2f", "/");
        resourceName = resourceName.replace("%2F", "/");
        return resourceName;
    }

    @Override
    protected String getLink(ObjectEntry object, IRI feedIri,
            RequestContext request) {
        return getObjectLink(object.getId(), request);
    }

    @Override
    public InputStream getMediaStream(ObjectEntry object)
            throws ResponseContextException {
        // TODO entry was fetched for mostly nothing...
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        try {
            ContentStream contentStream = spi.getContentStream(object, null);
            return contentStream == null ? null : contentStream.getStream();
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
    }

    // override to use a custom SizedMediaResponseContext
    @Override
    protected ResponseContext buildGetMediaResponse(String id,
            ObjectEntry entryObj) throws ResponseContextException {
        Date updated = getUpdated(entryObj);
        SizedMediaResponseContext ctx = new SizedMediaResponseContext(
                getMediaStream(entryObj), updated, 200);
        ctx.setSize(getContentSize(entryObj));
        ctx.setContentType(getContentType(entryObj));
        ctx.setEntityTag(EntityTag.generate(id, AtomDate.format(updated)));
        return ctx;
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
