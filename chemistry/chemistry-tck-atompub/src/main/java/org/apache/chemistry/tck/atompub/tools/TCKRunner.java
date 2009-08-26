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

import java.util.Enumeration;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;

import org.apache.chemistry.tck.atompub.TCKMessageWriter;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.TCKTestResult;
import org.apache.chemistry.tck.atompub.utils.LogMessageWriter;


/**
 * Test Runner for AtomPub TCK
 */
public class TCKRunner extends BaseTestRunner {

    private TCKRunnerOptions options;
    private TCKMessageWriter messageWriter;
    private TestSuite testSuite;

    /**
     * Construct
     */
    public TCKRunner() {
        this(new LogMessageWriter());
    }

    /**
     * Construct
     * 
     * @param messageWriter  message writer for all output
     */
    public TCKRunner(TCKMessageWriter messageWriter) {
        this(new TCKRunnerOptions(System.getProperties()), messageWriter);
    }
    
    /**
     * Construct
     * 
     * @param options  TCK runner options
     */
    public TCKRunner(TCKRunnerOptions options) {
        this(options, new LogMessageWriter());
    }

    /**
     * Construct
     * 
     * @param options  TCK runner options
     * @param messageWriter  message writer for all output
     */
    public TCKRunner(TCKRunnerOptions options, TCKMessageWriter messageWriter) {
        String testSuiteName = options.getTestSuiteName();
        Test test = getTest(testSuiteName);
        if (test == null) {
            throw new IllegalArgumentException("Failed to create Test Suite " + testSuiteName);
        }
        if (!(test instanceof TestSuite)) {
            throw new IllegalArgumentException("Test suite " + testSuiteName + " is not a TestSuite");
        }
        this.options = options;
        this.testSuite = (TestSuite) test;
        this.messageWriter = messageWriter;
    }

    /**
     * @return  options
     */
    public TCKRunnerOptions getOptions() {
        return options;
    }
    /**
     * @return  array of all available test names
     */
    public String[] getTestNames() {
        return getFilteredTestNames(options.getTestFilter());
    }

    /**
     * @param  filter  
     * @return  array of test names filtered by filter 
     */
    public String[] getFilteredTestNames(String filter) {
        TestSuite all = flattenTestSuite(testSuite, filter);
        String[] names = new String[all.testCount()];
        for (int i = 0; i < all.testCount(); i++) {
            Test test = all.testAt(i);
            names[i] = getTestName(test);
        }
        return names;
    }

    /**
     * @return  test suite containing flatten list of all tests
     */
    public TestSuite getTests() {
        TestSuite all = flattenTestSuite(testSuite, options.getTestFilter());
        return all;
    }

    /**
     * Execute TCK tests
     * @param properties  configuration of TCK tests (see TCKOptions)
     */
    public void execute(Properties properties) {
        // flatten tests and filter according to tests mask
        String filter = options.getTestFilter();
        TestSuite suite = flattenTestSuite((TestSuite) testSuite, filter);
        for (int i = 0; i < suite.testCount(); i++) {
            Test test = suite.testAt(i);
            if (test instanceof TCKTest) {
                ((TCKTest) test).setMessageWriter(messageWriter);
                ((TCKTest) test).setOptions(options);
            }
        }

        // execute the suite
        messageWriter.info("Executing tests: " + filter);
        TCKTestResult testResult = new TCKTestResult(new TestResult(), null);
        testResult.addListener(this);
        suite.run(testResult);

        // print summary
        printErrors(testResult);
        printFailures(testResult);
        printFooter(testResult);
    }
    
    @Override
    protected void runFailed(String message) {
        messageWriter.info(message);
    }

    @Override
    public void testEnded(String testName) {
    }

    @Override
    public void testFailed(int status, Test test, Throwable t) {
        messageWriter.info("Failed: " + test.toString() + " , Error: " + t.getMessage());
    }

    @Override
    public void testStarted(String testName) {
    }

