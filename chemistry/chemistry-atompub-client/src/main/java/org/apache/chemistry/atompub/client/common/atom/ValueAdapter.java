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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.model.AtomDate;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.atompub.CMIS;

/**
 *
 */
public abstract class ValueAdapter {

    public abstract Serializable readValue(String xml);

    public abstract String writeValue(Serializable val);

    public abstract Serializable[] createArray(int size);

    public abstract QName getPropertyName();

    public static final ValueAdapter STRING = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return xml;
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new String[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_STRING;
        }
    };

    public static final ValueAdapter XML = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return xml;
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new String[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_XML;
        }
    };

    public static final ValueAdapter HTML = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return xml;
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new String[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_HTML;
        }
    };

    public static final ValueAdapter BOOLEAN = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return Boolean.valueOf(xml);
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new Boolean[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_BOOLEAN;
        }
    };

    public static final ValueAdapter INTEGER = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return Integer.valueOf(xml);
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new Integer[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_INTEGER;
        }
    };

    public static final ValueAdapter DECIMAL = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return new BigDecimal(xml);
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new BigDecimal[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_DECIMAL;
        }
    };

    public static final ValueAdapter DATE = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return AtomDate.valueOf(xml).getCalendar();
        }

        // accepts both Calendar and Date
        @Override
        public String writeValue(Serializable val) {
            return val.getClass() == Calendar.class ? AtomDate.format(((Calendar) val).getTime())
                    : AtomDate.format((Date) val);
        }

        @Override
        public Serializable[] createArray(int size) {
            return new Calendar[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_DATETIME;
        }
    };

    public static final ValueAdapter ID = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            return xml;
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new String[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_ID;
        }
    };

    public static final ValueAdapter URI = new ValueAdapter() {
        @Override
        public Serializable readValue(String xml) {
            try {
                return new URI(xml);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid URI: " + xml);
            }
        }

        @Override
        public String writeValue(Serializable val) {
            return val.toString();
        }

        @Override
        public Serializable[] createArray(int size) {
            return new String[size];
        }

        @Override
        public QName getPropertyName() {
            return CMIS.PROPERTY_URI;
        }
    };

    private final static Map<String, ValueAdapter> adapters = new HashMap<String, ValueAdapter>();
    static {
        adapters.put("String", STRING);
        adapters.put("Boolean", BOOLEAN);
        adapters.put("Integer", INTEGER);
        adapters.put("Decimal", DECIMAL);
        adapters.put("DateTime", DATE);
        adapters.put("Id", ID);
        adapters.put("Uri", URI);
        adapters.put("Xml", XML);
        adapters.put("Html", HTML);

        adapters.put("string", STRING);
        adapters.put("boolean", BOOLEAN);
        adapters.put("integer", INTEGER);
        adapters.put("decimal", DECIMAL);
        adapters.put("datetime", DATE);
        adapters.put("id", ID);
        adapters.put("uri", URI);
        adapters.put("xml", XML);
        adapters.put("html", HTML);

    }

    public static void registerAdapter(String type, ValueAdapter va) {
        adapters.put(type, va);
    }

    public static ValueAdapter getAdapter(String type) {
        return adapters.get(type);
    }

    public static ValueAdapter getAdapter(PropertyType type) {
        switch (type.ordinal()) {
        case PropertyType.STRING_ORD:
            return ValueAdapter.STRING;
        case PropertyType.BOOLEAN_ORD:
            return ValueAdapter.BOOLEAN;
        case PropertyType.DATETIME_ORD:
            return ValueAdapter.DATE;
        case PropertyType.ID_ORD:
            return ValueAdapter.ID;
        case PropertyType.INTEGER_ORD:
            return ValueAdapter.INTEGER;
        case PropertyType.URI_ORD:
            return ValueAdapter.URI;
        case PropertyType.DECIMAL_ORD:
            return ValueAdapter.DECIMAL;
        case PropertyType.XML_ORD:
            return ValueAdapter.XML;
        case PropertyType.HTML_ORD:
            return ValueAdapter.HTML;
        }
        return null;
    }

}
