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

import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.atompub.client.stax.AbstractFeedReader;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public class APPObjectFeedReader extends
        AbstractFeedReader<ListPage<ObjectEntry>, APPObjectEntry> {

    public APPObjectFeedReader() {
        super(new APPObjectEntryReader());
    }

    @Override
    protected ListPage<ObjectEntry> createFeed(StaxReader reader) {
        return new SimpleListPage<ObjectEntry>();
    }

    @Override
    protected void addEntry(ListPage<ObjectEntry> feed, APPObjectEntry entry) {
        feed.add(entry);
    }

    @Override
    protected void setHasMoreItems(ListPage<ObjectEntry> feed,
            boolean hasMoreItems) {
        ((SimpleListPage<ObjectEntry>) feed).setHasMoreItems(hasMoreItems);
    }

    @Override
    protected void setNumItems(ListPage<ObjectEntry> feed, int numItems) {
        ((SimpleListPage<ObjectEntry>) feed).setNumItems(numItems);
    }

}
