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
package org.apache.chemistry.atompub.client.stax;

import java.io.Serializable;
import java.util.List;

import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.atompub.ValueAdapter;

/**
 * Implementation of a CMIS property to be used when reading data from XML.
 * <p>
 * This implementation is creating value objects from the XML strings only when
 * first accessed, this ensures faster loading of big feeds.
 */
public class XmlProperty implements Property {

    private static enum NoValue {
        NO_VALUE
    }

    public static final Serializable NULL = NoValue.NO_VALUE;

    private PropertyDefinition def;

    /**
     * The internal state can be:
     * <ol>
     * <li>if def is null, then value hold the property name,
     * <li>if def is not null, then value is either {@link NULL} if not yet
     * computed from xmlValue, or it holds the actual Java value.
     * </ol>
     */
    private Serializable value;

    /**
     * The XML value, either a {@code String} or a {@code List<String>}.
     */
    private Object xmlValue;

    public XmlProperty(String name) {
        value = name;
    }

    public XmlProperty(String name, String xmlValue) {
        value = name;
        this.xmlValue = xmlValue;
    }

    public XmlProperty(String name, List<String> xmlValue) {
        value = name;
        this.xmlValue = xmlValue;
    }

    public XmlProperty(PropertyDefinition def) {
        this.def = def;
        value = NULL;
    }

    public XmlProperty(PropertyDefinition def, String xmlValue) {
        this.def = def;
        this.xmlValue = xmlValue;
        value = NULL;
    }

    /**
     * Gets the property name.
     */
    public String getName() {
        return def == null ? (String) value : def.getName();
    }

    /**
     * Gets the property adapter which knows how to convert the raw value into a
     * suitable Java object.
     * <p>
     * Only useable if a property definition has been set for this property.
     */
    public ValueAdapter getAdapter() {
        if (def == null) {
            throw new NullPointerException("No definition set for: " + this);
        }
        return ValueAdapter.getAdapter(def.getType());
    }

    /**
     * Gets the property value. The returned object is either {@code null} or
     * one of the following types:
     * <ul>
     * <li> <code>String</code> or <code>String[]</code>
     * <li> <code>Calendar</code> or <code>Calendar[]</code>
     * <li> <code>Boolean</code> or <code>Boolean[]</code>
     * <li> <code>Integer</code> or <code>Integer[]</code>
     * <li> <code>URI</code> or <code>URI[]</code>
     * <li> <code>BigDecimal</code> or <code>BigDecimal[]</code>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    public Serializable getValue() {
        if (value == NULL) { // real value not yet computed
            if (xmlValue == null) {
                value = def.getDefaultValue(); // or null?
            } else {
                ValueAdapter va = getAdapter();
                if (va == null) {
                    throw new IllegalArgumentException("Unknow property type: "
                            + def.getType());
                }
                if (xmlValue instanceof String) {
                    if (def.isMultiValued()) {
                        Serializable[] ar = va.createArray(1);
                        ar[0] = va.readValue((String) xmlValue);
                        value = ar;
                    } else {
                        value = va.readValue((String) xmlValue);
                    }
                } else { // a list
                    List<String> list = (List<String>) xmlValue;
                    if (def.isMultiValued()) {
                        if (def.getType() == PropertyType.STRING) {
                            // optimization for string lists
                            value = list.toArray(va.createArray(list.size()));
                        } else {
                            Serializable[] ar = va.createArray(list.size());
                            for (int i = 0; i < ar.length; i++) {
                                ar[i] = va.readValue((String) xmlValue);
                            }
                            value = ar;
                        }
                    } else {
                        // TODO throw an exception ?
                        throw new IllegalArgumentException(
                                "Multiple value set on a scalar property: "
                                        + this);
                    }
                }
            }
            xmlValue = null; // TODO nullify - or reuse it to keep dirty state?
        }
        return value;
    }

    public void setValue(Serializable value) {
        String error = def.validationError(value);
        if (error != null) {
            throw new IllegalArgumentException(this + " cannot receive value: "
                    + value + ": " + error);
            // TODO use custom exceptions
        }
        this.value = value;
    }

    public Object getXmlValue() {
        return xmlValue;
    }

    public void setDefinition(PropertyDefinition def) {
        this.def = def;
        value = NULL;
    }

    public PropertyDefinition getDefinition() {
        return def;
    }

    public boolean isValueLoaded() {
        return value != NULL;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + getName() + "="
                + (xmlValue == null ? value : xmlValue) + ')';
    }

}
