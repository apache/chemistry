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

import java.io.Serializable;
import java.util.Properties;


/**
 * TCK Options
 */
public class TCKOptions implements Serializable{

    public final static String PROP_SERVICE_URL = "chemistry.tck.serviceUrl";
    public final static String PROP_USERNAME = "chemistry.tck.user";
    public final static String PROP_PASSWORD = "chemistry.tck.password";
    public final static String PROP_VALIDATE = "chemistry.tck.validate";
    public final static String PROP_FAIL_ON_VALIDATION_ERROR = "chemistry.tck.failOnValidationError";
    public final static String PROP_TRACE_REQUESTS = "chemistry.tck.traceRequests";
    public final static String PROP_DELETE_TEST_FIXTURE = "chemistry.tck.deleteTestFixture";
    public final static String PROP_TYPE_FOLDER = "chemistry.tck.type.folder";
    public final static String PROP_TYPE_DOCUMENT = "chemistry.tck.type.document";
    public final static String PROP_TYPE_RELATIONSHIP = "chemistry.tck.type.relationship";

    
    private static final long serialVersionUID = 5805080271712377369L;
    protected Properties properties;
    
    public TCKOptions(Properties properties) {
    	this.properties = properties;
    }

    /**
     * @return  username  (default: null, if not specified, or empty string)
     */
    public String getUsername() {
        String val = properties.getProperty(PROP_USERNAME);
        return val != null && val.length() > 0 ? val : null;
    }

    /**
     * @return  password  (default: null)
     */
    public String getPassword() {
        String val = properties.getProperty(PROP_PASSWORD);
        return val;
    }

    /**
     * @return  serviceUrl  (default: null, if not specified, or empty string)
     */
    public String getServiceUrl() {
        String val = properties.getProperty(PROP_SERVICE_URL);
        return val != null && val.length() > 0 ? val : null;
    }

    /**
     * @return  validate  (default: true)
     */
    public boolean getValidate() {
        String val = properties.getProperty(PROP_VALIDATE, "true");
        return Boolean.valueOf(val);
    }

    /**
     * @return  fail on validation error  (default: false)
     */
    public boolean getFailOnValidationError() {
        String val = properties.getProperty(PROP_FAIL_ON_VALIDATION_ERROR, "false");
        return Boolean.valueOf(val);
    }

    /**
     * @return  trace requests  (default: false)
     */
    public boolean getTraceRequests() {
        String val = properties.getProperty(PROP_TRACE_REQUESTS, "false");
        return Boolean.valueOf(val);
    }

    /**
     * @return  delete fixture data  (default: true)
     */
    public boolean getDeleteTestFixture() {
        String val = properties.getProperty(PROP_DELETE_TEST_FIXTURE, "true");
        return Boolean.valueOf(val);
    }

    /**
     * @return  folder type to create  (default: cmis:folder)
     */
    public String getFolderType() {
        return properties.getProperty(PROP_TYPE_FOLDER, "cmis:folder");
    }

    /**
     * @return  document type to create  (default: cmis:document)
     */
    public String getDocumentType() {
        return properties.getProperty(PROP_TYPE_DOCUMENT, "cmis:document");
    }

    /**
     * @return  relationship type to create  (default: cmis:relationship)
     */
    public String getRelationshipType() {
        return properties.getProperty(PROP_TYPE_RELATIONSHIP, "cmis:relationship");
    }

    public String getConnectionFactory() {
        return null;
    }

}
