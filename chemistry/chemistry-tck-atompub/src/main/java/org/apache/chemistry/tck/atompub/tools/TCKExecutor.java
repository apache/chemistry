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
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.tck.atompub.tools;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.chemistry.tck.atompub.TCKLogger;
import org.apache.chemistry.tck.atompub.TCKOptions;
import org.apache.chemistry.tck.atompub.TCKTest;


//
// TODO: Revisit this class...
//


/**
 * Front end for executing Tests
 */
public class TCKExecutor {

    private static class Options implements TCKOptions {
        private String serviceUrl = null;
        private String user = "admin";
        private String password = "admin";
        private Boolean validate = true;
        private Boolean failOnValidationError = false;
        private Boolean traceRequests = true;
        private Boolean deleteTestFixture = true;

        public String getServiceUrl() {
            return serviceUrl;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public Boolean getValidate() {
            return validate;
        }

        public Boolean getFailOnValidationError() {
            return failOnValidationError;
        }

        public Boolean getTraceRequests() {
            return traceRequests;
        }

        public Boolean getDeleteTestFixture() {
            return deleteTestFixture;
        }

        public String getConnectionFactory() {
            return null;
        }

    }

    private Options options = new Options();
    private Class<? extends TCKTest>[] testClasses = new Class[] { };
    private String match = null;

    public void setTestClasses(Class<? extends TCKTest>[] testClasses) {
        this.testClasses = testClasses;
    }

    /**
     * @param match  test name to execute (* for wildcards)
     */
    public void setMatch(String match) {
        this.match = match;
    }

    /**
     * @param serviceUrl  cmis service document url
     */
    public void setServiceUrl(String serviceUrl) {
        options.serviceUrl = serviceUrl;
    }

    /**
     * @param user
     */
    public void setUser(String user) {
        options.user = user;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        options.password = password;
    }

    /**
     * @param validateResponse  true => test response against CMIS XSDs
     */
    public void setValidate(boolean validate) {
        options.validate = validate;
    }

    public void setFailOnValidationError(boolean failOnValidationError) {
        options.failOnValidationError = failOnValidationError;
    }

    /**
     * @param traceReqRes  true => trace requests / responses
     */
    public void setTraceRequests(boolean traceRequests) {
        options.traceRequests = traceRequests;
    }

    public void setDeleteTestFixture(boolean deleteTestFixture) {
        options.deleteTestFixture = deleteTestFixture;
    }

    /**
     * Gets the names of CMIS tests
     *
     * @param match
     * @return array of test names
     */
    public String[] getTestNames(String match) {
        List<String> namesList = new ArrayList<String>();
        for (Class<? extends TCKTest> testClass : testClasses) {
            TestSuite suite = new TestSuite(testClass);
            for (int i = 0; i < suite.countTestCases(); i++) {
                TCKTest test = (TCKTest) suite.testAt(i);
                if (match == null || match.equals("*") || test.getName().matches(match.replace("*", "[A-Za-z0-9]*"))) {
                    namesList.add(test.getName());
                }
            }
        }
        String[] names = new String[namesList.size()];
        namesList.toArray(names);
        return names;
    }

    /**
     * Execute CMIS Tests
     */
    public void execute() {
        // dump TCK options
        if (options.traceRequests && TCKLogger.logger.isInfoEnabled()) {
            TCKLogger.logger.info("Service URL: "
                    + (options.getServiceUrl() == null ? "[not set]" : options.getServiceUrl()));
            TCKLogger.logger.info("User: " + (options.getUser() == null ? "[not set]" : options.getUser()));
            TCKLogger.logger.info("Password: "
                    + (options.getPassword() == null ? "[not set]" : options.getPassword()));
            TCKLogger.logger.info("Validate: " + options.getValidate());
            TCKLogger.logger.info("Fail on Validation Error: " + options.getFailOnValidationError());
            TCKLogger.logger.info("Trace Requests: " + options.getTraceRequests());
            TCKLogger.logger.info("Tests: " + (match == null ? "*" : match));
        }

        executeSuite(match, options);
    }

    /**
     * Execute suite of CMIS Tests
     */
    private void executeSuite(String match, TCKOptions options) {
        TestSuite allsuite = new TestSuite();
        for (Class<? extends TCKTest> testClass : testClasses) {
            TestSuite suite = new TestSuite(testClass);
            for (int i = 0; i < suite.countTestCases(); i++) {
                TCKTest test = (TCKTest) suite.testAt(i);
                if (match == null || match.equals("*") || test.getName().matches(match.replace("*", "[A-Za-z0-9]*"))) {
                    test.setOptions(options);
                    allsuite.addTest(test);
                }
            }
        }
        TestResult result = new TestResult();
        //allsuite.run(result);
        TestRunner runner = new TestRunner();
        runner.doRun(allsuite);
    }

    /**
     * Execute CMIS Tests from command-line
     *
     * url={serviceUrl} user={user} password={password}
     */
    public static void main(String[] args) {
        TCKExecutor runner = new TCKExecutor();

        for (String arg : args) {
            String[] argSegment = arg.split("=");
            if (argSegment[0].equals("url")) {
                runner.setServiceUrl(argSegment[1]);
            } else if (argSegment[0].equals("user")) {
                runner.setUser(argSegment[1]);
            } else if (argSegment[0].equals("password")) {
                runner.setPassword(argSegment[1]);
            }
        }

        // execute
        runner.execute();
        System.exit(0);
    }

}
