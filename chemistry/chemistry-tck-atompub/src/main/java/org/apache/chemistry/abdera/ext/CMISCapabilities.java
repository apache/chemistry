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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

/**
 * CMIS Repository Capabilities for the Abdera ATOM library.
 */
public class CMISCapabilities extends ElementWrapper {
    
    public CMISCapabilities(Element internal) {
        super(internal);
    }

    public CMISCapabilities(Factory factory) {
        super(factory, CMISConstants.REPOSITORY_INFO);
    }

    public boolean hasMultifiling() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_MULTIFILING);
        return Boolean.valueOf(child.getText());
    }

    public boolean hasUnfiling() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_UNFILING);
        return Boolean.valueOf(child.getText());
    }

    public boolean hasVersionSpecificFiling() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_VERSION_SPECIFIC_FILING);
        return Boolean.valueOf(child.getText());
    }

    public boolean isPWCUpdatable() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_PWC_UPDATEABLE);
        return Boolean.valueOf(child.getText());
    }

    public boolean isPWCSearchable() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_PWC_SEARCHABLE);
        return Boolean.valueOf(child.getText());
    }

    public boolean isAllVersionsSearchable() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_ALL_VERIONS_SEARCHABLE);
        return Boolean.valueOf(child.getText());
    }

    public boolean getDescendants() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_GET_DESCENDANTS);
        return Boolean.valueOf(child.getText());
    }

    public String getQuery() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_QUERY);
        return child.getText();
    }

    public String getJoin() {
        Element child = getFirstChild(CMISConstants.CAPABILITY_JOIN);
        return child.getText();
    }

}
