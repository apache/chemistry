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

import javax.xml.namespace.QName;


/**
 * CMIS Namespace and Schema definitions for the Abdera ATOM library.
 */
public interface CMISConstants {
    
    // Namespace
    public static final String CMIS_NS = "http://docs.oasis-open.org/ns/cmis/core/200908/";
    public static final String CMISRA_NS = "http://docs.oasis-open.org/ns/cmis/restatom/200908/";
    public static final String CMISLINK_NS = "http://docs.oasis-open.org/ns/cmis/link/200908/";

    // Mimetypes
    public static final String MIMETYPE_APP = "application/atomsvc+xml";
    public static final String MIMETYPE_ATOM = "application/atom+xml";
    public static final String MIMETYPE_ENTRY = "application/atom+xml;type=entry";
    public static final String MIMETYPE_FEED = "application/atom+xml;type=feed";
    public static final String MIMETYPE_TEXT = "text/plain";
    public static final String MIMETYPE_CMIS_QUERY = "application/cmisquery+xml";
    public static final String MIMETYPE_CMIS_ALLOWABLE_ACTIONS = "application/cmisallowableactions+xml";
    public static final String MIMETYPE_CMISATOM = "application/cmisatom+xml";
    public static final String MIMETYPE_CMISTREE = "application/cmistree+xml";

    // CMIS Service Document
    public static final QName COLLECTION_TYPE = new QName(CMISRA_NS, "collectionType");
    public static final String COLLECTION_ROOT = "root";
    public static final String COLLECTION_CHECKEDOUT = "checkedout";
    public static final String COLLECTION_UNFILED = "unfiled";
    public static final String COLLECTION_TYPES = "types";
    public static final String COLLECTION_QUERY = "query";

    // CMIS Repository Info
    public static final QName REPOSITORY_INFO = new QName(CMISRA_NS, "repositoryInfo");
    public static final QName REPOSITORY_ID = new QName(CMIS_NS, "repositoryId");
    public static final QName REPOSITORY_NAME = new QName(CMIS_NS, "repositoryName");
    public static final QName REPOSITORY_RELATIONSHIP = new QName(CMIS_NS, "repositoryRelationship");
    public static final QName REPOSITORY_DESCRIPTION = new QName(CMIS_NS, "repositoryDescription");
    public static final QName VENDOR_NAME = new QName(CMIS_NS, "vendorName");
    public static final QName PRODUCT_NAME = new QName(CMIS_NS, "productName");
    public static final QName PRODUCT_VERSION = new QName(CMIS_NS, "productVersion");
    public static final QName ROOT_FOLDER_ID = new QName(CMIS_NS, "rootFolderId");
    public static final QName VERSION_SUPPORTED = new QName(CMIS_NS, "cmisVersionSupported");

    // CMIS URI Templates
    public static final QName URI_TEMPLATE = new QName(CMISRA_NS, "uritemplate");
    public static final QName URI_TEMPLATE_PATH = new QName(CMISRA_NS, "template");
    public static final QName URI_TEMPLATE_TYPE = new QName(CMISRA_NS, "type");
    public static final QName URI_TEMPLATE_MEDIATYPE = new QName(CMISRA_NS, "mediatype");
    public static final String URI_OBJECT_BY_ID = "objectbyid";
    public static final String URI_OBJECT_BY_PATH = "objectbypath";
    public static final String URI_QUERY = "query";
    public static final String URI_TYPE_BY_ID = "typebyid";
    
    // CMIS Capabilities
    public static final QName CAPABILITIES = new QName(CMIS_NS, "capabilities");
    public static final QName CAPABILITY_GET_DESCENDANTS = new QName(CMIS_NS, "capabilityGetDescendants");
    public static final QName CAPABILITY_MULTIFILING = new QName(CMIS_NS, "capabilityMultifiling");
    public static final QName CAPABILITY_UNFILING = new QName(CMIS_NS, "capabilityUnfiling");
    public static final QName CAPABILITY_VERSION_SPECIFIC_FILING = new QName(CMIS_NS, "capabilityVersionSpecificFiling");
    public static final QName CAPABILITY_PWC_UPDATEABLE = new QName(CMIS_NS, "capabilityPWCUpdateable");
    public static final QName CAPABILITY_PWC_SEARCHABLE = new QName(CMIS_NS, "capabilityPWCSearchable");
    public static final QName CAPABILITY_ALL_VERIONS_SEARCHABLE = new QName(CMIS_NS, "capabilityAllVersionsSearchable");
    public static final QName CAPABILITY_QUERY = new QName(CMIS_NS, "capabilityQuery");
    public static final QName CAPABILITY_JOIN = new QName(CMIS_NS, "capabilityJoin");

