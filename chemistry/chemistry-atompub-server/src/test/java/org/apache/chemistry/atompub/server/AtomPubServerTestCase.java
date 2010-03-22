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
 *     Florian Roth, In-integrierte Informationssysteme
 */
package org.apache.chemistry.atompub.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.ws.rs.core.HttpHeaders;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.EntityProvider;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.util.AbstractEntityProvider;
import org.apache.abdera.writer.StreamWriter;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.Paging;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.RepositoryService;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleRepository;
import org.apache.chemistry.impl.simple.SimpleRepositoryService;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.mortbay.jetty.Server;

public abstract class AtomPubServerTestCase extends TestCase {

    public static final String TEST_FILE_CONTENT = "This is a test file.\nTesting, testing...\n";

    public static final String TEST_FILE_CONTENT2 = "<html><head><title>foo</title></head><body>bar</body></html>";

    protected static final AbderaClient client = new AbderaClient();

    protected static String rootFolderId;

    private static String folder1id;

    protected static String doc2id;

    protected static String doc3id;

    protected static String doc4id;

    protected RepositoryService repositoryService;

    public Server server;

    public String base;

    protected static final int PORT = (int) (8500 + System.currentTimeMillis() % 100);

    protected static final String CONTEXT_PATH = "/ctx";

    // also in web.xml for JAX-RS
    protected static final String SERVLET_PATH = "/srv";

    // additional path to use after the servlet, used by JAX-RS
    protected String getResourcePath() {
        return "";
    }

    @Override
    public void setUp() throws Exception {
        repositoryService = new SimpleRepositoryService(makeRepository(null));
        RepositoryManager.getInstance().registerService(repositoryService);
        startServer();
        base = "http://localhost:" + PORT + CONTEXT_PATH + SERVLET_PATH
                + getResourcePath();
    }

    @Override
    public void tearDown() throws Exception {
        stopServer();
        RepositoryManager.getInstance().unregisterService(repositoryService);
        repositoryService = null;
    }

    public abstract void startServer() throws Exception;

    public void stopServer() throws Exception {
        server.stop();
    }

