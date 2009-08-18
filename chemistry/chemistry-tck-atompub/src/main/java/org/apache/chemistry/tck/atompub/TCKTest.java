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

import org.apache.chemistry.tck.atompub.client.CMISAppModel;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.CMISTestFixture;
import org.apache.chemistry.tck.atompub.http.Connection;
import org.apache.chemistry.tck.atompub.http.ConnectionFactory;
import org.apache.chemistry.tck.atompub.http.httpclient.HttpClientConnectionFactory;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;
import org.apache.chemistry.tck.atompub.utils.SystemPropertyOptions;

/**
 * Base Test Class for TCK Tests
 */
public class TCKTest extends TestCase {

    protected TCKOptions options = null;
    protected CMISClient client;
    protected CMISAppModel model;
    protected CMISTestFixture fixture;
    protected ResourceLoader templates;

    public void setOptions(TCKOptions properties) {
        this.options = properties;
    }

    @Override
    public void setUp() {
        // construct TCK properties
        if (options == null)
            options = new SystemPropertyOptions();

        // construct connection to server
        // TODO: allow configuration of different connection factory
        ConnectionFactory connFactory = new HttpClientConnectionFactory();
        String user = options.getUser();
        String password = options.getPassword();
        Connection connection = (user == null) ? connFactory.createConnection() : connFactory.createConnection(user,
                password);

        // construct CMIS test client
        String url = options.getServiceUrl();
        if (url == null)
            fail("CMIS Service URL not specified");
        client = new CMISClient(connection, url);
        Boolean validate = (options.getValidate() == null) ? true : options.getValidate();
        client.setValidate(validate);
        Boolean failOnValidationError = (options.getFailOnValidationError() == null) ? false : options
                .getFailOnValidationError();
        client.setFailOnValidationError(failOnValidationError);
        Boolean trace = (options.getTraceRequests() == null) ? true : options.getTraceRequests();
        client.setTrace(trace);

        // construct model helper
        model = new CMISAppModel();

        // construct test templates
        templates = new ResourceLoader("/org/apache/chemistry/tck/atompub/templates/");

        // construct test fixture
        fixture = new CMISTestFixture(client, getName());

        if (TCKLogger.logger.isInfoEnabled()) {
            TCKLogger.logger.info("Start Test: " + getClass().getName() + "." + getName());
            TCKLogger.logger.info("Service URL: " + url);
            TCKLogger.logger.info("User: " + user);
            TCKLogger.logger.info("Password: " + password);
            TCKLogger.logger.info("Validate: " + validate);
            TCKLogger.logger.info("Fail on Validation Error: " + failOnValidationError);
            TCKLogger.logger.info("Trace Requests: " + trace);
        }
    }

    @Override
    public void tearDown() throws Exception {
        Boolean delete = (options.getDeleteTestFixture() == null) ? true : options.getDeleteTestFixture();
        if (delete)
            fixture.delete();

        if (TCKLogger.logger.isInfoEnabled())
            TCKLogger.logger.info("End Test: " + getClass().getName() + "." + getName());
    }

    public void skipTest(String reason) {
        if (TCKLogger.logger.isInfoEnabled())
            TCKLogger.logger.info("Skiped Test: " + getClass().getName() + "." + getName() + ": " + reason);
    }
}
