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
 *     David Ward, Alfresco
 */
package org.apache.chemistry.tck.atompub.fixture;

import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.EntryTree.TreeVisitor;
import org.apache.chemistry.tck.atompub.http.PutRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;
import org.apache.commons.codec.binary.Base64;

/**
 * Visitor that updates all documents in a tree.
 */
public class UpdateDocumentsVisitor implements TreeVisitor {

    private CMISClient client;
    private ResourceLoader templates;

    public UpdateDocumentsVisitor(CMISClient client, ResourceLoader templates) {
        this.client = client;
        this.templates = templates;
    }

    public void visit(EntryTree entry) throws Exception {
        if (entry.type.equals(CMISConstants.TYPE_DOCUMENT)) {
            String updateFile = templates.load("updatedocument.cmisatomentry.xml");
            updateFile = updateFile.replace("${ID}", entry.entry.getId().toString());
            String guid = String.valueOf(System.currentTimeMillis());
            updateFile = updateFile.replace("${NAME}", guid);
            updateFile = updateFile.replace("${CMISCONTENT}", new String(Base64
                    .encodeBase64(("updated content " + guid).getBytes())));
            Request putReq = new PutRequest(entry.entry.getSelfLink().getHref().toString(), updateFile,
                    CMISConstants.MIMETYPE_ENTRY);
            client.executeRequest(putReq, 200);
        }
    }
}
