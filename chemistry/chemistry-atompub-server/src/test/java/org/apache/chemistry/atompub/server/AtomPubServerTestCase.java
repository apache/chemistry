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
package org.apache.chemistry.atompub.server;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.EntityProvider;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.util.AbstractEntityProvider;
import org.apache.abdera.writer.StreamWriter;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleRepository;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mortbay.jetty.Server;

public abstract class AtomPubServerTestCase extends TestCase {

    public static final String TEST_FILE_CONTENT = "This is a test file.\nTesting, testing...\n";

    protected static final AbderaClient client = new AbderaClient();

    protected static String doc3id;

    public Server server;

    public Repository repository;

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
        repository = makeRepository(null);
        startServer();
    }

    @Override
    public void tearDown() throws Exception {
        stopServer();
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

        Folder folder1 = root.newFolder("fold");
        folder1.setValue("title", "The folder 1 description");
        folder1.setValue("description", "folder 1 title");
        folder1.save();

        Folder folder2 = folder1.newFolder("fold");
        folder2.setValue("title", "The folder 2 description");
        folder2.setValue("description", "folder 2 title");
        folder2.save();

        Document doc1 = folder1.newDocument("doc");
        doc1.setValue("title", "doc 1 title");
        doc1.setValue("description", "The doc 1 descr");
        doc1.save();

        Document doc2 = folder2.newDocument("doc");
        doc2.setValue("title", "doc 2 title");
        doc2.setValue("description", "The doc 2 descr");
        doc2.save();

        Document doc3 = folder2.newDocument("doc");
        doc3.setValue("title", "doc 3 title");
        doc3.setValue("description", "The doc 3 descr");
        ContentStream cs = new SimpleContentStream(
                TEST_FILE_CONTENT.getBytes("UTF-8"), "text/plain", "doc3.txt");
        doc3.setContentStream(cs);
        doc3.save();
        doc3id = doc3.getId();

        conn.close();
        return repo;
    }

    public void testConnect() throws Exception {
        String base = "http://localhost:" + PORT + CONTEXT_PATH + SERVLET_PATH
                + getResourcePath();
        ClientResponse resp;

        resp = client.get(base + "/repository");
        assertEquals(200, resp.getStatus());
        Service root = (Service) resp.getDocument().getRoot();
        Workspace workspace = root.getWorkspaces().get(0);
        assertNotNull(root);
        Element info = workspace.getFirstChild(AtomPubCMIS.REPOSITORY_INFO);
        assertNotNull(info);

        resp = client.get(base + "/types");
        assertEquals(200, resp.getStatus());
        Element el = resp.getDocument().getRoot();
        assertNotNull(el);

        resp = client.get(base + "/children/"
                + repository.getInfo().getRootFolderId().getId());
        assertEquals(200, resp.getStatus());
        Element ch = resp.getDocument().getRoot();
        assertNotNull(ch);

        resp = client.get(base + "/children/"
                + repository.getInfo().getRootFolderId().getId() + "?"
                + AtomPubCMIS.PARAM_MAX_ITEMS + "=4");
        assertEquals(200, resp.getStatus());
        ch = resp.getDocument().getRoot();
        assertNotNull(ch);

        resp = client.get(base + "/object/" + doc3id);
        assertEquals(200, resp.getStatus());
        Element ob = resp.getDocument().getRoot();
        assertNotNull(ob);

        resp = client.get(base + "/object/" + doc3id + '?'
                + AtomPubCMIS.PARAM_FILTER + "=cmis:name");
        assertEquals(200, resp.getStatus());
        ob = resp.getDocument().getRoot();
        assertNotNull(ob);

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

        EntityProvider provider = new QueryEntityProvider("SELECT * FROM doc");
        resp = client.post(base + "/query", provider);
        assertEquals(200, resp.getStatus());
        Element res = resp.getDocument().getRoot();
        assertNotNull(res);
    }

    public static class QueryEntityProvider extends AbstractEntityProvider {

        public String statement;

        public QueryEntityProvider(String statement) {
            this.statement = statement;
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
            sw.startElement("query", CMIS.CMIS_NS, CMIS.CMIS_PREFIX);
            sw.startElement("statement", CMIS.CMIS_NS, CMIS.CMIS_PREFIX).writeElementText(
                    statement).endElement();
            sw.startElement("searchAllVersions", CMIS.CMIS_NS, CMIS.CMIS_PREFIX).writeElementText(
                    "false").endElement();
            sw.startElement("pageSize", CMIS.CMIS_NS, CMIS.CMIS_PREFIX).writeElementText(
                    0).endElement();
            sw.startElement("skipCount", CMIS.CMIS_NS, CMIS.CMIS_PREFIX).writeElementText(
                    0).endElement();
            sw.endElement(); // query
            sw.endDocument();
        }
    }

}
