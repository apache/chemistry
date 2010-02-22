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
package org.apache.opencmis.inmemory.storedobj.api;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.opencmis.commons.provider.PropertyData;
import org.apache.opencmis.commons.provider.ProviderObjectFactory;

/**
 * Stored Object interface is common part that all objects handled by CMIS (Documents, Folders,
 * Relations, Policies, ACLs) share. Objects that implement this interface are always concrete
 * and can live in the object store. A stored object always has an id, a name and properties.
 * 
 * @author Jens
 */

public interface StoredObject {

  /**
   * Retrieve the id of this object.
   *  
   * @return
   *    id of this object
   */
  String getId();

  /**
   * Retrieve the name of this object
   * 
   * @return 
   *    name of this object
   */
  String getName();

  /**
   * Set the name of this document. This method does not persist the object.
   * 
   * @param name
   *    name that is assigned to this object
   */
  void setName(String name);

  /**
   * Retrieve the type of this document.
   * 
   * @return
   *    Id of the type identifying the type of this object
   */
  String getTypeId();

  /**
   * Set the type of this document. This method does not persist the object.
   * 
   * @param type
   *    id of the type this object gets assigned.
   */
  void setTypeId(String type);

  /**
   * Retrieve the user who created the document
   * 
   * @return
   *    user who created the document. 
   */
  String getCreatedBy();

  /** 
   * Set the user who last modified the object. This method does not persist the object.
   * 
   * @param createdBy
   *    user who last modified the document
   */
  void setCreatedBy(String createdBy);

  /**
   * Retrieve the user who last modified the document
   * 
   * @return
   *    user who last modified the document. 
   */
  String getModifiedBy();

  /** 
   * Set the user who last modified the object. This method does not persist the object.
   * 
   * @param modifiedBy
   *    user who last modified the document
   */
  void setModifiedBy(String modifiedBy);

  GregorianCalendar getCreatedAt();

  /**
   * Assign date and time when the object was created. Usually you should not call this
   * method externally. This method does not persist the object.
   * 
   * @param createdAt
   *    date the object was created
   */
  void setCreatedAt(GregorianCalendar createdAt);

  /** 
   * Retrieve date and time when the object was last modified.
   * @return
   *    date the object was last modified
   */
  GregorianCalendar getModifiedAt();

  /**
   * Assign current date and time when the object was last modified. Usually you should not call this
   * method externally. This method does not persist the object.
   */
  void setModifiedAtNow();
  
  /** Get the repository id of this object where the object is stored. 
   * @return
   */
  String getRepositoryId();

  /**
   * Assign a repository where this object will be stored. This method does not persist the object.
   * 
   * @param repositoryId
   *    id of the repository 
   */
  void setRepositoryId(String repositoryId);
  
  /**
   * Retrieve the list of properties
   * @return
   */
  Map<String, PropertyData<?>> getProperties();

  /**
   * Assign the properties to an object. This method does not persist the object.
   * 
   * @param props
   *    properties to be assigned
   */
  void setProperties(Map<String, PropertyData<?>> props);
  
  /**
   * Retrieve a change token uniquely identifying the state of the object when it was
   * persisted (used for optimistic locking)
   * 
   * @return
   *    String identifying the change token
   */
  String getChangeToken();

  /**
   * Persist the object so that it can be later retrieved by its id. Assign an id to the object
   */
  void persist();

  /** 
   * Rename an object 
   * @param newName
   *      the new name of the object
   */
  void rename(String newName);

  /**
   * Create all system base properties that need to be stored with every object in the repository
   * This method is called when a new object is created to record all of the capturing data like the
   * creation time, creator etc.
   * 
   * @param properties
   *          The properties passed by the client, containing, name, type, etc
   * @param user
   *          The user creating the document
   */
  void createSystemBasePropertiesWhenCreated(Map<String, PropertyData<?>> properties, String user);

  /**
   * Update all system base properties that need to be stored with every object in the repository
   * This method is called when an object is is updated to record all of the capturing data like the
   * modification time, updating user etc.
   * 
   * @param properties
   *          The properties passed by the client, containing, name, type, etc
   * @param user
   *          The user creating the document
   */
  void updateSystemBasePropertiesWhenModified(Map<String, PropertyData<?>> properties, String user);

  void fillProperties(Map<String, PropertyData<?>> properties, ProviderObjectFactory objFactory,
      List<String> requestedIds);

  /**
   * Set all properties which are not system properties. These are the properties as defined in Type
   * system definition. This method is called when an object is created or updated. The
   * implementation must ignore the system properties.
   * 
   * @param properties
   *          Set of properties as set by the client, including system parameters
   */
  void setCustomProperties(Map<String, PropertyData<?>> properties);

}