    /**
     * Flatten a hierarchy of test suites into a list of tests
     * 
     * @param suite  test suite to flatten
     * @param filter  mask (name) to filter out tests 
     * @return  test suite of flatten tests
     */
    private TestSuite flattenTestSuite(TestSuite suite, String filter) {
        FilterVisitor visitor = new FilterVisitor(filter);
        visitTests(suite, visitor);
        return visitor.getSuite();

    }

    /**
     * @param test
     * @return  test name
     */
    private String getTestName(Test test) {
        return test.getClass().getSimpleName() + "." + ((TestCase) test).getName();
    }

    /**
     * Visit each test in test suite hierarchy
     * 
     * @param test  test suite or test case
     * @param visitor  callback
     */
    private void visitTests(Test test, TestVisitor visitor) {
        if (test instanceof TestCase) {
            visitor.visitTest(test);
        } else if (test instanceof TestSuite) {
            TestSuite suite = (TestSuite) test;
            for (int i = 0; i < suite.testCount(); i++) {
                visitTests(suite.testAt(i), visitor);
            }
        }
    }

    /**
     * Test visitor callback
     */
    private interface TestVisitor {
        public void visitTest(Test test);
    }

    /**
     * Visitor that flattens and filters test suite 
     */
    private class FilterVisitor implements TestVisitor {
        private String filter;
        private TestSuite suite;

        public FilterVisitor(String filter) {
            this.filter = filter;
            this.suite = new TestSuite();
        }

        public void visitTest(Test test) {
            if (filter == null || filter.equals(TCKRunnerOptions.FILTER_WILDCARD)
                    || getTestName(test).matches(filter.replace(TCKRunnerOptions.FILTER_WILDCARD, "[A-Za-z0-9]*"))) {
                suite.addTest(test);
            }
        }

        public TestSuite getSuite() {
            return suite;
        }
    }
    
    protected void printErrors(TCKTestResult result) {
        printDefects(result.errors(), result.errorCount(), "error");
    }

    protected void printFailures(TCKTestResult result) {
        printDefects(result.failures(), result.failureCount(), "failure");
    }

    protected void printDefects(Enumeration<TestFailure> booBoos, int count, String type) {
        if (count == 0)
            return;
        messageWriter.info("");
        if (count == 1)
            messageWriter.info("There was " + count + " " + type + ":");
        else
            messageWriter.info("There were " + count + " " + type + "s:");
        for (int i = 1; booBoos.hasMoreElements(); i++) {
            printDefect(booBoos.nextElement(), i);
        }
    }

    public void printDefect(TestFailure booBoo, int count) {
        printDefectHeader(booBoo, count);
        printDefectTrace(booBoo);
    }

    protected void printDefectHeader(TestFailure booBoo, int count) {
        messageWriter.info(count + ") " + booBoo.failedTest());
    }

    protected void printDefectTrace(TestFailure booBoo) {
        messageWriter.info(BaseTestRunner.getFilteredTrace(booBoo.trace()));
    }

    protected void printFooter(TCKTestResult result) {
        if (result.wasSuccessful()) {
            messageWriter.info("");
            messageWriter.info("OK");
            messageWriter.info(result.runCount() + " successful test" + (result.runCount() == 1 ? "" : "s"));
            messageWriter.info(result.skipCount() + " skipped test" + (result.skipCount() == 1 ? "" : "s"));
        } else {
            messageWriter.info("");
            messageWriter.info("FAILURES!!!");
            messageWriter.info("Tests run: " + result.runCount());
            messageWriter.info("Failures: " + result.failureCount());
            messageWriter.info("Errors: " + result.errorCount());
        }
    }

    /**
     * Main entry point
     * 
     * @param args (see TCKOptions)
     */
    public static void main(String args[]) {
        // build properties from arguments
        Properties properties = new Properties();
        for (String arg : args) {
            String[] argComponents = arg.split("=");
            properties.setProperty(argComponents[0], argComponents[1]);
        }

        // construct and start runner
        TCKRunner runner = new TCKRunner(new TCKRunnerOptions(properties), new LogMessageWriter());
        runner.execute(properties);
    }

}