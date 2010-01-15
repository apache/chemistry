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
package org.apache.chemistry.atompub.abdera;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.ValueAdapter;

/**
 * Abdera ElementWrapper for an AtomPub cmis:properties element.
 */
public class PropertiesElement extends ExtensibleElementWrapper {

    protected final Repository repository;

    /**
     * Constructor used when parsing XML.
     */
    public PropertiesElement(Element internal, Repository repository) {
        super(internal);
        this.repository = repository;
    }

    /**
     * Constructor used when generating XML.
     */
    public PropertiesElement(Factory factory) {
        super(factory, CMIS.PROPERTIES);
        repository = null;
    }

    public Map<String, Serializable> getProperties(String typeId) {
        // collector raw values
        Map<String, List<Serializable>> raw = new HashMap<String, List<Serializable>>();
        Map<String, ValueAdapter> adapters = new HashMap<String, ValueAdapter>();
        for (Element element : getElements()) {
            ValueAdapter va = ValueAdapter.getAdapter(element.getQName());
            if (va == null) {
                continue;
            }
            String pdid = element.getAttributeValue(CMIS.PDID);
            boolean buggyCompat = false;
            if (pdid == null) {
                // compat with buggy CMISSpacesAir
                String name = element.getAttributeValue(new QName(CMIS.CMIS_NS,
                        "name"));
                if ("ObjectTypeId".equals(name)) {
                    pdid = Property.TYPE_ID;
                    buggyCompat = true;
                }
            }
            adapters.put(pdid, va);
            List<Serializable> list = new LinkedList<Serializable>();
            for (Element el : element.getElements()) {
                if (!el.getQName().equals(CMIS.VALUE)) {
                    continue;
                }
                Serializable value = va.readValue(el.getText());
                if (buggyCompat && "document".equals(value)) {
                    value = BaseType.DOCUMENT.getId();
                }
                list.add(value);
                if (pdid.equals(Property.TYPE_ID)) {
                    String tid = (String) value;
                    if (tid == null || !(typeId == null || tid.equals(typeId))) {
                        // mismatched types during put
                        throw new CMISRuntimeException("Invalid type: " + tid);
                    }
                    typeId = tid;
                }
            }
            raw.put(pdid, list);
        }
        if (typeId == null) {
            // TODO proper exception
            throw new RuntimeException("Invalid object with no "
                    + Property.TYPE_ID);
        }
        Type type = repository.getType(typeId);
        if (type == null) {
            // TODO proper exception
            throw new RuntimeException("Unknown type: " + typeId);
        }
        // now we have the type, build actual values
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        for (Entry<String, List<Serializable>> e : raw.entrySet()) {
            String name = e.getKey();
            PropertyDefinition pd = type.getPropertyDefinition(name);
            if (pd == null) {
                // TODO proper exception
                throw new RuntimeException("Illegal property: " + name
                        + " for type: " + typeId);
            }
            // check type matches qname
            ValueAdapter va = adapters.get(name);
            if (va.getPropertyType() != pd.getType()) {
                throw new RuntimeException("Property: " + name + " has type: "
                        + typeId + " but used element: "
                        + va.getPropertyQName());
            }
            // put multi-value in array
            List<Serializable> list = e.getValue();
            Serializable value;
            if (pd.isMultiValued()) {
                value = list.toArray(va.createArray(list.size()));
            } else {
                if (list.isEmpty()) {
                    value = null;
                } else if (list.size() == 1) {
                    value = list.get(0);
                } else {
                    throw new RuntimeException("Property: " + name
                            + " for type: " + typeId
                            + " cannot have multi-values: " + list);
                }
            }
            properties.put(name, value);
        }
        return properties;
    }

    public void setProperties(Map<String, Serializable> values, Type type) {
        if (type != null) {
            for (PropertyDefinition propertyDefinition : type.getPropertyDefinitions()) {
                setProperty(values.get(propertyDefinition.getId()),
                        propertyDefinition);
            }
        } else {
            // this is a simple record set from a query result, guess the types
            // TODO should get an ObjectEntry here, which should type its values
            for (Entry<String, Serializable> entry : values.entrySet()) {
                String key = entry.getKey();
                Serializable value = entry.getValue();
                PropertyType propertyType = guessType(key, value);
                setProperty(key, value, propertyType);
            }
        }
    }

    public void setProperty(Serializable value,
            PropertyDefinition propertyDefinition) {
        if (value == null) {
            // TODO assumes this isn't called several times
            return;
        }
        QName qname = propertyQName(propertyDefinition.getType());
        List<String> values = getStringsForValue(value,
                propertyDefinition.getType(),
                propertyDefinition.isMultiValued());
        ExtensibleElement el = addExtension(qname);
        el.setAttributeValue(CMIS.PDID, propertyDefinition.getId());
        String localName = propertyDefinition.getLocalName();
        if (localName != null) {
            el.setAttributeValue(CMIS.LOCAL_NAME_NONS, localName);
        }
        String displayName = propertyDefinition.getDisplayName();
        if (displayName != null) {
            el.setAttributeValue(CMIS.DISPLAY_NAME_NONS, displayName);
        }
        for (String s : values) {
            Element val = el.addExtension(CMIS.VALUE);
            // don't merge these two lines as JDK 5 has problems compiling it
            val.setText(s);
        }
    }

