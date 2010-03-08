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
 *     Amelie Avramo, EntropySoft
 */
package org.apache.chemistry.impl.simple;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.util.GregorianCalendar;

public class TestSimpleRepository extends TestCase {

    protected SimpleRepository repo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PropertyDefinition d1 = new SimplePropertyDefinition("title",
                "def:title", null, "title", "Title", "", false,
                PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition d2 = new SimplePropertyDefinition("description",
                "def:description", null, "description", "Description", "",
                false, PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition d3 = new SimplePropertyDefinition("date",
                "def:date", null, "date", "Date", "", false,
                PropertyType.DATETIME, false, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition d4 = new SimplePropertyDefinition("bool",
                "def:bool", null, "bool", "Bool", "", false,
                PropertyType.BOOLEAN, false, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        SimpleType mt1 = new SimpleType("doc", BaseType.DOCUMENT.getId(),
                "doc", null, "Doc", "My Doc Type", BaseType.DOCUMENT, "", true,
                true, true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(d1,
                        d2, d3, d4));
        SimpleType mt2 = new SimpleType("fold", BaseType.FOLDER.getId(),
                "fold", null, "Fold", "My Folder Type", BaseType.FOLDER, "",
                true, true, true, true, true, true, false, false,
                ContentStreamPresence.NOT_ALLOWED, null, null, Arrays.asList(
                        d1, d2));
        SimpleType mt3 = new SimpleType("subdoc", "doc", "subdoc", null,
                "SubDoc", "My SubDoc Type", BaseType.DOCUMENT, "", true, true,
                true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(d1,
                        d2, d3));
        repo = new SimpleRepository("test", Arrays.asList(mt1, mt2, mt3), null);

    }

    public void testInit() throws Exception {
        assertEquals("test", repo.getId());
        assertEquals("test", repo.getName());

        RepositoryInfo info = repo.getInfo();
        assertNotNull(info.getRootFolderId());
        assertEquals("Apache", info.getVendorName());
        assertEquals("Chemistry Simple Repository", info.getProductName());
        // TODO update this when releasing
        assertEquals("0.5-SNAPSHOT", info.getProductVersion());
        assertEquals("1.0", info.getVersionSupported());
        assertNull(info.getRepositorySpecificInformation());

        RepositoryCapabilities capabilities = info.getCapabilities();
        assertFalse(capabilities.hasMultifiling());
        assertFalse(capabilities.hasUnfiling());
        assertFalse(capabilities.hasVersionSpecificFiling());
        assertFalse(capabilities.isPWCUpdatable());
        assertFalse(capabilities.isPWCSearchable());
        assertFalse(capabilities.isAllVersionsSearchable());
        assertEquals(CapabilityJoin.NONE, capabilities.getJoinCapability());
        assertEquals(CapabilityQuery.BOTH_COMBINED,
                capabilities.getQueryCapability());
    }

    public void testTypes() {
        Collection<Type> types = repo.getTypes();
        assertEquals(5 + 3, types.size()); // default types have been added
        assertEquals(types.size(), repo.getTypeDescendants(null).size());
        assertNotNull(repo.getType("doc"));
        assertNotNull(repo.getType("subdoc"));
        assertNotNull(repo.getType("fold"));
        assertNull(repo.getType("no-such-type"));

        assertEquals(1, repo.getTypeChildren(BaseType.DOCUMENT.getId(), true,
                null).size());
        assertEquals(1, repo.getTypeChildren("doc", true, null).size());
        assertEquals(0, repo.getTypeChildren("subdoc", true, null).size());
        // chemistry:root and fold
        assertEquals(2, repo.getTypeChildren(BaseType.FOLDER.getId(), true,
                null).size());
        assertEquals(0, repo.getTypeChildren("fold", true, null).size());

        assertEquals(2, repo.getTypeDescendants(BaseType.DOCUMENT.getId(), -1,
                false).size());
        assertEquals(1, repo.getTypeDescendants(BaseType.DOCUMENT.getId(), 1,
                false).size());
        assertEquals(2, repo.getTypeDescendants(BaseType.DOCUMENT.getId(), 2,
                false).size());
        assertEquals(2, repo.getTypeDescendants(BaseType.DOCUMENT.getId(), 3,
                false).size());
        assertEquals(1, repo.getTypeDescendants("doc", -1, false).size());
        assertEquals(1, repo.getTypeDescendants("doc", 1, false).size());
        assertEquals(1, repo.getTypeDescendants("doc", 2, false).size());
        assertEquals(1, repo.getTypeDescendants("doc", 3, false).size());

        try {
            repo.getTypeDescendants("no-such-type");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testRoot() throws Exception {
        Connection conn = repo.getConnection(null);
        assertNotNull(conn);

        Folder root = conn.getRootFolder();
        assertNotNull(root);
        assertEquals(repo.getRootFolderId().getId(), root.getId());
        assertEquals("", root.getName());
        assertEquals(0, root.getChildren().size());
        assertNull(root.getParent());
    }

    public void testChildren() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Folder f1 = root.newFolder("fold");
        assertEquals(SimpleType.PROPS_FOLDER_BASE.size() + 2,
                f1.getType().getPropertyDefinitions().size());

        List<CMISObject> children = root.getChildren();
        assertEquals(0, children.size());

        f1.save();
        assertEquals(root.getId(), f1.getParent().getId());

        children = root.getChildren();
        assertEquals(1, children.size());
        assertTrue(children.get(0) instanceof Folder);

        Document d1 = root.newDocument("doc");
        d1.save();
        children = root.getChildren();
        assertEquals(2, children.size());
        assertTrue(children.get(0) instanceof Document
                || children.get(1) instanceof Document);
    }

    public void testDocument() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        assertEquals(SimpleType.PROPS_DOCUMENT_BASE.size() + 4,
                d1.getType().getPropertyDefinitions().size());

        d1.save();
        assertEquals(root.getId(), d1.getParent().getId());

        d1.setValue("title", "Yo!");
        assertEquals("Yo!", d1.getString("title"));
        // refetch

        d1 = (Document) conn.getObject(d1);
        assertEquals("Yo!", d1.getString("title"));

        Property prop = d1.getProperty("title");
        assertNotNull(prop);
        assertEquals("Yo!", prop.getValue());
        assertNotNull(d1.getProperties());
    }

    public void testContentStream() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        String string = "Houston, we have a problem...";
        ContentStream cs = new SimpleContentStream(string.getBytes("UTF-8"),
                "text/plain", "houston.txt");

        d1.setContentStream(cs);
        d1.save();
        cs = d1.getContentStream();

        assertEquals(29, cs.getLength());
        assertEquals("text/plain", cs.getMimeType());
        assertEquals("houston.txt", cs.getFileName());

        byte[] bytes = SimpleContentStream.getBytes(cs.getStream());
        assertEquals(string, new String(bytes, "UTF-8"));

        InputStream stream = null;
        cs = new SimpleContentStream(stream, null, "empty.txt");
        assertNull(cs.getStream());
    }

    public void testRemoveDocument() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        d1.save();
        d1.delete();
        assertNull(conn.getObject(d1));
    }

    public void testRemoveFolder() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        try {
            root.delete();
            fail();
        } catch (Exception e) {
            // ok
        }
        Folder f1 = root.newFolder("fold");
        f1.setName("foo");
        f1.save();
        Folder f2 = f1.newFolder("fold");
        f2.setName("foo2");
        f2.save();
        Document d1 = f2.newDocument("doc");
        d1.setName("bar");
        d1.save();
        f2.deleteTree(Unfiling.UNFILE);
        assertEquals(f1.getId(), conn.getObject(f1).getId());
        assertNull(conn.getObject(f2));
        assertNull(conn.getObject(d1));
    }

