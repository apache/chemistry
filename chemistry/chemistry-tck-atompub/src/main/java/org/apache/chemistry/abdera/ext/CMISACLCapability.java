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

import java.util.HashSet;
import java.util.Set;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * CMIS Repository ACL Capability metadata for the Abdera ATOM library.
 */
public class CMISACLCapability extends ExtensibleElementWrapper {

    public CMISACLCapability(Element internal) {
        super(internal);
    }

    public CMISACLCapability(Factory factory) {
        super(factory, CMISConstants.ACL_CAPABILITY);
    }

    public String getSupportedPermissions() {
        return getFirstChild(CMISConstants.ACL_SUPPORTED_PERMISSIONS).getText();
    }

    public String getPropagation() {
        return getFirstChild(CMISConstants.ACL_PROPAGATION).getText();
    }

    public Set<String> getRepositoryPermissions() {
        Set<String> permissions = new HashSet<String>(51);
        for (Element permission = getFirstChild(CMISConstants.ACL_PERMISSIONS); permission != null; permission = permission
                .getNextSibling(CMISConstants.ACL_PERMISSIONS)) {
            permissions.add(permission.getFirstChild(CMISConstants.ACL_PERMISSION).getText());
        }
        return permissions;
    }
}
