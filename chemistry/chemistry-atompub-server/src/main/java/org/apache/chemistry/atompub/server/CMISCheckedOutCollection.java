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

import java.util.Collection;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.AtomPub;

/**
 * CMIS Collection for the checked out documents.
 */
public class CMISCheckedOutCollection extends CMISObjectsCollection {

    public CMISCheckedOutCollection(Repository repository) {
        super("checkedout", null, null, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    @Override
    protected Feed createFeedBase(RequestContext request)
            throws ResponseContextException {
        Feed feed = super.createFeedBase(request);
        feed.addLink(getCheckedOutLink(request), AtomPub.LINK_SELF,
                AtomPub.MEDIA_TYPE_ATOM_FEED, null, null, -1);
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
        Target target = request.getTarget();
        ObjectId folderId = target.getParameter(PARAM_FOLDER_ID) == null ? null
                : spi.newObjectId(target.getParameter(PARAM_FOLDER_ID));
        String filter = target.getParameter(PARAM_FILTER);
        int maxItems = target.getParameter(PARAM_MAX_ITEMS) == null ? 0
                : Integer.parseInt(target.getParameter(PARAM_MAX_ITEMS));
        int skipCount = target.getParameter(PARAM_SKIP_COUNT) == null ? 0
                : Integer.parseInt(target.getParameter(PARAM_SKIP_COUNT));
        boolean includeAllowableActions = target.getParameter(PARAM_ALLOWABLE_ACTIONS) == null ? false
                : Boolean.parseBoolean(target.getParameter(PARAM_ALLOWABLE_ACTIONS));
        boolean includeRelationships = target.getParameter(PARAM_RELATIONSHIPS) == null ? false
                : Boolean.parseBoolean(target.getParameter(PARAM_RELATIONSHIPS));
        boolean[] hasMoreItems = new boolean[1];
        Collection<ObjectEntry> objectEntries = spi.getCheckedOutDocuments(
                folderId, filter, includeAllowableActions,
                includeRelationships, maxItems, skipCount, hasMoreItems);
        return objectEntries;
    }

}
