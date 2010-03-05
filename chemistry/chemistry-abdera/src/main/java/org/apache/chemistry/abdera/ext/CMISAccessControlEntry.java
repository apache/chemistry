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
 * CMIS Access Control Entry for the Abdera ATOM library.
 */
public class CMISAccessControlEntry extends ExtensibleElementWrapper {

    /**
     * The Constructor.
     * 
     * @param internal
     *            the internal element
     */
    public CMISAccessControlEntry(Element internal) {
        super(internal);
    }

    /**
     * The Constructor.
     * 
     * @param factory
     *            the factory
     */
    public CMISAccessControlEntry(Factory factory) {
        super(factory, CMISConstants.PERMISSION);
    }

    /**
     * Gets the identifier of a principal, i.e. a user, group, or role.
     * 
     * @return the principal id
     */
    public String getPrincipalId() {
        return getFirstChild(CMISConstants.PRINCIPAL).getFirstChild(CMISConstants.PRINCIPAL_ID).getText();
    }

    /**
     * Gets the permission names.
     * 
     * @return the permission names
     */
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<String>(5);
        for (Element permission = getFirstChild(CMISConstants.PERMISSION); permission != null; permission = permission
                .getNextSibling(CMISConstants.PERMISSION)) {
            permissions.add(permission.getText());
        }
        return permissions;
    }

    /**
     * Determines whether the ACE is directly assigned to its object.
     * 
     * @return <code>true</code> if the ACE is directly assigned to its object
     *         or <code>false</code> if the ACE is somehow derived.
     */
    public boolean isDirect() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.DIRECT).getText());
    }
}
