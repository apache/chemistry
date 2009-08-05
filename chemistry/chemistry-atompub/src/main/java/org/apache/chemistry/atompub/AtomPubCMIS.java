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
            + "restatom/200901";

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

    public static final QName MEDIA_TYPE = CMISRAName("mediatype");

    public static final QName OBJECT = CMISRAName("object");

    /*
     * ----- AtomPub Collection Types -----
     */

    public static final String COL_ROOT_CHILDREN = "root";

    public static final String COL_ROOT_DESCENDANTS = "rootdescendants"; // TODO

    public static final String COL_UNFILED = "unfiled";

    public static final String COL_CHECKED_OUT = "checkedout";

    public static final String COL_TYPES_CHILDREN = "types";

    public static final String COL_TYPES_DESCENDANTS = "typesdescendants"; // TODO

    public static final String COL_QUERY = "query";

    /*
     * ----- AtomPub Link Types -----
     */

    public static final String CMIS_LINK_NS_BASE = CMIS.CMIS_NS_BASE
            + "link/200901/";

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

    public static final String LINK_TYPES_DESCENDANTS = CMIS_LINK_NS_BASE
            + "typesdescendants";

    public static final String LINK_ROOT_DESCENDANTS = CMIS_LINK_NS_BASE
            + "rootdescendants";

    /*
     * ----- URI Template Types -----
     */

    public static final String URITMPL_ENTRY_BY_ID = "entrybyid";

    public static final String URITMPL_FOLDER_BY_PATH = "folderbypath"; // TODO-0.63

    public static final String URITMPL_QUERY = "query";

    /*
     * ----- Media Types -----
     */

    public static final String MEDIA_TYPE_CMIS_QUERY = "application/cmisquery+xml";

    public static final String MEDIA_TYPE_CMIS_ALLOWABLE_ACTIONS = "application/cmisallowableactions+xml";

    public static final String MEDIA_TYPE_CMIS_TREE = "application/cmistree+xml";

}
