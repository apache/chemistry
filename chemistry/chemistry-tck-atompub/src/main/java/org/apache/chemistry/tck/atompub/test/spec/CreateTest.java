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
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;

/**
 * CMIS Create Tests
 */
public class CreateTest extends TCKTest
{
    
    public void testCreateFolder()
        throws Exception
    {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        Assert.assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        Assert.assertNotNull(children);
        int entriesBefore = children.getEntries().size();
        Entry folder = client.createFolder(children.getSelfLink().getHref(), null, "testCreateFolder");
        Feed feedFolderAfter = client.getFeed(childrenLink.getHref());
        int entriesAfter = feedFolderAfter.getEntries().size();
        Assert.assertEquals(entriesBefore +1, entriesAfter);
        Entry entry = feedFolderAfter.getEntry(folder.getId().toString());
        Assert.assertNotNull(entry);
    }
    
    public void testCreateDocumentCMISContent()
        throws Exception
    {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        Assert.assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        Assert.assertNotNull(children);
        int entriesBefore = children.getEntries().size();
        Entry document = client.createDocument(children.getSelfLink().getHref(), null, "testCreateDocumentCMISContent", "createdocumentBase64.cmisatomentry.xml");
        Response documentContentRes = client.executeRequest(new GetRequest(document.getContentSrc().toString()), 200);
        String resContent = documentContentRes.getContentAsString();
        Assert.assertEquals(document.getTitle(), resContent);
        Feed feedFolderAfter = client.getFeed(childrenLink.getHref());
        int entriesAfter = feedFolderAfter.getEntries().size();
        Assert.assertEquals(entriesBefore +1, entriesAfter);
        Entry entry = feedFolderAfter.getEntry(document.getId().toString());
        Assert.assertNotNull(entry);
    }

    public void testCreateDocumentAtomContent()
        throws Exception
    {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        Assert.assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        Assert.assertNotNull(children);
        int entriesBefore = children.getEntries().size();
        Entry document = client.createDocument(children.getSelfLink().getHref(), null, "testCreateDocumentAtomContent");
        Response documentContentRes = client.executeRequest(new GetRequest(document.getContentSrc().toString()), 200);
        String resContent = documentContentRes.getContentAsString();
        Assert.assertEquals(document.getTitle(), resContent);
        Feed feedFolderAfter = client.getFeed(childrenLink.getHref());
        int entriesAfter = feedFolderAfter.getEntries().size();
        Assert.assertEquals(entriesBefore +1, entriesAfter);
        Entry entry = feedFolderAfter.getEntry(document.getId().toString());
        Assert.assertNotNull(entry);
    }

    public void testCreateAtomEntry()
        throws Exception
    {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        Assert.assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        Assert.assertNotNull(children);
        int entriesBefore = children.getEntries().size();
        Entry document = client.createDocument(children.getSelfLink().getHref(), null, "Iñtërnâtiônàlizætiøn - 1.html", "createatomentry.atomentry.xml");
        Response documentContentRes = client.executeRequest(new GetRequest(document.getContentSrc().toString()), 200);
        String resContent = documentContentRes.getContentAsString();
        Assert.assertEquals(document.getTitle(), resContent);
        Feed feedFolderAfter = client.getFeed(childrenLink.getHref());
        int entriesAfter = feedFolderAfter.getEntries().size();
        Assert.assertEquals(entriesBefore +1, entriesAfter);
        Entry entry = feedFolderAfter.getEntry(document.getId().toString());
        Assert.assertNotNull(entry);
    }

}
