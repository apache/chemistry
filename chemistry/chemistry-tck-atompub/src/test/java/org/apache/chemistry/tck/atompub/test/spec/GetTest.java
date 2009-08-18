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

import org.apache.abdera.model.Entry;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.junit.Assert;


/**
 * CMIS Get Tests
 */
public class GetTest extends TCKTest {

    public void testGetFolder() throws Exception {
        // get folder
        Entry folder = fixture.getTestCaseFolder();
        Assert.assertNotNull(folder);
        Entry getFolder = client.getEntry(folder.getSelfLink().getHref());
        Assert.assertEquals(folder.getId(), getFolder.getId());
        Assert.assertEquals(folder.getTitle(), getFolder.getTitle());
        Assert.assertEquals(folder.getSummary(), getFolder.getSummary());
    }

    public void testGetDocument() throws Exception {
        Entry document = fixture.createTestDocument("testDocument");
        Entry getDocument = client.getEntry(document.getSelfLink().getHref());
        Assert.assertEquals(document.getId(), getDocument.getId());
        Assert.assertEquals(document.getTitle(), getDocument.getTitle());
        // Assert.assertEquals(document(), getDocument());
    }

    public void testGetDocumentNotExist() throws Exception {
        Entry folder = fixture.getTestCaseFolder();
        // get something that doesn't exist
        // FIXME: Add a decent UID generation policy
        String guid = System.currentTimeMillis() + "";
        client.executeRequest(new GetRequest(folder.getSelfLink().getHref().toString() + guid), 404);
    }

}
