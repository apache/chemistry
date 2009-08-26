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
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.AssertNotExistVisitor;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.apache.chemistry.tck.atompub.http.DeleteRequest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.junit.Assert;

/**
 * CMIS Delete Tests
 */
public class DeleteTest extends TCKTest {

    public void testDeleteFolder() throws Exception {
        Entry folder = fixture.createTestFolder("testFolder");

        // delete
        client.executeRequest(new DeleteRequest(folder.getSelfLink().getHref().toString()), 204);

        // ensure removed
        client.executeRequest(new GetRequest(folder.getSelfLink().getHref().toString()), 404);
        Entry testCaseFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testCaseFolder);
        Feed children = client.getFeed(childrenLink.getHref());
        Assert.assertEquals(0, children.getEntries().size());
    }

    public void testDeleteDocument() throws Exception {
        Entry document = fixture.createTestDocument("testDocument");

        // delete
        client.executeRequest(new DeleteRequest(document.getSelfLink().getHref().toString()), 204);

        // ensure removed
        client.executeRequest(new GetRequest(document.getSelfLink().getHref().toString()), 404);
        Entry testCaseFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testCaseFolder);
        Feed children = client.getFeed(childrenLink.getHref());
        Assert.assertEquals(0, children.getEntries().size());
    }

    public void testDeleteDescendants() throws Exception {
        EntryTree descendants = fixture.createTestTree("descendants", 3, 2, null, null);

        // delete
        Link descendantsLink = client.getDescendantsLink(descendants.entry);
        Assert.assertNotNull(descendantsLink);
        client.executeRequest(new DeleteRequest(descendantsLink.getHref().toString()), 204);

        // ensure all have been deleted
        descendants.walkTree(new AssertNotExistVisitor(client));
    }

    public void testDeleteFolderTree() throws Exception {
        EntryTree descendants = fixture.createTestTree("descendants", 3, 0, null, null);

        // delete
        Link folderTreeLink = client.getFolderTreeLink(descendants.entry);
        Assert.assertNotNull(folderTreeLink);
        client.executeRequest(new DeleteRequest(folderTreeLink.getHref().toString()), 204);

        // ensure all have been deleted
        descendants.walkTree(new AssertNotExistVisitor(client));
    }

    public void testDeleteFolderTreeWithDocuments() throws Exception {
        EntryTree descendants = fixture.createTestTree("descendants", 3, 2, null, null);

        // delete
        Link folderTreeLink = client.getFolderTreeLink(descendants.entry);
        Assert.assertNotNull(folderTreeLink);
        client.executeRequest(new DeleteRequest(folderTreeLink.getHref().toString()), 204);

        // ensure all have been deleted
        descendants.walkTree(new AssertNotExistVisitor(client));
    }

}
