CMIS AtomPub Tck
----------------

The CMIS AtomPub Tck provides a series of tests that exercise a provider of the CMIS AtomPub binding.

Tests are implemented as a series of JUnit test cases.

Maven is used to execute the tests.

Tests are split into the following categories...

org.apache.chemistry.tck.atompub.test.schema: for testing CMIS schema and example files
org.apache.chemistry.tck.atompub.test.spec: for testing compliance with CMIS specification
  AllowableActionsTest
  ContentStreamTest
  CreateTest
  DeleteTest
  FolderChildrenTest
  FolderHierarchyTest
  GetTest
  QueryPagingTest
  QueryTest
  RepositoryServiceTest
  TypeDefinitionTest
  UpdateTest
  VersionsTest
org.apache.chemistry.tck.atompub.test.custom: for testing with custom type definitions

By default, the following tests are disabled...

org.apache.chemistry.tck.atompub.test.custom  (just a reflection of the current implementation)

All of the above tests are added to the following test suite:

org.apache.chemistry.tck.test.TCKTestSuite

 
CMIS AtomPub Tck Basic Usage Instructions
-----------------------------------------

Tests are executed with:

mvn test -p<provider>

where:

<provider> is the id of a maven profile which provides provider connection information

To create a profile:

1) Edit pom.xml  (in root directory of tck)
2) Copy example <profile> section
3) Edit the <id> value to represent your provider
4) Edit chemistry.tck.serviceUrl for your provider
5) Edit chemistry.tck.user and chemistry.tck.password properties for your provider
   (note: username and password may be removed if authentication is not required)
6) Edit other chemistry.tck.xxx settings as to your requirements

By default, all tests are executed.


Tck Test Results
----------------

Test results are found in /target/surefire-reports sub-directory.

Each test outputs the following files...

org.apache.chemistry.tck.atompub.test.TCKTestSuiteTest.txt : summary of test results
TEST-org.apache.chemistry.tck.atompub.test.TCKTestSuiteTest.txt : detailed report of test results and test environment
org.apache.chemistry.tck.atompub.test.TCKTestSuiteTest-output.txt : trace of all CMIS requests and responses


Executing Tck JUnit Tests
-------------------------

Individual Tck JUnit tests may be executed in any environment (e.g. Eclipse). They must be 
provided with appropriate Tck Options for successful execution.

Each Tck Option can be set as a system property e.g.

-Dchemistry.tck.serviceUrl=http://localhost:8080/cmis


Tck Runner
----------

The class...

org.apache.chemistry.tck.atompub.tools.TCKRunner

provides a programmatic front-end to the TCK tests. TCK options may be pulled from a custom
location and TCK output may be redirected to a custom destination.

This allows for example, a HTML form front-end to the TCK tests.


Tck Options
-----------

The TCK supports the following options:

chemistry.tck.serviceUrl : url to the repository service document
chemistry.tck.user : username to authenticate with
chemistry.tck.password : password to authenticate with
chemistry.tck.tests (*=default) : test name pattern identifying which tests to execute. * is wildcard.
chemistry.tck.validate (true=default|false) : true => validate responses against CMIS XSD
chemistry.tck.failOnValidationError (true|false=default) : true => fail on validation error
chemistry.tck.traceRequests (true|false=default) : true => trace requests and responses
chemistry.tck.deleteTestFixture (true=default|false) : true => remove test data at end of test


Tck Reporting instructions
--------------------------

The easiest way for having a fully comprehensive visual report of the tests run is to generate and run the maven site on the fly (already configured to produce the surefire report). This can be achieved with:

mvn test [-Pprovider] site:run

and the you should point your browser to :

http://localhost:8081/

to see the tests result.

NB: In order not to have port clashes with the possibly locally running server on port 8080, the site plugin has been configured to run on port 8081.
