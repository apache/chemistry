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

import java.util.List;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;

/**
* CMIS Collection for the children of an object.
 */
public class CMISChildrenCollection extends CMISObjectsCollection {

    public CMISChildrenCollection(String type, String id, Repository repository) {
        super(type, "children", id, repository);
    }

    @Override
    public Iterable<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        boolean[] hasMoreItems = new boolean[1];
        ObjectId objectId = spi.newObjectId(id);
        List<ObjectEntry> children = spi.getChildren(objectId, null, null,
                false, false, 0, 0, null, hasMoreItems);
        return children;
    }

}
