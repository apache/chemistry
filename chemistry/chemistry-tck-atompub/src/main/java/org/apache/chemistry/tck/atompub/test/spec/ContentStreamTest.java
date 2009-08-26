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

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.DeleteRequest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PutRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;

/**
 * CMIS Content Stream Tests
 */
public class ContentStreamTest extends TCKTest {

    public void testContentStreamEmpty() throws Exception {
        // create document for setting / getting content
        Entry document = fixture.createTestDocument("testContent", "createdocumentNoContent.atomentry.xml");

        // retrieve content
        client.executeRequest(new GetRequest(document.getContentSrc().toString()), 404);
    }

    public void testUpdateContentStream() throws Exception {
        // create document for setting / getting content
        Entry document = fixture.createTestDocument("testContent");

        // retrieve content
        Response documentContentRes = client.executeRequest(new GetRequest(document.getContentSrc().toString()), 200);
        String resContent = documentContentRes.getContentAsString();
        Assert.assertEquals(document.getTitle(), resContent);

        // set content
        String UPDATED_CONTENT = "Updated via SetContentStream()";
        Link editMediaLink = document.getEditMediaLink();
        Assert.assertNotNull(editMediaLink);
        Request putReq = new PutRequest(editMediaLink.getHref().toString(), UPDATED_CONTENT, CMISConstants.MIMETYPE_TEXT);
        Response res = client.executeRequest(putReq, 200);
        Assert.assertNotNull(res);

        // retrieve updated content
        Request getReq = new GetRequest(document.getContentSrc().toString());
        Response documentUpdatedContentRes = client.executeRequest(getReq, 200);
        String resUpdatedContent = documentUpdatedContentRes.getContentAsString();
        Assert.assertEquals(UPDATED_CONTENT, resUpdatedContent);
    }

    public void testDeleteContentStream() throws Exception {
        // create document for setting / getting content
        Entry document = fixture.createTestDocument("testContent");

        // retrieve content
        Response documentContentRes = client.executeRequest(new GetRequest(document.getContentSrc().toString()), 200);
        String resContent = documentContentRes.getContentAsString();
        Assert.assertEquals(document.getTitle(), resContent);

        // delete content
        Link editMediaLink = document.getEditMediaLink();
        Assert.assertNotNull(editMediaLink);
        Response res = client.executeRequest(new DeleteRequest(editMediaLink.getHref().toString()), 204);
        Assert.assertNotNull(res);

        // retrieve deleted content
        client.executeRequest(new GetRequest(document.getContentSrc().toString()), 404);
    }

}