    public void testBasicQuery() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        d1.setValue("date",
                GregorianCalendar.fromAtomPub("2010-01-01T01:01:01.000Z"));
        d1.setValue("bool", Boolean.TRUE);
        d1.save();

        Collection<CMISObject> res = conn.query("SELECT * FROM cmis:folder",
                false);
        assertEquals(1, res.size()); // the root
        // case insensitive on keywords and types
        res = conn.query("seLect * fRoM cmis:Folder", false);
        assertEquals(1, res.size());
        res = conn.query("SELECT * FROM fold", false);
        assertEquals(0, res.size());
        res = conn.query("SELECT * FROM doc", false);
        assertEquals(1, res.size());
        res = conn.query("SELECT * FROM cmis:folder WHERE cmis:name = ''",
                false);
        assertEquals(1, res.size());
        res = conn.query("SELECT * FROM doc WHERE cmis:objectId = 'nosuchid'",
                false);
        assertEquals(0, res.size());
        res = conn.query("SELECT * FROM doc WHERE cmis:objectId <> '123'",
                false);
        assertEquals(1, res.size());

        res = conn.query("SELECT * FROM doc WHERE bool = true", false);
        assertEquals(1, res.size());
        res = conn.query("SELECT * FROM doc WHERE bool <> FALSE", false);
        assertEquals(1, res.size());

        res = conn.query(
                "SELECT * FROM doc WHERE date <> TIMESTAMP '1999-09-09T01:01:01Z'",
                false);
        assertEquals(1, res.size());
        try {
            res = conn.query(
                    "SELECT * FROM doc WHERE date <> TIMESTAMP 'foobar'", false);
            fail();
        } catch (CMISRuntimeException e) {
            // ok
        }
    }

}
