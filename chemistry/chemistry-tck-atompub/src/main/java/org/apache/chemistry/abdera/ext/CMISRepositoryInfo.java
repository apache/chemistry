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
 * CMIS Repository Info for the Abdera ATOM library.
 * 
 * Encapsulates access and modification of CMIS extension values to ATOM Service
 * Document.
 */
public class CMISRepositoryInfo extends ElementWrapper {
    
    public CMISRepositoryInfo(Element internal) {
        super(internal);
    }

    public CMISRepositoryInfo(Factory factory) {
        super(factory, CMISConstants.REPOSITORY_INFO);
    }

    public String getId() {
        Element child = getFirstChild(CMISConstants.REPOSITORY_ID);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getName() {
        Element child = getFirstChild(CMISConstants.REPOSITORY_NAME);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getRelatonship() {
        Element child = getFirstChild(CMISConstants.REPOSITORY_RELATIONSHIP);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getDescription() {
        Element child = getFirstChild(CMISConstants.REPOSITORY_DESCRIPTION);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getVendorName() {
        Element child = getFirstChild(CMISConstants.VENDOR_NAME);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getProductName() {
        Element child = getFirstChild(CMISConstants.PRODUCT_NAME);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getProductVersion() {
        Element child = getFirstChild(CMISConstants.PRODUCT_VERSION);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getRootFolderId() {
        Element child = getFirstChild(CMISConstants.ROOT_FOLDER_ID);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getVersionSupported() {
        Element child = getFirstChild(CMISConstants.VERSION_SUPPORTED);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public CMISCapabilities getCapabilities() {
        Element child = getFirstChild(CMISConstants.CAPABILITIES);
        if (child != null) {
            return (CMISCapabilities) child;
        }
        return null;
    }
}
