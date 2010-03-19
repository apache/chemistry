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
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.DeleteRequest;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;


/**
 * Type Definition Tests
 * 
 */
public class RelationshipsTest extends TCKTest {

    public static final String ARG_INCLUDE_SUB_RELATIONSHIP_TYPES = "includeSubRelationshipTypes";

    
    @Override
    public void setUp() {
        super.setUp();
        
        try {
            // ensure relationships are supported by CMIS provider
            boolean relSupported = false;
            IRI typesHREF = client.getTypesChildrenCollection(client.getWorkspace());
            Feed types = client.getFeed(typesHREF);
            Assert.assertNotNull(types);
            for (Entry type : types.getEntries()) {
                CMISTypeDefinition typeDef = type.getExtension(CMISConstants.TYPE_DEFINITION);
                if (typeDef.getId().equals(CMISConstants.TYPE_RELATIONSHIP)) {
                    relSupported = true;
                    break;
                }
            }
            if (!relSupported) {
                throw new TCKSkipCapabilityException("relationships", "cmis:relationship type", "none");
            }

            // ensure specified relationship exists and is creatable
            CMISUriTemplate typeByIdTemplate = client.getTypeByIdUriTemplate(client.getWorkspace());
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("id", options.getRelationshipType());
            IRI typeByIdRequest = typeByIdTemplate.generateUri(variables);
            Entry typeById = client.getEntry(typeByIdRequest);
            CMISTypeDefinition relType = typeById.getExtension(CMISConstants.TYPE_DEFINITION);
            assertNotNull(relType);
            assertTrue(relType.getCreatable());
        }
        catch(Exception e)
        {
            fail(e.toString());
        }
    }

    public void testCreateRelationship() throws Exception {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        assertNotNull(children);
        Entry source = client.createDocument(children.getSelfLink().getHref(), null, "testSource");
        assertNotNull(source);
        Entry target = client.createDocument(children.getSelfLink().getHref(), null, "testTarget");
        assertNotNull(target);

        // retrieve relationships feed on source
        Link relsLink = source.getLink(CMISConstants.REL_RELATIONSHIPS);
        assertNotNull(relsLink);
        Feed relsBefore = client.getFeed(relsLink.getHref());
        assertNotNull(relsBefore);
        assertEquals(0, relsBefore.getEntries().size());

        // create relationship between source and target documents
        CMISObject sourceObject = source.getExtension(CMISConstants.OBJECT);
        assertNotNull(sourceObject);
        String sourceId = sourceObject.getObjectId().getStringValue();
        assertNotNull(sourceId);
        CMISObject targetObject = target.getExtension(CMISConstants.OBJECT);
        assertNotNull(targetObject);
        String targetId = targetObject.getObjectId().getStringValue();
        assertNotNull(targetId);
        Entry rel = client.createRelationship(relsLink.getHref(), options.getRelationshipType(), sourceId, targetId);
        assertNotNull(rel);

        // check created relationship
        CMISObject relObject = rel.getExtension(CMISConstants.OBJECT);
        assertNotNull(relObject);
        assertEquals(options.getRelationshipType(), relObject.getObjectTypeId().getStringValue());
        assertEquals(sourceId, relObject.getSourceId().getStringValue());
        assertEquals(targetId, relObject.getTargetId().getStringValue());
        assertEquals(source.getSelfLink().getHref(), rel.getLink(CMISConstants.REL_ASSOC_SOURCE).getHref());
        assertEquals(target.getSelfLink().getHref(), rel.getLink(CMISConstants.REL_ASSOC_TARGET).getHref());

        // check relationships for created item
        Map<String, String> args = new HashMap<String, String>();
        args.put(ARG_INCLUDE_SUB_RELATIONSHIP_TYPES, "true");
        Feed relsAfter = client.getFeed(relsLink.getHref(), args);
        assertNotNull(relsAfter);
        assertEquals(1, relsAfter.getEntries().size());
    }

