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
package org.apache.chemistry;

import static org.apache.chemistry.CMIS.CMISName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Utility class holding constants related to allowable actions.
 */
public class AllowableAction {

    // utility class
    private AllowableAction() {
    }

    public static final QName CAN_GET_DESCENDANTS = CMISName("canGetDescendants");

    public static final QName CAN_GET_FOLDER_TREE = CMISName("canGetFolderTree");

    public static final QName CAN_GET_CHILDREN = CMISName("canGetChildren");

    public static final QName CAN_GET_FOLDER_PARENT = CMISName("canGetFolderParent");

    public static final QName CAN_GET_OBJECT_PARENTS = CMISName("canGetObjectParents");

    public static final QName CAN_CREATE_DOCUMENT = CMISName("canCreateDocument");

    public static final QName CAN_CREATE_FOLDER = CMISName("canCreateFolder");

    public static final QName CAN_CREATE_RELATIONSHIP = CMISName("canCreateRelationship");

    public static final QName CAN_GET_PROPERTIES = CMISName("canGetProperties");

    public static final QName CAN_GET_RENDITIONS = CMISName("canGetRenditions");

    public static final QName CAN_GET_CONTENT_STREAM = CMISName("canGetContentStream");

    public static final QName CAN_UPDATE_PROPERTIES = CMISName("canUpdateProperties");

    public static final QName CAN_MOVE_OBJECT = CMISName("canMoveObject");

    public static final QName CAN_DELETE_OBJECT = CMISName("canDeleteObject");

    public static final QName CAN_SET_CONTENT_STREAM = CMISName("canSetContentStream");

    public static final QName CAN_DELETE_CONTENT_STREAM = CMISName("canDeleteContentStream");

    public static final QName CAN_DELETE_TREE = CMISName("canDeleteTree");

    public static final QName CAN_ADD_OBJECT_TO_FOLDER = CMISName("canAddObjectToFolder");

    public static final QName CAN_REMOVE_OBJECT_FROM_FOLDER = CMISName("canRemoveObjectFromFolder");

    public static final QName CAN_CHECK_OUT = CMISName("canCheckOut");

    public static final QName CAN_CANCEL_CHECK_OUT = CMISName("canCancelCheckOut");

    public static final QName CAN_CHECK_IN = CMISName("canCheckIn");

    public static final QName CAN_GET_ALL_VERSIONS = CMISName("canGetAllVersions");

    public static final QName CAN_GET_OBJECT_RELATIONSHIPS = CMISName("canGetObjectRelationships");

    public static final QName CAN_APPLY_POLICY = CMISName("canApplyPolicy");

    public static final QName CAN_REMOVE_POLICY = CMISName("canRemovePolicy");

    public static final QName CAN_GET_APPLIED_POLICIES = CMISName("canGetAppliedPolicies");

    public static final QName CAN_GET_ACL = CMISName("canGetACL");

    public static final QName CAN_APPLY_ACL = CMISName("canApplyACL");

    public static List<QName> DEFAULT = Collections.unmodifiableList(Arrays.asList(
            CAN_GET_DESCENDANTS, //
            CAN_GET_FOLDER_TREE, //
            CAN_GET_CHILDREN, //
            CAN_GET_FOLDER_PARENT, //
            CAN_GET_OBJECT_PARENTS, //
            CAN_CREATE_DOCUMENT, //
            CAN_CREATE_FOLDER, //
            CAN_CREATE_RELATIONSHIP, //
            CAN_GET_PROPERTIES, //
            CAN_GET_RENDITIONS, //
            CAN_GET_CONTENT_STREAM, //
            CAN_UPDATE_PROPERTIES, //
            CAN_MOVE_OBJECT, //
            CAN_DELETE_OBJECT, //
            CAN_SET_CONTENT_STREAM, //
            CAN_DELETE_CONTENT_STREAM, //
            CAN_DELETE_TREE, //
            CAN_ADD_OBJECT_TO_FOLDER, //
            CAN_REMOVE_OBJECT_FROM_FOLDER, //
            CAN_CHECK_OUT, //
            CAN_CANCEL_CHECK_OUT, //
            CAN_CHECK_IN, //
            CAN_GET_ALL_VERSIONS, //
            CAN_GET_OBJECT_RELATIONSHIPS, //
            CAN_APPLY_POLICY, //
            CAN_REMOVE_POLICY, //
            CAN_GET_APPLIED_POLICIES, //
            CAN_GET_ACL, //
            CAN_APPLY_ACL //
    ));