    // CMIS Object
    public static final QName OBJECT = new QName(CMISRA_NS, "object");
    public static final QName PROPERTIES = new QName(CMIS_NS, "properties");
    public static final QName PROPERTY_ID = new QName("propertyDefinitionId");
    public static final QName PROPERTY_DISPLAY_NAME = new QName("displayName");
    public static final QName PROPERTY_VALUE = new QName(CMIS_NS, "value");
    public static final QName STRING_PROPERTY = new QName(CMIS_NS, "propertyString");
    public static final QName DECIMAL_PROPERTY = new QName(CMIS_NS, "propertyDecimal");
    public static final QName INTEGER_PROPERTY = new QName(CMIS_NS, "propertyInteger");
    public static final QName BOOLEAN_PROPERTY = new QName(CMIS_NS, "propertyBoolean");
    public static final QName DATETIME_PROPERTY = new QName(CMIS_NS, "propertyDateTime");
    public static final QName URI_PROPERTY = new QName(CMIS_NS, "propertyUri");
    public static final QName ID_PROPERTY = new QName(CMIS_NS, "propertyId");
    public static final QName HTML_PROPERTY = new QName(CMIS_NS, "propertyHtml");

    // CMIS Content
    public static final QName CONTENT = new QName(CMISRA_NS, "content");
    public static final QName CONTENT_MEDIATYPE = new QName(CMISRA_NS, "mediatype");
    public static final QName CONTENT_BASE64 = new QName(CMISRA_NS, "base64");
    
    // CMIS Type Definition
    public static final QName TYPE_DEFINITION = new QName(CMISRA_NS, "type");
    public static final QName TYPE_ID = new QName(CMIS_NS, "id");
    public static final QName TYPE_LOCAL_NAME = new QName(CMIS_NS, "localName");
    public static final QName TYPE_LOCAL_NAMESPACE = new QName(CMIS_NS, "localNamespace");
    public static final QName TYPE_DISPLAY_NAME = new QName(CMIS_NS, "displayName");
    public static final QName TYPE_QUERY_NAME = new QName(CMIS_NS, "queryName");
    public static final QName TYPE_DESCRIPTION = new QName(CMIS_NS, "description");
    public static final QName TYPE_BASE_ID = new QName(CMIS_NS, "baseId");
    public static final QName TYPE_CREATABLE = new QName(CMIS_NS, "creatable");
    public static final QName TYPE_FILEABLE = new QName(CMIS_NS, "fileable");
    public static final QName TYPE_QUERYABLE = new QName(CMIS_NS, "queryable");
    public static final QName TYPE_FULL_TEXT_INDEXED = new QName(CMIS_NS, "fulltextIndexed");
    public static final QName TYPE_INCLUDED_IN_SUPERTYPE_QUERY = new QName(CMIS_NS, "includedInSupertypeQuery");
    public static final QName TYPE_CONTROLLABLE_POLICY = new QName(CMIS_NS, "controllablePolicy");
    public static final QName TYPE_CONTROLLABLE_ACL = new QName(CMIS_NS, "controllableACL");
    
