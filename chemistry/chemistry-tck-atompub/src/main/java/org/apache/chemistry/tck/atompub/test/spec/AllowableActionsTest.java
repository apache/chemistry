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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISAllowableAction;
import org.apache.chemistry.abdera.ext.CMISAllowableActions;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;

/**
 * CMIS Allowable Actions Tests 
 */
public class AllowableActionsTest extends TCKTest {

    public void testFolderAllowableActions() throws Exception {
        Entry folder = fixture.createTestFolder("testAllowableActions");

        Link allowableActionsLink = folder.getLink(CMISConstants.REL_ALLOWABLE_ACTIONS);
        Request req = new GetRequest(allowableActionsLink.getHref().toString());
        Response allowableActionsRes = client.executeRequest(req, 200, client.getAtomValidator());
        Assert.assertNotNull(allowableActionsRes);
        Element allowableActions = model.parse(new StringReader(allowableActionsRes.getContentAsString()), null);
        Assert.assertNotNull(allowableActions);
        Assert.assertTrue(allowableActions instanceof CMISAllowableActions);
        CMISObject childObject = folder.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        CMISAllowableActions objectAllowableActions = childObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
        Assert.assertNotNull(objectAllowableActions);
        compareAllowableActions((CMISAllowableActions) allowableActions, objectAllowableActions);

        // retrieve getProperties() with includeAllowableActions flag
        Map<String, String> args = new HashMap<String, String>();
        args.put("includeAllowableActions", "true");
        Entry properties = client.getEntry(folder.getSelfLink().getHref(), args);
        Assert.assertNotNull(properties);
        CMISObject propObject = properties.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(propObject);
        CMISAllowableActions propAllowableActions = propObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
        Assert.assertNotNull(propAllowableActions);
        compareAllowableActions((CMISAllowableActions) allowableActions, propAllowableActions);
    }

    public void testDocumentAllowableActions() throws Exception {
        Entry document = fixture.createTestDocument("testDocumentAllowableActions");

        Link allowableActionsLink = document.getLink(CMISConstants.REL_ALLOWABLE_ACTIONS);
        Request req = new GetRequest(allowableActionsLink.getHref().toString());
        Response allowableActionsRes = client.executeRequest(req, 200, client.getAtomValidator());
        Assert.assertNotNull(allowableActionsRes);
        Element allowableActions = model.parse(new StringReader(allowableActionsRes.getContentAsString()), null);
        Assert.assertNotNull(allowableActions);
        Assert.assertTrue(allowableActions instanceof CMISAllowableActions);
        CMISObject childObject = document.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(childObject);
        CMISAllowableActions objectAllowableActions = childObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
        Assert.assertNotNull(objectAllowableActions);
        compareAllowableActions((CMISAllowableActions) allowableActions, objectAllowableActions);

        // retrieve getProperties() with includeAllowableActions flag
        Map<String, String> args = new HashMap<String, String>();
        args.put("includeAllowableActions", "true");
        Entry properties = client.getEntry(document.getSelfLink().getHref(), args);
        Assert.assertNotNull(properties);
        CMISObject propObject = properties.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(propObject);
        CMISAllowableActions propAllowableActions = propObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
        Assert.assertNotNull(propAllowableActions);
        compareAllowableActions((CMISAllowableActions) allowableActions, propAllowableActions);
    }

    public void testGetChildrenAllowableActions() throws Exception {
        fixture.createTestFolder("testAllowableActions");
        fixture.createTestDocument("testDocumentAllowableActions");
        Entry testCase = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testCase);

        // test allowable actions for children
        Map<String, String> args = new HashMap<String, String>();
        args.put("includeAllowableActions", "true");
        Feed children = client.getFeed(childrenLink.getHref(), args);
        Assert.assertNotNull(children);
        for (Entry child : children.getEntries()) {
            // extract allowable actions from child
            CMISObject childObject = child.getExtension(CMISConstants.OBJECT);
            Assert.assertNotNull(childObject);
            CMISAllowableActions objectAllowableActions = childObject.getExtension(CMISConstants.ALLOWABLE_ACTIONS);
            Assert.assertNotNull(objectAllowableActions);

            // retrieve allowable actions from link
            Link allowableActionsLink = child.getLink(CMISConstants.REL_ALLOWABLE_ACTIONS);
            Request req = new GetRequest(allowableActionsLink.getHref().toString());
            Response allowableActionsRes = client.executeRequest(req, 200, client.getAtomValidator());
            Assert.assertNotNull(allowableActionsRes);
            Element allowableActions = model.parse(new StringReader(allowableActionsRes.getContentAsString()), null);
            Assert.assertNotNull(allowableActions);
            Assert.assertTrue(allowableActions instanceof CMISAllowableActions);

            // compare the two
            compareAllowableActions((CMISAllowableActions) allowableActions, objectAllowableActions);
        }
    }

    /**
     * Compare two sets of allowable actions
     */
    public static void compareAllowableActions(CMISAllowableActions left, CMISAllowableActions right) {
        List<String> rightactions = new ArrayList<String>(right.getNames());
        for (String action : left.getNames()) {
            Assert.assertTrue(rightactions.contains(action));
            CMISAllowableAction leftAction = left.find(action);
            Assert.assertNotNull(leftAction);
            CMISAllowableAction rightAction = right.find(action);
            Assert.assertNotNull(rightAction);
            Assert.assertEquals(leftAction.isAllowed(), rightAction.isAllowed());
            rightactions.remove(action);
        }
        Assert.assertTrue(rightactions.size() == 0);
    }

}
