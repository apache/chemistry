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
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.Choice;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.stax.ValueAdapter;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class APPPropertyDefinition implements PropertyDefinition {

    public final static Log log = LogFactory.getLog(APPPropertyDefinition.class);

    protected Map<String, Object> map;

    protected String name;

    protected PropertyType type;

    protected boolean multiValued;

    protected Updatability updatability;

    protected APPPropertyDefinition(Map<String, Object> map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return (String) map.get("id");
    }

    public String getDisplayName() {
        return (String) map.get("displayName");
    }

    public String getDescription() {
        return (String) map.get("description");
    }

    public boolean isInherited() {
        Boolean v = (Boolean) map.get("inherited");
        return v == null ? false : v.booleanValue();
    }

    public PropertyType getType() {
        if (type == null) {
            String t = (String) map.get("propertyType");
            if (t != null) {
                type = PropertyType.get(t);
            }
            if (type == null) {
                type = PropertyType.STRING;
            }
        }
        return type;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public List<Choice> getChoices() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected final boolean getBooleanValue(String name) {
        Boolean v = (Boolean) map.get(name);
        return v == null ? false : v.booleanValue();
    }

    public boolean isOpenChoice() {
        return getBooleanValue("openChoice");
    }

    public boolean isRequired() {
        return getBooleanValue("required");
    }

    public Serializable getDefaultValue() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Updatability getUpdatability() {
        if (updatability == null) {
            String text = (String) map.get("updateability");
            if ("readonly".equals(text)) {
                updatability = Updatability.READ_ONLY;
            } else if ("whencheckedout".equals(text)) {
                updatability = Updatability.WHEN_CHECKED_OUT;
            } else {
                updatability = Updatability.READ_WRITE;
            }
        }
        return updatability;
    }

    public boolean isQueryable() {
        return getBooleanValue("queryable");
    }

    public boolean isOrderable() {
        return getBooleanValue("orderable");
    }

    public int getPrecision() {
        Integer v = (Integer) map.get("precision");
        return v != null ? v.intValue() : 32;
    }

    public Integer getMinValue() {
        return (Integer) map.get("minValue");
    }

    public Integer getMaxValue() {
        return (Integer) map.get("maxValue");
    }

    public int getMaxLength() {
        Integer v = (Integer) map.get("maxLength");
        return v != null ? v.intValue() : -1;
    }

    public URI getSchemaURI() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getEncoding() {
        return (String) map.get("encoding");
    }

    public boolean validates(Serializable value) {
        return validationError(value) == null;
    }

    public String validationError(Serializable value) {
        if (getUpdatability() == Updatability.READ_ONLY) {
            // TODO Updatability.WHEN_CHECKED_OUT
            return "Property is read-only";
        }
        if (value == null) {
            if (isRequired()) {
                return "Property is required";
            }
            return null;
        }
        boolean multi = isMultiValued();
        if (multi != value.getClass().isArray()) {
            return multi ? "Property is multi-valued"
                    : "Property is single-valued";
        }
        Class<?> klass = getType().klass();
        if (klass == null) {
            throw new UnsupportedOperationException(getType().toString());
        }
        if (multi) {
            for (int i = 0; i < Array.getLength(value); i++) {
                Object v = Array.get(value, i);
                if (v == null) {
                    return "Array value cannot contain null elements";
                }
                if (!klass.isInstance(v)) {
                    return "Array value has type " + v.getClass()
                            + " instead of " + klass.getName();
                }
            }
        } else {
            if (!klass.isInstance(value)) {
                return "Value has type " + value.getClass() + " instead of "
                        + klass.getName();
            }
        }
        return null;
    }

    public static APPPropertyDefinition fromXml(StaxReader reader)
            throws XMLStreamException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        APPPropertyDefinition pd = new APPPropertyDefinition(map);
        ChildrenNavigator nav = reader.getChildren();
        while (nav.next()) {
            String tag = reader.getLocalName();
            if ("name".equals(tag)) {
                pd.name = reader.getElementText();
            } else if ("cardinality".equals(tag)) {
                String text = reader.getElementText();
                pd.multiValued = isMultiValued(text);
            } else if ("defaultValue".equals(tag)) {
                // TODO not yet implemented
            } else if (tag.startsWith("choice")) {
                // TODO not yet implemented
            } else {
                ValueAdapter adapter = adapters.get(tag);
                Object val;
                if (adapter == null) {
                    val = reader.getElementText();
                } else {
                    val = adapter.readValue(reader.getElementText());
                }
                map.put(tag, val);
            }
        }
        return pd;
    }

    protected static boolean isMultiValued(String text) {
        return "multi".equals(text);
    }

    private static Map<String, ValueAdapter> adapters = new HashMap<String, ValueAdapter>();
    static {
        adapters.put(CMIS.INHERITED.getLocalPart(), ValueAdapter.BOOLEAN);
        adapters.put(CMIS.REQUIRED.getLocalPart(), ValueAdapter.BOOLEAN);
        adapters.put(CMIS.QUERYABLE.getLocalPart(), ValueAdapter.BOOLEAN);
        adapters.put(CMIS.ORDERABLE.getLocalPart(), ValueAdapter.BOOLEAN);
        adapters.put("openChoice", ValueAdapter.BOOLEAN);
        adapters.put("maxLength", ValueAdapter.INTEGER);
    }

}
