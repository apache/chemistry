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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.ValueAdapter;
import org.apache.chemistry.atompub.client.stax.XmlProperty;
import org.apache.chemistry.xml.stax.XMLWriter;

/**
 *
 */
public class APPObjectEntry implements ObjectEntry {

    protected APPConnection connection;

    protected Map<String, XmlProperty> properties;

    protected Set<String> allowableActions; // TODO use set?

    protected String id;

    protected String name;

    protected String typeId;

    protected final List<String> links;

    public APPObjectEntry(APPConnection connection,
            Map<String, XmlProperty> properties, Set<String> allowableActions) {
        this.connection = connection;
        this.properties = properties;
        if (allowableActions == null) {
            allowableActions = new HashSet<String>();
        }
        this.allowableActions = allowableActions;
        links = new ArrayList<String>();
    }

    public void addLink(String rel, String href) {
        links.add(rel == null ? "" : rel);
        links.add(href);
    }

    public String[] getLinks() {
        return links.toArray(new String[links.size()]);
    }

    public String getLink(String rel) {
        for (int i = 0, len = links.size(); i < len; i += 2) {
            if (rel.equals(links.get(i))) {
                return links.get(i + 1);
            }
        }
        return null;
    }

    public String getEditLink() {
        String href = getLink("edit");
        return href == null ? getLink("self") : href;
    }

    // -----

    public APPConnection getConnection() {
        return connection;
    }

    protected boolean isCreation() {
        return getId() == null;
    }

    public String getId() {
        return (String) getValue(Property.ID);
    }

    public String getTypeId() {
        return (String) getValue(Property.TYPE_ID);
    }

    public BaseType getBaseType() {
        return BaseType.get((String) getValue(Property.BASE_TYPE_ID));
    }

    public Serializable getValue(String name) {
        XmlProperty p = properties.get(name);
        return p == null ? null : p.getValue();
    }

    // not in API
    public XmlProperty getProperty(PropertyDefinition pd) {
        XmlProperty p = properties.get(pd.getName());
        if (p != null) {
            return p;
        }
        if (isCreation()) {
            p = new XmlProperty(pd);
            properties.put(pd.getName(), p);
        } else {
            // TODO not fetched...
        }
        return p;
    }

    public Map<String, Serializable> getValues() {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        for (Entry<String, XmlProperty> e : properties.entrySet()) {
            String key = e.getKey();
            XmlProperty p = properties.get(key);
            map.put(key, p == null ? null : p.getValue());
        }
        return map;
    }

    public void setValue(String name, Serializable value) {
        XmlProperty p = properties.get(name);
        if (p != null) {
            p.setValue(value);
        } else {
            PropertyDefinition pd = connection.getRepository().getType(
                    getTypeId()).getPropertyDefinition(name);
            if (pd == null) {
                throw new IllegalArgumentException("No such property: " + name);
            }
            p = new XmlProperty(pd);
            p.setValue(value);
            properties.put(name, p);
        }
    }

    public void setValues(Map<String, Serializable> values) {
        for (Map.Entry<String, Serializable> entry : values.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }
    }

    // public Document getDocument() {
    // return (APPDocument) getConnector().getObject(
    // new ReadContext(getConnection(), getType()), getEditLink());
    // }
    //
    // public Folder getFolder() {
    // if (getType().getBaseType() == BaseType.FOLDER) {
    // return (APPFolder) getConnector().getObject(
    // new ReadContext(getConnection(), getType()), getEditLink());
    // }
    // return null;
    // }

    public Collection<String> getAllowableActions() {
        return allowableActions;
    }

    public Collection<ObjectEntry> getRelationships() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + getTypeId() + ',' + getId()
        + ')';
    }

    public void writeObjectTo(XMLWriter xw) throws IOException {
        xw.element(CMIS.OBJECT);
        xw.start();
        xw.element(CMIS.PROPERTIES);
        xw.start();
        for (XmlProperty p : properties.values()) {
            ValueAdapter va = p.getAdapter();
            xw.element(va.getPropertyQName()).attr(CMIS.NAME, p.getName());
            xw.start();
            if (p.isValueLoaded()) {
                Serializable v = p.getValue();
                if (v != null) {
                    if (v.getClass().isArray()) {
                        Serializable[] ar = (Serializable[]) v;
                        for (Serializable val : ar) {
                            xw.element(CMIS.VALUE).content(va.writeValue(val));
                        }
                    } else {
                        xw.element(CMIS.VALUE).content(va.writeValue(v));
                    }
                }
            } else {
                Object v = p.getXmlValue();
                if (v != null) {
                    if (v.getClass() == String.class) {
                        xw.element(CMIS.VALUE).content((String) v);
                    } else {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>) v;
                        for (String val : list) {
                            xw.element(CMIS.VALUE).content(val);
                        }
                    }
                }
            }
            xw.end();
        }
        xw.end();
        xw.end();
    }

}
