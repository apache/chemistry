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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.chemistry.AllowableAction;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.ObjectNotFoundException;
import org.apache.chemistry.Paging;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Tree;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.APPObjectEntry;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.chemistry.util.GregorianCalendar;
import org.apache.commons.io.IOUtils;

/**
 * Basic test on a repository populated with
 * {@link BasicHelper#populateRepository}.
 * <p>
 * The {@link #setUp} method must initialize repository, conn and spi.
 */
public abstract class BasicTestCase extends TestCase {

    public Repository repository;

    public Connection conn;

    public SPI spi;

    public String rootFolderName = ""; // not in spec

    public String expectedRepositoryId = "test";

    public String expectedRepositoryName = "test";

    public String expectedRepositoryDescription = "Repository test";

    public String expectedRepositoryVendor = "Apache";

    public String expectedRepositoryProductName = "Chemistry Simple Repository";

    // TODO update this when releasing
    public String expectedRepositoryProductVersion = "0.5-SNAPSHOT";

    public boolean expectedCapabilityHasGetDescendants = true;

    public boolean expectedCapabilityHasGetFolderTree = true;

    public boolean expectedCapabilityHasMultifiling = false;

    public CapabilityQuery expectedCapabilityQuery = CapabilityQuery.BOTH_COMBINED;

    public boolean expectedCapabilityHasUnfiling = false;

    public String expectedRootTypeId = "chemistry:root"; // not in spec

    /**
     * Must be implemented by actual testing classes.
     */
    public abstract Repository makeRepository() throws Exception;

    @Override
    public void setUp() throws Exception {
        repository = makeRepository();
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

    public void testRepository() {
        assertNotNull(repository);
        assertEquals(expectedRepositoryId, repository.getId());
        assertEquals(expectedRepositoryName, repository.getName());
        RepositoryInfo info = repository.getInfo();
        assertEquals(expectedRepositoryDescription, info.getDescription());
        assertEquals(expectedRepositoryVendor, info.getVendorName());
        assertEquals(expectedRepositoryProductName, info.getProductName());
        assertEquals(expectedRepositoryProductVersion, info.getProductVersion());
        // assertEquals(new SimpleObjectId("XYZ"), info.getRootFolderId());
        // assertEquals("", info.getLatestChangeLogToken());
        assertEquals("1.0", info.getVersionSupported());
        assertFalse(info.isChangeLogIncomplete());
        Set<BaseType> clbt = info.getChangeLogBaseTypes();
        Set<BaseType> clbtExpected = new HashSet<BaseType>(Arrays.asList(
                BaseType.FOLDER, BaseType.DOCUMENT, BaseType.RELATIONSHIP,
                BaseType.POLICY));
        assertEquals(clbtExpected, clbt);
        RepositoryCapabilities cap = info.getCapabilities();
        assertEquals(CapabilityACL.NONE, cap.getACLCapability());
        assertFalse(cap.isAllVersionsSearchable());
        assertEquals(CapabilityChange.NONE, cap.getChangeCapability());
        assertTrue(cap.isContentStreamUpdatableAnytime());
        assertEquals(expectedCapabilityHasGetDescendants,
                cap.hasGetDescendants());
        assertEquals(expectedCapabilityHasGetFolderTree, cap.hasGetFolderTree());
        assertEquals(expectedCapabilityHasMultifiling, cap.hasMultifiling());
        assertFalse(cap.isPWCSearchable());
        assertFalse(cap.isPWCUpdatable());
        assertEquals(expectedCapabilityQuery, cap.getQueryCapability());
        assertEquals(CapabilityRendition.NONE, cap.getRenditionCapability());
        assertEquals(expectedCapabilityHasUnfiling, cap.hasUnfiling());
        assertFalse(cap.hasVersionSpecificFiling());
        assertEquals(CapabilityJoin.NONE, cap.getJoinCapability());
    }

    public void testBasic() {
        assertNotNull(repository);
        assertNotNull(conn);
        Folder root = conn.getRootFolder();
        assertNotNull(root);
        Type rootType = root.getType();
        assertNotNull(rootType);
        assertEquals(expectedRootTypeId, rootType.getId());
        assertEquals(expectedRootTypeId, root.getTypeId());
        assertEquals(rootFolderName, root.getName());
        assertNull(root.getParent());
        Map<String, Property> props = root.getProperties();
        assertNotNull(props);
        assertTrue(props.size() > 0);

        List<CMISObject> entries = root.getChildren();
        assertEquals(1, entries.size());
        Folder f1 = (Folder) entries.get(0);
        Folder fold = f1.getParent();
        assertEquals(root.getId(), fold.getId());
    }

    public void testTypes() {
        Collection<Type> types = repository.getTypes();
        assertEquals(7, types.size());
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
            assertNotNull(child.getProperty("date"));
        }
    }

