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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub;

import javax.xml.namespace.QName;

/**
 * Utility class providing CMIS constants and names for AtomPub.
 */
public class CMIS {

    // utility class
    private CMIS() {
    }

    public static final String CMIS_NS = "http://docs.oasis-open.org/ns/cmis/core/200901";

    public static final String CMIS_PREFIX = "cmis";

    public static QName CMISName(String localPart) {
        return new QName(CMIS_NS, localPart, CMIS_PREFIX);
    }

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

    public static final QName VERSION_SUPPORTED = CMISName("cmisVersionSupported");

    public static final QName REPOSITORY_SPECIFIC_INFORMATION = CMISName("repositorySpecificInformation");

    public static final QName COLLECTION_TYPE = CMISName("collectionType");

    public static final QName DOCUMENT_TYPE = CMISName("documentType");

    public static final QName FOLDER_TYPE = CMISName("folderType");

    public static final QName RELATIONSHIP_TYPE = CMISName("relationshipType");

    public static final QName POLICY_TYPE = CMISName("policyType");

    public static final QName BASE_TYPE_ID = CMISName("baseTypeId");

    public static final QName ID = CMISName("id");

    public static final QName LOCAL_NAME = CMISName("localName");

    public static final QName LOCAL_NAMESPACE = CMISName("localNamespace");

    public static final QName QUERY_NAME = CMISName("queryName");

    public static final QName DISPLAY_NAME = CMISName("displayName");

    public static final QName PARENT_ID = CMISName("parentId");

    public static final QName DESCRIPTION = CMISName("description");

    public static final QName CREATABLE = CMISName("creatable");

    public static final QName FILEABLE = CMISName("fileable");

    public static final QName QUERYABLE = CMISName("queryable");

    public static final QName CONTROLLABLE = CMISName("controllable");

    public static final QName VERSIONABLE = CMISName("versionable");

    public static final QName CONTENT_STREAM_ALLOWED = CMISName("contentStreamAllowed");

    public static final QName INCLUDED_IN_SUPERTYPE_QUERY = CMISName("includedInSupertypeQuery");

    public static final QName PROPERTY_STRING_DEFINITION = CMISName("propertyStringDefinition");

    public static final QName PROPERTY_DECIMAL_DEFINITION = CMISName("propertyDecimalDefinition");

    public static final QName PROPERTY_INTEGER_DEFINITION = CMISName("propertyIntegerDefinition");

    public static final QName PROPERTY_BOOLEAN_DEFINITION = CMISName("propertyBooleanDefinition");

    public static final QName PROPERTY_DATETIME_DEFINITION = CMISName("propertyDateTimeDefinition");

    public static final QName PROPERTY_URI_DEFINITION = CMISName("propertyUriDefinition");

    public static final QName PROPERTY_ID_DEFINITION = CMISName("propertyIdDefinition");

    public static final QName PROPERTY_XML_DEFINITION = CMISName("propertyXmlDefinition");

    public static final QName PROPERTY_HTML_DEFINITION = CMISName("propertyHtmlDefinition");

    public static final QName PROPERTY_TYPE = CMISName("propertyType");

    public static final QName CARDINALITY = CMISName("cardinality");

    public static final QName UPDATABILITY = CMISName("updatability");

    public static final QName INHERITED = CMISName("inherited");

    public static final QName REQUIRED = CMISName("required");

    public static final QName ORDERABLE = CMISName("orderable");

    public static final QName DEFAULT_VALUE = CMISName("defaultValue");

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

    public static final QName PDID = CMISName("pdid");

    public static final QName LOCALNAME = CMISName("localname");

    public static final QName DISPLAYNAME = CMISName("displayname");

    public static final QName VALUE = CMISName("value");

    public static final QName ALLOWABLE_ACTIONS = CMISName("allowableActions");

    public static final QName QUERY = CMISName("query");

    public static final QName STATEMENT = CMISName("statement");

    public static final QName SEARCH_ALL_VERSIONS = CMISName("searchAllVersions");

    public static final QName MAX_ITEMS = CMISName("maxItems");

    public static final QName SKIP_COUNT = CMISName("skipCount");

    public static final QName INCLUDE_ALLOWABLE_ACTIONS = CMISName("includeAllowableActions");

    /*
     * ----- CMIS Collection Types -----
     */

    public static final String COL_ROOT_CHILDREN = "rootchildren";

    public static final String COL_ROOT_DESCENDANTS = "rootdescendants";

    public static final String COL_UNFILED = "unfiled";

    public static final String COL_CHECKED_OUT = "checkedout";

    public static final String COL_TYPES_CHILDREN = "typeschildren";

    public static final String COL_TYPES_DESCENDANTS = "typesdescendants";

    public static final String COL_QUERY = "query";

    /*
     * ----- CMIS Link Types -----
     */

    public static final String LINK_REPOSITORY = "repository";

    public static final String LINK_LATEST_VERSION = "latestversion";

    public static final String LINK_SOURCE = "source";

    public static final String LINK_TARGET = "target";

    public static final String LINK_TYPE = "type";

    public static final String LINK_ALLOWABLE_ACTIONS = "allowableactions";

    public static final String LINK_STREAM = "stream";

    public static final String LINK_PARENTS = "parents";

    public static final String LINK_CHILDREN = "children";

    public static final String LINK_DESCENDANTS = "descendants";

    public static final String LINK_ALL_VERSIONS = "allversions";

    public static final String LINK_PWC = "pwc";

    public static final String LINK_RELATIONSHIPS = "relationships";

    public static final String LINK_POLICIES = "policies";

}