    public void testGetRelationship() throws Exception {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        assertNotNull(children);
        Entry source = client.createDocument(children.getSelfLink().getHref(), null, "testSource");
        assertNotNull(source);
        Entry target = client.createDocument(children.getSelfLink().getHref(), null, "testTarget");
        assertNotNull(target);

        // retrieve relationships feed on source
        Link relsLink = source.getLink(CMISConstants.REL_RELATIONSHIPS);
        assertNotNull(relsLink);
        
        // retrieve all relationships
        Map<String, String> args = new HashMap<String, String>();
        args.put("includeSubRelationshipTypes", "true");
        Feed relsBeforeCreate = client.getFeed(relsLink.getHref(), args);
        assertNotNull(relsBeforeCreate);
        assertEquals(0, relsBeforeCreate.getEntries().size());
        
        // create relationship between source and target documents
        CMISObject sourceObject = source.getExtension(CMISConstants.OBJECT);
        assertNotNull(sourceObject);
        String sourceId = sourceObject.getObjectId().getStringValue();
        assertNotNull(sourceId);
        CMISObject targetObject = target.getExtension(CMISConstants.OBJECT);
        assertNotNull(targetObject);
        String targetId = targetObject.getObjectId().getStringValue();
        assertNotNull(targetId);
        Entry rel = client.createRelationship(relsLink.getHref(), options.getRelationshipType(), sourceId, targetId);
        assertNotNull(rel);

        // retrieve all relationships
        args.put("relationshipType", options.getRelationshipType());
        Feed relsAfterCreate = client.getFeed(relsLink.getHref(), args);
        assertNotNull(relsAfterCreate);
        assertEquals(1, relsAfterCreate.getEntries().size());

        // get created relationship
        Entry relEntry = client.getEntry(rel.getSelfLink().getHref());
        CMISObject relEntryObject = rel.getExtension(CMISConstants.OBJECT);
        CMISObject relObject = rel.getExtension(CMISConstants.OBJECT);
        assertNotNull(relObject);
        assertEquals(relObject.getObjectTypeId().getStringValue(), relEntryObject.getObjectTypeId().getStringValue());
        assertEquals(relObject.getSourceId().getStringValue(), relEntryObject.getSourceId().getStringValue());
        assertEquals(relObject.getTargetId().getStringValue(), relEntryObject.getTargetId().getStringValue());
        assertEquals(source.getSelfLink().getHref(), relEntry.getLink(CMISConstants.REL_ASSOC_SOURCE).getHref());
        assertEquals(target.getSelfLink().getHref(), relEntry.getLink(CMISConstants.REL_ASSOC_TARGET).getHref());
    }

    public void testDeleteRelationship() throws Exception {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        assertNotNull(children);
        Entry source = client.createDocument(children.getSelfLink().getHref(), null, "testSource");
        assertNotNull(source);
        Entry target = client.createDocument(children.getSelfLink().getHref(), null, "testTarget");
        assertNotNull(target);

        // retrieve relationships feed on source
        Link relsLink = source.getLink(CMISConstants.REL_RELATIONSHIPS);
        assertNotNull(relsLink);
        Feed relsBefore = client.getFeed(relsLink.getHref());
        assertNotNull(relsBefore);
        assertEquals(0, relsBefore.getEntries().size());

        // create relationship between source and target documents
        CMISObject sourceObject = source.getExtension(CMISConstants.OBJECT);
        assertNotNull(sourceObject);
        String sourceId = sourceObject.getObjectId().getStringValue();
        assertNotNull(sourceId);
        CMISObject targetObject = target.getExtension(CMISConstants.OBJECT);
        assertNotNull(targetObject);
        String targetId = targetObject.getObjectId().getStringValue();
        assertNotNull(targetId);
        Entry rel = client.createRelationship(relsLink.getHref(), options.getRelationshipType(), sourceId, targetId);
        assertNotNull(rel);

        // check relationships for created item
        Map<String, String> args = new HashMap<String, String>();
        args.put(ARG_INCLUDE_SUB_RELATIONSHIP_TYPES, "true");
        Feed relsAfterCreate = client.getFeed(relsLink.getHref(), args);
        assertNotNull(relsAfterCreate);
        assertEquals(1, relsAfterCreate.getEntries().size());

        // delete relationship
        Response deleteRes = client.executeRequest(new DeleteRequest(rel.getSelfLink().getHref().toString()), 204);
        assertNotNull(deleteRes);

        // check relationships for deleted item
        Feed relsAfterDelete = client.getFeed(relsLink.getHref(), args);
        assertNotNull(relsAfterDelete);
        assertEquals(0, relsAfterDelete.getEntries().size());
    }
    
}
