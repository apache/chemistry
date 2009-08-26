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
package org.apache.chemistry.tck.atompub;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;


public class TCKTestResult extends TestResult {

    private final TestResult orig;
    private List<TestFailure> skips;
    private final TCKMessageWriter messageWriter;

    
    public TCKTestResult(TestResult orig, TCKMessageWriter messageWriter) {
        this.orig = orig;
        this.messageWriter = messageWriter;
        this.skips = new ArrayList<TestFailure>();
    }

    @Override
    public synchronized void addError(Test test, Throwable throwable) {
        if (throwable instanceof TCKSkipCapabilityException) {
            if (messageWriter != null) {
                messageWriter.warn("Skipped Test: " + throwable.getMessage());
            }
            if (orig instanceof TCKTestResult) {
                ((TCKTestResult)orig).addSkip(test, (TCKSkipCapabilityException)throwable);
            }
        } else {
            orig.addError(test, throwable);
        }
    }

    public synchronized void addSkip(Test test, TCKSkipCapabilityException skipException) {
        skips.add(new TestFailure(test, skipException));
    }
    
    @Override
    public synchronized void addFailure(Test test, AssertionFailedError assertionFailedError) {
        orig.addFailure(test, assertionFailedError);
    }
    
    @Override
    public synchronized void addListener(TestListener testListener) {
        orig.addListener(testListener);
    }

    @Override
    public synchronized void removeListener(TestListener testListener) {
        orig.removeListener(testListener);
    }

    @Override
    public void endTest(Test test) {
        orig.endTest(test);
    }

    @Override
    public synchronized int errorCount() {
        return orig.errorCount();
    }

    @Override
    public synchronized Enumeration<TestFailure> errors() {
        return orig.errors();
    }

    @Override
    public synchronized int failureCount() {
        return orig.failureCount();
    }

    public synchronized int skipCount() {
        return skips.size();
    }
    
    @Override
    public synchronized Enumeration<TestFailure> failures() {
        return orig.failures();
    }

    @Override
    public synchronized int runCount() {
        return orig.runCount();
    }

    @Override
    public synchronized boolean shouldStop() {
        return orig.shouldStop();
    }

    @Override
    public void startTest(Test test) {
        orig.startTest(test);
    }

    @Override
    public synchronized void stop() {
        orig.stop();
    }

    @Override
    public synchronized boolean wasSuccessful() {
        return orig.wasSuccessful();
    }
}
