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
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client.app.model;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.apache.chemistry.ObjectId;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.w3c.dom.Document;

/**
 *
 */
public class APPRepositoryInfo implements RepositoryInfo {

    protected Map<String, Object> map;

    protected RepositoryCapabilities caps;

    public APPRepositoryInfo(RepositoryCapabilities caps,
            Map<String, Object> map) {
        this.map = map;
        this.caps = caps;
    }

    public URI getURI() {
        // TODO: what for?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getString(String name) {
        return (String) map.get(name);
    }

    public String getId() {
        return getString(CMIS.REPOSITORY_ID.getLocalPart());
    }

    public String getName() {
        return getString(CMIS.REPOSITORY_NAME.getLocalPart());
    }

    public String getRelationshipName() {
        return getString(CMIS.REPOSITORY_RELATIONSHIP.getLocalPart());
    }

    public String getDescription() {
        return getString(CMIS.REPOSITORY_DESCRIPTION.getLocalPart());
    }

    public String getProductName() {
        return getString(CMIS.PRODUCT_NAME.getLocalPart());
    }

    public String getProductVersion() {
        return getString(CMIS.PRODUCT_VERSION.getLocalPart());
    }

    public ObjectId getRootFolderId() {
        String id = getString(CMIS.ROOT_FOLDER_ID.getLocalPart());
        return new SimpleObjectId(id);
    }

    public String getVendorName() {
        return getString(CMIS.VENDOR_NAME.getLocalPart());
    }

    public String getVersionSupported() {
        return getString(CMIS.VERSION_SUPPORTED.getLocalPart());
    }

    public Document getRepositorySpecificInformation() {
        return (Document) map.get(CMIS.REPOSITORY_SPECIFIC_INFORMATION.getLocalPart());
    }

    public RepositoryCapabilities getCapabilities() {
        return caps;
    }

    public Collection<RepositoryEntry> getRelatedRepositories() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String toString() {
        return getName() + " - " + getURI();
    }
}
