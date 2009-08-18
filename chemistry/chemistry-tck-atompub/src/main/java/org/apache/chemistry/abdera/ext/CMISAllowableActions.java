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
import org.apache.abdera.model.ElementWrapper;


/**
 * CMIS Allowable Actions Element Wrapper for the Abdera ATOM library.
 */
public class CMISAllowableActions extends ElementWrapper {
    
    /**
     * @param internal
     */
    public CMISAllowableActions(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     */
    public CMISAllowableActions(Factory factory) {
        super(factory, CMISConstants.ALLOWABLE_ACTIONS);
    }

    /**
     * Gets all allowable actions names
     * 
     * @return list of property names
     */
    public List<String> getNames() {
        List<Element> actions = getElements();
        List<String> names = new ArrayList<String>(actions.size());
        for (Element action : actions) {
            if (action instanceof CMISAllowableAction) {
                names.add(((CMISAllowableAction) action).getName());
            }
        }
        return names;
    }

    /**
     * Finds action by name
     * 
     * @param name
     *            property name
     * @return property
     */
    public CMISAllowableAction find(String name) {
        List<Element> elements = getElements();
        for (Element element : elements) {
            if (element instanceof CMISAllowableAction) {
                CMISAllowableAction action = (CMISAllowableAction) element;
                if (action.getName().equals(name)) {
                    return action;
                }
            }
        }
        return null;
    }

    /**
     * Is Action allowed?
     * 
     * @param name
     * @return
     */
    public boolean isAllowed(String name) {
        CMISAllowableAction action = find(name);
        return (action == null) ? false : action.isAllowed();
    }
}
