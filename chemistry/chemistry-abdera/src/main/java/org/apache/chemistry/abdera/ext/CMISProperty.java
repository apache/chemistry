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
 *     David Caruana, Alfresco
 */
package org.apache.chemistry.abdera.ext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;


/**
 * CMIS Property for the Abdera ATOM library.
 */
public abstract class CMISProperty extends ExtensibleElementWrapper {
    
    /**
     * @param internal
     */
    public CMISProperty(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISProperty(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * Gets the property type
     * 
     * @return type
     */
    public abstract String getType();

    /**
     * Gets the property id
     * 
     * @return name
     */
    public String getId() {
        return getAttributeValue(CMISConstants.PROPERTY_ID);
    }

    /**
     * Gets the property display name
     * 
     * @return display name
     */
    public String getDisplayName() {
        return getAttributeValue(CMISConstants.PROPERTY_DISPLAY_NAME);
    }

    /**
     * Is property value null?
     * 
     * @return true => null
     */
    public boolean isNull() {
        return getFirstChild() == null ? true : false;
    }

    /**
     * Is property value multi-valued?
     * 
     * @return true => more than one value exists
     */
    public boolean isMultiValued() {
        List<CMISValue> children = getElements();
        return children.size() > 1;
    }

    /**
     * Gets property value
     * 
     * NOTE: Assumes there's only one value. In case of multi-valued, returns
     * first value.
     * 
     * @return value (or null, if not specified)
     */
    public CMISValue getValue() {
        CMISValue child = (CMISValue) getFirstChild(CMISConstants.PROPERTY_VALUE);
        if (child != null) {
            return child;
        }
        return null;
    }

    /**
     * Gets property values
     * 
     * NOTE: Always returns a collection, even when only one value
     * 
     * @return
     */
    public List<CMISValue> getValues() {
        return getElements();
    }

    /**
     * Gets native value
     * 
     * NOTE: Short-cut for retrieving first value of property
     * 
     * @return property value (or null, if not specified)
     */
    public Object getNativeValue() {
        CMISValue value = getValue();
        return value == null ? null : value.getNativeValue();
    }

    /**
     * Gets native values
     * 
     * NOTE: Short-cut for retrieving values as multi-valued collection
     * 
     * @return
     */
    public List<Object> getNativeValues() {
        List<CMISValue> values = getValues();
        ArrayList<Object> nativeValues = new ArrayList<Object>(values.size());
        for (CMISValue value : values) {
            nativeValues.add(value.getNativeValue());
        }
        return nativeValues;
    }

    /**
     * Gets String value
     * 
     * NOTE: Short-cut for retrieving first value of property
     * 
     * @return string value
     */
    public String getStringValue() {
        CMISValue value = getValue();
        return value == null ? null : value.getStringValue();
    }

    /**
     * Gets Decimal value
     * 
     * NOTE: Short-cut for retrieving first value of property
     * 
     * @return decimal value
     */
    public BigDecimal getDecimalValue() {
        CMISValue value = getValue();
        return value == null ? null : value.getDecimalValue();
    }

    /**
     * Gets Integer value
     * 
     * NOTE: Short-cut for retrieving first value of property
     * 
     * @return integer value
     */
    public int getIntegerValue() {
        CMISValue value = getValue();
        return value == null ? null : value.getIntegerValue();
    }

    /**
     * Gets Boolean value
     * 
     * NOTE: Short-cut for retrieving first value of property
     * 
     * @return boolean value
     */
    public boolean getBooleanValue() {
        CMISValue value = getValue();
        return value == null ? null : value.getBooleanValue();
    }

    /**
     * Gets Date value
     * 
     * NOTE: Short-cut for retrieving first value of property
     * 
     * @return date value
     */
    public Date getDateValue() {
        CMISValue value = getValue();
        return value == null ? null : value.getDateValue();
    }

    /**
     * String Property
     */
    public static class CMISPropertyString extends CMISProperty {
        public CMISPropertyString(Element internal) {
            super(internal);
        }

        public CMISPropertyString(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.abdera.ext.cmis.CMISProperty#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_STRING;
        }
    }

    /**
     * Decimal Property
     */
    public static class CMISPropertyDecimal extends CMISProperty {
        public CMISPropertyDecimal(Element internal) {
            super(internal);
        }

        public CMISPropertyDecimal(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.abdera.ext.cmis.CMISProperty#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_DECIMAL;
        }
    }

    /**
     * Integer Property
     */
    public static class CMISPropertyInteger extends CMISProperty {
        public CMISPropertyInteger(Element internal) {
            super(internal);
        }

        public CMISPropertyInteger(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.abdera.ext.cmis.CMISProperty#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_INTEGER;
        }
    }

    /**
     * Boolean Property
     */
    public static class CMISPropertyBoolean extends CMISProperty {
        public CMISPropertyBoolean(Element internal) {
            super(internal);
        }

        public CMISPropertyBoolean(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.abdera.ext.cmis.CMISProperty#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_BOOLEAN;
        }
    }

    /**
     * DateTime Property
     */
    public static class CMISPropertyDateTime extends CMISProperty {
        public CMISPropertyDateTime(Element internal) {
            super(internal);
        }

        public CMISPropertyDateTime(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.abdera.ext.cmis.CMISProperty#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_DATETIME;
        }
    }

    /**
     * URI Property
     */
    public static class CMISPropertyUri extends CMISPropertyString {
        public CMISPropertyUri(Element internal) {
            super(internal);
        }

        public CMISPropertyUri(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.abdera.ext.cmis.CMISProperty.CMISPropertyString#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_URI;
        }
    }

    /**
     * ID Property
     */
    public static class CMISPropertyId extends CMISPropertyString {
        public CMISPropertyId(Element internal) {
            super(internal);
        }

        public CMISPropertyId(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.abdera.ext.cmis.CMISProperty.CMISPropertyString#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_ID;
        }
    }

    /**
     * HTML Property
     */
    public static class CMISPropertyHtml extends CMISPropertyString {
        public CMISPropertyHtml(Element internal) {
            super(internal);
        }

        public CMISPropertyHtml(Factory factory, QName qname) {
            super(factory, qname);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.abdera.ext.cmis.CMISProperty.CMISPropertyString#getType()
         */
        @Override
        public String getType() {
            return CMISConstants.PROP_TYPE_HTML;
        }
    }
}
