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
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.abdera.ext;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * A CMIS Property Choice abstract wrapper. 
 * @see concrete datatype specific implementations 
 * @author gabriele
 * 
 */
public abstract class CMISChoice extends ExtensibleElementWrapper {

    /**
     * @param internal
     */
    public CMISChoice(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISChoice(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * Gets the property value
     * 
     * @return value
     */
    public String getValue() {
        Element child = getFirstChild(CMISConstants.PROPDEF_CHOICE_VALUE);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    /**
     * Gets the displayName
     * 
     * @return displayName
     */
    public String getDisplayName() {
        return getAttributeValue(CMISConstants.PROPDEF_CHOICE_DISPLAY_NAME);
    }

    /**
     * Get child choices at first level of depth
     * 
     * @return topLevel choices
     */
    public List<CMISChoice> getChoices() {
        return getChoices(false);
    }

    /**
     * Get choices for this property definition, including nested choices at any level of depth
     * 
     * @return a flattened list of all top level and nested choices
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
     * Concrete String Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceString extends CMISChoice {

        public CMISChoiceString(Element internal) {
            super(internal);
        }

        public CMISChoiceString(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Concrete Boolean Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceBoolean extends CMISChoice {

        public CMISChoiceBoolean(Element internal) {
            super(internal);
        }

        public CMISChoiceBoolean(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Concrete Integer Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceInteger extends CMISChoice {

        public CMISChoiceInteger(Element internal) {
            super(internal);
        }

        public CMISChoiceInteger(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Concrete Decimal Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceDecimal extends CMISChoice {

        public CMISChoiceDecimal(Element internal) {
            super(internal);
        }

        public CMISChoiceDecimal(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Concrete DateTime Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceDateTime extends CMISChoice {

        public CMISChoiceDateTime(Element internal) {
            super(internal);
        }

        public CMISChoiceDateTime(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    
    /**
     * Concrete Uri Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceUri extends CMISChoice {

        public CMISChoiceUri(Element internal) {
            super(internal);
        }

        public CMISChoiceUri(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Concrete Id Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceId extends CMISChoice {

        public CMISChoiceId(Element internal) {
            super(internal);
        }

        public CMISChoiceId(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

    /**
     * Concrete Html Choice Implementation 
     * @author gabriele
     * 
     */
    public static class CMISChoiceHtml extends CMISChoice {

        public CMISChoiceHtml(Element internal) {
            super(internal);
        }

        public CMISChoiceHtml(Factory factory, QName qname) {
            super(factory, qname);
        }
    }

}
