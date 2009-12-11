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

import java.io.File;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.Repository;
import org.apache.chemistry.jcr.JcrRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.TransientRepository;

/**
 * Test on a Jackrabbit repository.
 */
public class TestJcrRepository extends BasicTestCase {

    private static final String DIRECTORY = "target/test/jackrabbit";

    protected JackrabbitRepository jackrabbitRepo;

    @Override
    public Repository makeRepository() throws Exception {
        File dir = new File(DIRECTORY);
        FileUtils.deleteDirectory(dir);
        dir.mkdirs();
        String config = new File(dir, "repository.xml").toString();
        String home = new File(dir, "repository").toString();

        jackrabbitRepo = new TransientRepository(config, home);
        Session session = jackrabbitRepo.login(new SimpleCredentials("userid",
                "".toCharArray()));
        String workspaceName = session.getWorkspace().getName();

        expectedRepositoryId = "Jackrabbit";
        expectedRepositoryName = "Jackrabbit";
        expectedRepositoryDescription = "Jackrabbit";
        expectedRepositoryVendor = "Apache Software Foundation";
        expectedRepositoryProductName = "Jackrabbit";
        expectedRepositoryProductVersion = "1.6.0";
        expectedCapabilityHasGetDescendants = false;
        expectedCapabilityHasMultifiling = true;
        expectedCapabilityQuery = CapabilityQuery.BOTH_SEPARATE;
        expectedCapabilityHasUnfiling = true;
        expectedRootTypeId = "rep:root";
        Repository repo = new JcrRepository(jackrabbitRepo, workspaceName);
        BasicHelper.populateRepository(repo);
        return repo;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        jackrabbitRepo.shutdown();
    }

}