    public void testCreateSPI() throws Exception {
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(Property.TYPE_ID, "fold");
        properties.put("description", "some descr");
        ObjectId folderId = spi.createFolder(properties,
                repository.getInfo().getRootFolderId());
        assertNotNull(folderId);
        Folder folder = (Folder) conn.getObject(folderId);
        assertEquals("some descr", folder.getValue("description"));

        properties = new HashMap<String, Serializable>();
        properties.put(Property.TYPE_ID, "doc");
        properties.put("title", "some title");
        ObjectId docId = spi.createDocument(properties,
                repository.getInfo().getRootFolderId(), null, null);
        assertNotNull(docId);
        Document doc = (Document) conn.getObject(docId);
        assertEquals("some title", doc.getValue("title"));
    }

    public void testCopySPI() throws Exception {
        ObjectEntry doc1 = spi.getObjectByPath("/folder 1/doc 1", null);
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("title", "new title");
        try {
            ObjectId id = spi.createDocumentFromSource(doc1,
                    repository.getInfo().getRootFolderId(), properties, null);
            assertNotNull(id);
            assertNotSame(id, doc1.getId());
        } catch (CMISRuntimeException e) {
            assertTrue(e.getMessage().contains(
                    "AtomPub bindings do not support"));
            return;
        }
        // fetch
        ObjectEntry doc = spi.getObjectByPath("/doc 1", null);
        assertNotNull(doc);
        assertEquals("new title", doc.getValue("title"));
    }

    public void testCopy() throws Exception {
        ObjectEntry foldid = spi.getObjectByPath("/folder 1", null);
        Folder fold = (Folder) conn.getObject(foldid);

        ObjectEntry docid = spi.getObjectByPath("/folder 1/folder 2/doc 2",
                null);
        Document doc = (Document) conn.getObject(docid);
        try {
            Document newdoc = doc.copy(fold);
            assertNotSame(doc.getId(), newdoc.getId());
        } catch (CMISRuntimeException e) {
            assertTrue(e.getMessage().contains(
                    "AtomPub bindings do not support"));
            return;
        }
        ObjectEntry d = spi.getObjectByPath("/folder 1/doc 2", null);
        assertNotNull(d);
        assertEquals("doc 2 title", d.getValue("title"));
    }

