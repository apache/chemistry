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
 */
package org.apache.chemistry.atompub.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.stax.FOMFeed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.axiom.om.OMElement;
import org.apache.chemistry.ContentAlreadyExistsException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Paging;
import org.apache.chemistry.Property;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.UpdateConflictException;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.impl.simple.SimpleObjectId;

/**
 * CMIS Collection for the children of an object.
 */
public class CMISChildrenCollection extends CMISObjectsCollection {

    public CMISChildrenCollection(String type, String id, Repository repository) {
        super(type, "children", id, repository);
    }

    /*
     * ----- AbstractEntityCollectionAdapter -----
     */

    @Override
    public ResponseContext getFeed(RequestContext request) {
        try {
            ListPage<ObjectEntry> entries = getEntries(request);
            Feed feed = createFeedBase(entries, request);
            addFeedDetails(feed, entries, request);
            return buildGetFeedResponse(feed);
        } catch (ResponseContextException e) {
            return createErrorResponse(e);
        }
    }

    protected Feed createFeedBase(ListPage<ObjectEntry> entries,
            RequestContext request) throws ResponseContextException {
        Feed feed = super.createFeedBase(request);

        feed.addLink(getChildrenLink(id, request), AtomPub.LINK_SELF,
                AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);

        // link to parent children feed, needs parent id
        ObjectEntry entry;
        SPI spi = repository.getSPI();
        try {
            entry = spi.getProperties(spi.newObjectId(id), null);
        } finally {
            spi.close();
        }
        if (entry == null) {
            throw new ResponseContextException("Not found: " + id, 404);
        }
        String pid = (String) entry.getValue(Property.PARENT_ID);
        if (pid != null) {
            feed.addLink(getChildrenLink(pid, request), AtomPub.LINK_UP,
                    AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);
        }

        // AtomPub paging
        // next
        if (entries.getHasMoreItems()) {
            // find next skipCount
            int skipCount = getParameter(request, AtomPubCMIS.PARAM_SKIP_COUNT,
                    0);
            skipCount += entries.size();
            // compute new URI
            String uri = request.getResolvedUri().toString();
            Pattern pat = Pattern.compile("(.*[?&]"
                    + AtomPubCMIS.PARAM_SKIP_COUNT + "=)(-?[0-9]+)(.*)");
            Matcher m = pat.matcher(uri);
            if (m.matches()) {
                uri = m.group(1) + skipCount + m.group(3);
            } else {
                // unexpected URI...
            }
            feed.addLink(uri, AtomPub.LINK_NEXT, AtomPub.MEDIA_TYPE_ATOM_FEED,
                    null, null, -1);
        }
        // TODO prev, first, last

        // CMIS paging: numItems
        int numItems = entries.getNumItems();
        if (numItems != -1) {
            FOMFeed fomFeed = (FOMFeed) feed;
            OMElement el = fomFeed.getOMFactory().createOMElement(
                    AtomPubCMIS.NUM_ITEMS);
            el.setText(Integer.toString(numItems));
            fomFeed.addChild(el);
        }

        return feed;
    }

    protected void addFeedDetails(Feed feed, ListPage<ObjectEntry> entries,
            RequestContext request) throws ResponseContextException {
        feed.setUpdated(new Date()); // TODO
        if (entries != null) {
            for (ObjectEntry entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);

                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                }
            }
        }
    }

    @Override
    public ListPage<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            ObjectId objectId = spi.newObjectId(id);
            Target target = request.getTarget();
            String orderBy = target.getParameter(AtomPubCMIS.PARAM_ORDER_BY);
            String properties = target.getParameter(AtomPubCMIS.PARAM_FILTER);
            String renditions = target.getParameter(AtomPubCMIS.PARAM_RENDITION_FILTER);
            String rel = target.getParameter(AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS);
            RelationshipDirection relationships = RelationshipDirection.fromInclusion(rel);
            boolean allowableActions = getParameter(request,
                    AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
            Inclusion inclusion = new Inclusion(properties, renditions,
                    relationships, allowableActions, false, false);
            if ("descendants".equals(getType())) {
                int depth = getParameter(request, AtomPubCMIS.PARAM_DEPTH, 1);
                List<ObjectEntry> descendants = spi.getDescendants(objectId,
                        depth, orderBy, inclusion);
                SimpleListPage<ObjectEntry> page = new SimpleListPage<ObjectEntry>(
                        descendants);
                page.setHasMoreItems(false);
                page.setNumItems(page.size());
                return page;
            } else {
                int maxItems = getParameter(request,
                        AtomPubCMIS.PARAM_MAX_ITEMS, 0);
                int skipCount = getParameter(request,
                        AtomPubCMIS.PARAM_SKIP_COUNT, 0);
                ListPage<ObjectEntry> children = spi.getChildren(objectId,
                        inclusion, orderBy, new Paging(maxItems, skipCount));
                return children;
            }
        } finally {
            spi.close();
        }
    }

    @Override
    public void putMedia(ObjectEntry entry, MimeType contentType, String slug,
            InputStream in, RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            ContentStream cs = new SimpleContentStream(in,
                    contentType.toString(), slug);
            spi.setContentStream(entry, true, cs);
        } catch (IOException e) {
            throw new ResponseContextException(e.toString(), 500);
        } catch (UpdateConflictException e) {
            throw new ResponseContextException(e.toString(), 409); // Conflict
        } catch (ContentAlreadyExistsException e) {
            // cannot happen, overwrite = true
            throw new ResponseContextException(e.toString(), 409); // Conflict
        } finally {
            spi.close();
        }
    }

    @Override
    public void deleteMedia(String resourceName, RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            String id = getResourceName(request);
            spi.deleteContentStream(new SimpleObjectId(id));
        } catch (UpdateConflictException e) {
            throw new ResponseContextException(e.toString(), 409); // Conflict
        } finally {
            spi.close();
        }
    }
}
