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
package org.apache.opencmis.inmemory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.opencmis.commons.api.PropertyDefinition;
import org.apache.opencmis.commons.api.TypeDefinition;
import org.apache.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.opencmis.inmemory.types.InMemoryDocumentTypeDefinition;
import org.apache.opencmis.inmemory.types.PropertyCreationHelper;

public class VersionTestTypeSystemCreator implements TypeCreator {
  static public String VERSION_TEST_DOCUMENT_TYPE_ID = "MyVersionedType";
  static public String PROPERTY_ID = "StringProp";
  static public List<TypeDefinition> singletonTypes = buildTypesList();


  /**
   * in the public interface of this class we return the singleton containing the required types
   * for testing
   */
  public List<TypeDefinition> createTypesList() {
    return singletonTypes;
  }

  public static List<TypeDefinition> getTypesList() {
    return singletonTypes;
  }
  
  static public TypeDefinition getTypeById(String typeId) {
    for (TypeDefinition typeDef : singletonTypes)
      if (typeDef.getId().equals(typeId))
        return typeDef;
    return null;
  }

  /**
   * create root types and a collection of sample types
   * 
   * @return typesMap map filled with created types
   */
  private static List<TypeDefinition> buildTypesList() {
    // always add CMIS default types
    List<TypeDefinition> typesList = new LinkedList<TypeDefinition>();

    // create a complex type with properties
    InMemoryDocumentTypeDefinition cmisComplexType = new InMemoryDocumentTypeDefinition(VERSION_TEST_DOCUMENT_TYPE_ID,
        "VersionedType", InMemoryDocumentTypeDefinition.getRootDocumentType());
    
    // create a boolean property definition
    
    Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
    
    PropertyStringDefinitionImpl prop1 = PropertyCreationHelper.createStringDefinition(PROPERTY_ID, "Sample String Property");
    propertyDefinitions.put(prop1.getId(), prop1);
    
    cmisComplexType.addCustomPropertyDefinitions(propertyDefinitions);    
    cmisComplexType.setIsVersionable(true); // make it a versionable type;
    
    // add type to types collection
    typesList.add(cmisComplexType);

    
    return typesList;
  }

}


