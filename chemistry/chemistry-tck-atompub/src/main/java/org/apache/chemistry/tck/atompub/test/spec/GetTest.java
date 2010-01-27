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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISRepositoryInfo;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.CMISTree;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.apache.chemistry.tck.atompub.fixture.GatherRenditionsVisitor;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.junit.Assert;


/**
 * CMIS Get Tests
 */
public class GetTest extends TCKTest {

    public void testGetFolder() throws Exception {
        // get folder
        Entry folder = fixture.getTestCaseFolder();
        Assert.assertNotNull(folder);
        Entry getFolder = client.getEntry(folder.getSelfLink().getHref());
        Assert.assertEquals(folder.getId(), getFolder.getId());
        Assert.assertEquals(folder.getTitle(), getFolder.getTitle());
        Assert.assertEquals(folder.getSummary(), getFolder.getSummary());
    }

    public void testGetDocument() throws Exception {
        Entry document = fixture.createTestDocument("testDocument");
        Entry getDocument = client.getEntry(document.getSelfLink().getHref());
        Assert.assertEquals(document.getId(), getDocument.getId());
        Assert.assertEquals(document.getTitle(), getDocument.getTitle());
        // Assert.assertEquals(document(), getDocument());
    }

    public void testGetDocumentNotExist() throws Exception {
        Entry folder = fixture.getTestCaseFolder();
        // get something that doesn't exist
        // FIXME: Add a decent UID generation policy
        String guid = System.currentTimeMillis() + "";
        client.executeRequest(new GetRequest(folder.getSelfLink().getHref().toString() + guid), 404);
    }

    public void testGetDocumentRenditions() throws Exception {
        final Entry document = fixture.createTestDocument("testGetDocumentRenditions");

        GatherRenditionsVisitor visitor = new GatherRenditionsVisitor(client);
        visitor.testRenditions(new CMISTree(fixture.getTestCaseFolder(), document, CMISConstants.TYPE_DOCUMENT),
                new GatherRenditionsVisitor.EntryGenerator() {

                    public EntryTree getEntries(String renditionFilter) throws Exception {
                        return new CMISTree(fixture.getTestCaseFolder(), client.getEntry(document.getSelfLink()
                                .getHref(), Collections.singletonMap("renditionFilter", renditionFilter)),
                                CMISConstants.TYPE_DOCUMENT);
                    }
                });
    }
    
    public void testObjectById() throws Exception
    {
        // construct document
        Entry document = fixture.createTestDocument("testObjectById");
        Assert.assertNotNull(document);
        CMISObject documentObject = document.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(documentObject);
        String objectId = documentObject.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);

        // formulate get request via id
        CMISUriTemplate objectByIdTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("id", objectId);
        IRI objectByIdRequest = objectByIdTemplate.generateUri(variables);
        
