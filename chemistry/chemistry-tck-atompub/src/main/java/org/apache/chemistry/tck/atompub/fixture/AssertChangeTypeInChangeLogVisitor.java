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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISAccessControlList;
import org.apache.chemistry.abdera.ext.CMISChangeEventInfo;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.utils.CMISAppModel;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.EntryTree.TreeVisitor;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;

/**
 * Visitor that asserts that an entry of the given type exists for the given
 * object types in the change log feed after the given change log token.
 * Optionally follows paging links and checks included ACLs and properties.
 */
public class AssertChangeTypeInChangeLogVisitor implements TreeVisitor {

    private String changeType;
    private Set<String> changesOnType;
    private Map<String, Entry> entriesByObjectId;

    public AssertChangeTypeInChangeLogVisitor(CMISClient client, CMISAppModel model, String changeLogToken,
            Integer maxItems, boolean includeACL, boolean includeProperties, String changeType,
            Set<String> changesOnType) throws Exception {
        this.changeType = changeType;
        this.changesOnType = changesOnType;
        Feed feed = getChangeLog(client, changeLogToken, maxItems, includeACL, includeProperties);
        List<Entry> entries = feed.getEntries();
        this.entriesByObjectId = new HashMap<String, Entry>(entries.size() * 2);
        // Keep looping until we have no more "next" links
        for (;;) {
            if (maxItems != null) {
                Assert.assertFalse("maxItems exceeded", entries.size() > maxItems);
            }
            for (Entry entry : entries) {
                CMISObject object = entry.getExtension(CMISConstants.OBJECT);
                Assert.assertNotNull(object);
                String objectId = object.getObjectId().getStringValue();
                Assert.assertNotNull(objectId);
                CMISChangeEventInfo changeEventInfo = object.getChangeEventInfo();
                Assert.assertNotNull(changeEventInfo);
                if (changeEventInfo.getChangeType().equals(changeType)) {
                    entriesByObjectId.put(objectId, entry);

                    // Ensure the change log entry contains the expected ACL
                    if (includeACL) {
                        CMISAccessControlList accessControlList = object.getAccessControlList();
                        Assert.assertNotNull("Expected ACL", accessControlList);
                        Link accessControlListLink = entry.getLink(CMISConstants.REL_ACL);
                        Assert.assertNotNull("Expected ACL Link", accessControlListLink);
                        Request req = new GetRequest(accessControlListLink.getHref().toString());
                        Response accessControlListRes = client.executeRequest(req, 200);
                        Assert.assertNotNull(accessControlListRes);
                        Element fetchedAccessControlList = model.parse(new StringReader(accessControlListRes
                                .getContentAsString()), null);
                        Assert.assertNotNull(fetchedAccessControlList);
                        Assert.assertTrue(fetchedAccessControlList instanceof CMISAccessControlList);
                        Assert.assertEquals(accessControlList.getHashedEntries(),
                                ((CMISAccessControlList) fetchedAccessControlList).getHashedEntries());
                    }

                    // Ensure we have the expected properties
                    Set<String> properties = new HashSet<String>(object.getProperties().getIds());
                    Assert.assertTrue(properties.contains(CMISConstants.PROP_OBJECT_ID));

                    // Object ID MUST be the only property if we didn't include
                    // properties
                    if (!includeProperties) {
                        Assert.assertTrue("Unexpected properties in change log", properties.size() == 1);
                    }
                }
            }
            // Keep looping while we have a next page link
            Link nextPageLink = feed.getLink(CMISConstants.REL_NEXT);
            if (nextPageLink == null) {
                break;
            }
            feed = client.getFeed(nextPageLink.getHref());
            entries = feed.getEntries();
        }
    }

    public void visit(EntryTree entry) throws Exception {
        if (this.changesOnType.contains(entry.type)) {
            CMISObject object = entry.entry.getExtension(CMISConstants.OBJECT);
            Assert.assertNotNull(object);
            String objectId = object.getObjectId().getStringValue();
            Assert.assertNotNull(objectId);
            Assert.assertTrue(this.changeType + " change log entry should exist for " + objectId, entriesByObjectId
                    .containsKey(objectId));
        }
    }

    private Feed getChangeLog(CMISClient client, String changeLogToken, Integer maxItems, boolean includeACL,
            boolean includeProperties) throws Exception {
        Link changesLink = client.getChangesLink(client.getWorkspace());
        Assert.assertNotNull(changesLink);
        Map<String, String> args = new HashMap<String, String>(5);
        if (changeLogToken != null) {
            args.put("changeLogToken", changeLogToken);
        }
        if (maxItems != null) {
            args.put("maxItems", maxItems.toString());
        }
        if (includeACL) {
            args.put("includeACL", "true");
        }
        if (includeProperties) {
            args.put("includeProperties", "true");
            args.put("filter", "*");
        }
        Feed changes = client.getFeed(changesLink.getHref(), args);
        Assert.assertNotNull(changes);
        return changes;
    }
}
