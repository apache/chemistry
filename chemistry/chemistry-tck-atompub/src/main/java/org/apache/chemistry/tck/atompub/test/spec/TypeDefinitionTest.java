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

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISTypeDefinition;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.junit.Assert;


/**
 * Type Definition Tests
 * 
 */
public class TypeDefinitionTest extends TCKTest {

    public void testGetTypeDefinitionsBase() throws Exception {
        IRI typesHREF = client.getTypesChildrenCollection(client.getWorkspace());
        Feed types = client.getFeed(typesHREF);
        Assert.assertNotNull(types);
        Feed typesWithProps = client.getFeed(typesHREF);
        Assert.assertNotNull(typesWithProps);
        for (Entry type : types.getEntries()) {
            Entry retrievedType = client.getEntry(type.getSelfLink().getHref());
            Assert.assertEquals(type.getId(), retrievedType.getId());
            Assert.assertEquals(type.getTitle(), retrievedType.getTitle());
            // TODO: type specific properties - extension to abdera
        }
    }

    public void testGetTypeDefinitionsChild() throws Exception {
        IRI typesHREF = client.getTypesChildrenCollection(client.getWorkspace());
        Map<String, String> args = new HashMap<String, String>();
        args.put("type", "folder");
        args.put("includePropertyDefinitions", "true");
        args.put("maxItems", "5");
        while (typesHREF != null) {
            Feed types = client.getFeed(typesHREF, args);

            for (Entry type : types.getEntries()) {
                Entry retrievedType = client.getEntry(type.getSelfLink().getHref());
                Assert.assertEquals(type.getId(), retrievedType.getId());
                Assert.assertEquals(type.getTitle(), retrievedType.getTitle());
                // TODO: type specific properties - extension to Abdera
            }

            // next page
            Link nextLink = types.getLink("next");
            typesHREF = (nextLink != null) ? nextLink.getHref() : null;
            args.remove("maxItems");
        }
        ;
    }

    public void testGetTypeDefinition() throws Exception {
        // create document
        Entry document = fixture.createTestDocument("testGetEntryTypeDefinitionDoc");
        Assert.assertNotNull(document);
        // create folder
        Entry folder = fixture.createTestFolder("testGetEntryTypeDefinitionFolder");
        Assert.assertNotNull(folder);

        // retrieve children
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        Feed children = client.getFeed(childrenLink.getHref());
        for (Entry entry : children.getEntries()) {
            CMISObject entryObject = entry.getExtension(CMISConstants.OBJECT);
            Assert.assertNotNull(entryObject);
            // get type definition
            Link typeLink = entry.getLink(CMISConstants.REL_DESCRIBED_BY);
            Assert.assertNotNull(typeLink);
            Entry type = client.getEntry(typeLink.getHref());
            Assert.assertNotNull(type);
            CMISTypeDefinition entryType = type.getExtension(CMISConstants.TYPE_DEFINITION);
            Assert.assertNotNull(entryType);
            assertEquals(entryObject.getObjectTypeId().getStringValue(), entryType.getId());
        }
    }
    
    public void testGetTypeDefinitionById() throws Exception {
        // construct uri for cmis:document type
        CMISUriTemplate typeByIdTemplate = client.getTypeByIdUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("id", "cmis:document");
        IRI typeByIdRequest = typeByIdTemplate.generateUri(variables);
        
        // get type definition
        Entry typeById = client.getEntry(typeByIdRequest);
        Assert.assertNotNull(typeById);
        CMISTypeDefinition typeDef = typeById.getExtension(CMISConstants.TYPE_DEFINITION);
        Assert.assertNotNull(typeDef);
        Assert.assertEquals("cmis:document", typeDef.getId());
    }
    
}
