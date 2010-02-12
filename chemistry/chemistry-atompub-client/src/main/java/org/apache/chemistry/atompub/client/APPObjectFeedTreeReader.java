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
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Tree;
import org.apache.chemistry.atompub.client.stax.AbstractFeedReader;
import org.apache.chemistry.impl.simple.SimpleTree;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * A reader of {@link List} of {@link Tree} of {@link ObjectEntry}.
 */
public class APPObjectFeedTreeReader extends
        AbstractFeedReader<List<Tree<ObjectEntry>>, APPObjectEntry> {

    public APPObjectFeedTreeReader() {
        super(new APPObjectEntryReader());
    }

    @Override
    protected List<Tree<ObjectEntry>> createFeed(StaxReader reader) {
        return new ArrayList<Tree<ObjectEntry>>(5);
    }

    @Override
    protected void addEntry(List<Tree<ObjectEntry>> list, APPObjectEntry entry) {
        Tree<ObjectEntry> t = new SimpleTree<ObjectEntry>(entry, entry.children);
        entry.children = null; // not used anymore
        list.add(t);
    }

    @Override
    protected void setHasMoreItems(List<Tree<ObjectEntry>> feed,
            boolean hasMoreItems) {
    }

    @Override
    protected void setNumItems(List<Tree<ObjectEntry>> feed, int numItems) {
    }

}
