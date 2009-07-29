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
package org.apache.chemistry.atompub;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.chemistry.PropertyType;
import org.apache.chemistry.util.GregorianCalendar;

/**
 * Adapter between a Java value and a XML String representation.
 */
public abstract class ValueAdapter {

    public abstract Serializable readValue(String xml);

    public abstract String writeValue(Serializable val);

    public abstract Serializable[] createArray(int size);

    public abstract QName getPropertyQName();

    public abstract PropertyType getPropertyType();

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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_STRING;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.STRING;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_DECIMAL;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.DECIMAL;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_INTEGER;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.INTEGER;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_BOOLEAN;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.BOOLEAN;
        }
    }

    protected static final class DateTimeValueAdapter extends ValueAdapter {
        @Override
        public Serializable readValue(String xml) {
            return GregorianCalendar.fromAtomPub(xml);
        }

        // accepts both Calendar and Date
        @Override
        public String writeValue(Serializable val) {
            return val instanceof Calendar ? GregorianCalendar.toAtomPub((Calendar) val)
                    : GregorianCalendar.toAtomPub((Date) val);
        }

        @Override
        public Serializable[] createArray(int size) {
            return new Calendar[size];
        }

        @Override
        public QName getPropertyQName() {
            return CMIS.PROPERTY_DATETIME;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.DATETIME;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_URI;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.URI;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_ID;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.ID;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_XML;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.XML;
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_HTML;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.HTML;
        }
    }

    protected static final class XhtmlValueAdapter extends ValueAdapter {
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
        public QName getPropertyQName() {
            return CMIS.PROPERTY_XHTML;
        }

        @Override
        public PropertyType getPropertyType() {
            return PropertyType.XHTML;
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

    public static final ValueAdapter XHTML = new XhtmlValueAdapter();

    protected static final Map<PropertyType, ValueAdapter> byPropertyType = new HashMap<PropertyType, ValueAdapter>();

    protected static final Map<QName, ValueAdapter> byQName = new HashMap<QName, ValueAdapter>();

    static {
        for (ValueAdapter va : Arrays.asList( //
                STRING, //
                DECIMAL, //
                INTEGER, //
                BOOLEAN, //
                DATETIME, //
                URI, //
                ID, //
                XML, //
                HTML, //
                XHTML //
        )) {
            byPropertyType.put(va.getPropertyType(), va);
            byQName.put(va.getPropertyQName(), va);
        }
    }

    public static ValueAdapter getAdapter(PropertyType type) {
        return byPropertyType.get(type);
    }

    public static ValueAdapter getAdapter(QName qname) {
        return byQName.get(qname);
    }

}