        // get document
        Entry documentById = client.getEntry(objectByIdRequest);
        Assert.assertNotNull(documentById);
        CMISObject documentByIdObject = document.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(documentByIdObject);
        Assert.assertEquals(objectId, documentByIdObject.getObjectId().getStringValue());
    }

    public void testObjectByRootFolderId() throws Exception
    {
        CMISRepositoryInfo info = client.getRepositoryInfo();
        String rootFolderId = info.getRootFolderId();
        Assert.assertNotNull(rootFolderId);

        // formulate get request via id
        CMISUriTemplate objectByIdTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("id", rootFolderId);
        IRI rootFolderByIdRequest = objectByIdTemplate.generateUri(variables);
        
        // get folder
        Entry rootFolderById = client.getEntry(rootFolderByIdRequest);
        Assert.assertNotNull(rootFolderById);
        CMISObject rootFolderObject = rootFolderById.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(rootFolderObject);
        Assert.assertEquals(rootFolderId, rootFolderObject.getObjectId().getStringValue());
    }
    
    public void testObjectByPath() throws Exception
    {
        // construct folder
        Entry folder = fixture.createTestFolder("testObjectByPath");
        Assert.assertNotNull(folder);
        CMISObject folderObject = folder.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(folderObject);
        String objectId = folderObject.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);
        String path = folderObject.getPath().getStringValue();
        Assert.assertNotNull(path);

        // formulate get request via id
        CMISUriTemplate objectByPathTemplate = client.getObjectByPathUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", path);
        IRI objectByPathRequest = objectByPathTemplate.generateUri(variables);
        
        // get folder
        Entry folderByPath = client.getEntry(objectByPathRequest);
        Assert.assertNotNull(folderByPath);
        CMISObject folderByPathObject = folder.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(folderByPathObject);
        Assert.assertEquals(path, folderByPathObject.getPath().getStringValue());
        Assert.assertEquals(objectId, folderByPathObject.getObjectId().getStringValue());
    }
    
    public void testObjectByIdRenditions() throws Exception {
        // construct document
        Entry document = fixture.createTestDocument("testObjectByIdRenditions");
        Assert.assertNotNull(document);
        CMISObject documentObject = document.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(documentObject);
        final String objectId = documentObject.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);

        GatherRenditionsVisitor visitor = new GatherRenditionsVisitor(client);

        // Create simple entry tree and walk it with the renditions visitor
        EntryTree entryTree = new CMISTree(fixture.getTestCaseFolder(), document, CMISConstants.TYPE_DOCUMENT);
        visitor.testRenditions(entryTree, new GatherRenditionsVisitor.EntryGenerator() {

            public EntryTree getEntries(String renditionFilter) throws Exception {
                // formulate get request via id
                CMISUriTemplate objectByIdTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
                Map<String, Object> variables = new HashMap<String, Object>(5);
                variables.put("id", objectId);
                variables.put("renditionFilter", renditionFilter);
                IRI objectByIdRequest = objectByIdTemplate.generateUri(variables);

                return new CMISTree(fixture.getTestCaseFolder(), client.getEntry(objectByIdRequest),
                        CMISConstants.TYPE_DOCUMENT);
            }
        });
    }

    public void testObjectByPathRenditions() throws Exception {
        // construct document
        Entry document = fixture.createTestDocument("testObjectByPath");
        Assert.assertNotNull(document);
        
        // Get path of its folder
        final Entry folder = fixture.getTestCaseFolder();
        Assert.assertNotNull(folder);
        CMISObject folderObject = folder.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(folderObject);
        String folderPath = folderObject.getPath().getStringValue();
        Assert.assertNotNull(folderPath);
        
        // Get path of the document within its folder 
        Link parentLink = client.getObjectParentsLink(document);
        Assert.assertNotNull(parentLink);
        Feed parents = client.getFeed(parentLink.getHref(), Collections.singletonMap("includeRelativePathSegment", "true"));        
        Entry parent = parents.getEntry(folder.getId().toString());        
        Assert.assertNotNull(parent);
        Element pathEl = parent.getFirstChild(CMISConstants.RELATIVE_PATH_SEGMENT);
        Assert.assertNotNull(pathEl);
        String relPath = pathEl.getText();
        Assert.assertNotNull(relPath);
        
        // Generate the full path of the document
        final String path = folderPath + '/' + relPath;

        GatherRenditionsVisitor visitor = new GatherRenditionsVisitor(client);

        // Create simple entry tree and walk it with the renditions visitor
        EntryTree entryTree = new CMISTree(fixture.getTestCaseFolder(), document, CMISConstants.TYPE_DOCUMENT);
        visitor.testRenditions(entryTree, new GatherRenditionsVisitor.EntryGenerator() {

            public EntryTree getEntries(String renditionFilter) throws Exception {
                // formulate get request via path
                CMISUriTemplate objectByPathTemplate = client.getObjectByPathUriTemplate(client.getWorkspace());
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("path", path);
                variables.put("renditionFilter", renditionFilter);
                IRI objectByPathRequest = objectByPathTemplate.generateUri(variables);

                return new CMISTree(folder, client.getEntry(objectByPathRequest),
                        CMISConstants.TYPE_DOCUMENT);
            }
        });
    }
}
