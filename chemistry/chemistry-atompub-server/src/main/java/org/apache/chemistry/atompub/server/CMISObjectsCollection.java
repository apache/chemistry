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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.abdera.ObjectElement;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.chemistry.util.GregorianCalendar;

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
    protected Feed createFeedBase(RequestContext request) {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        feed.declareNS(CMIS.CMIS_NS, CMIS.CMIS_PREFIX);
        feed.setId(getId(request));
        feed.setTitle(getTitle(request));
        feed.addAuthor(getAuthor(request));
        feed.setUpdated(new Date()); // XXX

        feed.addLink(getChildrenLink(id, request), "self");
        feed.addLink(getObjectLink(id, request), AtomPubCMIS.LINK_SOURCE);

        // RFC 5005 paging

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

        String link = getLink(object, feedIri, request);
        entry.addLink(link, "self");
        entry.addLink(link, "edit");
        // alternate is mandated by Atom when there is no atom:content
        entry.addLink(link, "alternate");
        String typeId = object.getTypeId();
        entry.addLink(getTypeLink(typeId, request), AtomPub.LINK_DESCRIBED_BY);
        entry.addLink(getServiceLink(request), AtomPub.LINK_SERVICE);

        // CMIS links
        String oid = object.getId();
        BaseType baseType = object.getBaseType();
        if (baseType == BaseType.FOLDER) {
            String pid = (String) object.getValue(Property.PARENT_ID);
            if (pid != null) {
                entry.addLink(getObjectLink(pid, request), AtomPub.LINK_UP,
                        AtomPub.MEDIA_TYPE_ATOM_ENTRY, null, null, -1);
            }
            // TODO don't add links if no children/decendants
            entry.addLink(getChildrenLink(oid, request), AtomPub.LINK_DOWN,
                    AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);
            // TODO children descendants and folder tree
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

    protected static String bool(boolean bool) {
        return bool ? "true" : "false";
    }

    // getEntries is abstract, must be implemented

    @Override
    public ResponseContext postEntry(RequestContext request) {
        // TODO parameter sourceFolderId
        // TODO parameter versioningState
        Entry entry;
        try {
            entry = getEntryFromRequest(request);
        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        }
        if (entry == null /* || !ProviderHelper.isValidEntry(entry) TODO XXX TCK */) {
            return new EmptyResponseContext(400);
        }

        Element obb = entry.getFirstChild(AtomPubCMIS.OBJECT);
        ObjectElement objectElement = new ObjectElement(obb, repository);
        Map<String, Serializable> properties;
        try {
            properties = objectElement.getProperties();
        } catch (Exception e) { // TODO proper exception
            return createErrorResponse(new ResponseContextException(500, e));
        }

        String typeId = (String) properties.get(Property.TYPE_ID);
        Type type = repository.getType(typeId);
        if (type == null) {
            return createErrorResponse(new ResponseContextException(
                    "Unknown type: " + typeId, 500));
        }

        // set Atom-defined properties into entry
        // TODO hardcoded title properties...
        PropertyDefinition pdt = type.getPropertyDefinition("title");
        if (pdt == null) {
            pdt = type.getPropertyDefinition("dc:title");
        }
        if (pdt == null) {
            pdt = type.getPropertyDefinition(Property.NAME); // mandatory
        }
        String title = entry.getTitle(); // Atom MUST
        properties.put(pdt.getId(), title);
        // TODO summary
        // parse the date ourselves, as Abdera's AtomDate loses the timezone
        Calendar updated;
        DateTime updatedElement = entry.getUpdatedElement(); // Atom MUST
        if (updatedElement == null) { // TODO XXX TCK
            updated = null;
        } else {
            updated = GregorianCalendar.fromAtomPub(updatedElement.getText());
            properties.put(Property.LAST_MODIFICATION_DATE, updated);
        }

        SPI spi = repository.getSPI(); // TODO XXX connection leak
        ObjectId folderId = new SimpleObjectId(id);
        ObjectId objectId;
        switch (type.getBaseType()) {
        case DOCUMENT:
            ContentStream contentStream = null; // TODO
            VersioningState versioningState = null; // TODO
            objectId = spi.createDocument(properties, folderId, contentStream,
                    versioningState);
            break;
        case FOLDER:
            objectId = spi.createFolder(properties, folderId);
            break;
        default:
            throw new UnsupportedOperationException("not implemented: "
                    + type.getBaseType());
        }

        ObjectEntry object = spi.getProperties(objectId, null, false, false);

        // prepare the updated entry to return in the response
        // AbstractEntityCollectionAdapter#getEntryFromCollectionProvider is
        // package-private...
        entry = request.getAbdera().getFactory().newEntry();
        try {
            // AbstractEntityCollectionAdapter#buildEntry is private...
            addEntryDetails(request, entry, null, object);
            if (isMediaEntry(object)) {
                IRI feedIri = null;
                addMediaContent(feedIri, entry, object, request);
            } else {
                addContent(entry, object, request);
            }
        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        }
        String link = getObjectLink(object.getId(), request);
        return buildCreateEntryResponse(link, entry);
    }

    // unused but abstract in parent...
    @Override
    public ObjectEntry postEntry(String title, IRI id, String summary,
            Date updated, List<Person> authors, Content content,
            RequestContext request) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

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
        return spi.hasContentStream(object);
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
        return (String) object.getValue(Property.CONTENT_STREAM_MIME_TYPE);
    }

    @Override
    public ObjectEntry getEntry(String id, RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        return spi.getProperties(spi.newObjectId(id), null, false, false);
    }

    @Override
    public String getResourceName(RequestContext request) {
        return request.getTarget().getParameter("objectid");
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
        String title = null;
        try {
            title = (String) object.getValue("title"); // TODO improve
        } catch (Exception e) {
            // no such property or bad type
        }
        if (title == null) {
            title = (String) object.getValue(Property.NAME);
        }
        return title;
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
            return null;
        }
        Text text = request.getAbdera().getFactory().newSummary();
        text.setValue(summary);
        return text;
    }

}
