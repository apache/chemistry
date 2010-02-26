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
package org.apache.chemistry.tck.atompub.test.spec;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Service;
import org.apache.chemistry.abdera.ext.CMISCapabilities;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.junit.Assert;


/**
 * CMIS Repository Service Document Tests
 */
public class RepositoryServiceTest extends TCKTest {
    
    public void testRepository() throws Exception {
        Service service = client.getRepository();
        Assert.assertNotNull(service);
    }

    public void testGetRootCollection() throws Exception {
        IRI rootHREF = client.getRootCollection(client.getWorkspace());
        client.executeRequest(new GetRequest(rootHREF.toString()), 200);
    }

    public void testObjectByIdURITemplate() throws Exception {
        CMISUriTemplate uriTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
        assertNotNull(uriTemplate);
        String template = uriTemplate.getTemplate();
        assertNotNull(template);
        assertTemplateVariables(template, new String[] { "id", "filter", "includeAllowableActions",
                "includePolicyIds", "includeRelationships", "includeACL", "renditionFilter" });
    }

    public void testObjectByPathURITemplate() throws Exception {
        CMISUriTemplate uriTemplate = client.getObjectByPathUriTemplate(client.getWorkspace());
        assertNotNull(uriTemplate);
        String template = uriTemplate.getTemplate();
        assertNotNull(template);
        assertTemplateVariables(template, new String[] { "path", "filter", "includeAllowableActions",
                "includePolicyIds", "includeRelationships", "includeACL", "renditionFilter" });
    }

    public void testTypeByIdURITemplate() throws Exception {
        CMISUriTemplate uriTemplate = client.getTypeByIdUriTemplate(client.getWorkspace());
        assertNotNull(uriTemplate);
        String template = uriTemplate.getTemplate();
        assertNotNull(template);
        assertTemplateVariables(template, new String[] { "id" });
    }

    public void testQueryURITemplate() throws Exception {
        CMISCapabilities capabilities = client.getCapabilities();
        String capability = capabilities.getQuery();
        assertNotNull(capability);
        if (capability.equals("none"))
            throw new TCKSkipCapabilityException("query", "metadataonly or fulltextonly or bothseparate or bothcombined", capability);

        CMISUriTemplate uriTemplate = client.getQueryUriTemplate(client.getWorkspace());
        assertNotNull(uriTemplate);
        String template = uriTemplate.getTemplate();
        assertNotNull(template);
        assertTemplateVariables(template, new String[] { "q", "searchAllVersions", "maxItems", "skipCount",
                "includeAllowableActions", "includeRelationships"});
    }

    private void assertTemplateVariables(String template, String[] variables) {
        for (String variable : variables)
        {
            assertTrue("template " + template + " contains variable " + variable, template.contains("{" + variable + "}"));
        }
    }
}
