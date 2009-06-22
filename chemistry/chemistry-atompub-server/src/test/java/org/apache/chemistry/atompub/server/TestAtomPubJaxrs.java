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
package org.apache.chemistry.atompub.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.chemistry.atompub.server.jaxrs.AbderaResource;
import org.apache.cxf.helpers.FileUtils;
import org.apache.cxf.helpers.IOUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Test that uses a JAX-RS Application (itself wrapping our Abdera {@link CMIS).
 */
public class TestAtomPubJaxrs extends AtomPubServerTestCase {

    private File tmpDir;

    @Override
    public void startServer() throws Exception {
        AbderaResource.repository = repository; // TODO inject differently
        server = new Server(PORT);
        server.setHandler(new WebAppContext(server, makeWar(), CONTEXT_PATH));
        server.start();
    }

    public String makeWar() throws IOException {
        tmpDir = makeTmpDir();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream("jaxrs/web.xml");
        File war = new File(tmpDir, "war");
        File webinf = new File(war, "WEB-INF");
        webinf.mkdirs();
        File web = new File(webinf, "web.xml");
        OutputStream os = new FileOutputStream(web);
        IOUtils.copy(is, os);
        return war.getAbsolutePath();
    }

    @Override
    public void stopServer() throws Exception {
        try {
            super.stopServer();
        } finally {
            FileUtils.removeDir(tmpDir);
        }
    }

    public static File makeTmpDir() throws IOException {
        File tmpDir = File.createTempFile("test-chemistry-", null);
        tmpDir.delete();
        tmpDir.mkdir();
        return tmpDir;
    }

}
