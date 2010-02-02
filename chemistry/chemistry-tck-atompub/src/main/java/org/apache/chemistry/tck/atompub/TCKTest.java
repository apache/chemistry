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
package org.apache.chemistry.tck.atompub;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.chemistry.tck.atompub.client.CMISAppModel;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.CMISTestFixture;
import org.apache.chemistry.tck.atompub.http.Connection;
import org.apache.chemistry.tck.atompub.http.ConnectionFactory;
import org.apache.chemistry.tck.atompub.http.httpclient.HttpClientConnectionFactory;
import org.apache.chemistry.tck.atompub.utils.LogMessageWriter;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;

/**
 * Base Test Class for TCK Tests
 */
public class TCKTest extends TestCase {

	protected TCKMessageWriter messageWriter = null;
    protected TCKOptions options = null;
    protected CMISClient client;
    protected CMISAppModel model;
    protected CMISTestFixture fixture;
    protected ResourceLoader templates;

    
    public void setMessageWriter(TCKMessageWriter messageWriter) {
    	this.messageWriter = messageWriter;
    }
    
    public void setOptions(TCKOptions options) {
        this.options = options;
    }

    @Override
    public void setUp() {
        // construct TCK properties
        if (options == null)
            options = new TCKOptions(System.getProperties());
        
        // construct TCK message writer
        if (messageWriter == null)
        	messageWriter = new LogMessageWriter();

        // construct connection to server
        // TODO: allow configuration of different connection factory
        ConnectionFactory connFactory = new HttpClientConnectionFactory();
        String user = options.getUsername();
        String password = options.getPassword();
        Connection connection;
        if (user == null) {
            connection = connFactory.createConnection();
        } else {
            connection = connFactory.createConnection(user, password);
        }
        
        // construct CMIS test client
        String url = options.getServiceUrl();
        if (url == null)
            fail("CMIS Service URL not specified");
        client = new CMISClient(user, connection, url, messageWriter);
        boolean validate = options.getValidate();
        client.setValidate(validate);
        boolean failOnValidationError = options.getFailOnValidationError();
        client.setFailOnValidationError(failOnValidationError);
        boolean traceRequests = options.getTraceRequests();
        client.setTrace(traceRequests);

        // construct model helper
        model = new CMISAppModel();

        // construct test templates
        templates = new ResourceLoader("/org/apache/chemistry/tck/atompub/templates/");

        // construct test fixture
        fixture = new CMISTestFixture(client, getName());

        messageWriter.info("Start Test: " + getClass().getName() + "." + getName());
        messageWriter.info("Service URL: " + url);
        messageWriter.info("User: " + user);
        messageWriter.info("Password: " + password);
        messageWriter.info("Validate: " + validate);
        messageWriter.info("Fail on Validation Error: " + failOnValidationError);
        messageWriter.info("Trace Requests: " + traceRequests);
    }

    @Override
    public void run(TestResult result) {
        super.run(new TCKTestResult(result, messageWriter));
    }

	@Override
    public void tearDown() throws Exception {
        if (options.getDeleteTestFixture()) {
            fixture.delete();
        } else {
        	messageWriter.warn("Kept Test Data: " + getClass().getName() + "." + getName());
        }

        messageWriter.info("End Test: " + getClass().getName() + "." + getName());
    }
}
