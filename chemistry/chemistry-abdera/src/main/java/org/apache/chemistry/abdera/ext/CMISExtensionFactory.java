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
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceBoolean;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceString;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceInteger;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceDecimal;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceUri;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceHtml;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceId;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceDateTime;

import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyBoolean;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyDateTime;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyDecimal;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyHtml;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyId;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyInteger;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyString;
import org.apache.chemistry.abdera.ext.CMISProperty.CMISPropertyUri;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyBooleanDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyDateTimeDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyDecimalDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyHtmlDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyIdDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyIntegerDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyStringDefinition;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyUriDefinition;


/**
 * CMIS Extension Factory for the Abdera ATOM Library.
 */
public class CMISExtensionFactory extends AbstractExtensionFactory implements CMISConstants {

    public CMISExtensionFactory() {
        super(CMIS_NS, CMISRA_NS);
        addImpl(REPOSITORY_INFO, CMISRepositoryInfo.class);
        addImpl(CAPABILITIES, CMISCapabilities.class);
        addImpl(ACL_CAPABILITY, CMISACLCapability.class);
        addImpl(URI_TEMPLATE, CMISUriTemplate.class);
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
        addImpl(CHANGE_EVENT_INFO, CMISChangeEventInfo.class);
        addImpl(ACCESS_CONTROL_LIST, CMISAccessControlList.class);
        addImpl(PERMISSION, CMISAccessControlEntry.class);
        addImpl(TYPE_DEFINITION, CMISTypeDefinition.class);
        addImpl(STRING_PROPERTY_DEFINITION, CMISPropertyStringDefinition.class);
        addImpl(DECIMAL_PROPERTY_DEFINITION, CMISPropertyDecimalDefinition.class);
        addImpl(INTEGER_PROPERTY_DEFINITION, CMISPropertyIntegerDefinition.class);
        addImpl(BOOLEAN_PROPERTY_DEFINITION, CMISPropertyBooleanDefinition.class);
        addImpl(DATETIME_PROPERTY_DEFINITION, CMISPropertyDateTimeDefinition.class);
        addImpl(URI_PROPERTY_DEFINITION, CMISPropertyUriDefinition.class);
        addImpl(ID_PROPERTY_DEFINITION, CMISPropertyIdDefinition.class);
        addImpl(HTML_PROPERTY_DEFINITION, CMISPropertyHtmlDefinition.class);
        addImpl(CAN_DELETE_OBJECT, CMISAllowableAction.class);
        addImpl(CAN_UPDATE_PROPERTIES, CMISAllowableAction.class);
        addImpl(CAN_GET_FOLDER_TREE, CMISAllowableAction.class);
        addImpl(CAN_GET_PROPERTIES, CMISAllowableAction.class);
        addImpl(CAN_GET_OBJECT_RELATIONSHIPS, CMISAllowableAction.class);
        addImpl(CAN_GET_OBJECT_PARENTS, CMISAllowableAction.class);
        addImpl(CAN_GET_FOLDER_PARENT, CMISAllowableAction.class);
        addImpl(CAN_GET_DESCENDANTS, CMISAllowableAction.class);
        addImpl(CAN_MOVE_OBJECT, CMISAllowableAction.class);
        addImpl(CAN_DELETE_CONTENT_STREAM, CMISAllowableAction.class);
        addImpl(CAN_CHECK_OUT, CMISAllowableAction.class);
        addImpl(CAN_CANCEL_CHECK_OUT, CMISAllowableAction.class);
        addImpl(CAN_CHECK_IN, CMISAllowableAction.class);
        addImpl(CAN_SET_CONTENT_STREAM, CMISAllowableAction.class);
        addImpl(CAN_GET_ALL_VERSIONS, CMISAllowableAction.class);
        addImpl(CAN_ADD_OBJECT_TO_FOLDER, CMISAllowableAction.class);
        addImpl(CAN_REMOVE_OBJECT_FROM_FOLDER, CMISAllowableAction.class);
        addImpl(CAN_GET_CONTENT_STREAM, CMISAllowableAction.class);
        addImpl(CAN_APPLY_POLICY, CMISAllowableAction.class);
        addImpl(CAN_GET_APPLIED_POLICIES, CMISAllowableAction.class);
        addImpl(CAN_REMOVE_POLICY, CMISAllowableAction.class);
        addImpl(CAN_GET_CHILDREN, CMISAllowableAction.class);
        addImpl(CAN_CREATE_DOCUMENT, CMISAllowableAction.class);
        addImpl(CAN_CREATE_FOLDER, CMISAllowableAction.class);
        addImpl(CAN_CREATE_RELATIONSHIP, CMISAllowableAction.class);
        addImpl(CAN_DELETE_TREE, CMISAllowableAction.class);
        addImpl(CAN_GET_RENDITIONS, CMISAllowableAction.class);
        addImpl(CAN_GET_ACL, CMISAllowableAction.class);
        addImpl(CAN_APPLY_ACL, CMISAllowableAction.class);
        addImpl(CHILDREN, CMISChildren.class);
        addImpl(PROPDEF_STRING_CHOICE, CMISChoiceString.class);
        addImpl(PROPDEF_BOOLEAN_CHOICE, CMISChoiceBoolean.class);
        addImpl(PROPDEF_INTEGER_CHOICE, CMISChoiceInteger.class);
        addImpl(PROPDEF_DECIMAL_CHOICE, CMISChoiceDecimal.class);
        addImpl(PROPDEF_DATETIME_CHOICE, CMISChoiceDateTime.class);
        addImpl(PROPDEF_ID_CHOICE, CMISChoiceId.class);
        addImpl(PROPDEF_URI_CHOICE, CMISChoiceUri.class);
        addImpl(PROPDEF_HTML_CHOICE, CMISChoiceHtml.class);
    }

}
