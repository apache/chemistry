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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.abdera.model.Entry;
import org.apache.chemistry.abdera.ext.CMISConstants;


/**
 * General Tree of Folders and Documents
 */
public class EntryTree {
    
    public Entry parent;
    public Entry entry;
    public String type;
    public List<EntryTree> children;

    public EntryTree() {
    }

    public EntryTree(EntryTree tree) throws Exception {
        this(tree, -1, true);
    }

    public EntryTree(EntryTree tree, int depth) throws Exception {
        this(tree, depth, true);
    }

    public EntryTree(EntryTree tree, int depth, boolean includeDocs) throws Exception {
        parent = tree.parent;
        entry = tree.entry;
        type = tree.type;
        children = copyChildren(tree.children, depth, includeDocs);
    }

    private List<EntryTree> copyChildren(List<EntryTree> children, int depth, boolean includeDocs) throws Exception {
        if (children == null)
            return null;

        List<EntryTree> childrenCopy = new ArrayList<EntryTree>();
        if (depth == 0)
            return childrenCopy;

        for (EntryTree child : children) {
            if (includeDocs || child.type.equals(CMISConstants.TYPE_FOLDER)) {
                EntryTree childCopy = new EntryTree(child, depth - 1, includeDocs);
                childrenCopy.add(childCopy);
            }
        }
        return childrenCopy;
    }

    public boolean equalsTree(EntryTree tree) {
        if (parent == null && tree.parent != null)
            return false;
        if (parent != null && tree.parent == null)
            return false;
        if (!parent.getId().equals(tree.parent.getId()))
            return false;
        if (!entry.getId().equals(tree.entry.getId()))
            return false;
        if (!type.equals(tree.type))
            return false;
        if (children == null && tree.children != null && tree.children.size() != 0)
            return false;
        if (children != null && children.size() != 0 && tree.children == null)
            return false;
        if (children == null)
            return true;
        int childrenSize = (children == null) ? 0 : children.size();
        int treeChildrenSize = (tree.children == null) ? 0 : tree.children.size();
        if (childrenSize != treeChildrenSize)
            return false;
        if (childrenSize == 0)
            return true;

        // compare children, force sort of entries by id
        Collections.sort(children, new EntryTypeComparator());
        Collections.sort(tree.children, new EntryTypeComparator());
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).equalsTree(tree.children.get(i)))
                return false;
        }
        return true;
    }

    private class EntryTypeComparator implements Comparator<EntryTree> {
        public int compare(EntryTree o1, EntryTree o2) {
            return o1.entry.getId().toString().compareTo(o2.entry.getId().toString());
        }
    }

    public interface TreeVisitor {
        public void visit(EntryTree entry) throws Exception;
    }

    public void walkTree(TreeVisitor visitor) throws Exception {
        walkEntry(this, visitor);
    }

    public void walkChildren(TreeVisitor visitor) throws Exception {
        walkChildren(this, visitor);
    }

    private void walkEntry(EntryTree entryTree, TreeVisitor visitor) throws Exception {
        visitor.visit(entryTree);
        walkChildren(entryTree, visitor);
    }

    private void walkChildren(EntryTree entryTree, TreeVisitor visitor) throws Exception {
        if (entryTree.children != null) {
            for (EntryTree child : entryTree.children) {
                walkEntry(child, visitor);
            }
        }
    }

    public int getEntryCount() {
        int count = 1;
        if (children != null) {
            for (EntryTree entry : children) {
                count += entry.getEntryCount();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "EntryTree: " + entry.getId() + ", count=" + getEntryCount();
    }

}
