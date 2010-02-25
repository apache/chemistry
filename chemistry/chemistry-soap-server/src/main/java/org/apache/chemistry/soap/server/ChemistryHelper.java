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
package org.apache.chemistry.soap.server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.CmisPropertiesType;
import org.apache.chemistry.ws.CmisProperty;
import org.apache.chemistry.ws.CmisPropertyBoolean;
import org.apache.chemistry.ws.CmisPropertyDateTime;
import org.apache.chemistry.ws.CmisPropertyDecimal;
import org.apache.chemistry.ws.CmisPropertyHtml;
import org.apache.chemistry.ws.CmisPropertyId;
import org.apache.chemistry.ws.CmisPropertyInteger;
import org.apache.chemistry.ws.CmisPropertyString;
import org.apache.chemistry.ws.CmisPropertyUri;
import org.apache.chemistry.ws.EnumCapabilityJoin;
import org.apache.chemistry.ws.EnumCapabilityQuery;

/**
 * Helper for various Chemistry to JAXB conversions.
 */
public class ChemistryHelper {

    private ChemistryHelper() {
        // utility class;
    }

    public static EnumCapabilityQuery chemistryToJAXB(CapabilityQuery query) {
        switch (query) {
        case NONE:
            return EnumCapabilityQuery.NONE;
        case METADATA_ONLY:
            return EnumCapabilityQuery.METADATAONLY;
        case FULL_TEXT_ONLY:
            return EnumCapabilityQuery.FULLTEXTONLY;
        case BOTH_COMBINED:
            return EnumCapabilityQuery.BOTHCOMBINED;
        case BOTH_SEPARATE:
            return EnumCapabilityQuery.BOTHSEPARATE;
        default:
            throw new RuntimeException();
        }
    }

    public static EnumCapabilityJoin chemistryToJAXB(CapabilityJoin join) {
        switch (join) {
        case NONE:
            return EnumCapabilityJoin.NONE;
        case INNER_ONLY:
            return EnumCapabilityJoin.INNERONLY;
        case INNER_AND_OUTER:
            return EnumCapabilityJoin.INNERANDOUTER;
        default:
            throw new RuntimeException();
        }
    }

    public static void chemistryToJAXB(ObjectEntry entry, CmisObjectType object) {
        CmisPropertiesType properties = new CmisPropertiesType();
        List<CmisProperty> list = properties.getProperty();
        for (Entry<String, Serializable> e : entry.getValues().entrySet()) {
            list.add(getWSCmisProperty(e.getKey(), e.getValue()));
        }
        object.setProperties(properties);
        // object.setAllowableActions(null);
    }

    /**
     * Transforms a Chemistry property into a WS one.
     */
    public static CmisProperty getWSCmisProperty(String key, Serializable value) {
        CmisProperty p;
        PropertyType propertyType = guessType(key, value);
        // boolean multi = false; // TODO
        switch (propertyType.ordinal()) {
        case PropertyType.STRING_ORD:
            p = new CmisPropertyString();
            ((CmisPropertyString) p).getValue().add((String) value);
            break;
        case PropertyType.DECIMAL_ORD:
            p = new CmisPropertyDecimal();
            ((CmisPropertyDecimal) p).getValue().add((BigDecimal) value);
            break;
        case PropertyType.INTEGER_ORD:
            p = new CmisPropertyInteger();
            Long l;
            if (value == null) {
                l = null;
            } else if (value instanceof Long) {
                l = (Long) value;
            } else if (value instanceof Integer) {
                l = Long.valueOf(((Integer) value).longValue());
            } else {
                throw new AssertionError("not a int/long: " + value);
            }
            ((CmisPropertyInteger) p).getValue().add(
                    l == null ? null : BigInteger.valueOf(l.longValue()));
            break;
        case PropertyType.BOOLEAN_ORD:
            p = new CmisPropertyBoolean();
            ((CmisPropertyBoolean) p).getValue().add((Boolean) value);
            break;
        case PropertyType.DATETIME_ORD:
            p = new CmisPropertyDateTime();
            ((CmisPropertyDateTime) p).getValue().add(
                    getXMLGregorianCalendar((Calendar) value));
            break;
        case PropertyType.URI_ORD:
            p = new CmisPropertyUri();
            URI u = (URI) value;
            ((CmisPropertyUri) p).getValue().add(
                    u == null ? null : u.toString());
            break;
        case PropertyType.ID_ORD:
            p = new CmisPropertyId();
            ((CmisPropertyId) p).getValue().add((String) value);
            break;
        case PropertyType.HTML_ORD:
            p = new CmisPropertyHtml();
            // ((CmisPropertyHtml)property).getAny().add(element);
            break;
        default:
            throw new AssertionError();
        }
        p.setPropertyDefinitionId(key);
        return p;

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

    protected static DatatypeFactory datatypeFactory;

    protected static XMLGregorianCalendar getXMLGregorianCalendar(
            Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new java.lang.RuntimeException(e);
            }
        }
        return datatypeFactory.newXMLGregorianCalendar((GregorianCalendar) calendar);
    }

}
