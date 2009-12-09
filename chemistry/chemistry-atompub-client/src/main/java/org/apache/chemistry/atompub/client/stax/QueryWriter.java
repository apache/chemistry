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
 *     Ugo Cei, Sourcesense
 */
package org.apache.chemistry.atompub.client.stax;

import java.io.IOException;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.xml.stax.XMLWriter;

/**
 * Writer for the body of a CMIS query.
 */
public class QueryWriter extends AbstractXmlObjectWriter<String> {

    protected boolean searchAllVersions;

    protected boolean includeAllowableActions;

    protected RelationshipDirection includeRelationships;

    protected String renditionFilter;

    protected long maxItems = -1;

    protected long skipCount;

    public void setSearchAllVersions(boolean searchAllVersions) {
        this.searchAllVersions = searchAllVersions;
    }

    /**
     * Sets the max number of items to return.
     * <p>
     * The default, {@code -1}, means that no max number will be sent
     *
     * @param maxItems the max number of items
     */
    public void setMaxItems(long maxItems) {
        this.maxItems = maxItems;
    }

    /**
     * Sets the skip count.
     * <p>
     * The default is {@code 0}.
     *
     * @param skipCount the skip count
     */
    public void setSkipCount(long skipCount) {
        this.skipCount = skipCount;
    }

    public void setIncludeAllowableActions(boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }

    public void setIncludeRelationships(
            RelationshipDirection includeRelationships) {
        this.includeRelationships = includeRelationships;
    }

    public void setRenditionFilter(String renditionFilter) {
        this.renditionFilter = renditionFilter;
    }

    @Override
    public String getContentType() {
        return AtomPubCMIS.MEDIA_TYPE_CMIS_QUERY;
    }

    @Override
    public void write(String statement, XMLWriter xw) throws IOException {
        xw.start().putXmlns(CMIS.CMIS_PREFIX, CMIS.CMIS_NS);
        xw.element(CMIS.QUERY);
        xw.start();
        xw.element(CMIS.STATEMENT).econtent(statement);
        xw.element(CMIS.SEARCH_ALL_VERSIONS).content(searchAllVersions);
        xw.element(CMIS.INCLUDE_ALLOWABLE_ACTIONS).content(
                includeAllowableActions);
        xw.element(CMIS.INCLUDE_RELATIONSHIPS).content(
                RelationshipDirection.toInclusion(includeRelationships));
        if (renditionFilter != null) {
            xw.element(CMIS.RENDITION_FILTER).econtent(renditionFilter);
        }
        if (maxItems > -1) {
            xw.element(CMIS.MAX_ITEMS).content(maxItems);
        }
        xw.element(CMIS.SKIP_COUNT).content(skipCount);
        xw.element(CMIS.INCLUDE_ALLOWABLE_ACTIONS).content(
                includeAllowableActions);
        xw.end();
        xw.end();
    }

}
