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
 *     Bogdan Stefanescu, Nuxeo
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlProperty;

/**
 *
 */
public class APPType extends APPObjectEntry implements Type {

    private static final String UNDEFINED = "\0\0\0";

    protected Map<String, String> map;

    protected BaseType baseType;

    protected String parentId = UNDEFINED;

    protected Map<String, PropertyDefinition> propertyDefs;

    public APPType(APPConnection connection) {
        super(connection, new HashMap<String, XmlProperty>(), null);
    }

    public void init(Map<String, String> properties,
            Map<String, PropertyDefinition> props) {
        if (map != null) {
            throw new IllegalStateException("Type is already intialized");
        }
        map = properties;
        propertyDefs = props;
    }

    public String getDescription() {
        return map.get(CMIS.DESCRIPTION.getLocalPart());
    }

    public String getDisplayName() {
        return map.get(CMIS.DISPLAY_NAME.getLocalPart());
    }

    @Override
    public String getId() {
        return getTypeId();
    }

    @Override
    public String getTypeId() {
        return map.get(CMIS.ID.getLocalPart());
    }

    @Override
    public BaseType getBaseType() {
        if (baseType == null) {
            baseType = BaseType.get(map.get(CMIS.BASE_TYPE_ID.getLocalPart()));
        }
        return baseType;
    }

    // should be added to API
    public boolean isFolder() {
        return getBaseType() == BaseType.FOLDER;
    }

    public String getParentId() {
        if (parentId == UNDEFINED) {
            parentId = map.get(CMIS.PARENT_ID.getLocalPart());
            if ("".equals(parentId)) {
                parentId = null;
            }
        }
        return parentId;
    }

    public PropertyDefinition getPropertyDefinition(String name) {
        loadPropertyDef();
        return propertyDefs.get(name);
    }

    public Collection<PropertyDefinition> getPropertyDefinitions() {
        loadPropertyDef();
        return Collections.unmodifiableCollection(propertyDefs.values());
    }

    public String getLocalName() {
        return map.get(CMIS.LOCAL_NAME.getLocalPart());
    }

    public URI getLocalNamespace() {
        String uri = map.get(CMIS.LOCAL_NAMESPACE.getLocalPart());
        try {
            return uri == null ? null : new URI(uri);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String getQueryName() {
        return map.get(CMIS.QUERY_NAME.getLocalPart());
    }

    public boolean isControllablePolicy() {
        return "true".equals(map.get(CMIS.CONTROLLABLE_POLICY.getLocalPart()));
    }

    public boolean isControllableACL() {
        return "true".equals(map.get(CMIS.CONTROLLABLE_ACL.getLocalPart()));
    }

    public boolean isFulltextIndexed() {
        return "true".equals(map.get(CMIS.FULLTEXT_INDEXED.getLocalPart()));
    }

    public boolean isCreatable() {
        return "true".equals(map.get(CMIS.CREATABLE.getLocalPart()));
    }

    public boolean isFileable() {
        return "true".equals(map.get(CMIS.FILEABLE.getLocalPart()));
    }

    public boolean isQueryable() {
        return "true".equals(map.get(CMIS.QUERYABLE.getLocalPart()));
    }

    public boolean isVersionable() {
        return "true".equals(map.get(CMIS.VERSIONABLE.getLocalPart()));
    }

    public ContentStreamPresence getContentStreamAllowed() {
        return ContentStreamPresence.get(map.get(CMIS.CONTENT_STREAM_ALLOWED),
                ContentStreamPresence.NOT_ALLOWED);
    }

    public boolean isIncludedInSuperTypeQuery() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String[] getAllowedSourceTypes() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String[] getAllowedTargetTypes() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void loadPropertyDef() {
        if (propertyDefs == null) {
            APPType typeDef = (APPType) connection.getConnector().getType(
                    new ReadContext(connection), getEditLink());
            propertyDefs = typeDef.propertyDefs;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + getId() + ')';
    }

}
