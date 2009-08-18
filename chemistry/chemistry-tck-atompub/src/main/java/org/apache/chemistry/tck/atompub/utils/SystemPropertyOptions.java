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
package org.apache.chemistry.tck.atompub.utils;

import org.apache.chemistry.tck.atompub.TCKOptions;


/**
 * TCK Options sourced from System Properties
 */
public class SystemPropertyOptions implements TCKOptions {

    private final static String PROP_SERVICE_URL = "chemistry.tck.serviceUrl";
    private final static String PROP_USER = "chemistry.tck.user";
    private final static String PROP_PASSWORD = "chemistry.tck.password";
    private final static String PROP_VALIDATE = "chemistry.tck.validate";
    private final static String PROP_FAIL_ON_VALIDATION_ERROR = "chemistry.tck.failOnValidationError";
    private final static String PROP_TRACE_REQUESTS = "chemistry.tck.traceRequests";
    private final static String PROP_DELETE_TEST_FIXTURE = "chemistry.tck.deleteTestFixture";

    public Boolean getFailOnValidationError() {
        String val = System.getProperty(PROP_FAIL_ON_VALIDATION_ERROR);
        return val != null && val.length() > 0 ? Boolean.valueOf(val) : null;
    }

    public String getPassword() {
        String val = System.getProperty(PROP_PASSWORD);
        return val != null && val.length() > 0 ? val : null;
    }

    public String getServiceUrl() {
        String val = System.getProperty(PROP_SERVICE_URL);
        return val != null && val.length() > 0 ? val : null;
    }

    public Boolean getTraceRequests() {
        String val = System.getProperty(PROP_TRACE_REQUESTS);
        return val != null && val.length() > 0 ? Boolean.valueOf(val) : null;
    }

    public String getUser() {
        String val = System.getProperty(PROP_USER);
        return val != null && val.length() > 0 ? val : null;
    }

    public Boolean getValidate() {
        String val = System.getProperty(PROP_VALIDATE);
        return val != null && val.length() > 0 ? Boolean.valueOf(val) : null;
    }

    public String getConnectionFactory() {
        return null;
    }

    public Boolean getDeleteTestFixture() {
        String val = System.getProperty(PROP_DELETE_TEST_FIXTURE);
        return val != null && val.length() > 0 ? Boolean.valueOf(val) : null;
    }
}
