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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;


/**
 * CMIS Type Definition for the Abdera ATOM library.
 */
public class CMISTypeDefinition extends ExtensibleElementWrapper {
    
    /**
     * @param internal
     */
    public CMISTypeDefinition(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     */
    public CMISTypeDefinition(Factory factory) {
        super(factory, CMISConstants.TYPE_DEFINITION);
    }

    /**
     * Determines whether objects of this type are controllable by ACLs.
     * 
     * @return <code>true</code> if objects of this type are controllable by
     *         ACLs
     */
    public boolean getControllableACL() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_CONTROLLABLE_ACL).getText());
    }

    /**
     * Gets all Property Definitions for this CMIS Type
     * 
     * @return property definitions
     */
    public List<CMISPropertyDefinition> getPropertyDefinitions() {
        List<Element> elements = getElements();
        List<CMISPropertyDefinition> propertyDefs = new ArrayList<CMISPropertyDefinition>(elements.size());
        for (Element element : elements) {
            if (element instanceof CMISPropertyDefinition) {
                propertyDefs.add((CMISPropertyDefinition)element);
            }
        }
        return propertyDefs;
    }

    /**
     * Gets Property Definition
     * 
     * @param id property definition id
     * @return property definition
     */
    public CMISPropertyDefinition getPropertyDefinition(String id) {
        List<Element> elements = getElements();
        for (Element element : elements) {
            if (element instanceof CMISPropertyDefinition) {
                CMISPropertyDefinition propDef = (CMISPropertyDefinition)element;
                if (id.equals(propDef.getId())) {
                    return propDef;
                }
            }
        }
        return null;
    }
    
}
