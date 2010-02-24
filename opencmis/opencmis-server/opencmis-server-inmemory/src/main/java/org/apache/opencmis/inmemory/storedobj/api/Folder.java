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

import java.util.List;

/**
 * A folder is a concrete object (meaning it can be stored) and has 
 * Each folder is contained in a parent folder. The parent folder for
 * the special root folder is null.
 * 
 * @author Jens
 *
 */

public interface Folder extends Children, SingleFiling, StoredObject {
  
  /**
   * return a list of allowed types of children in this folder
   * @return
   */
  List<String> getAllowedChildObjectTypeIds();
  
}