    // CMIS Property Definition
    public static final QName STRING_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyStringDefinition");
    public static final QName DECIMAL_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyDecimalDefinition");
    public static final QName INTEGER_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyIntegerDefinition");
    public static final QName BOOLEAN_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyBooleanDefinition");
    public static final QName DATETIME_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyDateTimeDefinition");
    public static final QName URI_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyUriDefinition");
    public static final QName ID_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyIdDefinition");
    public static final QName HTML_PROPERTY_DEFINITION = new QName(CMIS_NS, "propertyHtmlDefinition");
    public static final QName PROPDEF_ID = new QName(CMIS_NS, "id");
    public static final QName PROPDEF_LOCAL_NAME = new QName(CMIS_NS, "localName");
    public static final QName PROPDEF_LOCAL_NAMESPACE = new QName(CMIS_NS, "localNamespace");
    public static final QName PROPDEF_DISPLAY_NAME = new QName(CMIS_NS, "displayName");
    public static final QName PROPDEF_QUERY_NAME = new QName(CMIS_NS, "queryName");
    public static final QName PROPDEF_DESCRIPTION = new QName(CMIS_NS, "description");
    public static final QName PROPDEF_PROPERTY_TYPE = new QName(CMIS_NS, "propertyType");
    public static final QName PROPDEF_CARDINALITY = new QName(CMIS_NS, "cardinality");
    public static final QName PROPDEF_UPDATABILITY = new QName(CMIS_NS, "updatability");
    public static final QName PROPDEF_INHERITED = new QName(CMIS_NS, "inherited");
    public static final QName PROPDEF_REQUIRED = new QName(CMIS_NS, "required");
    public static final QName PROPDEF_QUERYABLE = new QName(CMIS_NS, "queryable");
    public static final QName PROPDEF_ORDERABLE = new QName(CMIS_NS, "orderable");
    public static final QName PROPDEF_OPEN_CHOICE = new QName(CMIS_NS, "openChoice");
    // TODO: choice
    
    // CMIS Number of Items
    public static final QName NUM_ITEMS = new QName(CMISRA_NS, "numItems");
    
    // CMIS Data Types
    public static final String DATATYPE_STRING = "string";
    public static final String DATATYPE_DECIMAL = "decimal";
    public static final String DATATYPE_INTEGER = "integer";
    public static final String DATATYPE_BOOLEAN = "boolean";
    public static final String DATATYPE_DATETIME = "datetime";
    public static final String DATATYPE_URI = "uri";
    public static final String DATATYPE_ID = "id";
    public static final String DATATYPE_HTML = "html";

    // CMIS Relationships
    public static final String REL_SERVICE = "service";
    public static final String REL_UP = "up";
    public static final String REL_DOWN = "down";
    public static final String REL_DESCRIBED_BY = "describedby";
    public static final String REL_VERSION_HISTORY = "version-history";
    public static final String REL_CURRENT_VERSION = "current-version";
    public static final String REL_WORKING_COPY = "working-copy";
    public static final String REL_ROOT_DESCENDANTS = CMISLINK_NS + "rootdescendants";
    public static final String REL_TYPES_DESCENDANTS = CMISLINK_NS + "typesdescendants";
    public static final String REL_FOLDER_TREE = CMISLINK_NS + "foldertree";
    public static final String REL_ALLOWABLE_ACTIONS = CMISLINK_NS + "allowableactions";
    public static final String REL_POLICIES = CMISLINK_NS + "policies";
    public static final String REL_RELATIONSHIPS = CMISLINK_NS + "relationships";
    public static final String REL_ASSOC_SOURCE = CMISLINK_NS + "source";
    public static final String REL_ASSOC_TARGET = CMISLINK_NS + "target";

    // CMIS Nested feed
    public static final QName CHILDREN = new QName(CMISRA_NS, "children");

    // CMIS Property Types
    public static final String PROP_TYPE_STRING = "string";
    public static final String PROP_TYPE_DECIMAL = "decimal";
    public static final String PROP_TYPE_INTEGER = "integer";
    public static final String PROP_TYPE_BOOLEAN = "boolean";
    public static final String PROP_TYPE_DATETIME = "datetime";
    public static final String PROP_TYPE_URI = "uri";
    public static final String PROP_TYPE_ID = "id";
    public static final String PROP_TYPE_HTML = "html";

