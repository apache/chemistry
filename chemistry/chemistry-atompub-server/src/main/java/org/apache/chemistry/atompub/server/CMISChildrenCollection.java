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
 *     Amélie Avramo
 */
package org.apache.chemistry.atompub.server;

import java.util.List;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.AtomPub;

/**
 * CMIS Collection for the children of an object.
 */
public class CMISChildrenCollection extends CMISObjectsCollection {

    public CMISChildrenCollection(String type, String id, Repository repository) {
        super(type, "children", id, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    @Override
    protected Feed createFeedBase(RequestContext request)
            throws ResponseContextException {
        Feed feed = super.createFeedBase(request);

        feed.addLink(getChildrenLink(id, request), AtomPub.LINK_SELF,
                AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);

        // link to parent children feed, needs parent id
        SPI spi = repository.getSPI();
        ObjectEntry entry = spi.getProperties(spi.newObjectId(id), null, false,
                false);
        if (entry == null) {
            throw new ResponseContextException("Not found: " + id, 404);
        }
        String pid = (String) entry.getValue(Property.PARENT_ID);
        if (pid != null) {
            feed.addLink(getChildrenLink(pid, request), AtomPub.LINK_UP,
                    AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);
        }
        spi.close();

        // RFC 5005 paging
        return feed;
    }

    /*
     * ----- AbstractEntityCollectionAdapter -----
     */

    @Override
    public Iterable<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        ObjectId objectId = spi.newObjectId(id);
        Target target = request.getTarget();
        String filter = target.getParameter(PARAM_FILTER);
        boolean includeAllowableActions = target.getParameter(PARAM_ALLOWABLE_ACTIONS) == null ? false
                : Boolean.parseBoolean(target.getParameter(PARAM_ALLOWABLE_ACTIONS));
        boolean includeRelationships = target.getParameter(PARAM_RELATIONSHIPS) == null ? false
                : Boolean.parseBoolean(target.getParameter(PARAM_RELATIONSHIPS));
        // TODO proper renditionFilter use
        boolean includeRenditions = target.getParameter(PARAM_RENDITION_FILTER) == null ? false
                : true;
        String orderBy = target.getParameter(PARAM_ORDER_BY);
        if ("descendants".equals(getType())) {
            int depth = target.getParameter(PARAM_DEPTH) == null ? 1
                    : Integer.parseInt(target.getParameter(PARAM_DEPTH));
            List<ObjectEntry> descendants = spi.getDescendants(objectId, depth,
                    filter, includeAllowableActions, includeRelationships,
                    includeRenditions, orderBy);
            return descendants;
        } else {
            int maxItems = target.getParameter(PARAM_MAX_ITEMS) == null ? 0
                    : Integer.parseInt(target.getParameter(PARAM_MAX_ITEMS));
            int skipCount = target.getParameter(PARAM_SKIP_COUNT) == null ? 0
                    : Integer.parseInt(target.getParameter(PARAM_SKIP_COUNT));
            boolean[] hasMoreItems = new boolean[1];
            List<ObjectEntry> children = spi.getChildren(objectId, filter,
                    includeAllowableActions, includeRelationships,
                    includeRenditions, maxItems, skipCount, orderBy,
                    hasMoreItems);
            return children;
        }
    }

}