    public void testQuery() {
        String rootId = spi.getRepository().getInfo().getRootFolderId().getId();
        String folder1Id = spi.getObjectByPath("/folder 1", null).getId();
        String folder2Id = spi.getObjectByPath("/folder 1/folder 2", null).getId();
        Collection<CMISObject> res;

        res = conn.query("SELECT * FROM doc", false);
        assertNotNull(res);
        assertEquals(4, res.size());
        res = conn.query("SELECT * FROM cmis:document", false);
        assertNotNull(res);
        assertEquals(4, res.size());
        ObjectEntry doc = spi.getObjectByPath("/folder 1/folder 2/doc 2", null);
        res = conn.query(String.format(
                "SELECT * FROM cmis:document WHERE cmis:objectId = '%s'",
                doc.getId()), false);
        assertEquals(1, res.size());

        res = conn.query("SELECT * FROM fold", false);
        assertEquals(2, res.size());
        res = conn.query("SELECT * FROM cmis:folder", false);
        assertEquals(3, res.size()); // root as well
        res = conn.query(String.format(
                "SELECT * FROM cmis:folder WHERE cmis:objectId = '%s'",
                folder2Id), false);
        assertEquals(1, res.size());
        res = conn.query(String.format(
                "SELECT * FROM cmis:folder WHERE cmis:objectId = '%s'"
                        + " AND cmis:name = 'folder 2'"
                        + " AND title <> 'blarg'", //
                folder2Id), false);
        assertEquals(1, res.size());

        // IN_FOLDER
        String sqlpat = "SELECT * FROM cmis:document WHERE IN_FOLDER('%s')";
        res = conn.query(String.format(sqlpat, folder2Id), false);
        assertEquals(3, res.size());
        sqlpat = "SELECT * FROM cmis:folder WHERE IN_FOLDER('%s')";
        res = conn.query(String.format(sqlpat, folder1Id), false);
        assertEquals(1, res.size());

        // IN_TREE
        sqlpat = "SELECT * FROM cmis:document WHERE IN_TREE('%s')";
        res = conn.query(String.format(sqlpat, folder2Id), false);
        assertEquals(3, res.size());
        sqlpat = "SELECT * FROM cmis:document WHERE IN_TREE('%s')";
        res = conn.query(String.format(sqlpat, folder1Id), false);
        assertEquals(4, res.size());
        sqlpat = "SELECT * FROM cmis:folder WHERE IN_TREE('%s')";
        res = conn.query(String.format(sqlpat, rootId), false);
        assertEquals(2, res.size());

        // CONTAINS
        res = conn.query(
                "SELECT * FROM cmis:folder WHERE CONTAINS('description')",
                false);
        assertEquals(2, res.size());
        res = conn.query("SELECT * FROM cmis:document WHERE CONTAINS('small')",
                false);
        assertEquals(1, res.size());

        // SCORE
        res = conn.query(
                "SELECT cmis:name, SCORE() FROM cmis:document WHERE CONTAINS('small')",
                false);
        assertEquals(1, res.size());
    }

