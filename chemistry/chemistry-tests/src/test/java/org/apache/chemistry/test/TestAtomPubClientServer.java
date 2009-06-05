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

import javax.servlet.Servlet;

import junit.framework.TestCase;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.app.APPContentManager;
import org.apache.chemistry.atompub.client.app.model.APPRepository;
import org.apache.chemistry.atompub.server.CMISServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Tests the AtomPub client with the AtomPub server.
 */
public class TestAtomPubClientServer extends TestCase {

    private static final Log log = LogFactory.getLog(MainServlet.class);

    public static final String HOST = "0.0.0.0";

    public static final int PORT = 8080;

    public static final String SERVLET_PATH = "/cmis";

    public static final String CMIS_SERVICE = "/repository";

    public Server server;

    public String serverUrl;

    public void startServer() throws Exception {
        Repository repository = RepositoryCreationHelper.makeRepository(null);
        server = new Server();
        Connector connector = new SocketConnector();
        connector.setHost(HOST);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] { connector });
        Servlet servlet = new CMISServlet(repository);
        ServletHolder servletHolder = new ServletHolder(servlet);
        Context context = new Context(server, SERVLET_PATH, Context.SESSIONS);
        context.addServlet(servletHolder, "/*");
        server.start();
        serverUrl = "http://" + HOST + ':' + PORT + SERVLET_PATH + CMIS_SERVICE;
        log.info("CMIS server started, AtomPub service url: " + serverUrl);
    }

    public void stopServer() throws Exception {
        // Thread.sleep(60 * MINUTES);
        server.stop();
        log.info("CMIS server stopped");
    }

    @Override
    public void setUp() throws Exception {
        startServer();
    }

    @Override
    public void tearDown() throws Exception {
        stopServer();
    }

    public void testBasic() {
        ContentManager cm = new APPContentManager(serverUrl);

        Repository repo = cm.getDefaultRepository();
        Connection conn = ((APPRepository) repo).getConnection(null);

        Folder root = conn.getRootFolder();
        List<CMISObject> entries = root.getChildren(null);
        assertEquals(1, entries.size());
    }

}
