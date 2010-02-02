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
 *     David Ward, Alfresco
 */
package org.apache.chemistry.abdera.ext;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * CMIS Access Control List Element Wrapper for the Abdera ATOM library.
 */
public class CMISAccessControlList extends ExtensibleElementWrapper {

    /**
     * The Constructor.
     * 
     * @param internal
     *            the internal elemeny
     */
    public CMISAccessControlList(Element internal) {
        super(internal);
    }

    /**
     * The Constructor.
     * 
     * @param factory
     *            the factory
     */
    public CMISAccessControlList(Factory factory) {
        super(factory, CMISConstants.ACCESS_CONTROL_LIST);
    }

    /**
     * Gets all access control entries.
     * 
     * @return list of access control entries.
     */
    public List<CMISAccessControlEntry> getEntries() {
        List<Element> children = getElements();
        List<CMISAccessControlEntry> entries = new ArrayList<CMISAccessControlEntry>(children.size());
        for (Element child : children) {
            if (child instanceof CMISAccessControlEntry) {
                entries.add((CMISAccessControlEntry) child);
            }
        }
        return entries;
    }
}