    // CMIS Allowable Actions
    public static final QName ALLOWABLE_ACTIONS = new QName(CMIS_NS, "allowableActions");
    public static final QName CAN_DELETE = new QName(CMIS_NS, "canDelete");
    public static final QName CAN_UPDATE_PROPERTIES = new QName(CMIS_NS, "canUpdateProperties");
    public static final QName CAN_GET_PROPERTIES = new QName(CMIS_NS, "canGetProperties");
    public static final QName CAN_GET_OBJECT_RELATIONSHIPS = new QName(CMIS_NS, "canGetObjectRelationships");
    public static final QName CAN_GET_PARENTS = new QName(CMIS_NS, "canGetParents");
    public static final QName CAN_GET_FOLDER_PARENT = new QName(CMIS_NS, "canGetFolderParent");
    public static final QName CAN_GET_DESCENDANTS = new QName(CMIS_NS, "canGetDescendants");
    public static final QName CAN_GET_FOLDER_TREE = new QName(CMIS_NS, "canGetFolderTree");
    public static final QName CAN_MOVE = new QName(CMIS_NS, "canMove");
    public static final QName CAN_DELETE_VERSION = new QName(CMIS_NS, "canDeleteVersion");
    public static final QName CAN_DELETE_CONTENT = new QName(CMIS_NS, "canDeleteContent");
    public static final QName CAN_CHECKOUT = new QName(CMIS_NS, "canCheckout");
    public static final QName CAN_CANCEL_CHECKOUT = new QName(CMIS_NS, "canCancelCheckout");
    public static final QName CAN_CHECKIN = new QName(CMIS_NS, "canCheckin");
    public static final QName CAN_SET_CONTENT = new QName(CMIS_NS, "canSetContent");
    public static final QName CAN_GET_ALL_VERSIONS = new QName(CMIS_NS, "canGetAllVersions");
    public static final QName CAN_ADD_TO_FOLDER = new QName(CMIS_NS, "canAddToFolder");
    public static final QName CAN_REMOVE_FROM_FOLDER = new QName(CMIS_NS, "canRemoveFromFolder");
    public static final QName CAN_VIEW_CONTENT = new QName(CMIS_NS, "canViewContent");
    public static final QName CAN_ADD_POLICY = new QName(CMIS_NS, "canAddPolicy");
    public static final QName CAN_GET_APPLIED_POLICIES = new QName(CMIS_NS, "canGetAppliedPolicies");
    public static final QName CAN_REMOVE_POLICY = new QName(CMIS_NS, "canRemovePolicy");
    public static final QName CAN_GET_CHILDREN = new QName(CMIS_NS, "canGetChildren");
    public static final QName CAN_CREATE_DOCUMENT = new QName(CMIS_NS, "canCreateDocument");
    public static final QName CAN_CREATE_FOLDER = new QName(CMIS_NS, "canCreateFolder");
    public static final QName CAN_CREATE_RELATIONSHIP = new QName(CMIS_NS, "canCreateRelationship");
    public static final QName CAN_CREATE_POLICY = new QName(CMIS_NS, "canCreatePolicy");
    public static final QName CAN_DELETE_TREE = new QName(CMIS_NS, "canDeleteTree");

    // CMIS Type Names
    public static final String TYPE_DOCUMENT = "cmis:document";
    public static final String TYPE_FOLDER = "cmis:folder";
    public static final String TYPE_RELATIONSHIP = "cmis:relationship";
    public static final String TYPE_POLICY = "cmis:policy";

    // CMIS Properties Names
    public static final String PROP_NAME = "cmis:name";
    public static final String PROP_OBJECT_ID = "cmis:objectId";
    public static final String PROP_BASE_TYPE_ID = "cmis:baseTypeId";
    public static final String PROP_OBJECT_TYPE_ID = "cmis:objectTypeId";
    public static final String PROP_CREATED_BY = "cmis:createdBy";
    public static final String PROP_CREATION_DATE = "cmis:creationDate";
    public static final String PROP_LAST_MODIFIED_BY = "cmis:lastModifiedBy";
    public static final String PROP_LAST_MODIFICATION_DATE = "cmis:lastModificationDate";
    public static final String PROP_IS_IMMUTABLE = "cmis:isImmutable";
    public static final String PROP_IS_LATEST_VERSION = "cmis:isLatestVersion";
    public static final String PROP_IS_MAJOR_VERSION = "cmis:isMajorVersion";
    public static final String PROP_IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";
    public static final String PROP_VERSION_LABEL = "cmis:versionLabel";
    public static final String PROP_VERSION_SERIES_ID = "cmis:versionSeriesId";
    public static final String PROP_IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";
    public static final String PROP_VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";
    public static final String PROP_VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";
    public static final String PROP_CHECKIN_COMMENT = "cmis:checkinComment";
    public static final String PROP_CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";
    public static final String PROP_CONTENT_STREAM_MIMETYPE = "cmis:contentStreamMimetype";
    public static final String PROP_CONTENT_STREAM_FILENAME = "cmis:contentStreamFilename";
    public static final String PROP_CONTENT_STREAM_ID = "cmis:contentStreamId";
    public static final String PROP_PATH = "cmis:path";
    public static final String PROP_SOURCE_ID = "cmis:sourceId";
    public static final String PROP_TARGET_ID = "cmis:targetId";
    public static final String PROP_PARENT_ID = "cmis:parentId";

}
