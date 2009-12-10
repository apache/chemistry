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

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Paging;
import org.apache.chemistry.RelationshipDirection;
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
        SPI spi = repository.getSPI();
        try {
            Target target = request.getTarget();
            String folderIdString = target.getParameter(AtomPubCMIS.PARAM_FOLDER_ID);
            ObjectId folderId = folderIdString == null ? null
                    : spi.newObjectId(folderIdString);
            String properties = target.getParameter(AtomPubCMIS.PARAM_FILTER);
            boolean allowableActions = getParameter(request,
                    AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
            String incl = target.getParameter(AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS);
            RelationshipDirection relationships = RelationshipDirection.fromInclusion(incl);
            Inclusion inclusion = new Inclusion(properties, null,
                    relationships, allowableActions, false, false);
            int maxItems = getParameter(request, AtomPubCMIS.PARAM_MAX_ITEMS, 0);
            int skipCount = getParameter(request, AtomPubCMIS.PARAM_SKIP_COUNT,
                    0);
            ListPage<ObjectEntry> objectEntries = spi.getCheckedOutDocuments(
                    folderId, inclusion, new Paging(maxItems, skipCount));
            return objectEntries;
        } finally {
            spi.close();
        }
    }

}
