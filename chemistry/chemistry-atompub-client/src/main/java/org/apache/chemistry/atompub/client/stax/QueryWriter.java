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
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.Paging;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.xml.stax.XMLWriter;

/**
 * Writer for the body of a CMIS query.
 */
public class QueryWriter extends AbstractXmlObjectWriter<String> {

    protected final boolean searchAllVersions;

    protected final Inclusion inclusion;

    protected final Paging paging;

    public QueryWriter(boolean searchAllVersions, Inclusion inclusion,
            Paging paging) {
        this.searchAllVersions = searchAllVersions;
        this.inclusion = inclusion;
        this.paging = paging;
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
        if (inclusion != null) {
            if (inclusion.renditions != null) {
                xw.element(CMIS.RENDITION_FILTER).econtent(inclusion.renditions);
            }
            if (inclusion.relationships != null) {
                xw.element(CMIS.INCLUDE_RELATIONSHIPS).content(
                        RelationshipDirection.toInclusion(inclusion.relationships));
            }
            xw.element(CMIS.INCLUDE_ALLOWABLE_ACTIONS).content(
                    inclusion.allowableActions);
            xw.element(CMIS.INCLUDE_POLICY_IDS).content(inclusion.policies);
            xw.element(CMIS.INCLUDE_ACL).content(inclusion.acls);
        }
        if (paging != null) {
            if (paging.maxItems > -1) {
                xw.element(CMIS.MAX_ITEMS).content(paging.maxItems);
            }
            xw.element(CMIS.SKIP_COUNT).content(paging.skipCount);
        }
        xw.end();
        xw.end();
    }

}
