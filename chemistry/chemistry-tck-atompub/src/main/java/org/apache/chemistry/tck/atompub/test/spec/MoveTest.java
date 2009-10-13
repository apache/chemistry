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
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.AssertEntryInFeedVisitor;
import org.apache.chemistry.tck.atompub.fixture.CMISTree;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.junit.Assert;


/**
 * CMIS Move Tests
 */
public class MoveTest extends TCKTest {
    
    public void testMoveDocument() throws Exception {
        EntryTree sourceFolder = fixture.createTestTree("sourceTree", 1, 1, null, null);
        EntryTree destFolder = fixture.createTestTree("destTree", 1, 0, null, null);
        
        // assert one child in source folder
        Link sourceChildrenLink = client.getChildrenLink(sourceFolder.entry);
        Feed sourceChildren = client.getFeed(sourceChildrenLink.getHref());
        Assert.assertEquals(1, sourceChildren.getEntries().size());
        sourceFolder.walkChildren(new AssertEntryInFeedVisitor(sourceChildren));

        // assert no children in dest folder
        Link destChildrenLink = client.getChildrenLink(destFolder.entry);
        Feed destChildren = client.getFeed(destChildrenLink.getHref());
        Assert.assertEquals(0, destChildren.getEntries().size());
        
        // move document
        CMISObject sourceFolderObject = sourceFolder.entry.getExtension(CMISConstants.OBJECT);
        String sourceFolderId = sourceFolderObject.getObjectId().getStringValue();
        Entry document = sourceFolder.children.get(0).entry;
        client.moveObject(destChildrenLink.getHref(), document, sourceFolderId);
        
        // assert one source child in dest folder
        Feed destChildrenAfter = client.getFeed(destChildrenLink.getHref());
        EntryTree destFolderAfter = new CMISTree(destFolder, destChildrenAfter);
        Assert.assertEquals(1, destChildrenAfter.getEntries().size());
        destFolderAfter.walkChildren(new AssertEntryInFeedVisitor(sourceChildren));

        // assert no children in source folder
        Feed sourceChildrenAfter = client.getFeed(sourceChildrenLink.getHref());
        Assert.assertEquals(0, sourceChildrenAfter.getEntries().size());
    }
    
    public void testMoveFolder() throws Exception {
        EntryTree sourceFolder = fixture.createTestTree("sourceTree", 2, 0, null, null);
        EntryTree destFolder = fixture.createTestTree("destTree", 1, 0, null, null);
        
        // assert one child in source folder
        Link sourceChildrenLink = client.getChildrenLink(sourceFolder.entry);
        Feed sourceChildren = client.getFeed(sourceChildrenLink.getHref());
        Assert.assertEquals(1, sourceChildren.getEntries().size());
        sourceFolder.walkChildren(new AssertEntryInFeedVisitor(sourceChildren));

        // assert no children in dest folder
        Link destChildrenLink = client.getChildrenLink(destFolder.entry);
        Feed destChildren = client.getFeed(destChildrenLink.getHref());
        Assert.assertEquals(0, destChildren.getEntries().size());
        
        // move folder
        CMISObject sourceFolderObject = sourceFolder.entry.getExtension(CMISConstants.OBJECT);
        String sourceFolderId = sourceFolderObject.getObjectId().getStringValue();
        Entry folder = sourceFolder.children.get(0).entry;
        client.moveObject(destChildrenLink.getHref(), folder, sourceFolderId);
        
        // assert one source child in dest folder
        Feed destChildrenAfter = client.getFeed(destChildrenLink.getHref());
        EntryTree destFolderAfter = new CMISTree(destFolder, destChildrenAfter);
        Assert.assertEquals(1, destChildrenAfter.getEntries().size());
        destFolderAfter.walkChildren(new AssertEntryInFeedVisitor(sourceChildren));

        // assert no children in source folder
        Feed sourceChildrenAfter = client.getFeed(sourceChildrenLink.getHref());
        Assert.assertEquals(0, sourceChildrenAfter.getEntries().size());
    }

    public void testInvalidSourceFolderId() throws Exception {
        EntryTree sourceFolder = fixture.createTestTree("sourceTree", 1, 1, null, null);
        EntryTree destFolder = fixture.createTestTree("destTree", 1, 0, null, null);
        
        // assert one child in source folder
        Link sourceChildrenLink = client.getChildrenLink(sourceFolder.entry);
        Feed sourceChildren = client.getFeed(sourceChildrenLink.getHref());
        Assert.assertEquals(1, sourceChildren.getEntries().size());
        sourceFolder.walkChildren(new AssertEntryInFeedVisitor(sourceChildren));

        // assert no children in dest folder
        Link destChildrenLink = client.getChildrenLink(destFolder.entry);
        Feed destChildren = client.getFeed(destChildrenLink.getHref());
        Assert.assertEquals(0, destChildren.getEntries().size());
        
        // move document
        CMISObject sourceFolderObject = sourceFolder.entry.getExtension(CMISConstants.OBJECT);
        String sourceFolderId = "invalid" + sourceFolderObject.getObjectId().getStringValue();
        Entry document = sourceFolder.children.get(0).entry;
        client.moveObjectRequest(destChildrenLink.getHref(), document, sourceFolderId, 400);
    }

}
