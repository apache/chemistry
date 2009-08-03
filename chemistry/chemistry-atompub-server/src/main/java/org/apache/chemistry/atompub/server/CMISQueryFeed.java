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
 */
package org.apache.chemistry.atompub.server;

import java.util.Collection;

import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.axiom.om.OMDocument;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.abdera.QueryElement;

/**
 * CMIS Feed for a query.
 */
public class CMISQueryFeed extends CMISObjectsCollection {

    public static final TargetType TARGET_TYPE_CMIS_QUERY = TargetType.get(
            "CMISQUERY", true);

    protected String statement;

    public CMISQueryFeed(Repository repository) {
        super(CMIS.COL_QUERY, "query", null, repository);
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
        return getFeed(request); // calls getEntries
    }

    @Override
    public Iterable<ObjectEntry> getEntries(RequestContext request)
            throws ResponseContextException {
        SPI spi = repository.getSPI(); // TODO XXX connection leak
        boolean searchAllVersions = false;
        boolean includeAllowableActions = false;
        boolean includeRelationships = false;
        boolean includeRenditions = false;
        int maxItems = -1;
        int skipCount = 0;
        boolean[] hasMoreItems = new boolean[1];
        Collection<ObjectEntry> results = spi.query(statement,
                searchAllVersions, includeAllowableActions,
                includeRelationships, includeRenditions, maxItems, skipCount,
                hasMoreItems);
        return results;
    }

}
