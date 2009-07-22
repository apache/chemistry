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
package org.apache.chemistry.impl.simple;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.JoinCapability;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.QueryCapability;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.ReturnVersion;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.Updatability;

public class TestSimpleRepository extends TestCase {

    protected SimpleRepository repo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PropertyDefinition d1 = new SimplePropertyDefinition("title",
                "def:title", "Title", "", false, PropertyType.STRING, false,
                null, false, false, "", Updatability.READ_WRITE, true, true, 0,
                null, null, -1, null);
        PropertyDefinition d2 = new SimplePropertyDefinition("description",
                "def:description", "Description", "", false,
                PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition d3 = new SimplePropertyDefinition("date",
                "def:date", "Date", "", false, PropertyType.DATETIME, false,
                null, false, false, null, Updatability.READ_WRITE, true, true,
                0, null, null, -1, null);
        SimpleType mt1 = new SimpleType("doc", null, "Doc", "My Doc Type",
                BaseType.DOCUMENT, "", true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(d1,
                        d2, d3));
        SimpleType mt2 = new SimpleType("fold", null, "Fold", "My Folder Type",
                BaseType.FOLDER, "", true, true, true, true, false, false,
                ContentStreamPresence.NOT_ALLOWED, null, null, Arrays.asList(
                        d1, d2));
        repo = new SimpleRepository("test", Arrays.asList(mt1, mt2), null);

    }

    public void testInit() throws Exception {
        assertEquals("test", repo.getId());
        assertEquals("test", repo.getName());

        RepositoryInfo info = repo.getInfo();
        assertNotNull(info.getRootFolderId());
        assertEquals("Apache", info.getVendorName());
        assertEquals("Chemistry Simple Repository", info.getProductName());
        assertEquals("0.1-SNAPSHOT", info.getProductVersion());
        assertEquals("0.61", info.getVersionSupported());
        assertNull(info.getRepositorySpecificInformation());

        RepositoryCapabilities capabilities = info.getCapabilities();
        assertFalse(capabilities.hasMultifiling());
        assertFalse(capabilities.hasUnfiling());
        assertFalse(capabilities.hasVersionSpecificFiling());
        assertFalse(capabilities.isPWCUpdatable());
        assertFalse(capabilities.isPWCSearchable());
        assertFalse(capabilities.isAllVersionsSearchable());
        assertEquals(JoinCapability.NO_JOIN, capabilities.getJoinCapability());
        assertEquals(QueryCapability.BOTH_COMBINED,
                capabilities.getQueryCapability());

        Collection<Type> types = repo.getTypes(null, true);
        assertEquals(5 + 2, types.size()); // default types have been added
    }

    public void testRoot() throws Exception {
        Connection conn = repo.getConnection(null);
        assertNotNull(conn);

        Folder root = conn.getRootFolder();
        assertNotNull(root);
        assertEquals(repo.getRootFolderId().getId(), root.getId());
        assertEquals("CMIS_Root_Folder", root.getName());
        assertEquals(0, root.getChildren(null).size());
        assertNull(root.getParent());
    }

    public void testChildren() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Folder f1 = root.newFolder("fold");
        assertEquals(12 + 1, f1.getType().getPropertyDefinitions().size());

        List<CMISObject> children = root.getChildren(null);
        assertEquals(0, children.size());

        f1.save();
        assertEquals(root.getId(), f1.getParent().getId());

        children = root.getChildren(null);
        assertEquals(1, children.size());
        assertTrue(children.get(0) instanceof Folder);

        Document d1 = root.newDocument("doc");
        d1.save();
        children = root.getChildren(null);
        assertEquals(2, children.size());
        assertTrue(children.get(0) instanceof Document
                || children.get(1) instanceof Document);
    }

    public void testDocument() throws Exception {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        assertEquals(22 + 3, d1.getType().getPropertyDefinitions().size());

        d1.save();
        assertEquals(root.getId(), d1.getParent().getId());

        d1.setValue("title", "Yo!");
        assertEquals("Yo!", d1.getString("title"));
        // refetch

        d1 = (Document) conn.getObject(d1, null);
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
                "text/plain", "houston.txt", new URI(
                        "http://houston.example.com"));

        d1.setContentStream(cs);
        d1.save();
        cs = d1.getContentStream();

        assertEquals(29, cs.getLength());
        assertEquals("text/plain", cs.getMimeType());
        assertEquals("houston.txt", cs.getFilename());
        assertEquals(new URI("http://houston.example.com"), cs.getURI());

        byte[] bytes = SimpleContentStream.getBytes(cs.getStream());
        assertEquals(string, new String(bytes, "UTF-8"));
    }

    public void testRemoveDocument() {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        d1.save();
        d1.delete();
        assertNull(conn.getObject(d1, ReturnVersion.THIS));
    }

    public void testRemoveFolder() {
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
        assertEquals(f1.getId(), conn.getObject(f1, ReturnVersion.THIS).getId());
        assertNull(conn.getObject(f2, ReturnVersion.THIS));
        assertNull(conn.getObject(d1, ReturnVersion.THIS));
    }

    public void testBasicQuery() {
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        Document d1 = root.newDocument("doc");
        d1.save();

        Collection<CMISObject> res = conn.query("SELECT * FROM Folder", false);
        assertEquals(1, res.size()); // the root
        res = conn.query("SELECT * FROM fold", false);
        assertEquals(0, res.size());
        res = conn.query("SELECT * FROM doc", false);
        assertEquals(1, res.size());
        res = conn.query("SELECT * FROM Folder WHERE Name = 'CMIS_Root_Folder'",
                false);
        assertEquals(1, res.size());
        res = conn.query("SELECT * FROM doc WHERE ObjectId = 'nosuchid'", false);
        assertEquals(0, res.size());
        res = conn.query("SELECT * FROM doc WHERE ObjectId <> '123'", false);
        assertEquals(1, res.size());
    }

}
