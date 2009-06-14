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
 * Adapter between a Java value and a XML String representation.
 */
public abstract class ValueAdapter {

    public abstract Serializable readValue(String xml);

    public abstract String writeValue(Serializable val);

    public abstract Serializable[] createArray(int size);

    public abstract QName getPropertyName();

    protected static final class StringValueAdapter extends ValueAdapter {
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
    }

    protected static final class DecimalValueAdapter extends ValueAdapter {
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
    }

    protected static final class IntegerValueAdapter extends ValueAdapter {
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
    }

    protected static final class BooleanValueAdapter extends ValueAdapter {
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
    }

    protected static final class DateTimeValueAdapter extends ValueAdapter {
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
    }

    protected static final class UriValueAdapter extends ValueAdapter {
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
    }

    protected static final class IdValueAdapter extends ValueAdapter {
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
    }

    protected static final class XmlValueAdapter extends ValueAdapter {
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
    }

    protected static final class HtmlValueAdapter extends ValueAdapter {
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
    }

    public static final ValueAdapter STRING = new StringValueAdapter();

    public static final ValueAdapter DECIMAL = new DecimalValueAdapter();

    public static final ValueAdapter INTEGER = new IntegerValueAdapter();

    public static final ValueAdapter BOOLEAN = new BooleanValueAdapter();

    public static final ValueAdapter DATETIME = new DateTimeValueAdapter();

    public static final ValueAdapter URI = new UriValueAdapter();

    public static final ValueAdapter ID = new IdValueAdapter();

    public static final ValueAdapter XML = new XmlValueAdapter();

    public static final ValueAdapter HTML = new HtmlValueAdapter();

    private static final Map<Integer, ValueAdapter> adapters = new HashMap<Integer, ValueAdapter>();
    static {
        adapters.put(Integer.valueOf(PropertyType.STRING_ORD), STRING);
        adapters.put(Integer.valueOf(PropertyType.DECIMAL_ORD), DECIMAL);
        adapters.put(Integer.valueOf(PropertyType.INTEGER_ORD), INTEGER);
        adapters.put(Integer.valueOf(PropertyType.BOOLEAN_ORD), BOOLEAN);
        adapters.put(Integer.valueOf(PropertyType.DATETIME_ORD), DATETIME);
        adapters.put(Integer.valueOf(PropertyType.URI_ORD), URI);
        adapters.put(Integer.valueOf(PropertyType.ID_ORD), ID);
        adapters.put(Integer.valueOf(PropertyType.XML_ORD), XML);
        adapters.put(Integer.valueOf(PropertyType.HTML_ORD), HTML);
    }

    public static ValueAdapter getAdapter(PropertyType type) {
        return adapters.get(Integer.valueOf(type.ordinal()));
    }

}
