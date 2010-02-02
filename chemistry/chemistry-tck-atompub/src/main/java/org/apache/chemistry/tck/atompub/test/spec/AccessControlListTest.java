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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISACLCapability;
import org.apache.chemistry.abdera.ext.CMISAccessControlEntry;
import org.apache.chemistry.abdera.ext.CMISAccessControlList;
import org.apache.chemistry.abdera.ext.CMISAllowableActions;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISRepositoryInfo;
import org.apache.chemistry.abdera.ext.CMISTypeDefinition;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PutRequest;
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
        Assert.assertEquals(hashAccessControlList((CMISAccessControlList) accessControlList),
                hashAccessControlList(objectAccessControlList));
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
        Assert.assertEquals(hashAccessControlList((CMISAccessControlList) accessControlList),
                hashAccessControlList(objectAccessControlList));
    }

    /**
     * Test manage access control list.
     * 
     * @throws Exception
     *             on error
     */
    public void testManageAccessControlList() throws Exception {
        checkACLCapability(true);
        Entry document = fixture.createTestDocument("testManageAccessControlList");

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

        // Check whether apply ACL is an allowable action
        CMISAllowableActions objectAllowableActions = childObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
        Assert.assertNotNull(objectAllowableActions);
        boolean canApplyACL = objectAllowableActions.isAllowed("canApplyACL");

        // Check whether the typedef allows ACL management
        Link typeLink = document.getLink(CMISConstants.REL_DESCRIBED_BY);
        Assert.assertNotNull(typeLink);
        Entry type = client.getEntry(typeLink.getHref());
        Assert.assertNotNull(type);
        CMISTypeDefinition docType = type.getExtension(CMISConstants.TYPE_DEFINITION);
        Assert.assertNotNull(docType);
        boolean controllableACL = docType.getControllableACL();
        if (!controllableACL) {
            Assert.assertFalse(canApplyACL);
        }
        // If we are not allowed to apply the ACL, we should expect an error
        // status code
        int expectedStatusMin = 200, expectedStatusMax = 200;
        if (!canApplyACL) {
            expectedStatusMin = 400;
            expectedStatusMax = 499;
        }

        // Convert the ACL to an easy to use form
        Set<List<Object>> hashedACL = hashAccessControlList((CMISAccessControlList) accessControlList);

        // Take a back up for future reference
        Set<List<Object>> originalACL = new HashSet<List<Object>>(hashedACL);

        // Choose some permissions to add in to the ACL
        CMISACLCapability aclCapability = client.getACLCapability();
        Assert.assertNotNull(aclCapability);
        Set<String> repositoryPermissions = new HashSet<String>(aclCapability.getRepositoryPermissions());
        String supportedPermissions = aclCapability.getSupportedPermissions();
        CMISRepositoryInfo info = client.getRepositoryInfo();

        // Add some ACES with repository permissions if supported
        if (!supportedPermissions.equals("basic")) {
            chooseRepositoryPermission(repositoryPermissions, client.getUserId(), hashedACL);
            chooseRepositoryPermission(repositoryPermissions, info.getPrincipalAnonymous(), hashedACL);
            chooseRepositoryPermission(repositoryPermissions, info.getPrincipalAnyone(), hashedACL);
        }

        // Add some ACES with CMIS permissions if supported
        if (!supportedPermissions.equals("repository")) {
            addAce(client.getUserId(), hashedACL, "cmis:write");
            addAce(info.getPrincipalAnonymous(), hashedACL, "cmis:read");
            addAce(info.getPrincipalAnyone(), hashedACL, "cmis:read");
        }

        // Apply the ACL with the additions
        accessControlList = applyACL(accessControlListLink, hashedACL, expectedStatusMin, expectedStatusMax);

        // If we expected success, try removing the ACEs we added and restoring the ACL to its original state
        if (accessControlList != null) {
            applyACL(accessControlListLink, originalACL, expectedStatusMin, expectedStatusMax);
        }
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

    /**
     * Flattens an access control list into a structure that is easy to compare:
     * a set of triples containing the principal ID, the direct flag and the
     * permission name.
     * 
     * @param accessControlList
     *            the access control list
     * 
     * @return the set of triples
     */
    private static Set<List<Object>> hashAccessControlList(CMISAccessControlList accessControlList) {
        List<CMISAccessControlEntry> entries = accessControlList.getEntries();
        Set<List<Object>> hashSet = new HashSet<List<Object>>(entries.size() * 2);
        for (CMISAccessControlEntry accessControlEntry : entries) {
            String principalId = accessControlEntry.getPrincipalId();
            Boolean direct = accessControlEntry.isDirect();
            for (String permission : accessControlEntry.getPermissions()) {
                List<Object> comparable = new ArrayList<Object>(3);
                comparable.add(principalId);
                comparable.add(direct);
                comparable.add(permission);
                hashSet.add(comparable);
            }
        }
        return hashSet;
    }

    /**
     * Chooses a repository permission to add to an ACL for a given principal.
     * Tries to find one that would result in a new entry.
     * 
     * @param repositoryPermissions
     *            the repository permissions. Permissions are removed as they
     *            are used
     * @param principalId
     *            the principal id. If <code>null</code> the method returns
     *            immediately.
     * @param hashedACL
     *            the current state of the ACL. Tries to avoid creating an ACE
     *            that is already present. The ace with the chosen permission is
     *            added to this set
     * 
     * @return an ACE with the chosen permission or <code>null</code> if it was
     *         not possible to choose one< object>
     */
    private static List<Object> chooseRepositoryPermission(Set<String> repositoryPermissions, String principalId,
            Set<List<Object>> hashedACL) {
        if (principalId == null) {
            return null;
        }
        List<Object> potential = new ArrayList<Object>(3);
        potential.add(principalId);
        potential.add(Boolean.TRUE);
        for (String permission : repositoryPermissions) {
            potential.add(permission);
            if (hashedACL.add(potential)) {
                repositoryPermissions.remove(permission);
                return potential;
            }
            potential.remove(2);
        }
        return null;
    }

    /**
     * Adds an ACE for the given principal and permission to the given ACL if it
     * does not already exist.
     * 
     * @param principalId
     *            the principal id. If <code>null</code> the method returns
     *            immediately.
     * @param hashedACL
     *            the current state of the ACL. The ace with the chosen given is
     *            added to this set
     * @param permission
     *            the permission
     * 
     * @return an ACE with the permission if it did not already exist in the ACL
     *         or <code>null</code> otherwise
     */
    private static List<Object> addAce(String principalId, Set<List<Object>> hashedACL, String permission) {
        if (principalId == null) {
            return null;
        }
        List<Object> potential = new ArrayList<Object>(3);
        potential.add(principalId);
        potential.add(Boolean.TRUE);
        potential.add(permission);
        if (hashedACL.add(potential)) {
            return potential;
        }
        return null;
    }

    /**
     * Applies the given ACL using the CMIS API.
     * 
     * @param accessControlListLink
     *            the access control list link
     * @param hashedACL
     *            the hashed ACL
     * @param expectedStatusMin
     *            Minimum expected result status code
     * @param expectedStatusMax
     *            Maximum expected result status code
     * 
     * @return the resulting access control list
     * 
     * @throws Exception
     *             on error
     */
    private CMISAccessControlList applyACL(Link accessControlListLink, Set<List<Object>> hashedACL,
            int expectedStatusMin, int expectedStatusMax) throws Exception {
        StringBuilder buff = new StringBuilder(1024);
        buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
                "<cmis:acl xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\">");
        for (List<Object> ace : hashedACL) {
            buff.append("<cmis:permission><cmis:principal><cmis:principalId>").append(ace.get(0)).append(
                    "</cmis:principalId></cmis:principal><cmis:permission>").append(ace.get(2)).append(
                    "</cmis:permission><cmis:direct>").append(ace.get(1)).append("</cmis:direct></cmis:permission>");
        }
        buff.append("</cmis:acl>");
        String req = buff.toString();
        client.getAppValidator().validate(new StreamSource(new StringReader(req)));
        Request putReq = new PutRequest(accessControlListLink.getHref().toString(), req, CMISConstants.MIMETYPE_CMISACL);
        Response aclRes = client.executeRequest(putReq, expectedStatusMin, expectedStatusMax);
        Assert.assertNotNull(aclRes);
        if (aclRes.getStatus() == 200) {
            Element accessControlList = model.parse(new StringReader(aclRes.getContentAsString()), null);
            Assert.assertNotNull(accessControlList);
            Assert.assertTrue(accessControlList instanceof CMISAccessControlList);
            return (CMISAccessControlList) accessControlList;
        }
        return null;
    }
}