    public void testGetObjectByPath() {
        Folder root = conn.getRootFolder();
        assertEquals(rootFolderName, root.getName());
        assertNotNull(spi.getObjectByPath("/", null));
        assertNotNull(spi.getObjectByPath("/folder 1", null));
        assertNotNull(spi.getObjectByPath("/folder 1/doc 1", null));
        assertNotNull(spi.getObjectByPath("/folder 1/folder 2", null));
        assertNotNull(spi.getObjectByPath("/folder 1/folder 2/doc 2", null));
        assertNotNull(spi.getObjectByPath("/folder 1/folder 2/doc 3", null));
        assertNull(spi.getObjectByPath("/nosuchname", null));
        assertNull(spi.getObjectByPath("/nosuchname/foo", null));
        assertNull(spi.getObjectByPath("/folder 1/nosuchname", null));
        assertNull(spi.getObjectByPath("/folder 1/doc 1/nosuchname", null));

        assertNotNull(conn.getFolder("/"));
        assertNotNull(conn.getFolder("/folder 1"));
        assertNotNull(conn.getFolder("/folder 1/folder 2"));
        try {
            conn.getFolder("/folder 1/doc 1");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        assertNull(conn.getFolder("/notsuchname"));
    }

    public void testGetChildren() {
        Folder root = conn.getRootFolder();
        ListPage<ObjectEntry> page = spi.getChildren(root, null, null,
                new Paging(20, 0));
        assertEquals(1, page.size());
        assertFalse(page.getHasMoreItems());
        assertEquals(1, page.getNumItems());

        ObjectId folder1 = root.getChildren().get(0);
        page = spi.getChildren(folder1, null, null, new Paging(20, 0));
        assertEquals(2, page.size());
        assertFalse(page.getHasMoreItems());
        assertEquals(2, page.getNumItems());

        page = spi.getChildren(folder1, null, null, new Paging(1, 0));
        assertEquals(1, page.size());
        assertTrue(page.getHasMoreItems());
        assertEquals(2, page.getNumItems());

        page = spi.getChildren(folder1, null, null, new Paging(1, 1));
        assertEquals(1, page.size());
        assertFalse(page.getHasMoreItems());
        assertEquals(2, page.getNumItems());

        page = spi.getChildren(folder1, null, null, new Paging(2, 0));
        ObjectId folder2 = page.get(0).getTypeId().equals("fold") ? page.get(0)
                : page.get(1);
        page = spi.getChildren(folder2, null, null, new Paging(1, 1));
        assertEquals(1, page.size());
        assertTrue(page.getHasMoreItems());
        assertEquals(3, page.getNumItems());

        page = spi.getChildren(folder2, null, null, new Paging(2, 0));
        assertEquals(2, page.size());
        assertTrue(page.getHasMoreItems());
        assertEquals(3, page.getNumItems());

        ObjectEntry doc1 = spi.getObjectByPath("/folder 1/doc 1", null);
        try {
            spi.getChildren(doc1, null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            spi.getChildren(spi.newObjectId("no-such-id"), null, null, null);
            fail();
        } catch (ObjectNotFoundException e) {
            // ok
        }
    }

    public void testGetFolderTree() {
        // check whether repository supports this feature
        RepositoryInfo info = repository.getInfo();
        if (!info.getCapabilities().hasGetFolderTree()) {
            return;
        }
        Folder root = conn.getRootFolder();
        Tree<ObjectEntry> desc = spi.getFolderTree(root, 4, null);
        assertEquals(2, desc.size());

        ObjectEntry doc1 = spi.getObjectByPath("/folder 1/doc 1", null);
        try {
            spi.getFolderTree(doc1, -1, null);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            spi.getFolderTree(spi.newObjectId("no-such-id"), -1, null);
            fail();
        } catch (ObjectNotFoundException e) {
            // ok
        }
    }

    public void testGetDescendants() {
        // check whether repository supports this feature
        RepositoryInfo info = repository.getInfo();
        if (!info.getCapabilities().hasGetDescendants()) {
            return;
        }
        Folder root = conn.getRootFolder();
        Tree<ObjectEntry> desc = spi.getDescendants(root, 4, null, null);
        assertEquals(6, desc.size());

        ObjectEntry doc1 = spi.getObjectByPath("/folder 1/doc 1", null);
        try {
            spi.getDescendants(doc1, -1, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            spi.getDescendants(spi.newObjectId("no-such-id"), -1, null, null);
            fail();
        } catch (ObjectNotFoundException e) {
            // ok
        }
    }

    public void testTrees() throws Exception {
        // check whether repository supports this feature
        RepositoryInfo info = repository.getInfo();
        if (!info.getCapabilities().hasGetDescendants()) {
            return;
        }

        Tree<ObjectEntry> desc;

        Folder root = conn.getRootFolder();
        desc = spi.getDescendants(root, -1, null, null);
        assertEquals(6, desc.size());
        desc = spi.getDescendants(root, 1, null, null);
        assertEquals(1, desc.size());
        desc = spi.getDescendants(root, 2, null, null);
        assertEquals(3, desc.size());
        desc = spi.getDescendants(root, 3, null, null);
        assertEquals(6, desc.size());
        desc = spi.getDescendants(root, 4, null, null);
        assertEquals(6, desc.size());

        ObjectEntry fold1 = spi.getObjectByPath("/folder 1", null);
        desc = spi.getDescendants(fold1, -1, null, null);
        assertEquals(5, desc.size());
        desc = spi.getDescendants(fold1, 1, null, null);
        assertEquals(2, desc.size());
        desc = spi.getDescendants(fold1, 2, null, null);
        assertEquals(5, desc.size());
        desc = spi.getDescendants(fold1, 3, null, null);
        assertEquals(5, desc.size());
    }

    public void testGetFolderParent() {
        Folder root = conn.getRootFolder();
        assertNull(spi.getFolderParent(root, null));
        ObjectId folder1 = root.getChildren().get(0);
        ObjectEntry p1 = spi.getFolderParent(folder1, null);
        assertNotNull(p1);
        assertEquals(root.getId(), p1.getId());
    }

    public void testGetObjectParents() {
        Folder root = conn.getRootFolder();
        ObjectId folder1Id = root.getChildren().get(0);
        Folder folder1 = (Folder) conn.getObject(folder1Id);
        Document doc = getDocumentChild(folder1);
        Collection<ObjectEntry> parents = spi.getObjectParents(doc, null);
        assertEquals(1, parents.size());
    }

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

        assertNotNull("dog not found", dog);
        ContentStream cs = dog.getContentStream();
        assertNotNull(cs);
        assertTrue(cs.getLength() != 0);
        assertEquals("dog.jpg", cs.getFileName());
        assertEquals("image/jpeg", cs.getMimeType());
        assertNotNull(cs.getStream());
        InputStream in = dog.getContentStream().getStream();
        assertNotNull(in);
        byte[] array = IOUtils.toByteArray(in);
        assertTrue(array.length != 0);
        assertEquals(array.length, cs.getLength());
    }

    public void testContentStreamSPI() throws Exception {
        // set
        ObjectEntry ob = spi.getObjectByPath("/folder 1/doc 1", null);
        SimpleObjectId id = new SimpleObjectId(ob.getId());
        assertFalse(spi.hasContentStream(id)); // unfetched
        assertFalse(spi.hasContentStream(ob)); // fetched
        byte[] blobBytes = "A file...\n".getBytes("UTF-8");
        String filename = "doc.txt";
        ContentStream cs = new SimpleContentStream(blobBytes,
                "text/plain;charset=UTF-8", filename);
        spi.setContentStream(ob, cs, true);

        // refetch
        assertTrue(spi.hasContentStream(id));
        cs = spi.getContentStream(id, null);
        assertNotNull(cs);
        assertEquals(filename, cs.getFileName());
        assertEquals("text/plain;charset=UTF-8", cs.getMimeType().replace(" ",
                ""));
        InputStream in = cs.getStream();
        assertNotNull(in);
        byte[] array = IOUtils.toByteArray(in);
        assertEquals(blobBytes.length, array.length);
        assertEquals(blobBytes.length, cs.getLength());

        // delete
        spi.deleteContentStream(id);
        assertFalse(spi.hasContentStream(id));
    }

    public void testContentStream() throws Exception {
        ObjectEntry docId = spi.getObjectByPath("/folder 1/folder 2/doc 3",
                null);
        Document doc = (Document) conn.getObject(docId);
        ContentStream cs = doc.getContentStream();
        assertNotNull(cs);
        InputStream in = cs.getStream();
        assertNotNull(in);
        String s = new String(IOUtils.toByteArray(in), "UTF-8");
        assertEquals(BasicHelper.TEST_FILE_CONTENT, s);
    }

    public void testDeleteSPI() throws Exception {
        ObjectEntry doc1 = spi.getObjectByPath("/folder 1/doc 1", null);
        spi.deleteObject(doc1, false);
        doc1 = spi.getObjectByPath("/folder 1/doc 1", null);
        assertNull(doc1);
        try {
            spi.deleteObject(spi.newObjectId("nosuchid"), false);
            fail();
        } catch (ObjectNotFoundException e) {
            // ok
        }
        ObjectEntry folder1 = spi.getObjectByPath("/folder 1", null);
        try {
            spi.deleteObject(folder1, false);
            fail();
        } catch (ConstraintViolationException e) {
            // ok to fail, still has children
        }
    }

    public void testDeleteTreeSPI() throws Exception {
        ObjectEntry fold2 = spi.getObjectByPath("/folder 1/folder 2", null);
        spi.deleteTree(fold2, null, true);
        ObjectEntry oe = spi.getObjectByPath("/folder 1/doc 1", null);
        assertNotNull(oe);
        oe = spi.getObjectByPath("/folder 1/folder 2", null);
        assertNull(oe);
    }

    public void testNewDocument() throws Exception {
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
        // content stream
        String blobText = "Another file...\n";
        byte[] blobBytes = blobText.getBytes("UTF-8");
        ContentStream cs = new SimpleContentStream(blobBytes,
                "text/plain;charset=UTF-8", "mydoc.txt");
        doc.setContentStream(cs);
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
        cs = doc.getContentStream();
        assertNotNull(cs);
        assertTrue(cs.getLength() != 0);
        assertEquals("mydoc.txt", cs.getFileName());
        assertEquals("text/plain;charset=UTF-8", cs.getMimeType());
        assertNotNull(cs.getStream());
        InputStream in = doc.getContentStream().getStream();
        assertNotNull(in);
        byte[] array = IOUtils.toByteArray(in);
        assertEquals(blobBytes.length, array.length);
        assertEquals(blobBytes.length, cs.getLength());
    }

    public void testNewFolder() throws Exception {
        Folder root = conn.getRootFolder();
        assertNull(getDocumentChild(root));
        Folder fold = root.newFolder("fold");
        fold.setName("myfold");
        fold.setValue("title", "mytitle");
        assertNull(fold.getId()); // not yet saved
        fold.save();
        String id = fold.getId();
        assertNotNull(id);

        // new connection
        closeConn();
        openConn();
        root = conn.getRootFolder();
        fold = null;
        for (CMISObject child : root.getChildren()) {
            if (child.getName().equals("myfold")) {
                fold = (Folder) child;
                break;
            }
        }
        assertNotNull(fold);
        assertEquals(id, fold.getId());
        assertEquals("myfold", fold.getName());
        assertEquals("mytitle", fold.getString("title"));
    }

    public void testUpdateSPI() throws Exception {
        ObjectEntry ob = spi.getObjectByPath("/folder 1/doc 1", null);
        assertEquals("doc 1 title", ob.getValue("title"));
        assertEquals("The doc 1 descr", ob.getValue("description"));
        // update
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("description", "new descr");
        spi.updateProperties(ob, null, properties);
        // refetch
        ob = spi.getProperties(ob, null);
        assertEquals("doc 1 title", ob.getValue("title"));
        assertEquals("new descr", ob.getValue("description"));
    }

    public void testUpdate() throws Exception {
        // a non-text content type (-> base64)
        String string = "A file...\n";
        byte[] blobBytes = string.getBytes("UTF-8");
        String filename = "doc.gif";
        ContentStream cs = new SimpleContentStream(blobBytes, "image/gif",
                filename);

        // update a doc with a content stream
        ObjectEntry ob = spi.getObjectByPath("/folder 1/folder 2/doc 3", null);
        Document doc = (Document) conn.getObject(ob);
        doc.setContentStream(cs);
        doc.setValue("title", "my doc 3");
        doc.save();
        // refetch
        doc = (Document) conn.getObject(spi.newObjectId(ob.getId()));
        ContentStream cs2 = doc.getContentStream();
        assertEquals("doc.gif", cs2.getFileName());
        assertEquals("image/gif", cs2.getMimeType());
        assertEquals(blobBytes.length, cs2.getLength());
        byte[] array = IOUtils.toByteArray(cs2.getStream());
        assertEquals(string, new String(array, "UTF-8"));

        // update a doc that doesn't have a content stream yet
        ob = spi.getObjectByPath("/folder 1/doc 1", null);
        doc = (Document) conn.getObject(ob);
        doc.setContentStream(cs);
        doc.setValue("title", "my doc 1");
        doc.save();
    }

    public void testMoveSPI() throws Exception {
        ObjectEntry fold = spi.getObjectByPath("/folder 1", null);
        ObjectEntry doc = spi.getObjectByPath("/folder 1/folder 2/doc 3", null);
        ObjectId res = spi.moveObject(doc, fold, null);
        assertEquals(doc.getId(), res.getId());
        doc = spi.getObjectByPath("/folder 1/folder 2/doc 3", null);
        assertNull(doc);
        doc = spi.getObjectByPath("/folder 1/doc 3", null);
        assertNotNull(doc);
    }

    public void testMove() throws Exception {
        ObjectEntry foldid = spi.getObjectByPath("/folder 1", null);
        Folder fold = (Folder) conn.getObject(foldid);

        ObjectEntry docid = spi.getObjectByPath("/folder 1/folder 2/doc 3",
                null);
        Document doc = (Document) conn.getObject(docid);
        doc.move(fold, null);
        assertEquals(docid.getId(), doc.getId());
        ObjectEntry d = spi.getObjectByPath("/folder 1/folder 2/doc 3", null);
        assertNull(d);
        d = spi.getObjectByPath("/folder 1/doc 3", null);
        assertNotNull(d);
    }

    public void testObjectAllowableActions() throws Exception {
        ObjectEntry fold = spi.getObjectByPath("/folder 1", null);
        Set<QName> aa = fold.getAllowableActions();
        assertNotNull(aa);
        assertTrue(aa.contains(AllowableAction.CAN_UPDATE_PROPERTIES));
    }

    public void testAllowableActionsSPI() throws Exception {
        ObjectEntry foldentry = spi.getObjectByPath("/folder 1", null);
        // fetch from existing entry
        assertNotNull(foldentry.getAllowableActions());
        Set<QName> aa = spi.getAllowableActions(foldentry);
        assertNotNull(aa);
        assertTrue(aa.contains(AllowableAction.CAN_UPDATE_PROPERTIES));

        // check can refetch part of entry if missing aa
        if (foldentry instanceof APPObjectEntry) {
            ((APPObjectEntry) foldentry).setAllowableActions(null);
            aa = spi.getAllowableActions(foldentry);
            assertNotNull(aa);
            assertTrue(aa.contains(AllowableAction.CAN_UPDATE_PROPERTIES));
            assertNotNull(foldentry.getAllowableActions());
        }

        // fetch from just id
        ObjectId foldid = spi.newObjectId(foldentry.getId());
        aa = spi.getAllowableActions(foldid);
        assertNotNull(aa);
        assertTrue(aa.contains(AllowableAction.CAN_UPDATE_PROPERTIES));
    }

    /**
     * Update properties of a folder and a document, including read-only properties.
     * These should be silently ignored.
     *
     * @see http://issues.apache.org/jira/browse/CMIS-186
     * @throws Exception
     */
    public void testUpdateReadOnlyProperty() throws Exception {
        ObjectEntry ob = spi.getObjectByPath("/folder 1", null);
        // update
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("description", "new descr");
        properties.put(Property.PATH, ob.getValue(Property.PATH));
        spi.updateProperties(ob, null, properties);
        // refetch
        ob = spi.getProperties(ob, null);
        assertEquals("new descr", ob.getValue("description"));

        ob = spi.getObjectByPath("/folder 1/doc 1", null);
        assertEquals("doc 1 title", ob.getValue("title"));
        assertEquals("The doc 1 descr", ob.getValue("description"));
        // update
        properties = new HashMap<String, Serializable>();
        properties.put("description", "new descr");
        properties.put(Property.LAST_MODIFICATION_DATE, Calendar.getInstance());
        spi.updateProperties(ob, null, properties);
        // refetch
        ob = spi.getProperties(ob, null);
        assertEquals("doc 1 title", ob.getValue("title"));
        assertEquals("new descr", ob.getValue("description"));
    }

}
