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
package org.apache.chemistry.tck.atompub.test.custom;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISProperties;
import org.apache.chemistry.abdera.ext.CMISProperty;
import org.apache.chemistry.tck.atompub.http.DeleteRequest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PatchRequest;
import org.apache.chemistry.tck.atompub.http.PostRequest;
import org.apache.chemistry.tck.atompub.http.PutRequest;
import org.apache.chemistry.tck.atompub.http.Response;


//
// TODO: Determine mechanism for repo specific registration of custom types
//


public class CMISCustomTypeTest extends TCKCustomTest {
    
    public static final String ARG_INCLUDE_SUB_RELATIONSHIP_TYPES = "includeSubRelationshipTypes";

    public void testCreateFolder() throws Exception {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        assertNotNull(children);
        int entriesBefore = children.getEntries().size();
        Entry folder = client.createFolder(children.getSelfLink().getHref(), "testCreateCustomFolder",
                "/org/apache/chemistry/tck/atompub/test/createcustomfolder.atomentry.xml");
        Feed feedFolderAfter = client.getFeed(childrenLink.getHref());
        int entriesAfter = feedFolderAfter.getEntries().size();
        assertEquals(entriesBefore + 1, entriesAfter);
        Entry entry = feedFolderAfter.getEntry(folder.getId().toString());
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        assertEquals("F/cmiscustom:folder", object.getObjectTypeId().getStringValue());
        CMISProperty customProp = object.getProperties().find("cmiscustom:folderprop_string");
        assertNotNull(customProp);
        assertEquals("custom string", customProp.getStringValue());
    }

