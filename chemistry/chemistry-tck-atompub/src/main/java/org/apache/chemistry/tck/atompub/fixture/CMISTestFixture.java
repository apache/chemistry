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
 */
package org.apache.chemistry.tck.atompub.fixture;

import java.util.ArrayList;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.http.DeleteRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.junit.Assert;


/**
 * CMIS Test Data
 */
public class CMISTestFixture {

    private static Long testStartTime = null;

    private CMISClient client;
    private String name;
    private Entry testCaseFolder = null;

    public CMISTestFixture(CMISClient client, String name) {
        this.client = client;
        this.name = name;
    }

    private long getStartTime() {
        if (testStartTime == null) {
            testStartTime = System.currentTimeMillis();
        }
        return testStartTime;
    }

    public Entry getTestCaseFolder() throws Exception {
        if (testCaseFolder == null) {
            Service service = client.getRepository();
            IRI rootFolderHREF = client.getRootCollection(client.getWorkspace(service));
            Assert.assertNotNull(rootFolderHREF);
            String folderName = "CMISTCK " + getStartTime() + " - " + name;
            testCaseFolder = client.createFolder(rootFolderHREF, folderName);
        }
        return testCaseFolder;
    }

    public Entry createTestDocument(String name) throws Exception {
        return createTestDocument(name, null);
    }

    public Entry createTestDocument(String name, String template) throws Exception {
        return createTestDocument(name, template, true);
    }

    public Entry createTestDocument(String name, String template,
            boolean expectNoContent) throws Exception {
        Link children = client.getChildrenLink(getTestCaseFolder());
        return client.createDocument(children.getHref(), name, template,
                expectNoContent);
    }

    public Entry createTestFolder(String name) throws Exception {
        return createTestFolder(name, null);
    }

    public Entry createTestFolder(String name, String template) throws Exception {
        Link children = client.getChildrenLink(getTestCaseFolder());
        return client.createFolder(children.getHref(), name, template);
    }

    public EntryTree createTestTree(String name, int depth, int docCount, String folderTemplate, String docTemplate) throws Exception {
        if (depth <= 0)
            return null;

        Entry root = createTestFolder(name, folderTemplate);
        return createTree(getTestCaseFolder(), root, depth - 1, docCount, folderTemplate, docTemplate);
    }

    private EntryTree createTree(Entry parent, Entry entry, int depth, int docCount, String folderTemplate, String docTemplate) throws Exception {
        String name = entry.getTitle();

        // create entry for parent
        EntryTree folderEntry = new EntryTree();
        folderEntry.parent = parent;
        folderEntry.entry = entry;
        folderEntry.type = CMISConstants.TYPE_FOLDER;
        folderEntry.children = new ArrayList<EntryTree>();

        // construct child documents, if required
        Link childrenLink = client.getChildrenLink(entry);
        for (int docIdx = 0; docIdx < docCount; docIdx++) {
            EntryTree docEntry = new EntryTree();
            String docName = name + " doc " + docIdx;
            docEntry.parent = entry;
            String cmisType = null;
            String cmisContentPath = null;
            String content = null;
            // If no template is specified, use a selection of files of
            // different types
            if (docTemplate == null) {
                switch (docIdx % 3) {
                case 1:
                    cmisType = "image/jpeg";
                    cmisContentPath = "org/apache/chemistry/tck/atompub/images/image1.jpg";
                    break;
                case 2:
                    cmisType = "image/png";
                    cmisContentPath = "org/apache/chemistry/tck/atompub/images/image2.png";
                    break;
                default:
                    content = name;
                    break;
                }
                docEntry.entry = client.createDocument(childrenLink.getHref(), docName, null, false, content, cmisType,
                        cmisContentPath);
            } else {
                docEntry.entry = client.createDocument(childrenLink.getHref(), docName, docTemplate);
            }
            docEntry.type = CMISConstants.TYPE_DOCUMENT;
            folderEntry.children.add(docEntry);
        }

        // do deeper, if required
        if (depth > 0) {
            String folderName = name + " (child)";
            Entry subFolder = client.createFolder(childrenLink.getHref(), folderName, folderTemplate);
            folderEntry.children.add(createTree(entry, subFolder, depth - 1, docCount, folderTemplate, docTemplate));
        }

        return folderEntry;
    }

    public void delete() throws Exception {
        if (testCaseFolder != null) {
            Link descendants = client.getDescendantsLink(testCaseFolder);
            Assert.assertNotNull(descendants);
            Request delete = new DeleteRequest(descendants.getHref().toString());
            client.executeRequest(delete, -1);
        }
    }

}
