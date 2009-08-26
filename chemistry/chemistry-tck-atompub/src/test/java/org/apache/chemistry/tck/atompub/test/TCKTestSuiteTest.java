package org.apache.chemistry.tck.atompub.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.chemistry.tck.atompub.tools.TCKRunner;

/**
 * TCK Test
 */
public class TCKTestSuiteTest extends TestSuite {

    public static Test suite()
    {
        TCKRunner runner = new TCKRunner();
        return runner.getTests();
    }
}
