/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis.server.atompub;

import java.util.HashSet;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.jackrabbit.cmis.SimpleCapabilities;
import org.apache.jackrabbit.cmis.SimpleRepository;

public class SimpleRepositoryProvider extends AbstractRepositoryProvider {

    public SimpleRepositoryProvider() {
        super();
        init();

        SimpleRepository repository  = new SimpleRepository();
        repository.setId("repid1");
        repository.setName("repository1");
        repository.setDescription("Repository Description");
        repository.setVendorName("ACME Vendor");
        repository.setProductName("ACME Repository");
        repository.setProductVersion("1.0");
        repository.setRootFolderId("rootFolderId");
        repository.setCapabilities(new SimpleCapabilities());
        repository.setVersionsSupported("0.43");

        setRepository(repository);

        // TODO move this down to AbstractRepositoryProvider
        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo(repository.getDescription());
        wi.setCollections(new HashSet<CollectionInfo>());
        addWorkspace(wi);
    }

    private void init() {
        RouteManager routeManager =
          new RouteManager()
            .addRoute("service", "/", TargetType.TYPE_SERVICE)
            .addRoute("feed", "/:feed", TargetType.TYPE_COLLECTION)
            .addRoute("entry", "/:feed/:entry", TargetType.TYPE_ENTRY);
        setTargetBuilder(routeManager);
        setTargetResolver(routeManager);
    }

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

}
