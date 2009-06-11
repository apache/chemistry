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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.stax.AbstractFeedReader;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public class TypeFeedReader extends
        AbstractFeedReader<Map<String, Type>, APPType> {

    public final static TypeFeedReader INSTANCE = new TypeFeedReader();

    public TypeFeedReader() {
        super(TypeEntryReader.INSTANCE);
    }

    public TypeFeedReader(TypeEntryReader entryReader) {
        super(entryReader);
    }

    @Override
    protected void addEntry(Map<String, Type> feed, APPType entry) {
        feed.put(entry.getId(), entry);
    }

    @Override
    protected Map<String, Type> createFeed(StaxReader reader) {
        return new HashMap<String, Type>();
    }

}
