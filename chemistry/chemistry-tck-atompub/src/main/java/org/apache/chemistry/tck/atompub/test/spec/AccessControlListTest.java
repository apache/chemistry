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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISAccessControlList;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.apache.chemistry.tck.atompub.fixture.ManageAccessControlListVisitor;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;

/**
 * CMIS Access Control List Tests.
 */
public class AccessControlListTest extends TCKTest {

    /**
     * Test folder access control list.
     * 
     * @throws Exception
     *             on error
     */
    public void testFolderAccessControlList() throws Exception {
        checkACLCapability(false);
        Entry folder = fixture.createTestFolder("testAccessControlList");

        Link accessControlListLink = folder.getLink(CMISConstants.REL_ACL);
        Request req = new GetRequest(accessControlListLink.getHref().toString());
        Response accessControlListRes = client.executeRequest(req, 200);
        Assert.assertNotNull(accessControlListRes);
        Element accessControlList = model.parse(new StringReader(accessControlListRes.getContentAsString()), null);
        Assert.assertNotNull(accessControlList);
        Assert.assertTrue(accessControlList instanceof CMISAccessControlList);
        CMISObject childObject = folder.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        String objectId = childObject.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);

        // Fetch via ID, this time with ACL
        CMISUriTemplate objectByIdTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>(5);
        variables.put("id", objectId);
        variables.put("includeACL", "true");
        IRI objectByIdRequest = objectByIdTemplate.generateUri(variables);
        folder = client.getEntry(objectByIdRequest);

        Assert.assertNotNull(folder);
        childObject = folder.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        CMISAccessControlList objectAccessControlList = childObject.getExtension(CMISConstants.ACCESS_CONTROL_LIST);
        Assert.assertNotNull(objectAccessControlList);
        Assert.assertEquals(((CMISAccessControlList) accessControlList).getHashedEntries(), objectAccessControlList
                .getHashedEntries());
    }

    /**
     * Test document access control list.
     * 
     * @throws Exception
     *             on error
     */
    public void testDocumentAccessControlList() throws Exception {
        checkACLCapability(false);
        Entry document = fixture.createTestDocument("testDocumentAccessControlList");

        Link accessControlListLink = document.getLink(CMISConstants.REL_ACL);
        Request req = new GetRequest(accessControlListLink.getHref().toString());
        Response accessControlListRes = client.executeRequest(req, 200);
        Assert.assertNotNull(accessControlListRes);
        Element accessControlList = model.parse(new StringReader(accessControlListRes.getContentAsString()), null);
        Assert.assertNotNull(accessControlList);
        Assert.assertTrue(accessControlList instanceof CMISAccessControlList);
        CMISObject childObject = document.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        String objectId = childObject.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);

        // Fetch via ID, this time with ACL
        CMISUriTemplate objectByIdTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>(5);
        variables.put("id", objectId);
        variables.put("includeACL", "true");
        IRI objectByIdRequest = objectByIdTemplate.generateUri(variables);
        document = client.getEntry(objectByIdRequest);

        Assert.assertNotNull(document);
        childObject = document.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        CMISAccessControlList objectAccessControlList = childObject.getExtension(CMISConstants.ACCESS_CONTROL_LIST);
        Assert.assertNotNull(objectAccessControlList);
        Assert.assertEquals(((CMISAccessControlList) accessControlList).getHashedEntries(), objectAccessControlList
                .getHashedEntries());
    }

    /**
     * Tests managing the access control lists of a folder and a document
     * 
     * @throws Exception
     *             on error
     */
    public void testManageAccessControlList() throws Exception {
        checkACLCapability(true);
        EntryTree folderTree = fixture.createTestTree("testManageAccessControlList", 1, 1, null, null);
        folderTree.walkTree(new ManageAccessControlListVisitor(client, model));
    }

    /**
     * Check whether we have sufficient capability to read and/or manage acls.
     * 
     * @param manage
     *            <code>true</code> if we require management capability or
     *            <code>false</code> if read capability is sufficient.
     * 
     * @throws TCKSkipCapabilityException
     *             if we don't have sufficient capability
     * @throws Exception
     *             on error
     */
    private void checkACLCapability(boolean manage) throws TCKSkipCapabilityException, Exception {
        String aclCapability = client.getCapabilities().getACL();
        if (manage) {
            if (!aclCapability.equals("manage")) {
                throw new TCKSkipCapabilityException("ACL", "manage", aclCapability);
            }
        } else {
            if (aclCapability.equals("none")) {
                throw new TCKSkipCapabilityException("ACL", "read", aclCapability);
            }
        }
        // Ensure the ACL capability info has been supplied as required
        assertNotNull(client.getACLCapability());
    }
}
