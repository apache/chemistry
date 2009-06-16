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

import javax.servlet.Servlet;

import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.server.CMISServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Test class that runs a servlet on a memory repository initialized with a few
 * simple documents.
 */
public class MainServlet {

    private static final Log log = LogFactory.getLog(MainServlet.class);

    // use a fixed root id, this helps with caching in some clients
    public static final String ROOT_ID = "b7666828-f1aa-41e1-9d0a-94a7898ae569";

    private static final int MINUTES = 60 * 1000; // in ms

    public static final String HOST = "0.0.0.0";

    public static final int PORT = 8080;

    public static final String SERVLET_PATH = "/cmis";

    public static final String CMIS_SERVICE = "/repository";

    public static void main(String[] args) throws Exception {
        Repository repository = BasicHelper.makeRepository(ROOT_ID);
        Server server = new Server();
        Connector connector = new SocketConnector();
        connector.setHost(HOST);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] { connector });
        Servlet servlet = new CMISServlet(repository);
        ServletHolder servletHolder = new ServletHolder(servlet);
        Context context = new Context(server, SERVLET_PATH, Context.SESSIONS);
        context.addServlet(servletHolder, "/*");
        server.start();
        String url = "http://" + HOST + ':' + PORT + SERVLET_PATH
                + CMIS_SERVICE;
        log.info("CMIS server started, AtomPub service url: " + url);
        Thread.sleep(60 * MINUTES);
        server.stop();
        log.info("CMIS server stopped");
    }

}
