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
 */
package org.apache.chemistry.tck.atompub.fixture;

import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.EntryTree.TreeVisitor;
import org.apache.chemistry.tck.atompub.http.GetRequest;


/**
 * Asserts each Entry in EntryTree does not exist in CMIS Repository
 */
public class AssertNotExistVisitor implements TreeVisitor {
    
    private CMISClient client;

    public AssertNotExistVisitor(CMISClient client) {
        this.client = client;
    }

    public void visit(EntryTree entry) throws Exception {
        client.executeRequest(new GetRequest(entry.entry.getSelfLink().getHref().toString()), 404);
    }
}
