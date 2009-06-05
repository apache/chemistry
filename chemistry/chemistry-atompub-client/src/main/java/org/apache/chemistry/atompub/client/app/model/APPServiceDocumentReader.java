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
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client.app.model;

import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.atompub.client.app.APPContentManager;
import org.apache.chemistry.atompub.client.common.atom.ReadContext;
import org.apache.chemistry.atompub.client.common.atom.ServiceDocumentReader;

/**
 *
 */
public class APPServiceDocumentReader extends
        ServiceDocumentReader<APPRepository> {

    @Override
    protected void addCollection(APPRepository repo, String href, String type) {
        repo.addCollection(type, href);
    }

    @Override
    protected APPRepository createRepository(ReadContext ctx) {
        return new APPRepository(
                (APPContentManager) ctx.get(APPContentManager.class));
    }

    @Override
    protected void setInfo(APPRepository repo, RepositoryInfo info) {
        repo.setInfo(info);
    }

}
