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
 *     Michael Durig, Day
 *     Serge Huber, Jahia
 */
package org.apache.chemistry.test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.chemistry.jcr.JcrRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.RepositoryConfig;

/**
 * Test on a Jackrabbit repository.
 */
public class TestJcrRepository extends BasicTestCase {

    private static final String DIRECTORY = "target/test/jackrabbit";

    private static final String NODETYPES_CND = "/nodetypes.cnd";

    private static final String REPOSITORY_XML = "/repository.xml";

    protected JackrabbitRepository jackrabbitRepo;

    protected Session session;

    @Override
    public Repository makeRepository() throws Exception {
        File dir = new File(DIRECTORY);
        FileUtils.deleteDirectory(dir);
        dir.mkdirs();

        InputStream xml = getClass().getResourceAsStream(REPOSITORY_XML);
        String home = new File(dir, "repository").toString();

        RepositoryConfig config = RepositoryConfig.create(xml, home);
        jackrabbitRepo = new TransientRepository(config);

        session = jackrabbitRepo.login(new SimpleCredentials("userid", "".toCharArray()));
        Workspace workspace = session.getWorkspace();
        String workspaceName = workspace.getName();

        // add mix:unstructured if needed
        NodeTypeManager ntm = workspace.getNodeTypeManager();
        if (!ntm.hasNodeType(JcrRepository.MIX_UNSTRUCTURED)) {
            InputStream is = getClass().getResourceAsStream(NODETYPES_CND);
            try {
                CndImporter.registerNodeTypes(new InputStreamReader(is), session);
            } finally {
                is.close();
            }
        }

        // create root folder for tests
        Node testRoot = session.getRootNode().addNode("testroot", JcrConstants.NT_FOLDER);
        testRoot.addMixin(JcrRepository.MIX_UNSTRUCTURED);
        testRoot.setProperty(Property.TYPE_ID, JcrRepository.ROOT_TYPE_ID);
        testRoot.setProperty(Property.BASE_TYPE_ID, BaseType.FOLDER.getId());
        session.save();
        String rootNodeId = testRoot.getIdentifier();

        expectedRepositoryId = "Jackrabbit";
        expectedRepositoryName = "Jackrabbit";
        expectedRepositoryDescription = "Jackrabbit";
        expectedRepositoryVendor = "Apache Software Foundation";
        expectedRepositoryProductName = "Jackrabbit";
        expectedRepositoryProductVersion = "2.0.0";
        expectedCapabilityHasGetDescendants = false;
        expectedCapabilityHasGetFolderTree = false;
        expectedCapabilityHasMultifiling = true;
        expectedCapabilityQuery = CapabilityQuery.BOTH_SEPARATE;
        expectedCapabilityHasUnfiling = true;
        rootFolderName = "testroot";

        PropertyDefinition p1 = new SimplePropertyDefinition("title",
                "def:title", null, "title", "Title", "", false,
                PropertyType.STRING, false, null, false, false, "(no title)",
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
        SimpleType ft = new SimpleType("fold", BaseType.FOLDER.getId(), "fold",
                null, "Fold", "My Folder Type", BaseType.FOLDER, "", true,
                true, true, true, true, true, false, false,
                ContentStreamPresence.NOT_ALLOWED, null, null, Arrays.asList(
                        p1, p2));

        JcrRepository repo = new JcrRepository(jackrabbitRepo, Arrays.asList(dt, ft));
        repo.setRootNodeId(rootNodeId);
        repo.setWorkspace(workspaceName);
        repo.setCredentials(new SimpleCredentials("admin", "admin".toCharArray()));
        BasicHelper.populateRepository(repo);
        return repo;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        session.logout();
        jackrabbitRepo.shutdown();
    }

}
