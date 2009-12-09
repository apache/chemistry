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
 *     Florent Guillaume, Nuxeo
 *     Amelie Avramo, EntropySoft
 */
package org.apache.chemistry.atompub.server;

import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.axiom.om.OMDocument;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Paging;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.abdera.QueryElement;
import org.apache.commons.httpclient.HttpStatus;

/**
 * CMIS Feed for a query.
 */
public class CMISQueryFeed extends CMISObjectsCollection {

    public static final TargetType TARGET_TYPE_CMIS_QUERY = TargetType.get(
            "CMISQUERY", true);

    protected String statement;

    protected boolean searchAllVersions;

    protected boolean includeAllowableActions;

    protected RelationshipDirection includeRelationships;

    protected String renditionFilter;

    protected int maxItems;

    protected int skipCount;

    public CMISQueryFeed(Repository repository) {
        super(AtomPubCMIS.COL_QUERY, "query", null, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    @Override
    public String getId(RequestContext request) {
        return "urn:id:1234collid";
    }

    // called by AbstractProvider.process if unknown TargetType
    @Override
    public ResponseContext extensionRequest(RequestContext request) {
        if (request.getTarget().getType() != TARGET_TYPE_CMIS_QUERY) {
            return ProviderHelper.notsupported(request);
        }
        if (request.getMethod().equalsIgnoreCase("POST")) {
            return postEntry(request);
        } else {
            // stupid signature prevents use of varargs...
            return ProviderHelper.notallowed(request, new String[] { "POST" });
        }
    }

    /*
     * ----- AbstractEntityCollectionAdapter -----
     */

    @Override
    public ResponseContext postEntry(RequestContext request) {
        OMDocument document;
        try {
            document = (OMDocument) request.getDocument(request.getAbdera().getParser());
        } catch (Exception e) {
            return createErrorResponse(new ResponseContextException(500, e));
        }
        if (document == null) {
            return null;
        }
        Element element = (Element) document.getOMDocumentElement();
        QueryElement q = new QueryElement(element);
        statement = q.getStatement();
        searchAllVersions = q.getSearchAllVersions();
        includeAllowableActions = q.getIncludeAllowableActions();
        includeRelationships = q.getIncludeRelationships();
        renditionFilter = q.getRenditionFilter();
        maxItems = q.getMaxItems();
        skipCount = q.getSkipCount();
        ResponseContext res = getFeed(request); // calls getEntries
        if (res.getStatus() == HttpStatus.SC_OK) {
            res.setStatus(HttpStatus.SC_CREATED);
        }
        return res;
    }

    @Override
    public Iterable<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            ListPage<ObjectEntry> results = spi.query(statement,
                    searchAllVersions, includeAllowableActions,
                    includeRelationships, renditionFilter, new Paging(maxItems,
                            skipCount));
            return results;
        } finally {
            spi.close();
        }
    }

}
