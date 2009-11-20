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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.client.stax.AbstractFeedReader;
import org.apache.chemistry.impl.simple.SimpleTypeManager;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Reader for a feed of types.
 */
public class TypeFeedReader extends AbstractFeedReader<TypeManager, APPType> {

    public static final TypeFeedReader INSTANCE = new TypeFeedReader();

    public TypeFeedReader() {
        super(TypeEntryReader.INSTANCE);
    }

    public TypeFeedReader(TypeEntryReader entryReader) {
        super(entryReader);
    }

    @Override
    protected TypeManager createFeed(StaxReader reader) {
        return new SimpleTypeManager();
    }

    @Override
    protected void addEntry(TypeManager typeManager, APPType type) {
        typeManager.addType(type);
    }

    @Override
    protected void setHasMoreItems(TypeManager typeManager, boolean hasMoreItems) {
        // nothing
    }

    @Override
    protected void setNumItems(TypeManager typeManager, int numItems) {
        // nothing
    }

}
