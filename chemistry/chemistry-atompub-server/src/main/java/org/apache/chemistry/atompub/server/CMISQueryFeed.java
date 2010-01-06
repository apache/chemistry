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
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.axiom.om.OMDocument;
import org.apache.chemistry.Inclusion;
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

    protected int maxItems;

    protected int skipCount;

    protected String renditions;

    protected RelationshipDirection relationships;

    protected boolean allowableActions;

    protected boolean policies;

    protected boolean acls;

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
        if (request.getMethod().equalsIgnoreCase("GET")) {
            return getEntry(request);
        } else if (request.getMethod().equalsIgnoreCase("POST")) {
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
        maxItems = q.getMaxItems();
        skipCount = q.getSkipCount();
        renditions = q.getRenditionFilter();
        relationships = q.getIncludeRelationships();
        allowableActions = q.getIncludeAllowableActions();
        policies = q.getIncludePolicyIds();
        acls = q.getIncludeACL();
        return doSearch(request);
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        Target target = request.getTarget();
        statement = target.getParameter(AtomPubCMIS.PARAM_QUERY);
        searchAllVersions = getParameter(request,
                AtomPubCMIS.PARAM_SEARCH_ALL_VERSIONS, false);
        maxItems = getParameter(request, AtomPubCMIS.PARAM_MAX_ITEMS, -1);
        skipCount = getParameter(request, AtomPubCMIS.PARAM_SKIP_COUNT, 0);
        renditions = target.getParameter(AtomPubCMIS.PARAM_RENDITION_FILTER);
        String rel = target.getParameter(AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS);
        relationships = RelationshipDirection.fromInclusion(rel);
        allowableActions = getParameter(request,
                AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
        policies = getParameter(request, AtomPubCMIS.PARAM_INCLUDE_POLICY_IDS,
                false);
        acls = getParameter(request, AtomPubCMIS.PARAM_INCLUDE_ACL, false);
        return doSearch(request);
    }

    protected ResponseContext doSearch(RequestContext request) {
        ResponseContext res = getFeed(request); // calls getEntries
        if (res.getStatus() == HttpStatus.SC_OK
                && request.getMethod().equalsIgnoreCase("POST")) {
            res.setStatus(HttpStatus.SC_CREATED);
        }
        return res;
    }

    @Override
    public Iterable<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI();
        try {
            Paging paging = new Paging(maxItems, skipCount);
            Inclusion inclusion = new Inclusion(null, renditions,
                    relationships, allowableActions, policies, acls);
            ListPage<ObjectEntry> results = spi.query(statement,
                    searchAllVersions, inclusion, paging);
            return results;
        } finally {
            spi.close();
        }
    }

}
