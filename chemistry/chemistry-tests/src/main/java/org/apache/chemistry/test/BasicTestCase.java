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

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.util.GregorianCalendar;
import org.apache.commons.io.IOUtils;

/**
 * Basic test on a repository created with {@link BasicHelper#makeRepository}.
 * <p>
 * The {@link #setUp} method must initialize repository, conn and spi.
 */
public abstract class BasicTestCase extends TestCase {

    public static final String ROOT_TYPE_ID = "chemistry:root"; // not in spec

    public Repository repository;

    public Connection conn;

    public SPI spi;

    public abstract void makeRepository() throws Exception;

    @Override
    public void setUp() throws Exception {
        makeRepository();
        openConn();
    }

    @Override
    public void tearDown() throws Exception {
        closeConn();
        super.tearDown();
    }

    protected void openConn() {
        conn = repository.getConnection(null);
        spi = conn.getSPI();
    }

    protected void closeConn() {
        conn.close();
        conn = null;
        spi = null;
    }

    public static Folder getFolderChild(Folder folder) {
        for (CMISObject child : folder.getChildren()) {
            if (child.getBaseType() == BaseType.FOLDER) {
                return (Folder) child;
            }
        }
        return null;
    }

    public static Document getDocumentChild(Folder folder) {
        for (CMISObject child : folder.getChildren()) {
            if (child.getBaseType() == BaseType.DOCUMENT) {
                return (Document) child;
            }
        }
        return null;
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

        List<CMISObject> entries = root.getChildren();
        assertEquals(1, entries.size());
        Folder f1 = (Folder) entries.get(0);
        Folder fold = f1.getParent();
        assertEquals(root.getId(), fold.getId());
    }

    public void testDefaultValues() {
        Folder root = conn.getRootFolder();
        Folder f1 = (Folder) root.getChildren().get(0);
        Folder f2 = getFolderChild(f1);
        List<CMISObject> children = f2.getChildren();
        assertEquals(3, children.size());
        // check default values
        for (CMISObject child : children) {
            String name = child.getName();
            String title = child.getString("title");
            String descr = child.getString("description");
            Calendar date = child.getDateTime("date");
            if (name.equals("doc 3")) {
                assertEquals("(no title)", title); // uses defaultValue
                assertEquals("", descr); // emptry string defaultValue
            } else {
                assertNotSame("", title);
                assertNotNull(descr);
            }
            assertNull(date);
        }
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
        assertEquals(1, spi.getChildren(root, null, true, false, 20, 0, null,
                hasMoreItems).size());
        assertFalse(hasMoreItems[0]);
        ObjectId folder1 = root.getChildren().get(0);
        assertEquals(2, spi.getChildren(folder1, null, false, false, 20, 0,
                null, hasMoreItems).size());
        assertFalse(hasMoreItems[0]);
        assertEquals(1, spi.getChildren(folder1, null, false, false, 1, 0,
                null, hasMoreItems).size());
        assertTrue(hasMoreItems[0]);
        assertEquals(1, spi.getChildren(folder1, null, false, false, 1, 1,
                null, hasMoreItems).size());
        assertFalse(hasMoreItems[0]);
        List<ObjectEntry> temp = spi.getChildren(folder1, null, false, false,
                2, 0, null, hasMoreItems);
        ObjectId folder2 = temp.get(0).getTypeId().equals("fold") ? temp.get(0)
                : temp.get(1);
        assertEquals(1, spi.getChildren(folder2, null, false, false, 1, 1,
                null, hasMoreItems).size());
        assertTrue(hasMoreItems[0]);
        assertEquals(2, spi.getChildren(folder2, null, false, false, 2, 0,
                null, hasMoreItems).size());
        assertTrue(hasMoreItems[0]);
    }

    public void testGetDescendants() {
        Folder root = conn.getRootFolder();
        List<ObjectEntry> desc = spi.getDescendants(root, 4, null, false,
                false, null);
        assertEquals(6, desc.size());
    }

    public void testGetFolderParent() {
        Folder root = conn.getRootFolder();
        assertEquals(0,
                spi.getFolderParent(root, null, false, false, false).size());
        ObjectId folder1 = root.getChildren().get(0);
        assertEquals(1,
                spi.getFolderParent(folder1, null, false, false, true).size());
        assertEquals(root.getId(), spi.getFolderParent(folder1, null, false,
                false, false).get(0).getId());
    }

    public void testGetObjectParents() {
        Folder root = conn.getRootFolder();
        ObjectId folder1Id = root.getChildren().get(0);
        Folder folder1 = (Folder) conn.getObject(folder1Id);
        Document doc = getDocumentChild(folder1);
        Collection<ObjectEntry> parents = spi.getObjectParents(doc, null,
                false, false);
        assertEquals(1, parents.size());
    }

    @SuppressWarnings("null")
    public void testGetStream() throws Exception {
        Folder f1 = (Folder) conn.getRootFolder().getChildren().get(0);
        Folder f2 = getFolderChild(f1);
        Document other = null;
        Document dog = null;
        for (CMISObject child : f2.getChildren()) {
            String name = child.getName();
            if (name.equals("doc 2")) {
                other = (Document) child;
            } else if (name.equals("dog.jpg")) {
                dog = (Document) child;
            }
        }
        assertNotNull("doc 2 not found", other);
        assertNull(other.getContentStream());
        assertNull(other.getStream());

        assertNotNull("dog not found", dog);
        ContentStream cs = dog.getContentStream();
        assertTrue(cs.getLength() != 0);
        assertEquals("dog.jpg", cs.getFilename());
        assertEquals("image/jpeg", cs.getMimeType());
        assertNotNull(cs.getStream());
        InputStream in = dog.getStream();
        assertNotNull(in);
        byte[] array = IOUtils.toByteArray(in);
        assertTrue(array.length != 0);
        assertEquals(array.length, cs.getLength());
    }

    public void testNewDocument() {
        Folder root = conn.getRootFolder();
        assertNull(getDocumentChild(root));
        Document doc = root.newDocument("doc");
        doc.setName("mydoc");
        doc.setValue("title", "mytitle");
        GregorianCalendar cal = new GregorianCalendar(
                TimeZone.getTimeZone("GMT+05:00"));
        cal.clear();
        cal.set(2009, 7 - 1, 14, 12, 00, 00);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals("GregorianCalendar(2009-07-14T12:00:00.000+05:00)",
                cal.toString());
        doc.setValue("date", cal);
        assertNull(doc.getId()); // not yet saved
        doc.save();
        String id = doc.getId();
        assertNotNull(id);

        // new connection
        closeConn();
        openConn();
        root = conn.getRootFolder();
        doc = getDocumentChild(root);
        assertNotNull(doc);
        assertEquals(id, doc.getId());
        assertEquals("mydoc", doc.getName());
        assertEquals("mytitle", doc.getString("title"));
        Calendar cal2 = doc.getDateTime("date");
        assertEquals(cal.toString(), cal2.toString());
    }

}
