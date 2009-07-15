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
 *     Ugo Cei, Sourcesense
 */
package org.apache.chemistry.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;

/**
 * Basic test on a repository created with {@link BasicHelper#makeRepository}.
 * <p>
 * The {@link #setUp} method must initialize repository, conn and spi.
 */
public abstract class BasicTestCase extends TestCase {

    public static final String ROOT_TYPE_ID = "Root"; // not in the spec

    public Repository repository;

    public Connection conn;

    public SPI spi;

    @Override
    public void tearDown() throws Exception {
        conn.close();
        super.tearDown();
    }

    public void testBasic() {
        assertNotNull(repository);
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

    public void testQuery() {
        Connection conn = repository.getConnection(null);
        Collection<CMISObject> res = conn.query("SELECT * FROM doc", false);
        assertNotNull(res);
        assertEquals(4, res.size());
        res = conn.query("SELECT * FROM fold", false);
        assertEquals(2, res.size());
    }

    public void testGetChildren() {
        boolean[] hasMoreItems = new boolean[1];
        Folder root = conn.getRootFolder();
        assertEquals(1, spi.getChildren(root, BaseType.FOLDER, null, true,
                false, 20, 0, null, hasMoreItems).size());
        assertFalse(hasMoreItems[0]);
        ObjectId folder1 = root.getChildren(null).get(0);
        assertEquals(2, spi.getChildren(folder1, null, null, false, false, 20,
                0, null, hasMoreItems).size());
        assertFalse(hasMoreItems[0]);
        assertEquals(1, spi.getChildren(folder1, null, null, false, false, 1,
                0, null, hasMoreItems).size());
        assertTrue(hasMoreItems[0]);
        assertEquals(1, spi.getChildren(folder1, null, null, false, false, 1,
                1, null, hasMoreItems).size());
        assertFalse(hasMoreItems[0]);
        List<ObjectEntry> temp = spi.getChildren(folder1, null, null, false,
                false, 2, 0, null, hasMoreItems);
        ObjectId folder2 = temp.get(0).getTypeId().equals("fold") ? temp.get(0)
                : temp.get(1);
        assertEquals(1, spi.getChildren(folder2, null, null, false, false, 1,
                1, null, hasMoreItems).size());
        assertTrue(hasMoreItems[0]);
        assertEquals(2, spi.getChildren(folder2, null, null, false, false, 2,
                0, null, hasMoreItems).size());
        assertTrue(hasMoreItems[0]);
    }

    public void testGetDescendants() {
        Folder root = conn.getRootFolder();
        assertEquals(6, spi.getDescendants(root, null, 4, null, false, false,
                null).size());
        List<ObjectEntry> desc = spi.getDescendants(root, BaseType.FOLDER, 4,
                null, false, false, null);
        assertEquals(2, desc.size());
    }

    public void testGetFolderParent() {
        Folder root = conn.getRootFolder();
        assertEquals(0,
                spi.getFolderParent(root, null, false, false, false).size());
        ObjectId folder1 = root.getChildren(null).get(0);
        assertEquals(1,
                spi.getFolderParent(folder1, null, false, false, true).size());
        assertEquals(root.getId(), spi.getFolderParent(folder1, null, false,
                false, false).get(0).getId());
    }

    public void testGetObjectParents() {
        Folder root = conn.getRootFolder();
        ObjectId folder1Id = root.getChildren(null).get(0);
        Folder folder1 = (Folder) conn.getObject(folder1Id, null);
        Document doc = (Document) folder1.getChildren(BaseType.DOCUMENT).get(0);
        Collection<ObjectEntry> parents = spi.getObjectParents(doc, null,
                false, false);
        assertEquals(1, parents.size());
    }

}
