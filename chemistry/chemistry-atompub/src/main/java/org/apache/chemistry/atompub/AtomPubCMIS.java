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
 *     Amelie Avramo, EntropySoft
 */
package org.apache.chemistry.atompub;

import javax.xml.namespace.QName;

import org.apache.chemistry.CMIS;

/**
 * Utility class providing CMIS constants and names for AtomPub.
 */
public class AtomPubCMIS {

    // utility class
    private AtomPubCMIS() {
    }

    /*
     * ----- Namespaces -----
     */

    public static final String CMISRA_NS = CMIS.CMIS_NS_BASE
            + "restatom/200908/";

    public static final String CMISRA_PREFIX = "cmisra";

    public static QName CMISRAName(String localPart) {
        return new QName(CMISRA_NS, localPart, CMISRA_PREFIX);
    }

    /*
     * ----- XML Qualified Names-----
     */

    public static final QName REPOSITORY_INFO = CMISRAName("repositoryInfo");

    public static final QName COLLECTION_TYPE = CMISRAName("collectionType");

    public static final QName URI_TEMPLATE = CMISRAName("uritemplate");

    public static final QName TEMPLATE = CMISRAName("template");

    public static final QName TYPE = CMISRAName("type");

    public static final QName ID = CMISRAName("id");

    public static final QName MEDIA_TYPE = CMISRAName("mediatype");

    public static final QName OBJECT = CMISRAName("object");

    public static final QName CHILDREN = CMISRAName("children");

    public static final QName NUM_ITEMS = CMISRAName("numItems");

    public static final QName CONTENT = CMISRAName("content");

    public static final QName BASE64 = CMISRAName("base64");

    /*
     * ----- AtomPub Collection Types -----
     */

    public static final String COL_ROOT = "root";

    public static final String COL_TYPES = "types";

    public static final String COL_CHECKED_OUT = "checkedout";

    public static final String COL_QUERY = "query";

    public static final String COL_UNFILED = "unfiled";

    /*
     * ----- AtomPub Link Types -----
     */

    public static final String CMIS_LINK_NS_BASE = CMIS.CMIS_NS_BASE
            + "link/200908/";

    public static final String LINK_SOURCE = CMIS_LINK_NS_BASE + "source";

    public static final String LINK_TARGET = CMIS_LINK_NS_BASE + "target";

    public static final String LINK_ALLOWABLE_ACTIONS = CMIS_LINK_NS_BASE
            + "allowableactions";

    public static final String LINK_RELATIONSHIPS = CMIS_LINK_NS_BASE
            + "relationships";

    public static final String LINK_POLICIES = CMIS_LINK_NS_BASE + "policies";

    public static final String LINK_ACL = CMIS_LINK_NS_BASE + "acl";

    public static final String LINK_CHANGES = CMIS_LINK_NS_BASE + "changes";

    public static final String LINK_FOLDER_TREE = CMIS_LINK_NS_BASE
            + "foldertree";

    public static final String LINK_ROOT_DESCENDANTS = CMIS_LINK_NS_BASE
            + "rootdescendants";

    /*
     * ----- AtomPub Link Parameters -----
     */

    public static final String PARAM_ID = "id";

    public static final String PARAM_PATH = "path";

    public static final String PARAM_QUERY = "q";

    public static final String PARAM_FILTER = "filter";

    public static final String PARAM_MAX_ITEMS = "maxItems";

    public static final String PARAM_SKIP_COUNT = "skipCount";

    public static final String PARAM_ORDER_BY = "orderBy";

    public static final String PARAM_DEPTH = "depth";

    public static final String PARAM_INCLUDE_PROPERTY_DEFINITIONS = "includePropertyDefinitions";

    public final static String PARAM_FOLDER_ID = "folderId";

    public static final String PARAM_RENDITION_FILTER = "renditionFilter";

    public static final String PARAM_INCLUDE_ALLOWABLE_ACTIONS = "includeAllowableActions";

    public static final String PARAM_INCLUDE_RELATIONSHIPS = "includeRelationships";

    public static final String PARAM_INCLUDE_POLICY_IDS = "includePolicyIds";

    public static final String PARAM_INCLUDE_ACL = "includeACL";

    public static final String PARAM_INCLUDE_PATH_SEGMENT = "includePathSegment";

    public static final String PARAM_SEARCH_ALL_VERSIONS = "searchAllVersions";

    public static final String PARAM_CONTINUE_ON_FAILURE = "continueOnFailure";

    public static final String PARAM_UNFILE_OBJECTS = "unfileObjects";

    /*
     * ----- URI Template Types -----
     */

    public static final String URITMPL_OBJECT_BY_ID = "objectbyid";

    public static final String URITMPL_OBJECT_BY_PATH = "objectbypath";

    public static final String URITMPL_QUERY = "query";

    public static final String URITMPL_TYPE_BY_ID = "typebyid";

    /*
     * ----- Media Types -----
     */

    public static final String MEDIA_TYPE_CMIS_QUERY = "application/cmisquery+xml";

    public static final String MEDIA_TYPE_CMIS_ALLOWABLE_ACTIONS = "application/cmisallowableactions+xml";

    public static final String MEDIA_TYPE_CMIS_TREE = "application/cmistree+xml";

}
