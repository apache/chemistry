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

import org.apache.opencmis.commons.enums.VersioningState;
import org.apache.opencmis.commons.provider.ContentStreamData;

/**
 * A version series is a concrete object (meaning it can be stored) and has 
 * methods for check-out and checkin. It has a path (is contained in a folder)
 * In contrast to a non-versioned document it has no content, but versions
 * instead.
 * 
 * @author Jens
 *
 */
public interface VersionedDocument extends Path, StoredObject {
  
  DocumentVersion addVersion(ContentStreamData content, VersioningState verState, String user);
  
  boolean isCheckedOut();
  
  void cancelCheckOut(String user);
  
  DocumentVersion checkOut(ContentStreamData content, String user);
  
  void checkIn(boolean isMajor, String checkinComment, String user);
  
  List<DocumentVersion> getAllVersions(String filter);
  
  DocumentVersion getLatestVersion(boolean major);
  
  String getCheckedOutBy();
   
  DocumentVersion getPwc();

}
