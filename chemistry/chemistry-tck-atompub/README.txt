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
5) Edit chemistry.tck.username and chemistry.tck.password properties for your provider
   (note: username and password may be removed if authentication is not required)

By default, all tests are executed.

Individual tests are executed with:

mvn test -p<provider> -Dtest=<comma separated list of tests>

e.g.

mvn test -p<provider> -Dtest=RepositoryTest


Tck Test Results
----------------

Test results are found in /target/surefire-reports sub-directory.

Each test outputs the following files...

org.apache.chemistry.tck.atompub.test.xxx.XXXTest.txt : summary of test results
TEST-org.apache.chemistry.tck.atompub.test.spec.CMISTest.txt : detailed report of test results and test environment
org.apache.chemistry.tck.atompub.test.xxx.XXXTest-output.txt : trace of all CMIS requests and responses


Tck Advanced Configuration
--------------------------

The following properties may also be set in the profile:

chemistry.tck.validate (true=default|false) : true => validate responses against CMIS XSD
chemistry.tck.failOnValidationError (true|false=default) : true => fail on validation error
chemistry.tck.traceRequests (true=default|false) : true => trace requests and responses
chemistry.tck.deleteTestFixture (true=default|false) : true => remove test data at end of test


Tck Reporting instructions
--------------------------

The easiest way for having a fully comprehensive visual report of the tests run is to generate and run the maven site on the fly (already configured to produce the surefire report). This can be achieved with:

mvn test [-Pprovider] site:run

and the you should point your browser to :

http://localhost:8081/

to see the tests result.

NB: In order not to have port clashes with the possibly locally running server on port 8080, the site plugin has been configured to run on port 8081.
