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

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.abdera.model.Entry;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PatchRequest;
import org.apache.chemistry.tck.atompub.http.PutRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;


/**
 * CMIS Update Tests
 */
public class UpdateTest extends TCKTest {
    
    public void testUpdatePatch() throws Exception {
        // retrieve test folder for update
        Entry document = fixture.createTestDocument("testUpdatePatch");
        String mimetype = (document.getContentMimeType() != null) ? document.getContentMimeType().toString() : null;
        if (mimetype != null) {
            Assert.assertEquals("text/html", mimetype);
        }

        // TODO: check for content update allowable action
        // if update allowed, perform update, else update and check for
        // appropriate error

        // update
        String updateFile = templates.load("updatedocument.atomentry.xml");
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        updateFile = updateFile.replace("${NAME}", guid);
        Request patchReq = new PatchRequest(document.getSelfLink().getHref().toString(), updateFile, CMISConstants.MIMETYPE_ENTRY);
        Response res = client.executeRequest(patchReq, 200, client.getAtomValidator());
        Assert.assertNotNull(res);
        Entry updated = model.parseEntry(new StringReader(res.getContentAsString()), null);

        // ensure update occurred
        Assert.assertEquals(document.getId(), updated.getId());
        Assert.assertEquals(document.getPublished(), updated.getPublished());
        Assert.assertEquals("Updated Title " + guid, updated.getTitle());
        // TODO: why is this testing for text/plain? it should be test/html
        Assert.assertEquals("text/plain", updated.getContentMimeType().toString());
        Response contentRes = client.executeRequest(new GetRequest(updated.getContentSrc().toString()), 200);
        Assert.assertEquals("updated content " + guid, contentRes.getContentAsString());
    }
    
    public void testUpdatePutCMISContent() throws Exception {
        // retrieve test folder for update
        Entry document = fixture.createTestDocument("testUpdatePutCMISContent");
        String mimetype = (document.getContentMimeType() != null) ? document.getContentMimeType().toString() : null;
        if (mimetype != null) {
            Assert.assertEquals("text/html", mimetype);
        }

        // TODO: check for content update allowable action
        // if update allowed, perform update, else update and check for
        // appropriate error

        // update
        String updateFile = templates.load("updatedocument.cmisatomentry.xml");
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        updateFile = updateFile.replace("${NAME}", guid);
        updateFile = updateFile.replace("${CMISCONTENT}", new String(Base64.encodeBase64(("updated content " + guid).getBytes())));
        Request putReq = new PutRequest(document.getSelfLink().getHref().toString(), updateFile, CMISConstants.MIMETYPE_ENTRY);
        Response res = client.executeRequest(putReq, 200, client.getAtomValidator());
        Assert.assertNotNull(res);
        Entry updated = model.parseEntry(new StringReader(res.getContentAsString()), null);

        // ensure update occurred
        Assert.assertEquals(document.getId(), updated.getId());
        Assert.assertEquals(document.getPublished(), updated.getPublished());
        Assert.assertEquals("Updated Title " + guid, updated.getTitle());
        // TODO: why is this testing for text/plain? it should be text/html
        Assert.assertEquals("text/plain", updated.getContentMimeType().toString());
        Response contentRes = client.executeRequest(new GetRequest(updated.getContentSrc().toString()), 200);
        Assert.assertEquals("updated content " + guid, contentRes.getContentAsString());
    }

    public void testUpdatePutAtomContent() throws Exception {
        // retrieve test folder for update
        Entry document = fixture.createTestDocument("testUpdatePutAtomContent");
        String mimetype = (document.getContentMimeType() != null) ? document.getContentMimeType().toString() : null;
        if (mimetype != null) {
            Assert.assertEquals("text/html", mimetype);
        }

        // TODO: check for content update allowable action
        // if update allowed, perform update, else update and check for
        // appropriate error

        // update
        String updateFile = templates.load("updatedocument.atomentry.xml");
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        updateFile = updateFile.replace("${NAME}", guid);
        Request putReq = new PutRequest(document.getSelfLink().getHref().toString(), updateFile, CMISConstants.MIMETYPE_ENTRY);
        Response res = client.executeRequest(putReq, 200, client.getAtomValidator());
        Assert.assertNotNull(res);
        Entry updated = model.parseEntry(new StringReader(res.getContentAsString()), null);

        // ensure update occurred
        Assert.assertEquals(document.getId(), updated.getId());
        Assert.assertEquals(document.getPublished(), updated.getPublished());
        Assert.assertEquals("Updated Title " + guid, updated.getTitle());
        // TODO: why is this testing for text/plain? it should be test/html
        Assert.assertEquals("text/plain", updated.getContentMimeType().toString());
        Response contentRes = client.executeRequest(new GetRequest(updated.getContentSrc().toString()), 200);
        Assert.assertEquals("updated content " + guid, contentRes.getContentAsString());
    }

    public void testUpdatePutAtomEntry() throws Exception {
        // retrieve test folder for update
        Entry document = fixture.createTestDocument("testUpdatePutAtomEntry");
        // edit title
        String updatedTitle = "Iñtërnâtiônàlizætiøn - 2";
        document.setTitle(updatedTitle);
        StringWriter writer = new StringWriter();
        document.writeTo(writer);

        // put document
        Request putReq = new PutRequest(document.getSelfLink().getHref().toString(), writer.toString(), CMISConstants.MIMETYPE_ENTRY);
        Response res = client.executeRequest(putReq, 200, client.getAtomValidator());
        Assert.assertNotNull(res);
        Entry updated = model.parseEntry(new StringReader(res.getContentAsString()), null);
        Assert.assertEquals(updatedTitle, updated.getTitle());
    }

}
