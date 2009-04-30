/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry.atompub;

import javax.xml.namespace.QName;

/**
 * Utility class providing CMIS constants and names for AtomPub.
 *
 * @author Florent Guillaume
 */
public class CMIS {

    // utility class
    private CMIS() {
    }

    public static QName CMISName(String localPart) {
        return new QName(CMIS_NS, localPart, CMIS_PREFIX);
    }

    public static final String CMIS_NS = "http://www.cmis.org/2008/05";

    public static final String CMIS_PREFIX = "cmis";

    /*
     * ----- XML Qualified Names -----
     */

    public static final QName REPOSITORY_INFO = CMISName("repositoryInfo");

    public static final QName REPOSITORY_ID = CMISName("repositoryId");

    public static final QName REPOSITORY_NAME = CMISName("repositoryName");

    public static final QName REPOSITORY_RELATIONSHIP = CMISName("repositoryRelationship");

    public static final QName REPOSITORY_DESCRIPTION = CMISName("repositoryDescription");

    public static final QName VENDOR_NAME = CMISName("vendorName");

    public static final QName PRODUCT_NAME = CMISName("productName");

    public static final QName PRODUCT_VERSION = CMISName("productVersion");

    public static final QName ROOT_FOLDER_ID = CMISName("rootFolderId");

    public static final QName CAPABILITIES = CMISName("capabilities");

    public static final QName CAPABILITY_MULTIFILING = CMISName("capabilityMultifiling");

    public static final QName CAPABILITY_UNFILING = CMISName("capabilityUnfiling");

    public static final QName CAPABILITY_VERSION_SPECIFIC_FILING = CMISName("capabilityVersionSpecificFiling");

    public static final QName CAPABILITY_PWC_UPDATEABLE = CMISName("capabilityPWCUpdateable");

    public static final QName CAPABILITY_PWC_SEARCHABLE = CMISName("capabilityPWCSearchable");

    public static final QName CAPABILITY_ALL_VERSIONS_SEARCHABLE = CMISName("capabilityAllVersionsSearchable");

    public static final QName CAPABILITY_QUERY = CMISName("capabilityQuery");

    public static final QName CAPABILITY_JOIN = CMISName("capabilityJoin");

    public static final QName CAPABILITY_FULL_TEXT = CMISName("capabilityFullText");

    public static final QName VERSIONS_SUPPORTED = CMISName("cmisVersionsSupported");

    public static final QName REPOSITORY_SPECIFIC_INFORMATION = CMISName("repositorySpecificInformation");

    public static final QName COLLECTION_TYPE = CMISName("collectionType");

    public static final QName DOCUMENT_TYPE = CMISName("documentType");

    public static final QName TYPE_ID = CMISName("typeId");

    public static final QName QUERY_NAME = CMISName("queryName");

    public static final QName DISPLAY_NAME = CMISName("displayName");

    public static final QName BASE_TYPE = CMISName("baseType");

    public static final QName BASE_TYPE_QUERY_NAME = CMISName("baseTypeQueryName");

    public static final QName PARENT_ID = CMISName("parentId");

    public static final QName DESCRIPTION = CMISName("description");

    public static final QName CREATABLE = CMISName("creatable");

    public static final QName FILEABLE = CMISName("fileable");

    public static final QName QUERYABLE = CMISName("queryable");

    public static final QName CONTROLLABLE = CMISName("controllable");

    public static final QName VERSIONABLE = CMISName("versionable");

    public static final QName OBJECT = CMISName("object");

    public static final QName PROPERTIES = CMISName("properties");

    public static final QName PROPERTY_STRING = CMISName("propertyString");

    public static final QName PROPERTY_DECIMAL = CMISName("propertyDecimal");

    public static final QName PROPERTY_INTEGER = CMISName("propertyInteger");

    public static final QName PROPERTY_BOOLEAN = CMISName("propertyBoolean");

    public static final QName PROPERTY_DATETIME = CMISName("propertyDateTime");

    public static final QName PROPERTY_URI = CMISName("propertyUri");

    public static final QName PROPERTY_ID = CMISName("propertyId");

    public static final QName PROPERTY_XML = CMISName("propertyXml");

    public static final QName PROPERTY_HTML = CMISName("propertyHtml");

    public static final QName NAME = CMISName("name");

    public static final QName VALUE = CMISName("value");

    public static final QName ALLOWABLE_ACTIONS = CMISName("allowableActions");

    /*
     * ----- CMIS Collection Types -----
     */

    public static final String COL_ROOT_CHILDREN = "root-children";

    public static final String COL_ROOT_DESCENDANTS = "root-descendants";

    public static final String COL_UNFILED = "unfiled";

    public static final String COL_CHECKED_OUT = "checkedout";

    public static final String COL_TYPES_CHILDREN = "types-children";

    public static final String COL_TYPES_DESCENDANTS = "types-descendants";

    public static final String COL_QUERY = "query";

    /*
     * ----- CMIS Link Types -----
     */

    public static final String LINK_REPOSITORY = "cmis-repository";

    public static final String LINK_LATEST_VERSION = "cmis-latestversion";

    public static final String LINK_PARENT = "cmis-parent";

    public static final String LINK_SOURCE = "cmis-source";

    public static final String LINK_TARGET = "cmis-target";

    public static final String LINK_TYPE = "cmis-type";

    public static final String LINK_ALLOWABLE_ACTIONS = "cmis-allowableactions";

    public static final String LINK_STREAM = "cmis-stream";

    public static final String LINK_PARENTS = "cmis-parents";

    public static final String LINK_CHILDREN = "cmis-children";

    public static final String LINK_DESCENDANTS = "cmis-descendants";

    public static final String LINK_ALL_VERSIONS = "cmis-allversions";

    public static final String LINK_RELATIONSHIPS = "cmis-relationships";

    public static final String LINK_POLICIES = "cmis-policies";

}
