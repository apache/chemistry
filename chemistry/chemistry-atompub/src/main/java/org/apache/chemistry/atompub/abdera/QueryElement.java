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
package org.apache.chemistry.atompub.abdera;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.axiom.om.OMElement;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.RelationshipDirection;

/**
 * Element wrapping for a CMIS query.
 */
public class QueryElement extends ExtensibleElementWrapper {

    /**
     * Constructor used when parsing XML.
     */
    public QueryElement(Element internal) {
        super(internal);
    }

    /**
     * Constructor used when generating XML.
     */
    public QueryElement(Factory factory) {
        super(factory, CMIS.QUERY);
    }

    protected String getChildElementText(QName name) {
        OMElement child = ((FOMElement) getInternal()).getFirstChildWithName(name);
        return child == null ? null : child.getText();
    }

    public String getStatement() {
        return getChildElementText(CMIS.STATEMENT);
    }

    public void setStatement(String statement) {
        addExtension(new StatementElement(getFactory(), statement));
    }

    public boolean getSearchAllVersions() {
        return Boolean.parseBoolean(getChildElementText(CMIS.SEARCH_ALL_VERSIONS));
    }

    public boolean getIncludeAllowableActions() {
        return Boolean.parseBoolean(getChildElementText(CMIS.INCLUDE_ALLOWABLE_ACTIONS));
    }

    public RelationshipDirection getIncludeRelationships() {
        return RelationshipDirection.fromInclusion(getChildElementText(CMIS.INCLUDE_RELATIONSHIPS));
    }

    public boolean getIncludePolicyIds() {
        return Boolean.parseBoolean(getChildElementText(CMIS.INCLUDE_POLICY_IDS));
    }

    public boolean getIncludeACL() {
        return Boolean.parseBoolean(getChildElementText(CMIS.INCLUDE_ACL));
    }

    public String getRenditionFilter() {
        return getChildElementText(CMIS.RENDITION_FILTER);
    }

    public int getMaxItems() {
        String maxItems = getChildElementText(CMIS.MAX_ITEMS);
        return maxItems == null ? -1 : Integer.parseInt(maxItems);
    }

    public int getSkipCount() {
        String skipCount = getChildElementText(CMIS.SKIP_COUNT);
        return skipCount == null ? 0 : Integer.parseInt(skipCount);
    }

    protected static class StatementElement extends ElementWrapper {
        public StatementElement(Factory factory, String statement) {
            super(factory, CMIS.STATEMENT);
            setText(statement);
        }
    }

}