    public void testCreateDocument() throws Exception {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        assertNotNull(children);
        int entriesBefore = children.getEntries().size();
        Entry document = client.createDocument(children.getSelfLink().getHref(), "testCreateCustomDocument",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        Feed feedFolderAfter = client.getFeed(childrenLink.getHref());
        int entriesAfter = feedFolderAfter.getEntries().size();
        assertEquals(entriesBefore + 1, entriesAfter);
        Entry entry = feedFolderAfter.getEntry(document.getId().toString());
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        assertEquals("D/cmiscustom:document", object.getObjectTypeId().getStringValue());
        CMISProperty customProp = object.getProperties().find("cmiscustom:docprop_string");
        assertNotNull(customProp);
        assertEquals("custom string", customProp.getStringValue());
        CMISProperty multiProp = object.getProperties().find("cmiscustom:docprop_boolean_multi");
        assertNotNull(multiProp);
        List<Object> multiValues = multiProp.getNativeValues();
        assertNotNull(multiValues);
        assertEquals(2, multiValues.size());
        assertEquals(true, multiValues.get(0));
        assertEquals(false, multiValues.get(1));
    }

    public void testUpdatePatch() throws Exception {
        // retrieve test folder for update
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);

        // create document for update
        Entry document = client.createDocument(childrenLink.getHref(), "testUpdatePatchCustomDocument",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(document);

        // update
        String updateFile = customTemplates.load("updatecustomdocument.atomentry.xml");
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        updateFile = updateFile.replace("${NAME}", guid);
        Response res = client.executeRequest(new PatchRequest(document.getSelfLink().getHref().toString(), updateFile,
                CMISConstants.MIMETYPE_ENTRY), 200);
        assertNotNull(res);
        Entry updated = model.parseEntry(new StringReader(res.getContentAsString()), null);

        // ensure update occurred
        assertEquals(document.getId(), updated.getId());
        assertEquals(document.getPublished(), updated.getPublished());
        assertEquals("Updated Title " + guid, updated.getTitle());
        CMISObject object = updated.getExtension(CMISConstants.OBJECT);
        assertEquals("D/cmiscustom:document", object.getObjectTypeId().getStringValue());
        CMISProperty customProp = object.getProperties().find("cmiscustom:docprop_string");
        assertNotNull(customProp);
        assertEquals("custom " + guid, customProp.getStringValue());
        CMISProperty multiProp = object.getProperties().find("cmiscustom:docprop_boolean_multi");
        assertNotNull(multiProp);
        List<Object> multiValues = multiProp.getNativeValues();
        assertNotNull(multiValues);
        assertEquals(2, multiValues.size());
        assertEquals(false, multiValues.get(0));
        assertEquals(true, multiValues.get(1));
    }

    public void testUpdatePut() throws Exception {
        // retrieve test folder for update
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);

        // create document for update
        Entry document = client.createDocument(childrenLink.getHref(), "testUpdatePutCustomDocument",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(document);

        // update
        String updateFile = customTemplates.load("updatecustomdocument.atomentry.xml");
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        updateFile = updateFile.replace("${NAME}", guid);
        Response res = client.executeRequest(new PutRequest(document.getSelfLink().getHref().toString(), updateFile,
                CMISConstants.MIMETYPE_ENTRY), 200);
        assertNotNull(res);
        Entry updated = model.parseEntry(new StringReader(res.getContentAsString()), null);

        // ensure update occurred
        assertEquals(document.getId(), updated.getId());
        assertEquals(document.getPublished(), updated.getPublished());
        assertEquals("Updated Title " + guid, updated.getTitle());
        CMISObject object = updated.getExtension(CMISConstants.OBJECT);
        assertEquals("D/cmiscustom:document", object.getObjectTypeId().getStringValue());
        CMISProperty customProp = object.getProperties().find("cmiscustom:docprop_string");
        assertNotNull(customProp);
        assertEquals("custom " + guid, customProp.getStringValue());
        CMISProperty multiProp = object.getProperties().find("cmiscustom:docprop_boolean_multi");
        assertNotNull(multiProp);
        List<Object> multiValues = multiProp.getNativeValues();
        assertNotNull(multiValues);
        assertEquals(2, multiValues.size());
        assertEquals(false, multiValues.get(0));
        assertEquals(true, multiValues.get(1));
    }

    public void testDelete() throws Exception {
        // retrieve test folder for deletes
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        Feed children = client.getFeed(childrenLink.getHref());
        int entriesBefore = children.getEntries().size();

        // create document for delete
        Entry document = client.createDocument(childrenLink.getHref(), "testDeleteCustomDocument",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        Response documentRes = client.executeRequest(new GetRequest(document.getSelfLink().getHref().toString()), 200);
        assertNotNull(documentRes);

        // ensure document has been created
        Feed children2 = client.getFeed(childrenLink.getHref());
        assertNotNull(children2);
        int entriesAfterCreate = children2.getEntries().size();
        assertEquals(entriesAfterCreate, entriesBefore + 1);

        // delete
        Response deleteRes = client.executeRequest(new DeleteRequest(document.getSelfLink().getHref().toString()), 204);
        assertNotNull(deleteRes);

        // ensure document has been deleted
        Feed children3 = client.getFeed(childrenLink.getHref());
        assertNotNull(children3);
        int entriesAfterDelete = children3.getEntries().size();
        assertEquals(entriesBefore, entriesAfterDelete);
    }

    public void testQuery() throws Exception {
        // retrieve query collection
        IRI queryHREF = client.getQueryCollection(client.getWorkspace());

        // retrieve test folder for query
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject testFolderObject = testFolder.getExtension(CMISConstants.OBJECT);
        Link childrenLink = client.getChildrenLink(testFolder);

        // create documents to query
        // Standard root document
        Entry document1 = client.createDocument(childrenLink.getHref(), "apple1");
        assertNotNull(document1);
        CMISObject document1Object = document1.getExtension(CMISConstants.OBJECT);
        assertNotNull(document1Object);
        String doc2name = "name" + System.currentTimeMillis();
        // Custom documents
        Entry document2 = client.createDocument(childrenLink.getHref(), doc2name,
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(document2);
        CMISObject document2Object = document2.getExtension(CMISConstants.OBJECT);
        assertNotNull(document2Object);
        Entry document3 = client.createDocument(childrenLink.getHref(), "banana1",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(document3);
        CMISObject document3Object = document3.getExtension(CMISConstants.OBJECT);
        assertNotNull(document3Object);

        // retrieve query request document
        String queryDoc = customTemplates.load("query.cmisquery.xml");

        {
            // construct structured query
            String query = "SELECT ObjectId, Name, ObjectTypeId, cmiscustom_docprop_string, cmiscustom_docprop_boolean_multi FROM cmiscustom_document "
                    + "WHERE IN_FOLDER('"
                    + testFolderObject.getObjectId().getStringValue()
                    + "') "
                    + "AND cmiscustom_docprop_string = 'custom string' ";
            String queryReq = queryDoc.replace("${STATEMENT}", query);
            queryReq = queryReq.replace("${SKIPCOUNT}", "0");
            queryReq = queryReq.replace("${PAGESIZE}", "5");

            // issue structured query
            Response queryRes = client.executeRequest(new PostRequest(queryHREF.toString(), queryReq,
                    CMISConstants.MIMETYPE_CMIS_QUERY), 200);
            assertNotNull(queryRes);
            Feed queryFeed = model.parseFeed(new StringReader(queryRes.getContentAsString()), null);
            assertNotNull(queryFeed);
            assertEquals(2, queryFeed.getEntries().size());

            assertNotNull(queryFeed.getEntry(document2.getId().toString()));
            CMISObject result1 = queryFeed.getEntry(document2.getId().toString()).getExtension(CMISConstants.OBJECT);
            assertNotNull(result1);
            assertEquals(document2Object.getName().getStringValue(), result1.getName().getStringValue());
            assertEquals(document2Object.getObjectId().getStringValue(), result1.getObjectId().getStringValue());
            assertEquals(document2Object.getObjectTypeId().getStringValue(), result1.getObjectTypeId().getStringValue());
            CMISProperties result1properties = result1.getProperties();
            assertNotNull(result1properties);
            CMISProperty result1property = result1properties.find("cmiscustom:docprop_string");
            assertNotNull(result1property);
            assertEquals("custom string", result1property.getStringValue());
            CMISProperty result1multiproperty = result1properties.find("cmiscustom:docprop_boolean_multi");
            assertNotNull(result1multiproperty);
            List<Object> result1multiValues = result1multiproperty.getNativeValues();
            assertNotNull(result1multiValues);
            assertEquals(2, result1multiValues.size());
            assertEquals(true, result1multiValues.get(0));
            assertEquals(false, result1multiValues.get(1));

            assertNotNull(queryFeed.getEntry(document3.getId().toString()));
            CMISObject result2 = queryFeed.getEntry(document3.getId().toString()).getExtension(CMISConstants.OBJECT);
            assertNotNull(result2);
            assertEquals(document3Object.getName().getStringValue(), result2.getName().getStringValue());
            assertEquals(document3Object.getObjectId().getStringValue(), result2.getObjectId().getStringValue());
            assertEquals(document3Object.getObjectTypeId().getStringValue(), result2.getObjectTypeId().getStringValue());
            CMISProperties result2properties = result2.getProperties();
            assertNotNull(result2properties);
            CMISProperty result2property = result2properties.find("cmiscustom:docprop_string");
            assertNotNull(result2property);
            assertEquals("custom string", result2property.getStringValue());
            CMISProperty result2multiproperty = result1properties.find("cmiscustom:docprop_boolean_multi");
            assertNotNull(result2multiproperty);
            List<Object> result2multiValues = result2multiproperty.getNativeValues();
            assertNotNull(result2multiValues);
            assertEquals(2, result2multiValues.size());
            assertEquals(true, result2multiValues.get(0));
            assertEquals(false, result2multiValues.get(1));
        }
    }

    public void testCreateRelationship() throws Exception {
        Entry testFolder = fixture.getTestCaseFolder();
        Link childrenLink = client.getChildrenLink(testFolder);
        assertNotNull(childrenLink);
        Feed children = client.getFeed(childrenLink.getHref());
        assertNotNull(children);
        Entry source = client.createDocument(children.getSelfLink().getHref(), "testSource",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(source);
        Entry target = client.createDocument(children.getSelfLink().getHref(), "testTarget",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(target);

        // retrieve relationships feed on source
        Link relsLink = source.getLink(CMISConstants.REL_RELATIONSHIPS);
        assertNotNull(relsLink);
        Feed relsBefore = client.getFeed(relsLink.getHref());
        assertNotNull(relsBefore);
        assertEquals(0, relsBefore.getEntries().size());

        // create relationship between source and target documents
        CMISObject targetObject = target.getExtension(CMISConstants.OBJECT);
        assertNotNull(targetObject);
        String targetId = targetObject.getObjectId().getStringValue();
        assertNotNull(targetId);
        Entry rel = client.createRelationship(relsLink.getHref(), "R/cmiscustom:assoc", targetId);
        assertNotNull(rel);

        // check created relationship
        CMISObject sourceObject = source.getExtension(CMISConstants.OBJECT);
        assertNotNull(sourceObject);
        String sourceId = sourceObject.getObjectId().getStringValue();
        assertNotNull(sourceId);
        CMISObject relObject = rel.getExtension(CMISConstants.OBJECT);
        assertNotNull(relObject);
        assertEquals("R/cmiscustom:assoc", relObject.getObjectTypeId().getStringValue());
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
        Entry source = client.createDocument(children.getSelfLink().getHref(), "testSource",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(source);
        Entry target = client.createDocument(children.getSelfLink().getHref(), "testTarget",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(target);

        // retrieve relationships feed on source
        Link relsLink = source.getLink(CMISConstants.REL_RELATIONSHIPS);
        assertNotNull(relsLink);

        // create relationship between source and target documents
        CMISObject targetObject = target.getExtension(CMISConstants.OBJECT);
        assertNotNull(targetObject);
        String targetId = targetObject.getObjectId().getStringValue();
        assertNotNull(targetId);
        Entry rel = client.createRelationship(relsLink.getHref(), "R/cmiscustom:assoc", targetId);
        assertNotNull(rel);

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
        Entry source = client.createDocument(children.getSelfLink().getHref(), "testSource",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(source);
        Entry target = client.createDocument(children.getSelfLink().getHref(), "testTarget",
                "/org/apache/chemistry/tck/atompub/test/createcustomdocument.atomentry.xml");
        assertNotNull(target);

        // retrieve relationships feed on source
        Link relsLink = source.getLink(CMISConstants.REL_RELATIONSHIPS);
        assertNotNull(relsLink);
        Feed relsBefore = client.getFeed(relsLink.getHref());
        assertNotNull(relsBefore);
        assertEquals(0, relsBefore.getEntries().size());

        // create relationship between source and target documents
        CMISObject targetObject = target.getExtension(CMISConstants.OBJECT);
        assertNotNull(targetObject);
        String targetId = targetObject.getObjectId().getStringValue();
        assertNotNull(targetId);
        Entry rel = client.createRelationship(relsLink.getHref(), "R/cmiscustom:assoc", targetId);
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
