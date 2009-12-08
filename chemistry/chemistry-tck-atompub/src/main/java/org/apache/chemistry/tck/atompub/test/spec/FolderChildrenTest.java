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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition;
import org.apache.chemistry.abdera.ext.CMISTypeDefinition;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.AssertEntryInFeedVisitor;
import org.apache.chemistry.tck.atompub.fixture.AssertValidFolderParentVisitor;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.junit.Assert;

/**
 * CMIS Folder Children Tests
 */
public class FolderChildrenTest extends TCKTest {

    public void testGetChildren() throws Exception {
        EntryTree folder = fixture.createTestTree("children", 1, 3, null, null);

        // get children
        Link childrenLink = client.getChildrenLink(folder.entry);
        Feed children = client.getFeed(childrenLink.getHref());

        // ensure they all exist
        Assert.assertEquals(3, children.getEntries().size());
        folder.walkChildren(new AssertEntryInFeedVisitor(children));
    }

    public void testGetChildrenPaging() throws Exception {
        EntryTree folder = fixture.createTestTree("children", 1, 15, null, null);

        // record child ids
        Set<IRI> unread = new HashSet<IRI>();
        for (EntryTree child : folder.children) {
            unread.add(child.entry.getId());
        }
        Assert.assertEquals(15, unread.size());

        // page thru children
        Link nextLink = client.getChildrenLink(folder.entry);
        Map<String, String> args = new HashMap<String, String>();
        args.put("maxItems", "4");

        int pageCount = 0;
        while (nextLink != null) {
            pageCount++;
            IRI nextLinkHref = nextLink.getHref();
            assertNotNull(nextLinkHref.getHost());
            Feed children = client.getFeed(nextLinkHref, args);
            Assert.assertNotNull(children);
            Assert.assertEquals(pageCount < 4 ? 4 : 3, children.getEntries().size());

            // mark children as read
            for (Entry entry : children.getEntries()) {
                unread.remove(entry.getId());
            }

            // next page
            nextLink = children.getLink("next");
            if (pageCount < 4) {
                Assert.assertNotNull(nextLink);
            }
            args = null;
        }
        ;
        Assert.assertEquals(4, pageCount);
        Assert.assertEquals(0, unread.size());
    }

    public void testGetChildrenAllPropertyFilter() throws Exception {
        EntryTree folder = fixture.createTestTree("children", 1, 1, null, null);

        // get children (with all properties)
        Link childrenLink = client.getChildrenLink(folder.entry);
        Map<String, String> args = new HashMap<String, String>();
        args.put("filter", "*");
        Feed children = client.getFeed(childrenLink.getHref(), args);

        for (Entry entry : children.getEntries()) {
            CMISObject object = entry.getExtension(CMISConstants.OBJECT);
            Assert.assertNotNull(object.getObjectId().getStringValue());
            Assert.assertNotNull(object.getObjectTypeId().getStringValue());
        }
    }

    public void testGetChildrenNamedPropertyFilter() throws Exception {
        EntryTree folder = fixture.createTestTree("children", 1, 1, null, null);

        // retrieve query name for object id
        CMISUriTemplate typeUriTemplate = client.getTypeByIdUriTemplate(client.getWorkspace());
        Assert.assertNotNull(typeUriTemplate);
        Map<String, Object> typeVariables = new HashMap<String, Object>();
        typeVariables.put("id", CMISConstants.TYPE_DOCUMENT);
        IRI docTypeUri = typeUriTemplate.generateUri(typeVariables);
        Entry docTypeEntry = client.getEntry(docTypeUri);
        Assert.assertNotNull(docTypeEntry);
        CMISTypeDefinition docType = docTypeEntry.getExtension(CMISConstants.TYPE_DEFINITION);
        Assert.assertNotNull(docType);
        CMISPropertyDefinition objectIdpropDef = docType.getPropertyDefinition(CMISConstants.PROP_OBJECT_ID);
        Assert.assertNotNull(objectIdpropDef);
        String objectIdQueryName = objectIdpropDef.getQueryName();
        Assert.assertNotNull(objectIdQueryName);
        CMISPropertyDefinition objectTypePropDef = docType.getPropertyDefinition(CMISConstants.PROP_OBJECT_TYPE_ID);
        Assert.assertNotNull(objectTypePropDef);
        String objectTypeIdQueryName = objectTypePropDef.getQueryName();
        Assert.assertNotNull(objectTypeIdQueryName);

        // get children with object_id only
        Link childrenLink = client.getChildrenLink(folder.entry);
        Map<String, String> args = new HashMap<String, String>();
        args.put("filter", objectIdQueryName + " " + objectTypeIdQueryName);
        Feed children = client.getFeed(childrenLink.getHref(), args);

        for (Entry entry : children.getEntries()) {
            CMISObject object = entry.getExtension(CMISConstants.OBJECT);
            Assert.assertNotNull(object.getObjectId().getStringValue());
            Assert.assertNotNull(object.getObjectTypeId());
            Assert.assertNull(object.getBaseTypeId());
        }
    }

    public void testGetParents() throws Exception {
        EntryTree folder = fixture.createTestTree("children", 3, 0, null, null);

        folder.walkTree(new AssertValidFolderParentVisitor(client));
    }

}
