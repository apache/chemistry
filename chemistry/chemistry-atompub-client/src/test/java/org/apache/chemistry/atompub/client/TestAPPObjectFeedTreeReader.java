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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Tree;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.chemistry.impl.simple.SimpleTypeManager;

public class TestAPPObjectFeedTreeReader extends TestCase {

    protected ReadContext getReadContext() {
        Set<BaseType> changeLogBaseTypes = new HashSet<BaseType>();
        changeLogBaseTypes.add(BaseType.DOCUMENT);
        changeLogBaseTypes.add(BaseType.FOLDER);
        APPRepositoryInfo ri = new APPRepositoryInfo(
                new APPRepositoryCapabilities(), new HashMap<String, Object>(),
                changeLogBaseTypes);
        APPRepository repo = new APPRepository(new APPContentManager(null), ri);

        SimpleTypeManager tm = new SimpleTypeManager();
        PropertyDefinition ps = new SimplePropertyDefinition("string",
                "def:string", null, "string", "string", "", false,
                PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        SimpleType t = new SimpleType("doc", BaseType.DOCUMENT.getId(), "doc",
                null, "Doc", "My Doc Type", BaseType.DOCUMENT, "", true, true,
                true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(ps));
        tm.addType(t);
        repo.typeManager = tm;

        return new ReadContext(new APPConnection(repo));
    }

    public void testReadAPPObjectFeed() throws Exception {
        InputStream is = getClass().getResourceAsStream("/feed-nested.xml");
        List<Tree<ObjectEntry>> list = new APPObjectFeedTreeReader().read(
                getReadContext(), is);
        assertEquals(2, list.size());
        Tree<ObjectEntry> tree = list.get(0);
        assertEquals(3, tree.size());
        assertEquals(1, tree.getChildren().size());
        assertEquals(2, tree.getChildren().get(0).size());
        assertEquals(1, tree.getChildren().get(0).getChildren().size());
        tree = list.get(1);
        assertEquals(3, tree.size());
        assertEquals(2, tree.getChildren().size());
    }

}
