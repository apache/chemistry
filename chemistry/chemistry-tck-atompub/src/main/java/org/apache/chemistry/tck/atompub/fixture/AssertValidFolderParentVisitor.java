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
 */
package org.apache.chemistry.tck.atompub.fixture;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.EntryTree.TreeVisitor;
import org.junit.Assert;


/**
 * Asserts each Folder Entry in EntryTree has a valid parent
 */
public class AssertValidFolderParentVisitor implements TreeVisitor {

    private CMISClient client;

    public AssertValidFolderParentVisitor(CMISClient client) {
        this.client = client;
    }

    public void visit(EntryTree entry) throws Exception {
        Link parentLink = client.getFolderParentLink(entry.entry);

        if (entry.parent == null) {
            Assert.assertNull(parentLink);
            return;
        }

        if (entry.type.equals(CMISConstants.TYPE_FOLDER))
            Assert.assertNotNull(parentLink);

        if (parentLink == null)
            return;

        // ensure link is to parent
        Entry parent = client.getEntry(parentLink.getHref());
        Assert.assertEquals(entry.parent.getId(), parent.getId());
    }
}
