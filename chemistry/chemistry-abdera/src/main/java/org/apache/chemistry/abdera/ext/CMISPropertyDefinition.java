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
     * Gets the property query name
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
     * String Property
     */
    public static class CMISPropertyStringDefinition extends CMISPropertyDefinition {
        public CMISPropertyStringDefinition(Element internal) {
            super(internal);
        }

        public CMISPropertyStringDefinition(Factory factory, QName qname) {
            super(factory, qname);
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
