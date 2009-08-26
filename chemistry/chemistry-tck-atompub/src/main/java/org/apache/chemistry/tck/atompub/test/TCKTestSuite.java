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
 *     David Caruana, Alfresco
 */
package org.apache.chemistry.tck.atompub.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.chemistry.tck.atompub.test.spec.AllowableActionsTest;
import org.apache.chemistry.tck.atompub.test.spec.ContentStreamTest;
import org.apache.chemistry.tck.atompub.test.spec.CreateTest;
import org.apache.chemistry.tck.atompub.test.spec.DeleteTest;
import org.apache.chemistry.tck.atompub.test.spec.FolderChildrenTest;
import org.apache.chemistry.tck.atompub.test.spec.FolderHierarchyTest;
import org.apache.chemistry.tck.atompub.test.spec.GetTest;
import org.apache.chemistry.tck.atompub.test.spec.QueryPagingTest;
import org.apache.chemistry.tck.atompub.test.spec.QueryTest;
import org.apache.chemistry.tck.atompub.test.spec.RepositoryServiceTest;
import org.apache.chemistry.tck.atompub.test.spec.TypeDefinitionTest;
import org.apache.chemistry.tck.atompub.test.spec.UpdateTest;
import org.apache.chemistry.tck.atompub.test.spec.VersionsTest;

/**
 * Suite of all TCK Tests
 */
public class TCKTestSuite extends TestCase {

    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        
        // spec tests
        suite.addTestSuite(RepositoryServiceTest.class);
        suite.addTestSuite(TypeDefinitionTest.class);
        suite.addTestSuite(CreateTest.class);
        suite.addTestSuite(GetTest.class);
        suite.addTestSuite(DeleteTest.class);
        suite.addTestSuite(FolderChildrenTest.class);
        suite.addTestSuite(FolderHierarchyTest.class);
        suite.addTestSuite(ContentStreamTest.class);
        suite.addTestSuite(UpdateTest.class);
        suite.addTestSuite(QueryTest.class);
        suite.addTestSuite(QueryPagingTest.class);
        suite.addTestSuite(AllowableActionsTest.class);
        suite.addTestSuite(VersionsTest.class);
        
        // custom type tests
        // TODO: when mechanism for registering custom types is done
        
        return suite;
    }

}
