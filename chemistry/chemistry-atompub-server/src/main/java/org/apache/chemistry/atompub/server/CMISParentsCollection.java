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

import java.util.Collection;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.AtomPub;

public class CMISParentsCollection extends CMISObjectsCollection {

    public CMISParentsCollection(String type, String id, Repository repository) {
        super(type, "parents", id, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    @Override
    protected Feed createFeedBase(RequestContext request)
            throws ResponseContextException {
        Feed feed = super.createFeedBase(request);
        feed.addLink(getParentsLink(id, request), AtomPub.LINK_SELF,
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
        SPI spi = getSPI(request);
        try {
            ObjectId objectId = spi.newObjectId(id);
            Collection<ObjectEntry> parents = spi.getObjectParents(objectId,
                    null);
            return parents;
        } finally {
            spi.close();
        }
    }

}
