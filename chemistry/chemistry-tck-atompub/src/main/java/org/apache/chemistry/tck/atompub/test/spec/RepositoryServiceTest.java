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
 *     David Caruana, Alfresco
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.tck.atompub.test.spec;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Service;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.junit.Assert;


/**
 * CMIS Repository Service Document Tests
 */
public class RepositoryServiceTest extends TCKTest {
    
    public void testRepository() throws Exception {
        Service service = client.getRepository();
        Assert.assertNotNull(service);
    }

    public void testGetRootCollection() throws Exception {
        IRI rootHREF = client.getRootCollection(client.getWorkspace());
        client.executeRequest(new GetRequest(rootHREF.toString()), 200, client.getAtomValidator());
    }

}
