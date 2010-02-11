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
 *     David Ward, Alfresco
 */
package org.apache.chemistry.tck.atompub.test.spec;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.AssertChangeTypeInChangeLogVisitor;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.apache.chemistry.tck.atompub.fixture.ManageAccessControlListVisitor;
import org.apache.chemistry.tck.atompub.fixture.UpdateDocumentsVisitor;
import org.junit.Assert;

/**
 * CMIS Change Log Tests.
 */
public class ChangeLogTest extends TCKTest {

    /**
     * Tests all change types on documents and folders.
     * 
     * @throws Exception
     *             on error
     */
    public void testDocumentsAndFolders() throws Exception {
        testDocumentsAndFolders(null);
    }

    /**
     * Run the same tests, but with paged change logs.
     * 
     * @throws Exception
     *             on error
     */
    public void testPaging() throws Exception {
        testDocumentsAndFolders(1);
    }

    /**
     * Tests all change types on documents and folders.
     * 
     * @param maxItems
     *            the maximum number of items per page or <code>null</code> if
     *            unlimited
     * 
     * @throws Exception
     *             on error
     */
    private void testDocumentsAndFolders(Integer maxItems) throws Exception {
        // Validate the capability
        checkChangeLogCapability(false);

        // Get the change log token
        String changeLogToken = client.getRepositoryInfo(true).getLatestChangeLogToken();

        // Create a folder tree
        EntryTree folderTree = fixture.createTestTree("changeLog", 4, 3, null, null);

        // Fetch changesOnType
        Set<String> changesOnType = client.getRepositoryInfo().getChangesOnType();
        assertTrue(changesOnType.size() > 0);

        // Check created entries exist for the objects of applicable types
        folderTree.walkTree(new AssertChangeTypeInChangeLogVisitor(client, model, changeLogToken, maxItems, false,
                false, CMISConstants.CHANGE_TYPE_CREATED, changesOnType));

        // Get the new change log token
        changeLogToken = client.getRepositoryInfo(true).getLatestChangeLogToken();
        assertNotNull(changeLogToken);

        // Walk the tree and update all the documents

        // TODO: Work out a reliable way of updating folders!
        // 2.1.11.1: "A Repository MAY record events such as
        // filing/unfiling/moving of Documents as change events on the
        // Documents, their parent Folder(s), or both the Documents and the
        // parent Folders."
        if (changesOnType.contains(CMISConstants.TYPE_DOCUMENT)) {
            folderTree.walkTree(new UpdateDocumentsVisitor(client, templates));

            // Check updated entries exist for the document objects
            folderTree.walkTree(new AssertChangeTypeInChangeLogVisitor(client, model, changeLogToken, maxItems, false,
                    true, CMISConstants.CHANGE_TYPE_UPDATED, Collections.singleton(CMISConstants.TYPE_DOCUMENT)));

            // Get the new change log token
            changeLogToken = client.getRepositoryInfo(true).getLatestChangeLogToken();
            assertNotNull(changeLogToken);
        }

        // Skip security tests if we don't have ACL manage capability
        String aclCapability = client.getCapabilities().getACL();
        if (aclCapability.equals("manage")) {
            // Walk the tree and update all ACLs
            ManageAccessControlListVisitor visitor = new ManageAccessControlListVisitor(client, model); 
            folderTree.walkTree(visitor);

            // Create a filtered set of change types that we know we should be
            // able to manage
            Set<String> filteredChangesOnType = new HashSet<String>(changesOnType);
            filteredChangesOnType.removeAll(visitor.getRejectedTypes());

            // Check security entries exist for the objects of applicable types
            // and include the ACL
            folderTree.walkTree(new AssertChangeTypeInChangeLogVisitor(client, model, changeLogToken, maxItems, true,
                    true, CMISConstants.CHANGE_TYPE_SECURITY, changesOnType));

            // Get the new change log token
            changeLogToken = client.getRepositoryInfo(true).getLatestChangeLogToken();
            assertNotNull(changeLogToken);
        }

        // Delete the tree
        fixture.delete();

        // Check deleted entries exist for the objects of applicable types
        folderTree.walkTree(new AssertChangeTypeInChangeLogVisitor(client, model, changeLogToken, maxItems, false,
                false, CMISConstants.CHANGE_TYPE_DELETED, changesOnType));
    }

    /**
     * Check whether we have sufficient capability to read change logs and
     * optionally their properties.
     * 
     * @param properties
     *            <code>true</code> if we require properties capability or
     *            <code>false</code> if objectidsonly capability is sufficient.
     * 
     * @throws TCKSkipCapabilityException
     *             if we don't have sufficient capability
     * @throws Exception
     *             on error
     */
    private void checkChangeLogCapability(boolean properties) throws TCKSkipCapabilityException, Exception {
        String changesCapability = client.getCapabilities().getChanges();
        if (properties) {
            if (!changesCapability.equals("properties") && !changesCapability.equals("all")) {
                throw new TCKSkipCapabilityException("Changes", "properties", changesCapability);
            }
        } else {
            if (changesCapability.equals("none")) {
                throw new TCKSkipCapabilityException("Changes", "objectidsonly", changesCapability);
            }
        }
        // Ensure we have some types that will show up in the change log!
        Assert.assertFalse(client.getRepositoryInfo().getChangesOnType().isEmpty());
    }
}
