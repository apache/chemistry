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
package org.apache.chemistry.tck.atompub.tools;

import java.util.Properties;

import org.apache.chemistry.tck.atompub.TCKOptions;
import org.apache.chemistry.tck.atompub.test.TCKTestSuite;

public class TCKRunnerOptions extends TCKOptions {

    public static final String FILTER_WILDCARD = "*";
    public static final String TCK_TEST_SUITE = TCKTestSuite.class.getName();

    private static final long serialVersionUID = 4441003106227678366L;
    private final static String PROP_TEST_SUITE = "chemistry.tck.testSuite";
    private final static String PROP_TESTS = "chemistry.tck.tests";
    
    
    public TCKRunnerOptions(Properties properties) {
        super(properties);
    }

    /**
     * @return  test suite name  (default: TCK test suite)
     */
    public String getTestSuiteName() {
        String val = properties.getProperty(PROP_TEST_SUITE);
        return (val == null || val.length() == 0) ? TCK_TEST_SUITE : val;
    }
    
    /**
     * @return  test names to execute  (default: *, empty string = no tests) 
     */
    public String getTestFilter() {
        String val = properties.getProperty(PROP_TESTS);
        return val != null ? val : FILTER_WILDCARD;
    }
    
}
