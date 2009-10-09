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

import org.apache.abdera.util.AbstractExtensionFactory;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyBoolean;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyDateTime;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyDecimal;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyHtml;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyId;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyInteger;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyString;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyUri;


/**
 * CMIS Extension Factory for the Abdera ATOM Library.
 */
public class CMISExtensionFactory extends AbstractExtensionFactory implements CMISConstants {

    public CMISExtensionFactory() {
        super(CMIS_NS, CMISRA_NS);
        addImpl(REPOSITORY_INFO, CMISRepositoryInfo.class);
        addImpl(CAPABILITIES, CMISCapabilities.class);
        addImpl(OBJECT, CMISObject.class);
        addImpl(CONTENT, CMISContent.class);
        addImpl(NUM_ITEMS, CMISNumItems.class);
        addImpl(PROPERTIES, CMISProperties.class);
        addImpl(STRING_PROPERTY, CMISPropertyString.class);
        addImpl(DECIMAL_PROPERTY, CMISPropertyDecimal.class);
        addImpl(INTEGER_PROPERTY, CMISPropertyInteger.class);
        addImpl(BOOLEAN_PROPERTY, CMISPropertyBoolean.class);
        addImpl(DATETIME_PROPERTY, CMISPropertyDateTime.class);
        addImpl(URI_PROPERTY, CMISPropertyUri.class);
        addImpl(ID_PROPERTY, CMISPropertyId.class);
        addImpl(HTML_PROPERTY, CMISPropertyHtml.class);
        addImpl(PROPERTY_VALUE, CMISValue.class);
        addImpl(ALLOWABLE_ACTIONS, CMISAllowableActions.class);
        addImpl(CAN_DELETE, CMISAllowableAction.class);
        addImpl(CAN_UPDATE_PROPERTIES, CMISAllowableAction.class);
        addImpl(CAN_GET_PROPERTIES, CMISAllowableAction.class);
        addImpl(CAN_GET_OBJECT_RELATIONSHIPS, CMISAllowableAction.class);
        addImpl(CAN_GET_PARENTS, CMISAllowableAction.class);
        addImpl(CAN_GET_FOLDER_PARENT, CMISAllowableAction.class);
        addImpl(CAN_GET_DESCENDANTS, CMISAllowableAction.class);
        addImpl(CAN_MOVE, CMISAllowableAction.class);
        addImpl(CAN_DELETE_VERSION, CMISAllowableAction.class);
        addImpl(CAN_DELETE_CONTENT, CMISAllowableAction.class);
        addImpl(CAN_CHECKOUT, CMISAllowableAction.class);
        addImpl(CAN_CANCEL_CHECKOUT, CMISAllowableAction.class);
        addImpl(CAN_CHECKIN, CMISAllowableAction.class);
        addImpl(CAN_SET_CONTENT, CMISAllowableAction.class);
        addImpl(CAN_GET_ALL_VERSIONS, CMISAllowableAction.class);
        addImpl(CAN_ADD_TO_FOLDER, CMISAllowableAction.class);
        addImpl(CAN_REMOVE_FROM_FOLDER, CMISAllowableAction.class);
        addImpl(CAN_VIEW_CONTENT, CMISAllowableAction.class);
        addImpl(CAN_ADD_POLICY, CMISAllowableAction.class);
        addImpl(CAN_GET_APPLIED_POLICIES, CMISAllowableAction.class);
        addImpl(CAN_REMOVE_POLICY, CMISAllowableAction.class);
        addImpl(CAN_GET_CHILDREN, CMISAllowableAction.class);
        addImpl(CAN_CREATE_DOCUMENT, CMISAllowableAction.class);
        addImpl(CAN_CREATE_FOLDER, CMISAllowableAction.class);
        addImpl(CAN_CREATE_RELATIONSHIP, CMISAllowableAction.class);
        addImpl(CAN_CREATE_POLICY, CMISAllowableAction.class);
        addImpl(CAN_DELETE_TREE, CMISAllowableAction.class);
        addImpl(CHILDREN, CMISChildren.class);
    }

}
