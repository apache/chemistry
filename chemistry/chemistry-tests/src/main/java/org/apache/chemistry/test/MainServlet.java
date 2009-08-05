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
import org.apache.chemistry.atompub.server.servlet.CMISServlet;
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

    private static final int DEFAULT_MINUTES = 60;

    public static final String DEFAULT_HOST = "0.0.0.0";

    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Repository repository = BasicHelper.makeRepository(ROOT_ID);
        MainServlet.run(args, repository, "/cmis", "/repository");
    }

    public static void run(String[] args, Repository repository,
            String servletPath, String cmisService) throws Exception {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        int minutes = DEFAULT_MINUTES;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--help".equals(arg)) {
                System.err.println("Usage: ... [-h HOST] [-p PORT] [-t TIME (minutes)]");
                System.exit(0);
            }
            if (i == args.length - 1) {
                continue;
            }
            // 2-arg
            if ("--host".equals(arg) || "-h".equals(arg)) {
                host = args[++i];
            } else if ("--port".equals(arg) || "-p".equals(arg)) {
                port = Integer.parseInt(args[++i]);
            } else if ("--time".equals(arg) || "-t".equals(arg)) {
                minutes = Integer.parseInt(args[++i]);
            }
        }
        Server server = new Server();
        Connector connector = new SocketConnector();
        connector.setHost(host);
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });
        Servlet servlet = new CMISServlet(repository);
        ServletHolder servletHolder = new ServletHolder(servlet);
        Context context = new Context(server, servletPath, Context.SESSIONS);
        context.addServlet(servletHolder, "/*");
        server.start();
        String url = "http://" + host + ':' + port + servletPath + cmisService;
        log.info(getServerName(repository) + " started, AtomPub service url: "
                + url);
        Thread.sleep(1000 * 60 * minutes);
        server.stop();
        log.info(getServerName(repository) + " stopped");
    }

    private static String getServerName(Repository repository) {
        return "CMIS repository " + repository.getInfo().getProductName();
    }

}
