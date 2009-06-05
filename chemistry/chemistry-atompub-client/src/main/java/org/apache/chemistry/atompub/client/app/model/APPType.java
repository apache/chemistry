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
package org.apache.chemistry.atompub.client.app.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.common.atom.ReadContext;
import org.apache.chemistry.atompub.client.common.atom.XmlProperty;

/**
 *
 */
public class APPType extends APPObjectEntry implements Type {

    protected Map<String, String> map;

    protected BaseType baseType;

    protected String parentId;

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

    // should be added to API
    public boolean isFolder() {
        return getBaseType() == BaseType.FOLDER;
    }

    public BaseType getBaseType() {
        if (baseType == null) {
            baseType = BaseType.valueOf(map.get(CMIS.BASE_TYPE.getLocalPart()));
        }
        return baseType;
    }

    public String getBaseTypeQueryName() {
        return map.get(CMIS.BASE_TYPE_QUERY_NAME.getLocalPart());
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

    public String getParentId() {
        if (parentId == null) {
            parentId = map.get(CMIS.TYPE_ID.getLocalPart());
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

    public String getQueryName() {
        return map.get(CMIS.QUERY_NAME);
    }

    public boolean isControllable() {
        return "true".equals(map.get(CMIS.CONTROLLABLE));
    }

    public boolean isCreatable() {
        return "true".equals(map.get(CMIS.CREATABLE));
    }

    public boolean isFileable() {
        return "true".equals(map.get(CMIS.FILEABLE));
    }

    public boolean isQueryable() {
        return "true".equals(map.get(CMIS.QUERYABLE));
    }

    public boolean isVersionable() {
        return "true".equals(map.get(CMIS.VERSIONABLE));
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

    public ContentStreamPresence getContentStreamAllowed() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void loadPropertyDef() {
        if (propertyDefs == null) {
            APPType typeDef = (APPType) getConnector().getType(
                    new ReadContext(connection), getEditLink());
            propertyDefs = typeDef.propertyDefs;
        }
    }

    @Override
    public String toString() {
        return getId();
    }
}
