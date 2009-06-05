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
 */
package org.apache.chemistry.atompub.client.common.atom;

import java.io.Serializable;
import java.util.List;

import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;

/**
 * Implementation of a CMIS property to be used when reading data from XML. This
 * implementation is creating value objects from the XML strings only when first
 * accessed, this ensure faster loading of big feeds.
 */
public class XmlProperty implements Property {

    protected static final String NULL = new String();

    protected PropertyDefinition def;

    protected Serializable value = NULL;

    protected Object xmlValue;

    public XmlProperty() {

    }

    public XmlProperty(PropertyDefinition def) {
        this.def = def;
    }

    public XmlProperty(PropertyDefinition def, String value) {
        this.def = def;
        this.xmlValue = value;
    }

    public XmlProperty(PropertyDefinition def, List<String> value) {
        this.def = def;
        this.xmlValue = value;
    }

    /**
     * Get the property name
     *
     * @return the property name
     */
    public String getName() {
        return def.getName();
    }

    /**
     * Get the property adapter which know how to convert the raw value into a
     * suitable Java object
     *
     * @return
     */
    public ValueAdapter getAdapter() {
        return ValueAdapter.getAdapter(def.getType());
    }

    /**
     * Get the property value. The returned object is either null either one of
     * the following types: <li> <code>String</code> or <code>String[]</code> if
     * an array <li> <code>Calendar</code> or <code>Calendar[]</code> if an array
     * <li> <code>Boolean</code> or <code>Boolean[]</code> if an array <li>
     * <code>Integer</code> or <code>Integer[]</code> if an array <li>
     * <code>URI</code> or <code>URI[]</code> if an array <li>
     * <code>BugDecimail</code> or <code>BigDecimal[]</code> if an array
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
                if (xmlValue.getClass() == String.class) {
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
                        if (def.getType() == PropertyType.STRING) { // optimization
                            // for
                            // string
                            // lists
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
                                        + getName());
                    }
                }
            }
            xmlValue = null; // TODO nullify - or reuse it to keep dirty state?
        }
        return value;
    }

    public void setValue(Serializable value) {
        if (!def.validates(value)) {
            throw new IllegalArgumentException("Not a valid value: " + value); // TODO
            // use
            // custom
            // exceptions
        }
        this.value = value;
    }

    public void setValueUnsafe(Serializable value) {
        this.value = value;
    }

    public Object getXmlValue() {
        return xmlValue;
    }

    public void setXmlValue(String value) {
        this.xmlValue = value;
        this.value = NULL;
    }

    public void setXmlValue(List<String> value) {
        this.xmlValue = value;
        this.value = NULL;
    }

    public void setDefinition(PropertyDefinition def) {
        this.def = def;
    }

    public PropertyDefinition getDefinition() {
        return def;
    }

    public boolean isValueLoaded() {
        return value != NULL;
    }

    @Override
    public String toString() {
        return getName() + "=" + (xmlValue == null ? value : xmlValue);
    }

}
