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
 * CMIS Properties Element Wrapper for the Abdera ATOM library.
 */
public class CMISProperties extends ExtensibleElementWrapper {
    
    /**
     * @param internal
     */
    public CMISProperties(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     */
    public CMISProperties(Factory factory) {
        super(factory, CMISConstants.PROPERTIES);
    }

    /**
     * Gets all property ids
     * 
     * @return list of property ids
     */
    public List<String> getIds() {
        List<CMISProperty> props = getElements();
        List<String> ids = new ArrayList<String>(props.size());
        for (CMISProperty prop : props) {
            ids.add(prop.getId());
        }
        return ids;
    }

    /**
     * Finds property by id
     * 
     * @param id
     *            property id
     * @return property
     */
    public CMISProperty find(String id) {
        List<Element> elements = getElements();
        for (Element element : elements) {
            if (element instanceof CMISProperty) {
                CMISProperty prop = (CMISProperty) element;
                if (id.equals(prop.getId())) {
                    return prop;
                }
            }
        }
        return null;
    }

}