    public static final String GET_DESCENDENTS_FOLDER = "canGetDescendents.Folder";

    public static final String GET_FOLDER_TREE_FOLDER = "canGetFolderTree.Folder";

    public static final String GET_CHILDREN_FOLDER = "canGetChildren.Folder";

    public static final String GET_FOLDER_PARENT_FOLDER = "canGetFolderParent.Folder";

    public static final String GET_OBJECT_PARENTS_OBJECT = "canGetObjectParents.Object";

    public static final String CREATE_DOCUMENT_FOLDER = "canCreateDocument.Folder";

    public static final String CREATE_FOLDER_FOLDER = "canCreateFolder.Folder";

    public static final String CREATE_RELATIONSHIP_SOURCE = "canCreateRelationship.Source";

    public static final String CREATE_RELATIONSHIP_TARGET = "canCreateRelationship.Target";

    public static final String GET_PROPERTIES_OBJECT = "canGetProperties.Object";

    public static final String GET_RENDITIONS_OBJECT = "canGetRenditions.Object";

    public static final String GET_CONTENT_STREAM_OBJECT = "canGetContentStream.Object";

    public static final String UPDATE_PROPERTIES_OBJECT = "canUpdateProperties.Object";

    public static final String MOVE_OBJECT_OBJECT = "canMoveObject.Object";

    public static final String MOVE_OBJECT_TARGET = "canMoveObject.Target";

    public static final String MOVE_OBJECT_SOURCE = "canMoveObject.Source";

    public static final String DELETE_OBJECT = "canDelete.Object";

    public static final String DELETE_FOLDER = "canDelete.Folder";

    public static final String SET_CONTENT_STREAM_DOCUMENT = "canSetContentStream.Document";

    public static final String DELETE_CONTENT_STREAM_DOCUMENT = "canDeleteContentStream.Document";

    public static final String DELETE_TREE_FOLDER = "canDeleteTree.Folder";

    public static final String ADD_TO_FOLDER_OBJECT = "canAddToFolder.Object";

    public static final String ADD_TO_FOLDER_FOLDER = "canAddToFolder.Folder";

    public static final String REMOVE_FROM_OBJECT_FROM_FOLDER_OBJECT = "canRemoveObjectFromFolder.Object";

    public static final String REMOVE_FROM_OBJECT_FROM_FOLDER_FOLDER = "canRemoveObjectFromFolder.Folder";

    public static final String CHECK_OUT_DOCUMENT = "canCheckOut.Document";

    public static final String CANCEL_CHECK_OUT_DOCUMENT = "canCancelCheckout.Document";

    public static final String CHECK_IN_DOCUMENT = "canCheckIn.Document";

    public static final String GET_ALL_VERSIONS_DOCUMENT = "canGetAllVersions.Document";

    public static final String GET_OBJECT_RELATIONSHIPS_OBJECT = "canGetObjectRelationships.Object";

    public static final String ADD_POLICY_OBJECT = "canAddPolicy.Object";

    public static final String ADD_POLICY_POLICY = "canAddPolicy.Policy";

    public static final String REMOVE_POLICY_OBJECT = "canRemovePolicy.Object";

    public static final String REMOVE_POLICY_POLICY = "canRemovePolicy.Policy";

    public static final String GET_APPLIED_POLICIES_OBJECT = "canGetAppliedPolicies.Object";

    public static final String GET_ACL_OBJECT = "canGetACL.Object";

    public static final String APPLY_ACL_OBJECT = "canApplyACL.Object";

}
