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
 *     Ugo Cei, Sourcesense
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.chemistry.TypeManager;

public class TestTypeFeedReader extends TestCase {

    public void testReadTypesFeed() throws Exception {
        InputStream is = getClass().getResourceAsStream("/types-feed.xml");
        TypeManager typeManager = new TypeFeedReader(true).read(
                new APPContext((APPRepository) null), is);
        assertEquals(5, typeManager.getTypes().size());
    }

}
