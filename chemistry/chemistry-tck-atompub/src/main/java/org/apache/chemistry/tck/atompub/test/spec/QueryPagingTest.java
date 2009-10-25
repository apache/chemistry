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
import java.util.HashSet;
import java.util.Set;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISCapabilities;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.fixture.EntryTree;
import org.apache.chemistry.tck.atompub.http.PostRequest;
import org.apache.chemistry.tck.atompub.http.Response;
import org.junit.Assert;


/**
 * CMIS Query Paging Tests
 */
public class QueryPagingTest extends TCKTest {
    private EntryTree searchFolder;

    @Override
    public void setUp() {
        super.setUp();

        try {
            searchFolder = fixture.createTestTree("paging", 1, 15, null, null);
        } catch (Exception e) {
            // TODO: appropriate exception handling
            throw new RuntimeException(e);
        }
    }

    public void testQueryPaging() throws Exception {
        CMISCapabilities capabilities = client.getCapabilities();
        String capability = capabilities.getQuery();
        if (capability.equals("none")) {
        	throw new TCKSkipCapabilityException("query", "anything other than none", capability);
        }

        // create folder and children to page through
        Set<IRI> unread = new HashSet<IRI>();
        for (EntryTree child : searchFolder.children) {
            unread.add(child.entry.getId());
        }
        Assert.assertEquals(15, unread.size());

        // query children
        // TODO: use property query name
        IRI queryHREF = client.getQueryCollection(client.getWorkspace());
        String queryDoc = templates.load("query.cmisquery.xml");
        CMISObject testFolderObject = searchFolder.entry.getExtension(CMISConstants.OBJECT);
        String query = 
                "SELECT cmis:ObjectId, cmis:ObjectTypeId, cmis:Name FROM cmis:document " +
                "WHERE IN_FOLDER('" + testFolderObject.getObjectId().getStringValue() + "')";
        String queryReq = queryDoc.replace("${STATEMENT}", query);
        queryReq = queryReq.replace("${SKIPCOUNT}", "0");
        queryReq = queryReq.replace("${MAXITEMS}", "4");
        Response queryRes = client.executeRequest(new PostRequest(queryHREF.toString(), queryReq,
                CMISConstants.MIMETYPE_CMIS_QUERY), 201);
        Assert.assertNotNull(queryRes);

        // retrieve entries for first page
        Feed queryFeed = model.parseFeed(new StringReader(queryRes.getContentAsString()), null);
        Assert.assertNotNull(queryFeed);

        // page through results
        int page = 0;
        Link nextLink = null;
        do {
            page++;
            Assert.assertEquals(page < 4 ? 4 : 3, queryFeed.getEntries().size());

            // mark entries as read
            for (Entry entry : queryFeed.getEntries()) {
                unread.remove(entry.getId());
            }

            // next page
            nextLink = queryFeed.getLink("next");
            if (page == 4) {
                Assert.assertNull(nextLink);
            } else {
                Assert.assertNotNull(nextLink);
            }

            if (nextLink != null) {
                queryFeed = client.getFeed(nextLink.getHref());
            }
        } while (nextLink != null);

        Assert.assertEquals(4, page);
        Assert.assertEquals(0, unread.size());
    }

}
