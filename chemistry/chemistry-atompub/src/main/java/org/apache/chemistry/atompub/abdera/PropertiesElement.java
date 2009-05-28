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
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;

/**
 * Abdera ElementWrapper for an AtomPub cmis:properties element.
 */
public class PropertiesElement extends ExtensibleElementWrapper {

    /**
     * Constructor. ContentStreamUri is special-cased as it depends on the
     * server context (base URL).
     */
    public PropertiesElement(Factory factory, String contentStreamURI) {
        super(factory, CMIS.PROPERTIES);
        if (contentStreamURI != null) {
            ExtensibleElement el = addExtension(CMIS.PROPERTY_URI);
            el.setAttributeValue(CMIS.NAME, Property.CONTENT_STREAM_URI);
            Element val = el.addExtension(CMIS.VALUE);
            // don't merge these two lines as JDK 5 has problems compiling it
            val.setText(contentStreamURI);

            // Alfresco compat (incorrect property name and type):
            el = addExtension(CMIS.PROPERTY_STRING);
            el.setAttributeValue(CMIS.NAME, "ContentStreamURI");
            val = el.addExtension(CMIS.VALUE);
            // don't merge these two lines
            val.setText(contentStreamURI);
        }
    }

    public void setProperties(Map<String, Serializable> values, Type type) {
        for (PropertyDefinition propertyDefinition : type.getPropertyDefinitions()) {
            String name = propertyDefinition.getName();
            if (name.equals(Property.CONTENT_STREAM_URI)) {
                // special-cased in constructor
                continue;
            }
            setProperty(values.get(name), propertyDefinition);
        }
    }

    @SuppressWarnings("null")
    public void setProperty(Serializable value,
            PropertyDefinition propertyDefinition) {
        if (value == null) {
            // TODO assumes this isn't called several times
            return;
        }
        QName qname = propertyQName(propertyDefinition);
        boolean multi = propertyDefinition.isMultiValued();
        List<String> values = null;
        if (multi) {
            if (value.getClass().isArray()) {
                values = new ArrayList<String>(Array.getLength(value));
            } else { // TODO: complex property don't know how to handle skip it
                return;
            }
        }
        PropertyType type = propertyDefinition.getType();
        switch (type.ordinal()) {
        case PropertyType.STRING_ORD:
        case PropertyType.ID_ORD:
            if (multi) {
                values.addAll(Arrays.asList((String[]) value));
            } else {
                values = Collections.singletonList((String) value);
            }
            break;
        case PropertyType.DECIMAL_ORD:
            if (multi) {
                for (BigDecimal v : (BigDecimal[]) value) {
                    values.add(v.toString());
                }
            } else {
                values = Collections.singletonList(((BigDecimal) value).toString());
            }
            break;
        case PropertyType.INTEGER_ORD:
            if (multi) {
                for (Number v : (Number[]) value) {
                    values.add(v.toString());
                }
            } else {
                values = Collections.singletonList(((Number) value).toString());
            }
            break;
        case PropertyType.BOOLEAN_ORD:
            if (multi) {
                for (Boolean v : (Boolean[]) value) {
                    values.add(v.toString());
                }
            } else {
                values = Collections.singletonList(((Boolean) value).toString());
            }
            break;
        case PropertyType.DATETIME_ORD:
            if (multi) {
                for (Calendar v : (Calendar[]) value) {
                    values.add(calendarString(v));
                }
            } else {
                values = Collections.singletonList(calendarString((Calendar) value));
            }
            break;
        case PropertyType.URI_ORD:
            throw new UnsupportedOperationException(type.toString());
        case PropertyType.XML_ORD:
            throw new UnsupportedOperationException(type.toString());
        case PropertyType.HTML_ORD:
            throw new UnsupportedOperationException(type.toString());
        default:
            throw new UnsupportedOperationException(type.toString());
        }
        ExtensibleElement el = addExtension(qname);
        el.setAttributeValue(CMIS.NAME, propertyDefinition.getName());
        for (String s : values) {
            Element val = el.addExtension(CMIS.VALUE);
            // don't merge these two lines as JDK 5 has problems compiling it
            val.setText(s);
        }
    }

    protected static QName propertyQName(PropertyDefinition def) {
        switch (def.getType().ordinal()) {
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
        case PropertyType.XML_ORD:
            return CMIS.PROPERTY_XML;
        case PropertyType.HTML_ORD:
            return CMIS.PROPERTY_HTML;
        default:
            throw new UnsupportedOperationException(def.getType().toString());
        }
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
