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

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceManager;
import org.apache.chemistry.Repository;

/**
 * Workspace manager that correctly finds the appropriate collection adapter by
 * comparing paths, given that ci.getHref returns an absolute URI.
 */
public class CMISWorkspaceManager extends AbstractWorkspaceManager {

    private final CMISProvider provider;

    public CMISWorkspaceManager(CMISProvider provider) {
        this.provider = provider;
    }

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        Repository repository = provider.getRepository();
        String path = request.getTargetPath();
        String paths = path + '/';
        if (paths.startsWith("/types/") || paths.startsWith("/types?")) {
            return new CMISCollectionForTypes(null, repository);
        }
        if (paths.startsWith("/children/")) {
            String id = request.getTarget().getParameter("objectid");
            return new CMISCollectionForChildren(null, id, repository);
        }
        if (paths.startsWith("/object/")) {
            return new CMISCollectionForChildren(null, null, repository);
        }
        if (paths.startsWith("/file/")) {
            return new CMISCollectionForChildren(null, null, repository);
        }
        if (paths.startsWith("/unfiled/")) {
            return new CMISCollectionForOther(null, "unfiled", null, repository);
        }
        return null;
    }

}
