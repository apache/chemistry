/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry.atompub.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ReturnVersion;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.ObjectElement;
import org.apache.chemistry.property.Property;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.type.BaseType;

/**
 * CMIS Collection for the children of a Folder.
 *
 * @author Florent Guillaume
 */
public class CMISCollectionForChildren extends CMISCollection<ObjectEntry> {

    public CMISCollectionForChildren(String type, String id,
            Repository repository) {
        super(type, "children", id, repository);
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
        feed.addLink(getObjectLink(id, request), CMIS.LINK_SOURCE);

        // RFC 5005 paging

        return feed;
    }

    @Override
    public String getId(RequestContext request) {
        return "urn:x-children:" + id;
    }

    public String getTitle(RequestContext request) {
        return "Children";
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
        // CMIS links
        entry.addLink(getRepositoryLink(request), CMIS.LINK_REPOSITORY);
        entry.addLink(getTypeLink(object.getTypeId(), request), CMIS.LINK_TYPE);
        if (object.getType().getBaseType() == BaseType.FOLDER) {
            String oid = object.getId();
            entry.addLink(getChildrenLink(oid, request), CMIS.LINK_CHILDREN);
            entry.addLink(getDescendantsLink(oid, request),
                    CMIS.LINK_DESCENDANTS);
            entry.addLink(getParentsLink(oid, request), CMIS.LINK_PARENTS);
            String pid = object.getId(Property.PARENT_ID);
            if (pid != null) {
                // TODO unclear in spec (parent vs parents)
                entry.addLink(getObjectLink(pid, request), CMIS.LINK_PARENT);
            }
        }
        // entry.addLink("XXX", CMIS.LINK_ALLOWABLE_ACTIONS);
        // entry.addLink("XXX", CMIS.LINK_RELATIONSHIPS);

        // ContentStreamUri needs to know the media link
        String mediaLink = isMediaEntry(object) ? getMediaLink(object.getId(),
                request) : null;
        entry.addExtension(new ObjectElement(factory, object, mediaLink));

        return link;
    }

    protected static String bool(boolean bool) {
        return bool ? "true" : "false";
    }

    @Override
    public Iterable<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getConnection(null).getSPI();
        boolean[] hasMoreItems = new boolean[1];
        List<ObjectEntry> children = spi.getChildren(id, null, null, false,
                false, 0, 0, null, hasMoreItems);
        return children;
    }

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
            author = object.getString(Property.CREATED_BY);
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
        return object.hasContentStream();
    }

    @Override
    protected String addMediaContent(IRI feedIri, Entry entry,
            ObjectEntry object, RequestContext request)
            throws ResponseContextException {
        String mediaLink = getMediaLink(object.getId(), request);
        entry.setContent(new IRI(mediaLink), getContentType(object));
        entry.addLink(mediaLink, "edit-media");
        entry.addLink(mediaLink, "cmis-stream");
        return mediaLink;
    }

    // called when this is not a media entry
    @Override
    public Object getContent(ObjectEntry object, RequestContext request)
            throws ResponseContextException {
        return null;
    }

    @Override
    public String getContentType(ObjectEntry object) {
        return object.getString(Property.CONTENT_STREAM_MIME_TYPE);
    }

    @Override
    public ObjectEntry getEntry(String id, RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getConnection(null).getSPI();
        return spi.getProperties(id, ReturnVersion.THIS, null, false, false);
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
        SPI spi = repository.getConnection(null).getSPI();
        try {
            return spi.getContentStream(object.getId(), 0, -1);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
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
            title = object.getString("title"); // TODO improve
        } catch (Exception e) {
            // no such property or bad type
        }
        if (title == null) {
            title = object.getName();
        }
        return title;
    }

    @Override
    public Date getUpdated(ObjectEntry object) {
        Date date = null;
        try {
            Calendar calendar = object.getDateTime(Property.LAST_MODIFICATION_DATE);
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
            summary = object.getString("description"); // TODO improve
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
