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

import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.chemistry.abdera.ext.CMISChildren;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISProperty;


/**
 * CMIS Tree of Folders and Documents or Types
 */
public class CMISTree extends EntryTree {
    
    public CMISTree(EntryTree parentEntryTree, Feed cmisTree) {
        parent = parentEntryTree.parent;
        entry = parentEntryTree.entry;
        type = parentEntryTree.type;
        children = new ArrayList<EntryTree>();

        for (Entry child : cmisTree.getEntries()) {
            EntryTree childEntryTree = createEntryTree(entry, child);
            children.add(childEntryTree);
        }
    }

    private EntryTree createEntryTree(Entry parent, Entry entry) {
        EntryTree entryTree = new EntryTree();
        entryTree.parent = parent;
        entryTree.entry = entry;
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        CMISProperty baseTypeId = object.getBaseTypeId();
        if (baseTypeId != null) {
            entryTree.type = baseTypeId.getStringValue();
        }

        CMISChildren children = entry.getFirstChild(CMISConstants.CHILDREN);
        if (children != null) {
            Feed childrenFeed = children.getFeed();
            if (childrenFeed != null) {
                entryTree.children = new ArrayList<EntryTree>();
                for (Entry child : childrenFeed.getEntries()) {
                    EntryTree childEntryTree = createEntryTree(entry, child);
                    entryTree.children.add(childEntryTree);
                }
            }
        }
        return entryTree;
    }
}