    // sets a property without all the type information, used for result sets
    public void setProperty(String key, Serializable value,
            PropertyType propertyType) {
        if (value == null) {
            // TODO assumes this isn't called several times
            return;
        }
        QName qname = propertyQName(propertyType);
        List<String> values = getStringsForValue(value, propertyType,
                value.getClass().isArray());
        ExtensibleElement el = addExtension(qname);
        el.setAttributeValue(CMIS.PDID, key);
        for (String s : values) {
            Element val = el.addExtension(CMIS.VALUE);
            // don't merge these two lines as JDK 5 has problems compiling it
            val.setText(s);
        }
    }

    /**
     * Finds the list of Strings that are the XML form for the value.
     *
     * @param value the native value
     * @param propertyDefinition the property definition
     * @return the list of serialized strings
     */
    // TODO move this to a helper somewhere else
    @SuppressWarnings( { "unchecked" })
    public static List<String> getStringsForValue(Serializable value,
            PropertyType propertyType, boolean multi) {
        List<String> values = null;
        if (multi) {
            if (value.getClass().isArray()) {
                // turn it into a list to work with it easily
                value = (Serializable) Arrays.asList((Serializable[]) value);
            }
            if (value instanceof List<?>) {
                values = new ArrayList<String>(((List<?>) value).size());
            } else {
                // TODO: complex property don't know how to handle skip it
                return null;
            }
        }
        switch (propertyType.ordinal()) {
        case PropertyType.STRING_ORD:
        case PropertyType.ID_ORD:
            if (multi) {
                values.addAll((List<? extends String>) value);
            } else {
                values = Collections.singletonList((String) value);
            }
            break;
        case PropertyType.DECIMAL_ORD:
            if (multi) {
                for (BigDecimal v : (List<? extends BigDecimal>) value) {
                    values.add(v.toString());
                }
            } else {
                values = Collections.singletonList(((BigDecimal) value).toString());
            }
            break;
        case PropertyType.INTEGER_ORD:
            if (multi) {
                for (Number v : (List<? extends Number>) value) {
                    values.add(v.toString());
                }
            } else {
                values = Collections.singletonList(((Number) value).toString());
            }
            break;
        case PropertyType.BOOLEAN_ORD:
            if (multi) {
                for (Boolean v : (List<? extends Boolean>) value) {
                    values.add(v.toString());
                }
            } else {
                values = Collections.singletonList(((Boolean) value).toString());
            }
            break;
        case PropertyType.DATETIME_ORD:
            if (multi) {
                for (Calendar v : (List<? extends Calendar>) value) {
                    values.add(calendarString(v));
                }
            } else {
                values = Collections.singletonList(calendarString((Calendar) value));
            }
            break;
        case PropertyType.URI_ORD:
            throw new UnsupportedOperationException(propertyType.toString());
        case PropertyType.HTML_ORD:
            throw new UnsupportedOperationException(propertyType.toString());
        default:
            throw new UnsupportedOperationException(propertyType.toString());
        }
        return values;
    }

    protected static QName propertyQName(PropertyType propertyType) {
        switch (propertyType.ordinal()) {
        case PropertyType.STRING_ORD:
            return CMIS.PROPERTY_STRING;
        case PropertyType.DECIMAL_ORD:
            return CMIS.PROPERTY_DECIMAL;
        case PropertyType.INTEGER_ORD:
            return CMIS.PROPERTY_INTEGER;
        case PropertyType.BOOLEAN_ORD:
            return CMIS.PROPERTY_BOOLEAN;
        case PropertyType.DATETIME_ORD:
            return CMIS.PROPERTY_DATETIME;
        case PropertyType.ID_ORD:
            return CMIS.PROPERTY_ID;
        case PropertyType.URI_ORD:
            return CMIS.PROPERTY_URI;
        case PropertyType.HTML_ORD:
            return CMIS.PROPERTY_HTML;
        default:
            throw new UnsupportedOperationException(propertyType.toString());
        }
    }

    // TODO XXX we shouldn't guess, values should be typed in ObjectEntry
    protected static PropertyType guessType(String key, Serializable value) {
        for (String n : Arrays.asList( //
                Property.ID, //
                Property.TYPE_ID, //
                Property.BASE_TYPE_ID, //
                Property.VERSION_SERIES_ID, //
                Property.VERSION_SERIES_CHECKED_OUT_ID, //
                Property.PARENT_ID, //
                Property.SOURCE_ID, //
                Property.TARGET_ID)) {
            if (key.toUpperCase().endsWith(n.toUpperCase())) {
                return PropertyType.ID;
            }
        }
        if (value instanceof String) {
            return PropertyType.STRING;
        }
        if (value instanceof BigDecimal) {
            return PropertyType.DECIMAL;
        }
        if (value instanceof Number) {
            return PropertyType.INTEGER;
        }
        if (value instanceof Boolean) {
            return PropertyType.BOOLEAN;
        }
        if (value instanceof Calendar) {
            return PropertyType.DATETIME;
        }
        return PropertyType.STRING;
    }

    @SuppressWarnings("boxing")
    protected static String calendarString(Calendar cal) {
        char sign;
        int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis()) / 60000;
        if (offset < 0) {
            offset = -offset;
            sign = '-';
        } else {
            sign = '+';
        }
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d%c%02d:%02d",
                cal.get(Calendar.YEAR), //
                cal.get(Calendar.MONTH) + 1, //
                cal.get(Calendar.DAY_OF_MONTH), //
                cal.get(Calendar.HOUR_OF_DAY), //
                cal.get(Calendar.MINUTE), //
                cal.get(Calendar.SECOND), //
                sign, offset / 60, offset % 60);
    }

}
