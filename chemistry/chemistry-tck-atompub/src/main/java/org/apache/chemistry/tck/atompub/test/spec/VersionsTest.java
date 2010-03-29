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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.http.DeleteRequest;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PostRequest;
import org.apache.chemistry.tck.atompub.http.PutRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;


/**
 * CMIS Versions Tests
 */
public class VersionsTest extends TCKTest {

    @Override
    public void tearDown() throws Exception {
        // cancel any outstanding checkouts
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject object = testFolder.getExtension(CMISConstants.OBJECT);
        String scopeId = object.getObjectId().getStringValue();
        Map<String, String> args = new HashMap<String, String>();
        args.put("folderId", scopeId);
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        Feed checkedout = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout);

        for (Entry pwc : checkedout.getEntries()) {
            client.executeRequest(new DeleteRequest(pwc.getSelfLink().getHref().toString()), 204);
        }

        super.tearDown();
    }

    public void testGetCheckedOut() throws Exception {
        // retrieve test folder for checkouts
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject object = testFolder.getExtension(CMISConstants.OBJECT);
        String scopeId = object.getObjectId().getStringValue();
        Assert.assertNotNull(scopeId);

        // retrieve checkouts within scope of test checkout folder
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        Map<String, String> args = new HashMap<String, String>();
        args.put("folderId", scopeId);
        Feed checkedout = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout);
        Assert.assertEquals(0, checkedout.getEntries().size());
    }

    public void testCheckout() throws Exception {
        // create document for checkout
        Entry document = fixture.createTestDocument("testCheckout");
        CMISObject docObject = document.getExtension(CMISConstants.OBJECT);
        Request documentReq = new GetRequest(document.getSelfLink().getHref().toString());
        Response documentRes = client.executeRequest(documentReq, 200);
        Assert.assertNotNull(documentRes);
        String documentXML = documentRes.getContentAsString();
        Assert.assertNotNull(documentXML);

        // checkout
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        Request checkoutReq = new PostRequest(checkedoutHREF.toString(), documentXML, CMISConstants.MIMETYPE_ENTRY);
        Response pwcRes = client.executeRequest(checkoutReq, 201);
        Assert.assertNotNull(pwcRes);
        String pwcXml = pwcRes.getContentAsString();
        Assert.assertNotNull(pwcXml);
        Entry pwc = model.parseEntry(new StringReader(pwcXml), null);
        Assert.assertNotNull(pwc);
        CMISObject pwcObject = pwc.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(pwcObject);
        Assert.assertTrue(pwcObject.isVersionSeriesCheckedOut().getBooleanValue());
        Assert.assertEquals(docObject.getObjectId().getStringValue(), pwcObject.getVersionSeriesId().getStringValue());
        Assert.assertEquals(docObject.getVersionSeriesId().getStringValue(), pwcObject.getVersionSeriesId().getStringValue());
        Assert.assertEquals(pwcObject.getObjectId().getStringValue(), pwcObject.getVersionSeriesCheckedOutId().getStringValue());
        Assert.assertNotNull(pwcObject.getVersionSeriesCheckedOutBy().getStringValue());

        // retrieve pwc directly
        Response pwcGetRes = client.executeRequest(new GetRequest(pwc.getSelfLink().getHref().toString()), 200);
        Assert.assertNotNull(pwcGetRes);
        String pwcGetXml = pwcRes.getContentAsString();
        Entry pwcGet = model.parseEntry(new StringReader(pwcGetXml), null);
        Assert.assertNotNull(pwcGet);
        CMISObject pwcGetObject = pwc.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(pwcGetObject);
        Assert.assertTrue(pwcGetObject.isVersionSeriesCheckedOut().getBooleanValue());
        Assert.assertEquals(docObject.getObjectId().getStringValue(), pwcGetObject.getVersionSeriesId().getStringValue());
        Assert.assertEquals(pwcGetObject.getObjectId().getStringValue(), pwcGetObject.getVersionSeriesCheckedOutId().getStringValue());
        Assert.assertNotNull(pwcGetObject.getVersionSeriesCheckedOutBy().getStringValue());

        // test getCheckedOut is updated
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject object = testFolder.getExtension(CMISConstants.OBJECT);
        String scopeId = object.getObjectId().getStringValue();
        Map<String, String> args = new HashMap<String, String>();
        args.put("folderId", scopeId);
        Feed checkedout = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout);
        Assert.assertEquals(1, checkedout.getEntries().size());
    }

    public void testCancelCheckout() throws Exception {
        // create document for checkout
        Entry document = fixture.createTestDocument("testCancelCheckout");
        Request documentReq = new GetRequest(document.getSelfLink().getHref().toString());
        Response documentRes = client.executeRequest(documentReq, 200);
        Assert.assertNotNull(documentRes);
        String xml = documentRes.getContentAsString();
        Assert.assertNotNull(xml);

        // checkout
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        Request checkoutReq = new PostRequest(checkedoutHREF.toString(), xml, CMISConstants.MIMETYPE_ENTRY);
        Response pwcRes = client.executeRequest(checkoutReq, 201);
        Assert.assertNotNull(pwcRes);
        String pwcXml = pwcRes.getContentAsString();

        // test getCheckedOut is updated
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject object = testFolder.getExtension(CMISConstants.OBJECT);
        String scopeId = object.getObjectId().getStringValue();
        Map<String, String> args = new HashMap<String, String>();
        args.put("folderId", scopeId);
        Feed checkedout = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout);
        Assert.assertEquals(1, checkedout.getEntries().size());

        // cancel checkout
        Entry pwc = model.parseEntry(new StringReader(pwcXml), null);
        Assert.assertNotNull(pwc);
        Response cancelRes = client.executeRequest(new DeleteRequest(pwc.getSelfLink().getHref().toString()), 204);
        Assert.assertNotNull(cancelRes);

        // test getCheckedOut is updated
        Feed checkedout2 = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout2);
        Assert.assertEquals(0, checkedout2.getEntries().size());
    }

    public void testCheckIn() throws Exception {
        // create document for checkout
        Entry document = fixture.createTestDocument("testCheckin");
        Request documentReq = new GetRequest(document.getSelfLink().getHref().toString());
        Response documentRes = client.executeRequest(documentReq, 200);
        Assert.assertNotNull(documentRes);
        String xml = documentRes.getContentAsString();
        Assert.assertNotNull(xml);

        // checkout
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        Request checkoutReq = new PostRequest(checkedoutHREF.toString(), xml, CMISConstants.MIMETYPE_ENTRY);
        Response pwcRes = client.executeRequest(checkoutReq, 201);
        Assert.assertNotNull(pwcRes);
        Entry pwc = model.parseEntry(new StringReader(pwcRes.getContentAsString()), null);
        Assert.assertNotNull(pwc);

        // test getCheckedOut is updated
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject object = testFolder.getExtension(CMISConstants.OBJECT);
        String scopeId = object.getObjectId().getStringValue();
        Map<String, String> args = new HashMap<String, String>();
        args.put("folderId", scopeId);
        Feed checkedout = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout);
        Assert.assertEquals(1, checkedout.getEntries().size());

        // test version properties of checked-out item
        // test checked-in version properties
        Entry checkedoutdoc = client.getEntry(document.getSelfLink().getHref());
        CMISObject checkedoutdocObject = checkedoutdoc.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(checkedoutdocObject);
        Assert.assertTrue(checkedoutdocObject.isVersionSeriesCheckedOut().getBooleanValue());
        // Assert.assertEquals(checkedoutdocObject.getObjectId().getStringValue(),
        // checkedoutdocObject.getVersionSeriesId().getStringValue());
        Assert.assertNotNull(checkedoutdocObject.getVersionSeriesCheckedOutId().getStringValue());
        Assert.assertNotNull(checkedoutdocObject.getVersionSeriesCheckedOutBy().getStringValue());

        // test update of private working copy
        String updateFile = templates.load("updatedocument.atomentry.xml");
        updateFile = updateFile.replace("${ID}", document.getId().toString());
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        updateFile = updateFile.replace("${NAME}", guid);
        Request updateReq = new PutRequest(pwc.getEditLink().getHref().toString(), updateFile, CMISConstants.MIMETYPE_ENTRY);
        Response pwcUpdatedres = client.executeRequest(updateReq, 200);
        Assert.assertNotNull(pwcUpdatedres);
        Entry updated = model.parseEntry(new StringReader(pwcUpdatedres.getContentAsString()), null);
        // ensure update occurred
        Assert.assertEquals(pwc.getId(), updated.getId());
        Assert.assertEquals(pwc.getPublished(), updated.getPublished());
        Assert.assertEquals("Updated Title " + guid, updated.getTitle());
        Assert.assertEquals("text/plain", updated.getContentMimeType().toString());
        Response pwcContentRes = client.executeRequest(new GetRequest(pwc.getContentSrc().toString()), 200);
        Assert.assertEquals("updated content " + guid, pwcContentRes.getContentAsString());

        // checkin
        String checkinFile = templates.load("checkindocument.atomentry.xml");
        String checkinUrl = pwc.getSelfLink().getHref().toString();
        Map<String, String> args2 = new HashMap<String, String>();
        args2.put("checkinComment", guid);
        args2.put("checkin", "true");
        Request checkinReq = new PutRequest(checkinUrl, checkinFile, CMISConstants.MIMETYPE_ENTRY).setArgs(args2);
        Response checkinRes = client.executeRequest(checkinReq, 200);
        Assert.assertNotNull(checkinRes);
        String checkinResXML = checkinRes.getContentAsString();

        // test getCheckedOut is updated
        Feed checkedout2 = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout2);
        Assert.assertEquals(0, checkedout2.getEntries().size());

        // test checked-in doc has new updates
        Entry checkedIn = model.parseEntry(new StringReader(checkinResXML), null);
        Entry updatedDoc = client.getEntry(checkedIn.getSelfLink().getHref());
        Assert.assertEquals("Updated Title " + guid, updatedDoc.getTitle());
        Assert.assertEquals("text/plain", updatedDoc.getContentMimeType().toString());
        Response updatedContentRes = client.executeRequest(new GetRequest(updatedDoc.getContentSrc().toString()), 200);
        Assert.assertEquals("updated content " + guid, updatedContentRes.getContentAsString());

        // test checked-in version properties
        CMISObject updatedObject = updatedDoc.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(updatedObject);
        Assert.assertFalse(updatedObject.isVersionSeriesCheckedOut().getBooleanValue());
        // Assert.assertEquals(updatedObject.getObjectId().getStringValue(),
        // updatedObject.getVersionSeriesId().getStringValue());
        Assert.assertNull(updatedObject.getVersionSeriesCheckedOutId().getStringValue());
        Assert.assertNull(updatedObject.getVersionSeriesCheckedOutBy().getStringValue());
        
        // There is no guarantee that the object returned by checkin is the object against which our checkin comment
        // was recorded, so let's get the 'latest major version'
        updatedDoc = client.getEntry(updatedDoc.getSelfLink().getHref(), Collections.singletonMap("returnVersion", "latestmajor"));
        updatedObject = updatedDoc.getExtension(CMISConstants.OBJECT);
        Assert.assertEquals(guid, updatedObject.getCheckinComment().getStringValue());
    }

    public void testUpdateOnCheckIn() throws Exception {
        // create document for checkout
        Entry document = fixture.createTestDocument("testUpdateOnCheckIn");
        Request documentReq = new GetRequest(document.getSelfLink().getHref().toString());
        Response documentRes = client.executeRequest(documentReq, 200);
        Assert.assertNotNull(documentRes);
        String xml = documentRes.getContentAsString();
        Assert.assertNotNull(xml);

        // checkout
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        Request checkoutReq = new PostRequest(checkedoutHREF.toString(), xml, CMISConstants.MIMETYPE_ENTRY);
        Response pwcRes = client.executeRequest(checkoutReq, 201);
        Assert.assertNotNull(pwcRes);
        Entry pwc = model.parseEntry(new StringReader(pwcRes.getContentAsString()), null);
        Assert.assertNotNull(pwc);

        // test getCheckedOut is updated
        Entry testFolder = fixture.getTestCaseFolder();
        CMISObject object = testFolder.getExtension(CMISConstants.OBJECT);
        String scopeId = object.getObjectId().getStringValue();
        Map<String, String> args = new HashMap<String, String>();
        args.put("folderId", scopeId);
        Feed checkedout = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout);
        Assert.assertEquals(1, checkedout.getEntries().size());

        // checkin (with update)
        String checkinFile = templates.load("checkinandupdatedocument.atomentry.xml");
        // FIXME: Add a decent UID generation policy
        // String guid = GUID.generate();
        String guid = System.currentTimeMillis() + "";
        checkinFile = checkinFile.replace("${NAME}", guid);
        String checkinUrl = pwc.getSelfLink().getHref().toString();
        Map<String, String> args2 = new HashMap<String, String>();
        args2.put("checkinComment", guid);
        args2.put("checkin", "true");
        Request checkinReq = new PutRequest(checkinUrl, checkinFile, CMISConstants.MIMETYPE_ENTRY).setArgs(args2);
        Response checkinRes = client.executeRequest(checkinReq, 200);
        Assert.assertNotNull(checkinRes);
        String checkinResXML = checkinRes.getContentAsString();

        // test getCheckedOut is updated
        Feed checkedout2 = client.getFeed(new IRI(checkedoutHREF.toString()), args);
        Assert.assertNotNull(checkedout2);
        Assert.assertEquals(0, checkedout2.getEntries().size());

        // test checked-in doc has new updates
        Entry checkedIn = model.parseEntry(new StringReader(checkinResXML), null);
        Entry updatedDoc = client.getEntry(checkedIn.getSelfLink().getHref());
        // TODO: issue with updating name on PWC and it not reflecting on
        // checked-in document
        // Assert.assertEquals("Updated Title " + guid, updatedDoc.getTitle());
        Assert.assertEquals("text/plain", updatedDoc.getContentMimeType().toString());
        Response updatedContentRes = client.executeRequest(new GetRequest(updatedDoc.getContentSrc().toString()), 200);
        Assert.assertEquals("updated content " + guid, updatedContentRes.getContentAsString());
    }

    public void testGetAllVersions() throws Exception {
        int NUMBER_OF_VERSIONS = 3;

        // create document for checkout
        Entry document = fixture.createTestDocument("testGetAllVersions");
        Request documentReq = new GetRequest(document.getSelfLink().getHref().toString());
        Response documentRes = client.executeRequest(documentReq, 200);
        Assert.assertNotNull(documentRes);
        String xml = documentRes.getContentAsString();
        Assert.assertNotNull(xml);

        // get all versions
        Link allVersionsLink = document.getLink(CMISConstants.REL_VERSION_HISTORY);
        Assert.assertNotNull(allVersionsLink);
        Feed allVersions = client.getFeed(allVersionsLink.getHref());
        Assert.assertNotNull(allVersions);
        
        // Remember the initial number of versions. This should be at least one, but may vary across repositories that
        // may maintain a 'current' version in tandem with version history snapshots.
        int initialVersions = allVersions.getEntries().size();
        Assert.assertTrue(initialVersions > 0);
        
        IRI checkedoutHREF = client.getCheckedOutCollection(client.getWorkspace());
        for (int i = 0; i < NUMBER_OF_VERSIONS; i++) {
            // checkout
            Request checkoutReq = new PostRequest(checkedoutHREF.toString(), xml, CMISConstants.MIMETYPE_ENTRY);
            Response pwcRes = client.executeRequest(checkoutReq, 201);
            Assert.assertNotNull(pwcRes);
            Entry pwc = model.parseEntry(new StringReader(pwcRes.getContentAsString()), null);
            Assert.assertNotNull(pwc);

            // checkin
            String checkinFile = templates.load("checkinandupdatedocument.atomentry.xml");
            checkinFile = checkinFile.replace("${NAME}", "checkin " + i);
            String checkinUrl = pwc.getSelfLink().getHref().toString();
            Map<String, String> args2 = new HashMap<String, String>();
            args2.put("checkinComment", "checkin" + i);
            args2.put("checkin", "true");
            Request checkinReq = new PutRequest(checkinUrl, checkinFile, CMISConstants.MIMETYPE_ENTRY).setArgs(args2);
            Response checkinRes = client.executeRequest(checkinReq, 200);
            Assert.assertNotNull(checkinRes);

            // use result of checkin (i.e. document returned), for next checkout
            xml = checkinRes.getContentAsString();
            Assert.assertNotNull(xml);
        }

        // get all versions
        allVersions = client.getFeed(allVersionsLink.getHref());
        Assert.assertNotNull(allVersions);
        Assert.assertEquals(NUMBER_OF_VERSIONS + initialVersions, allVersions.getEntries().size());
        boolean pastLatestMajor = false;
        int versionIndex = NUMBER_OF_VERSIONS - 1;
        for (int i = 0; i < allVersions.getEntries().size(); i++) {
            Link versionLink = allVersions.getEntries().get(i).getSelfLink();
            Assert.assertNotNull(versionLink);
            Entry version = client.getEntry(versionLink.getHref());
            Assert.assertNotNull(version);
            // TODO: issue with updating name on PWC and it not reflecting on
            // checked-in document
            // Assert.assertEquals("Update Title checkin " + i,
            // version.getTitle());
            CMISObject versionObject = version.getExtension(CMISConstants.OBJECT);
            Assert.assertNotNull(versionObject);

            // Validate latest major version flag
            if (pastLatestMajor) {
                Assert.assertFalse("More than one latest major version!", versionObject.isLatestMajorVersion()
                        .getBooleanValue());
            } else {
                pastLatestMajor = versionObject.isLatestMajorVersion().getBooleanValue();
            }
            
            if (versionIndex >= 0) {
                // Validate non-initial versions have content
                Response versionContentRes = client.executeRequest(new GetRequest(version.getContentSrc().toString()),
                        200);

                // Ensure that the latest major version and its preceding
                // checkins match up to what we checked in
                if (pastLatestMajor) {
                    Assert.assertEquals("updated content checkin " + versionIndex, versionContentRes
                            .getContentAsString());
                    Assert.assertEquals("checkin" + versionIndex, versionObject.getCheckinComment().getStringValue());
                    versionIndex--;
                }
            }
        }
        // Make sure all versions were found
        Assert.assertTrue("No latest major version found", pastLatestMajor);
        Assert.assertEquals("Not all versions found", -1, versionIndex);
    }

}
