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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.test;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;

/**
 * Basic test on a repository created with {@link BasicHelper#makeRepository}.
 */
public abstract class BasicTestCase extends TestCase {

    public static final String ROOT_TYPE_ID = "Root"; // not in the spec

    public Repository repository;

    public void testBasic() {
        assertNotNull(repository);
        Connection conn = repository.getConnection(null);
        assertNotNull(conn);

        Folder root = conn.getRootFolder();
        assertNotNull(root);
        Type rootType = root.getType();
        assertNotNull(rootType);
        assertEquals(ROOT_TYPE_ID, rootType.getId());
        assertEquals(ROOT_TYPE_ID, root.getTypeId());
        assertEquals("CMIS_Root_Folder", root.getName()); // from the spec
        assertEquals(null, root.getParent());
        Map<String, Property> props = root.getProperties();
        assertNotNull(props);
        assertTrue(props.size() > 0);

        List<CMISObject> entries = root.getChildren(null);
        assertEquals(1, entries.size());
    }

}
