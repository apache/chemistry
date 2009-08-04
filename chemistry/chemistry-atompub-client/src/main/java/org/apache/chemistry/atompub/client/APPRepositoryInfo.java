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
package org.apache.chemistry.atompub.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.ACLCapabilityType;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 *
 */
public class APPRepositoryInfo implements RepositoryInfo {

    public static final Log log = LogFactory.getLog(APPRepositoryInfo.class);

    protected final Map<String, Object> map;

    protected final RepositoryCapabilities caps;

    protected final Set<BaseType> changeLogBaseTypes;

    public APPRepositoryInfo(RepositoryCapabilities caps,
            Map<String, Object> map, Set<BaseType> changeLogBaseTypes) {
        this.map = map;
        this.caps = caps;
        this.changeLogBaseTypes = changeLogBaseTypes.isEmpty() ? Collections.<BaseType> emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(changeLogBaseTypes));
    }

    public String getString(String name) {
        return (String) map.get(name);
    }

    public boolean getBoolean(String name) {
        return Boolean.parseBoolean((String) map.get(name));
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

    public URI getThinClientURI() {
        String uri = getString(CMIS.THIN_CLIENT_URI.getLocalPart());
        try {
            return uri == null ? null : new URI(uri);
        } catch (URISyntaxException e) {
            log.error("Invalid URI: " + uri, e);
            return null;
        }
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

    public Set<BaseType> getChangeLogBaseTypes() {
        return changeLogBaseTypes;
    }

    public boolean isChangeLogIncomplete() {
        return getBoolean(CMIS.CHANGES_INCOMPLETE.getLocalPart());
    }

    public String getLatestChangeLogToken() {
        return getString(CMIS.LATEST_CHANGE_LOG_TOKEN.getLocalPart());
    }

    public ACLCapabilityType getACLCapabilityType() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<RepositoryEntry> getRelatedRepositories() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + getName() + ')';
    }

}
