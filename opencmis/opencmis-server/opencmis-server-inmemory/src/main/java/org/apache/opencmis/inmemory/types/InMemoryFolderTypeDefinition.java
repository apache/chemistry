/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.opencmis.inmemory.types;

import java.util.HashMap;
import java.util.Map;

import org.apache.opencmis.commons.api.PropertyDefinition;
import org.apache.opencmis.commons.enums.BaseObjectTypeIds;
import org.apache.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.opencmis.commons.impl.dataobjects.FolderTypeDefinitionImpl;
import org.apache.opencmis.inmemory.NameValidator;

public class InMemoryFolderTypeDefinition extends FolderTypeDefinitionImpl {

  private static final long serialVersionUID = 1L;
  private static InMemoryFolderTypeDefinition FOLDER_TYPE = new InMemoryFolderTypeDefinition();

  public static InMemoryFolderTypeDefinition getRootFolderType() {
    return FOLDER_TYPE;
  }

  /* This constructor is just for creating the root document */
  public InMemoryFolderTypeDefinition() {
    init(BaseObjectTypeIds.CMIS_FOLDER.value(), "CMIS Folder");
    setParentId(null);
    // set base properties
    Map<String, PropertyDefinition<?>> props = getPropertyDefinitions();
    DocumentTypeCreationHelper.setBasicFolderPropertyDefinitions(props);
  }

  public InMemoryFolderTypeDefinition(String id, String displayName) {
    init(id, displayName);
    setParentId(FOLDER_TYPE.getId());
  }

  public InMemoryFolderTypeDefinition(String id, String displayName, InMemoryFolderTypeDefinition parentType) {
    // get root type
    init(id, displayName);
    if (parentType != null)
      setBaseId(parentType.getBaseId());
    else
      throw new IllegalArgumentException(
          "Must provide a parent type when creating a folder type definition");
    setParentId(parentType.getId());
  }

  /* 
   * Set the property definitions for this type. The parameter propertyDefinitions should only
   * contain the custom property definitions for this type. The standard property definitions are
   * added automatically.
   * @see org.apache.opencmis.commons.impl.dataobjects.AbstractTypeDefinition#setPropertyDefinitions(java.util.Map)
   */
  public void addCustomPropertyDefinitions(Map<String, PropertyDefinition<?>> propertyDefinitions) {
    DocumentTypeCreationHelper.mergePropertyDefinitions(getPropertyDefinitions(), propertyDefinitions);
  }

  private void init(String id, String displayName) {
    if (!NameValidator.isValidId(id))
        throw new CmisInvalidArgumentException(NameValidator.ERROR_ILLEGAL_NAME);

    setBaseId(BaseObjectTypeIds.CMIS_FOLDER);
    setId(id);
    if (displayName == null)
      displayName = '#' + id + '#';
    setDisplayName(displayName);
    // create some suitable defaults for convenience
    setDescription("Description of " + getDisplayName() + " Type");
    setLocalName(id);
    setLocalNamespace("local");
    setQueryName(id);
    setIsControllableAcl(false);
    setIsControllablePolicy(false);
    setIsCreatable(true);
    setIsFileable(true);
    setIsFulltextIndexed(false);
    setIsIncludedInSupertypeQuery(true);
    setIsQueryable(false);

    Map<String, PropertyDefinition<?>> props = new HashMap<String, PropertyDefinition<?>>();
    setPropertyDefinitions(props); // set initial empty set of properties    
  }
}
