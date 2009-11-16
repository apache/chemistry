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
import org.apache.chemistry.atompub.AtomPubCMIS;

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
        ObjectId folderId = target.getParameter(AtomPubCMIS.PARAM_FOLDER_ID) == null ? null
                : spi.newObjectId(target.getParameter(AtomPubCMIS.PARAM_FOLDER_ID));
        String filter = target.getParameter(AtomPubCMIS.PARAM_FILTER);
        int maxItems = target.getParameter(AtomPubCMIS.PARAM_MAX_ITEMS) == null ? 0
                : Integer.parseInt(target.getParameter(AtomPubCMIS.PARAM_MAX_ITEMS));
        int skipCount = target.getParameter(AtomPubCMIS.PARAM_SKIP_COUNT) == null ? 0
                : Integer.parseInt(target.getParameter(AtomPubCMIS.PARAM_SKIP_COUNT));
        boolean includeAllowableActions = target.getParameter(AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS) == null ? false
                : Boolean.parseBoolean(target.getParameter(AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
        boolean includeRelationships = target.getParameter(AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null ? false
                : Boolean.parseBoolean(target.getParameter(AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS));
        boolean[] hasMoreItems = new boolean[1];
        Collection<ObjectEntry> objectEntries = spi.getCheckedOutDocuments(
                folderId, filter, includeAllowableActions,
                includeRelationships, maxItems, skipCount, hasMoreItems);
        return objectEntries;
    }

}
