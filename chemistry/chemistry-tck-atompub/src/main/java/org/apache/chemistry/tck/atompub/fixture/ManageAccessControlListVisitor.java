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
package org.apache.chemistry.tck.atompub.fixture;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISACLCapability;
import org.apache.chemistry.abdera.ext.CMISAccessControlList;
import org.apache.chemistry.abdera.ext.CMISAllowableActions;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISRepositoryInfo;
import org.apache.chemistry.abdera.ext.CMISTypeDefinition;
import org.apache.chemistry.abdera.ext.utils.CMISAppModel;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.EntryTree.TreeVisitor;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PutRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;

/**
 * Visitor that manages ACLs on all objects in a tree.
 */
public class ManageAccessControlListVisitor implements TreeVisitor {

    private CMISClient client;
    private CMISAppModel model;
    private Set<String> rejectedTypes = new HashSet<String>(5);

    public ManageAccessControlListVisitor(CMISClient client, CMISAppModel model) {
        this.client = client;
        this.model = model;
    }

    public void visit(EntryTree entry) throws Exception {
        Link accessControlListLink = entry.entry.getLink(CMISConstants.REL_ACL);
        Request req = new GetRequest(accessControlListLink.getHref().toString());
        Response accessControlListRes = client.executeRequest(req, 200);
        Assert.assertNotNull(accessControlListRes);
        Element accessControlList = model.parse(new StringReader(accessControlListRes.getContentAsString()), null);
        Assert.assertNotNull(accessControlList);
        Assert.assertTrue(accessControlList instanceof CMISAccessControlList);
        CMISObject childObject = entry.entry.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        String objectId = childObject.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);

        // Check whether apply ACL is an allowable action
        CMISAllowableActions objectAllowableActions = childObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
        Assert.assertNotNull(objectAllowableActions);
        boolean canApplyACL = objectAllowableActions.isAllowed("canApplyACL");

        // Check whether the typedef allows ACL management
        Link typeLink = entry.entry.getLink(CMISConstants.REL_DESCRIBED_BY);
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
            // Remember the set of rejected types to help with change log
            // validation
            rejectedTypes.add(docType.getBaseId());
        }

        // Convert the ACL to an easy to use form
        Set<List<Object>> hashedACL = ((CMISAccessControlList) accessControlList).getHashedEntries();

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

        // If change logging supported + changesOnType includes document, ensure
        // an entry shows up in the change log and ensure ACL is identical when
        // includeACL=true

        // If we expected success, try removing the ACEs we added and restoring
        // the ACL to its original state
        if (accessControlList != null) {
            applyACL(accessControlListLink, originalACL, expectedStatusMin, expectedStatusMax);
        }
    }

    /**
     * Gets the set of base types that we have not been able to handle, and thus
     * do not expect to show in the change log.
     * 
     * @return the rejected types
     */
    public Set<String> getRejectedTypes() {
        return rejectedTypes;
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
