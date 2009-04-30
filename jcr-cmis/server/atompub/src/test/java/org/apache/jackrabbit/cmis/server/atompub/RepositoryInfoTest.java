/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis.server.atompub;

import java.io.InputStream;

import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.jackrabbit.cmis.server.atompub.CMISRepositoryInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class RepositoryInfoTest extends Assert {

    private static JettyServer server;
    private static AbderaClient client = new AbderaClient();

    @BeforeClass
    public static void setUp() throws Exception {
        if (server == null) {
            server = new JettyServer();
            server.start(SimpleRepositoryProvider.class);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    @Test
    public void testGetService() throws Exception {
        System.in.read();

        ClientResponse resp = client.get("http://localhost:9002/");
        System.out.println(resp.getDocument().getRoot());

        Service root = (Service) resp.getDocument().getRoot();
        System.out.println(root);

        Workspace workspace = root.getWorkspaces().get(0);
        System.out.println(workspace);

        CMISRepositoryInfo info = workspace.getFirstChild(CMIS.REPOSITORY_INFO);
        System.out.println(info.getId());
        System.out.println(info.getVendorName());
        System.out.println(info.getProductName());
        System.out.println(info.getRootFolderId());
    }

    private InputStream getInputStream(String name) {
        return RepositoryInfoTest.class.getClassLoader().getResourceAsStream(name);
    }

}