    public static Repository makeRepository(String rootId) throws Exception {
        PropertyDefinition p1 = new SimplePropertyDefinition("title",
                "def:title", null, "title", "Title", "", false,
                PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition p2 = new SimplePropertyDefinition("description",
                "def:description", null, "description", "Description", "",
                false, PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition p3 = new SimplePropertyDefinition("date",
                "def:date", null, "date", "Date", "", false,
                PropertyType.DATETIME, false, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        SimpleType dt = new SimpleType("doc", BaseType.DOCUMENT.getId(), "doc",
                null, "Doc", "My Doc Type", BaseType.DOCUMENT, "", true, true,
                true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(p1,
                        p2, p3));
        SimpleType ft = new SimpleType("fold", BaseType.FOLDER.getId(), "doc",
                null, "Fold", "My Folder Type", BaseType.FOLDER, "", true,
                true, true, true, true, true, false, false,
                ContentStreamPresence.NOT_ALLOWED, null, null, Arrays.asList(
                        p1, p2));
        SimpleRepository repo = new SimpleRepository("test", Arrays.asList(dt,
                ft), rootId);
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();
        rootFolderId = root.getId();

        Folder folder1 = root.newFolder("fold");
        folder1.setName("folder1");
        folder1.setValue("title", "The folder 1 description");
        folder1.setValue("description", "folder 1 title");
        folder1.save();
        folder1id = folder1.getId();

        Folder folder2 = folder1.newFolder("fold");
        folder2.setName("folder2");
        folder2.setValue("title", "The folder 2 description");
        folder2.setValue("description", "folder 2 title");
        folder2.save();

        Document doc1 = folder1.newDocument("doc");
        doc1.setName("doc1");
        doc1.setValue("title", "doc 1 title");
        doc1.setValue("description", "The doc 1 descr");
        doc1.save();

        Document doc2 = folder2.newDocument("doc");
        doc2.setName("doc2");
        doc2.setValue("title", "doc 2 title");
        doc2.setValue("description", "The doc 2 descr");
        doc2.save();
        doc2id = doc2.getId();

        Document doc3 = folder2.newDocument("doc");
        doc3.setName("doc3");
        doc3.setValue("title", "doc 3 title");
        doc3.setValue("description", "The doc 3 descr");
        ContentStream cs = new SimpleContentStream(
                TEST_FILE_CONTENT.getBytes("UTF-8"), "text/plain", "doc3.txt");
        doc3.setContentStream(cs);
        doc3.save();
        doc3id = doc3.getId();

        Document doc4 = folder2.newDocument("doc");
        doc4.setName("doc4");
        doc4.setValue("title", "doc 4 title");
        cs = new SimpleContentStream(TEST_FILE_CONTENT.getBytes("UTF-8"),
                "invalid_mime", "doc4.txt");
        doc4.setContentStream(cs);
        doc4.save();
        doc4id = doc4.getId();

        conn.close();
        return repo;
    }

    public void testRepository() throws Exception {
        ClientResponse resp = client.get(base + "/repository");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Service root = (Service) resp.getDocument().getRoot();
        Workspace workspace = root.getWorkspaces().get(0);
        assertNotNull(root);
        Element info = workspace.getFirstChild(AtomPubCMIS.REPOSITORY_INFO);
        assertNotNull(info);
        Element uritmpl = workspace.getFirstChild(AtomPubCMIS.URI_TEMPLATE);
        assertNotNull(uritmpl);
        Element tmpl = uritmpl.getFirstChild(AtomPubCMIS.TEMPLATE);
        assertNotNull(tmpl);
        assertTrue(tmpl.getText().startsWith(base + "/object/{id}"));
        resp.release();
    }

    public void testTypes() throws Exception {
        ClientResponse resp = client.get(base + "/typechildren");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element el = resp.getDocument().getRoot();
        assertNotNull(el);
        resp.release();

        resp = client.get(base + "/typechildren/doc");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        el = resp.getDocument().getRoot();
        assertNotNull(el);
        resp.release();

        resp = client.get(base + "/typedescendants");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        el = resp.getDocument().getRoot();
        assertNotNull(el);
        resp.release();

        resp = client.get(base + "/typedescendants/doc");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        el = resp.getDocument().getRoot();
        assertNotNull(el);
        resp.release();
    }

    public void testType() throws Exception {
        ClientResponse resp = client.get(base + "/type/doc");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element el = resp.getDocument().getRoot();
        assertNotNull(el);
        Element t = el.getFirstChild(new QName(AtomPubCMIS.CMISRA_NS, "type"));
        assertNotNull(t);
        // check that when we get a simple type, the property definitions are
        // returned as well
        Element p = t.getFirstChild(new QName(CMIS.CMIS_NS,
                "propertyIdDefinition"));
        assertNotNull(p);
        resp.release();
    }

    public void testParents() throws Exception {
        ClientResponse resp = client.get(base + "/parents/" + doc2id);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element ch = resp.getDocument().getRoot();
        assertNotNull(ch);
        resp.release();
    }

    public void testChildren() throws Exception {
        ClientResponse resp = client.get(base + "/children/" + rootFolderId);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element ch = resp.getDocument().getRoot();
        assertNotNull(ch);
        resp.release();

        resp = client.get(base + "/children/" + rootFolderId + "?"
                + AtomPubCMIS.PARAM_MAX_ITEMS + "=4");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ch = resp.getDocument().getRoot();
        assertNotNull(ch);
        resp.release();

        // post of new document, test using various content types
        for (String contentType : Arrays.<String> asList(
                AtomPub.MEDIA_TYPE_ATOM, //
                AtomPub.MEDIA_TYPE_ATOM_ENTRY, //
                AtomPub.MEDIA_TYPE_ATOM_ENTRY + ";charset=UTF-8")) {
            PostMethod postMethod = new PostMethod(base + "/children/"
                    + rootFolderId);
            postMethod.setRequestEntity(new InputStreamRequestEntity(
                    load("templates/createdocument.atomentry.xml"), contentType));
            int status = new HttpClient().executeMethod(postMethod);
            assertEquals(HttpStatus.SC_CREATED, status);
            assertNotNull(postMethod.getResponseHeader(HttpHeaders.LOCATION));
            assertNotNull(postMethod.getResponseHeader(HttpHeaders.CONTENT_LOCATION));
            postMethod.releaseConnection();
        }
    }

    public void testObject() throws Exception {
        ClientResponse resp = client.get(base + "/object/" + doc3id);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        resp.release();

        resp = client.get(base + "/object/" + doc3id + '?'
                + AtomPubCMIS.PARAM_FILTER + "=cmis:name");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        resp.release();

        // update content
        RequestOptions options = new RequestOptions();
        options.setContentType(AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        resp = client.put(base + "/object/" + doc3id,
                load("templates/updatedocument.atomentry.xml"), options);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        resp.release();

        // update single prop without cmis:objectTypeId
        options = new RequestOptions();
        options.setContentType(AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        resp = client.put(base + "/object/" + doc3id,
                load("templates/updatedocument2.atomentry.xml"), options);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        resp.release();
    }

    public void testGetObjectByPath() {
        String path = "/folder1/folder2/doc3";

        // URL-encoded (what a compliant client using the URI template does)
        ClientResponse resp = client.get(base + "/object?path="
                + path.replace("/", "%2F"));
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        Element id = ob.getFirstChild(new QName(AtomPub.ATOM_NS, "id"));
        assertEquals("urn:uuid:" + doc3id, id.getText());
        resp.release();

        // unencoded
        resp = client.get(base + "/object?path=" + path);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        id = ob.getFirstChild(new QName(AtomPub.ATOM_NS, "id"));
        assertEquals("urn:uuid:" + doc3id, id.getText());
        resp.release();

        // non-URI-template version using /path

        // URL-encoded
        resp = client.get(base + "/path/" + path.replace("/", "%2F"));
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        id = ob.getFirstChild(new QName(AtomPub.ATOM_NS, "id"));
        assertEquals("urn:uuid:" + doc3id, id.getText());
        resp.release();

        // unencoded, single slash
        resp = client.get(base + "/path" + path);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        id = ob.getFirstChild(new QName(AtomPub.ATOM_NS, "id"));
        assertEquals("urn:uuid:" + doc3id, id.getText());
        resp.release();

        // unencoded, double slash
        resp = client.get(base + "/path/" + path);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);
        id = ob.getFirstChild(new QName(AtomPub.ATOM_NS, "id"));
        assertEquals("urn:uuid:" + doc3id, id.getText());
        resp.release();
    }

    public void testDelete() {
        ClientResponse resp = client.delete(base + "/object/" + doc3id);
        assertEquals(HttpStatus.SC_NO_CONTENT, resp.getStatus());
        resp.release();

        resp = client.delete(base + "/object/no-such-id");
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
        resp.release();
    }

    public void testDescendants() {
        ClientResponse resp = client.get(base + "/descendants/" + rootFolderId
                + "?" + AtomPubCMIS.PARAM_DEPTH + "=1");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element ch = resp.getDocument().getRoot();
        assertNotNull(ch);
        resp.release();

        resp = client.delete(base + "/descendants/" + folder1id);
        assertEquals(HttpStatus.SC_NO_CONTENT, resp.getStatus());
        resp.release();

        resp = client.delete(base + "/descendants/no-such-id");
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
        resp.release();
    }

    public void testFolderTree() {
        ClientResponse resp = client.get(base + "/descendants/" + rootFolderId
                + "?" + AtomPubCMIS.PARAM_DEPTH + "=-1");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element f = resp.getDocument().getRoot();
        Element e = f.getFirstChild(AtomPub.ATOM_ENTRY);
        assertNotNull(e);
        Element ch = e.getFirstChild(AtomPubCMIS.CHILDREN);
        assertNotNull(ch);
        f = ch.getFirstChild(AtomPub.ATOM_FEED);
        e = f.getFirstChild(AtomPub.ATOM_ENTRY);
        assertNotNull(e);
        e = e.getNextSibling(AtomPub.ATOM_ENTRY);
        assertNotNull(e);
        resp.release();

        resp = client.delete(base + "/foldertree/" + folder1id);
        assertEquals(HttpStatus.SC_NO_CONTENT, resp.getStatus());
        resp.release();

        resp = client.delete(base + "/foldertree/no-such-id");
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
        resp.release();
    }

    public void testFile() throws Exception {
        HttpMethod method = new GetMethod(base + "/file/" + doc3id);
        int status = new HttpClient().executeMethod(method);
        assertEquals(HttpStatus.SC_OK, status);
        assertEquals("text/plain",
                method.getResponseHeader("Content-Type").getValue());
        assertEquals(String.valueOf(TEST_FILE_CONTENT.getBytes().length),
                method.getResponseHeader("Content-Length").getValue());
        byte[] body = method.getResponseBody();
        assertEquals(TEST_FILE_CONTENT, new String(body, "UTF-8"));
        method.releaseConnection();

        // get of missing content stream
        method = new GetMethod(base + "/file/" + doc2id);
        status = new HttpClient().executeMethod(method);
        assertEquals(HttpStatus.SC_CONFLICT, status);
        method.releaseConnection();

        // put file (
        RequestOptions options = new RequestOptions();
        options.setContentType("text/html");
        InputStream is = new ByteArrayInputStream(TEST_FILE_CONTENT2.getBytes());
        ClientResponse resp = client.put(base + "/file/" + doc2id, is, options);
        assertEquals(HttpStatus.SC_CREATED, resp.getStatus());
        assertNotNull(resp.getLocation());
        assertNotNull(resp.getContentLocation());
        resp.release();

        // get it again
        method = new GetMethod(base + "/file/" + doc2id);
        status = new HttpClient().executeMethod(method);
        assertEquals(HttpStatus.SC_OK, status);
        assertEquals("text/html",
                method.getResponseHeader("Content-Type").getValue());
        assertEquals(String.valueOf(TEST_FILE_CONTENT2.getBytes().length),
                method.getResponseHeader("Content-Length").getValue());
        body = method.getResponseBody();
        assertEquals(TEST_FILE_CONTENT2, new String(body, "UTF-8"));
        method.releaseConnection();
    }

    public void testBadContentType() throws Exception {
        HttpMethod method = new GetMethod(base + "/file/" + doc4id);
        int status = new HttpClient().executeMethod(method);
        assertEquals(HttpStatus.SC_OK, status);
        assertEquals("application/octet-stream", method.getResponseHeader(
                "Content-Type").getValue());
        byte[] body = method.getResponseBody();
        assertEquals(TEST_FILE_CONTENT, new String(body, "UTF-8"));
        method.releaseConnection();
    }

    public void testQueryPOST() throws Exception {
        EntityProvider provider = new QueryEntityProvider("SELECT * FROM doc",
                true, null, null);
        ClientResponse resp = client.post(base + "/query", provider);
        assertEquals(HttpStatus.SC_CREATED, resp.getStatus());
        Element res = resp.getDocument().getRoot();
        assertNotNull(res);
        resp.release();
    }

    public void testQueryGET() throws Exception {
        ClientResponse resp = client.get(base + "/query?q=SELECT+*+FROM+doc");
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        Element res = resp.getDocument().getRoot();
        assertNotNull(res);
        resp.release();
    }

    protected InputStream load(String resource) throws Exception {
        return getClass().getClassLoader().getResource(resource).openStream();
    }

    public static class QueryEntityProvider extends AbstractEntityProvider {

        public String statement;

        public boolean searchAllVersions;

        public Inclusion inclusion;

        public Paging paging;

        public QueryEntityProvider(String statement, boolean searchAllVersions,
                Inclusion inclusion, Paging paging) {
            this.statement = statement;
            this.searchAllVersions = searchAllVersions;
            this.inclusion = inclusion;
            this.paging = paging;
        }

        @Override
        public String getContentType() {
            return AtomPubCMIS.MEDIA_TYPE_CMIS_QUERY;
        }

        public boolean isRepeatable() {
            return true;
        }

        public void writeTo(StreamWriter sw) {
            sw.startDocument();
            sw.startElement(CMIS.QUERY);
            sw.startElement(CMIS.STATEMENT).writeElementText(statement).endElement();
            sw.startElement(CMIS.SEARCH_ALL_VERSIONS).writeElementText(
                    Boolean.toString(searchAllVersions)).endElement();
            if (inclusion != null) {
                if (inclusion.renditions != null) {
                    sw.startElement(CMIS.RENDITION_FILTER).writeElementText(
                            inclusion.renditions).endElement();
                }
                if (inclusion.relationships != null) {
                    sw.startElement(CMIS.INCLUDE_RELATIONSHIPS).writeElementText(
                            RelationshipDirection.toInclusion(inclusion.relationships)).endElement();
                }
                sw.startElement(CMIS.INCLUDE_ALLOWABLE_ACTIONS).writeElementText(
                        Boolean.toString(inclusion.allowableActions)).endElement();
                sw.startElement(CMIS.INCLUDE_POLICY_IDS).writeElementText(
                        Boolean.toString(inclusion.policies)).endElement();
                sw.startElement(CMIS.INCLUDE_ACL).writeElementText(
                        Boolean.toString(inclusion.acls)).endElement();
            }
            if (paging != null) {
                if (paging.maxItems > -1) {
                    sw.startElement(CMIS.MAX_ITEMS).writeElementText(
                            Integer.toString(paging.maxItems)).endElement();
                }
                sw.startElement(CMIS.SKIP_COUNT).writeElementText(
                        Integer.toString(paging.skipCount)).endElement();
            }
            sw.endElement(); // query
            sw.endDocument();
        }
    }

}
