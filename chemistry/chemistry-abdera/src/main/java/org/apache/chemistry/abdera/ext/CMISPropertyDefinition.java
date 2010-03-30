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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * CMIS Property Definition for the Abdera ATOM library.
 */
public abstract class CMISPropertyDefinition extends ExtensibleElementWrapper {

    /**
     * @param internal
     */
    public CMISPropertyDefinition(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISPropertyDefinition(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * Gets the property id
     * 
     * @return id
     */
    public String getId() {
        Element child = getFirstChild(CMISConstants.PROPDEF_ID);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property local name
     * 
     * @return local name
     */
    public String getLocalName() {
        Element child = getFirstChild(CMISConstants.PROPDEF_LOCAL_NAME);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property local namespace
     * 
     * @return local namespace
     */
    public String getLocalNamespace() {
        Element child = getFirstChild(CMISConstants.PROPDEF_LOCAL_NAMESPACE);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property display name
     * 
     * @return query name
     */
    public String getQueryName() {
        Element child = getFirstChild(CMISConstants.PROPDEF_QUERY_NAME);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property display name
     * 
     * @return local name
     */
    public String getDisplayName() {
        Element child = getFirstChild(CMISConstants.PROPDEF_DISPLAY_NAME);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property description
     * 
     * @return description
     */
    public String getDescription() {
        Element child = getFirstChild(CMISConstants.PROPDEF_DESCRIPTION);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property type
     * 
     * @return property type
     */
    public String getPropertyType() {
        Element child = getFirstChild(CMISConstants.PROPDEF_PROPERTY_TYPE);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property cardinality
     * 
     * @return cardinality
     */
    public String getCardinality() {
        Element child = getFirstChild(CMISConstants.PROPDEF_CARDINALITY);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property updatability
     * 
     * @return updatability
     */
    public String getUpdatability() {
        Element child = getFirstChild(CMISConstants.PROPDEF_UPDATABILITY);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Return true if the property is inherited
     * 
     * @return if the property is inherited
     */
    public Boolean getInherited() {
        Element child = getFirstChild(CMISConstants.PROPDEF_INHERITED);
        if (child != null) {
            return Boolean.parseBoolean(child.getText());
        }
        return null;
    }

    /**
     * Return true if the property is required
     * 
     * @return if the property is required
     */
    public Boolean getRequired() {
        Element child = getFirstChild(CMISConstants.PROPDEF_REQUIRED);
        if (child != null) {
            return Boolean.parseBoolean(child.getText());
        }
        return null;
    }

    /**
     * Return true if the property is queryable
     * 
     * @return if the property is queryable
     */
    public Boolean getQueryable() {
        Element child = getFirstChild(CMISConstants.PROPDEF_QUERYABLE);
        if (child != null) {
            return Boolean.parseBoolean(child.getText());
        }
        return null;
    }

    /**
     * Return true if the property is orderable
     * 
     * @return if the property is orderable
     */
    public Boolean getOrderable() {
        Element child = getFirstChild(CMISConstants.PROPDEF_ORDERABLE);
        if (child != null) {
            return Boolean.parseBoolean(child.getText());
        }
        return null;
    }

    /**
     * Return true if the property is open choice
     * 
     * @return if the property is open choice
     */
    public Boolean getOpenChoice() {
        Element child = getFirstChild(CMISConstants.PROPDEF_OPEN_CHOICE);
        if (child != null) {
            return Boolean.parseBoolean(child.getText());
        }
        return null;
    }

    /**
     * Gets the property default value
     * 
     * @return default value
     */
    public String getDefaultValue() {
        Element child = getFirstChild(CMISConstants.PROPDEF_DEFAULT_VALUE);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the property (top level) choices, nested choices should be navigated
     * through manually.
     * 
     * TODO: This method is very similar to getChoices in CMISChoice class. Better recursion could be introduced here.
     *  
     * @return choices
     */
    public List<CMISChoice> getChoices(boolean includeNestedChoices) {
        List<Element> children = getElements();
        List<CMISChoice> entries = new ArrayList<CMISChoice>(children.size());
        for (Element child : children) {
            if (child instanceof CMISChoice) {
                CMISChoice childChoice = (CMISChoice) child;
                entries.add(childChoice);
                if (includeNestedChoices)
                    entries.addAll(childChoice.getChoices(true));
            }
        }
        return entries;
    }

    /**
     * String Property
     */
    public static class CMISPropertyStringDefinition extends CMISPropertyDefinition {
        public CMISPropertyStringDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyStringDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }

        /**
         * Gets the property max length
         * 
         * @return resolution AKA max length
         */
        public Integer getResolution() {
            Element child = getFirstChild(CMISConstants.PROPDEF_STRING_RESOLUTION);
            if (child != null) {
                return Integer.parseInt(child.getText());
            }
            return null;
        }

    }

    /**
     * Decimal Property
     */
    public static class CMISPropertyDecimalDefinition extends CMISPropertyDefinition {
        public CMISPropertyDecimalDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyDecimalDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }

        /**
         * Gets the property min value
         * 
         * @return min value
         */
        public Integer getMinValue() {
            Element child = getFirstChild(CMISConstants.PROPDEF_INT_MIN_VALUE);
            if (child != null) {
                return Integer.parseInt(child.getText());
            }
            return null;
        }

        /**
         * Gets the property max value
         * 
         * @return max value
         */
        public Integer getMaxValue() {
            Element child = getFirstChild(CMISConstants.PROPDEF_INT_MAX_VALUE);
            if (child != null) {
                return Integer.parseInt(child.getText());
            }
            return null;
        }

    }

    /**
     * Integer Property
     */
    public static class CMISPropertyIntegerDefinition extends CMISPropertyDefinition {
        public CMISPropertyIntegerDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyIntegerDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Boolean Property
     */
    public static class CMISPropertyBooleanDefinition extends CMISPropertyDefinition {
        public CMISPropertyBooleanDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyBooleanDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * DateTime Property
     */
    public static class CMISPropertyDateTimeDefinition extends CMISPropertyDefinition {
        public CMISPropertyDateTimeDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyDateTimeDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }

        /**
         * Gets the property resolution
         * 
         * @return resolution
         */
        public String getResolution() {
            Element child = getFirstChild(CMISConstants.PROPDEF_DATE_RESOLUTION);
            if (child != null) {
                return child.getText();
            }
            return null;
        }
    }

    /**
     * URI Property
     */
    public static class CMISPropertyUriDefinition extends CMISPropertyDefinition {
        public CMISPropertyUriDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyUriDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * ID Property
     */
    public static class CMISPropertyIdDefinition extends CMISPropertyDefinition {
        public CMISPropertyIdDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyIdDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * HTML Property
     */
    public static class CMISPropertyHtmlDefinition extends CMISPropertyDefinition {
        public CMISPropertyHtmlDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyHtmlDefinition(Factory factory, QName qname) {
            super(factory, qname);
        }
    }
}
