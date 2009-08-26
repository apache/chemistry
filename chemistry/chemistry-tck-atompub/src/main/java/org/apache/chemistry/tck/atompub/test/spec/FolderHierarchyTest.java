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

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISCapabilities;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.AssertValidObjectParentsVisitor;
import org.apache.chemistry.tck.atompub.fixture.CMISTree;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.junit.Assert;


/**
 * CMIS Folder Hierarchy Tests
 */
public class FolderHierarchyTest extends TCKTest {

    private void getFolderTreeDepthN(int depth, int getDepth) throws Exception {
        // construct hierarchy of folders and docs
        EntryTree folderTree = fixture.createTestTree("foldertree", depth, 2, null, null);

        // retrieve folder tree
        Link treeLink = client.getFolderTreeLink(folderTree.entry);
        Assert.assertNotNull(treeLink);
        Map<String, String> args = new HashMap<String, String>();
        args.put("depth", "" + getDepth);
        Feed tree = client.getFeed(treeLink.getHref(), args);

        // compare constructed folder tree with retrieved one
        EntryTree constructedFoldersOnly = new EntryTree(folderTree, getDepth, false);
        EntryTree retrievedFoldersOnly = new CMISTree(folderTree, tree);
        Assert.assertTrue(constructedFoldersOnly.equalsTree(retrievedFoldersOnly));
        Assert.assertFalse(folderTree.equalsTree(retrievedFoldersOnly));
    }

    public void testGetFolderTreeMinusOne() throws Exception {
        getFolderTreeDepthN(3, -1);
    }

    public void testGetFolderTreeOne() throws Exception {
        getFolderTreeDepthN(3, 1);
    }

    public void testGetFolderTreeExactDepth() throws Exception {
        getFolderTreeDepthN(3, 3);
    }

    public void testGetFolderTreeOverDepth() throws Exception {
        getFolderTreeDepthN(3, 4);
    }

    private void getDescendantsDepthN(int depth, int getDepth) throws Exception {
        // construct hierarchy of folders and docs
        EntryTree descendantsTree = fixture.createTestTree("descendants", depth, 2, null, null);

        // retrieve folder tree
        Link descendantsLink = client.getDescendantsLink(descendantsTree.entry);
        Assert.assertNotNull(descendantsLink);
        Map<String, String> args = new HashMap<String, String>();
        args.put("depth", "" + getDepth);
        Feed descendants = client.getFeed(descendantsLink.getHref(), args);

        // compare constructed folder tree with retrieved one
        EntryTree constructedDescendantsOnly = new EntryTree(descendantsTree, getDepth);
        EntryTree retrievedDescendantsOnly = new CMISTree(descendantsTree, descendants);
        Assert.assertTrue(constructedDescendantsOnly.equalsTree(retrievedDescendantsOnly));
    }
    
    private void checkGetDescendantsCapability() throws TCKSkipCapabilityException, Exception {
        CMISCapabilities capabilities = client.getCapabilities();
        if (!capabilities.getDescendants()) {
        	throw new TCKSkipCapabilityException("getDescendants", "true", "false");
        }
    }
    
    public void testGetDescendantsMinusOne() throws Exception {
    	checkGetDescendantsCapability();    	
        getDescendantsDepthN(3, -1);
    }

    public void testGetDescendantsOne() throws Exception {
    	checkGetDescendantsCapability();    	
        getDescendantsDepthN(3, 1);
    }

    public void testGetDescendantsExactDepth() throws Exception {
    	checkGetDescendantsCapability();    	
        getDescendantsDepthN(3, 3);
    }

    public void testGetDescendantsOverDepth() throws Exception {
    	checkGetDescendantsCapability();    	
        getDescendantsDepthN(3, 4);
    }

    public void testGetObjectParents() throws Exception {
        EntryTree folder = fixture.createTestTree("children", 3, 2, null, null);

        folder.walkTree(new AssertValidObjectParentsVisitor(client));
    }

}
