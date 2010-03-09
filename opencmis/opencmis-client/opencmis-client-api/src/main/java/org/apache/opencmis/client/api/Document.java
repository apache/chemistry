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
package org.apache.opencmis.client.api;

import java.util.List;

import org.apache.opencmis.commons.enums.VersioningState;

/**
 * Domain Model 2.4
 */
public interface Document extends FileableCmisObject {

  // object service

  void deleteAllVersions();

  ContentStream getContentStream();

  ObjectId setContentStream(boolean overwrite, ContentStream contentStream);

  ObjectId deleteContentStream();

  // versioning service

  boolean checkOut(); // returns contentCopied

  void cancelCheckOut();

  void checkIn(boolean major, List<Property<?>> properties, ContentStream contentStream,
      String checkinComment, List<Policy> policies, List<Ace> addACEs, List<Ace> removeACEs);

  Document getObjectOfLatestVersion(boolean major);

  Document getObjectOfLatestVersion(boolean major, OperationContext context);

  List<Document> getAllVersions();

  List<Document> getAllVersions(OperationContext context);

  // document specific properties

  Boolean isImmutable(); // cmis:isImmutable

  Boolean isLatestVersion(); // cmis:isLatestVersion

  Boolean isMajorVersion(); // cmis:isMajorVersion

  Boolean isLatestMajorVersion(); // cmis:isLatestMajorVersion

  String getVersionLabel(); // cmis:versionLabel

  String getVersionSeriesId(); // cmis:versionSeriesId

  Boolean isVersionSeriesCheckedOut(); // cmis:isVersionSeriesCheckedOut

  String getVersionSeriesCheckedOutBy(); // cmis:versionSeriesCheckedOutBy

  String getVersionSeriesCheckedOutId(); // cmis:versionSeriesCheckedOutId

  String getCheckinComment(); // cmis:checkinComment

  long getContentStreamLength(); // cmis:contentStreamLength

  String getContentStreamMimeType(); // cmis:contentStreamMimeType

  String getContentStreamFileName(); // cmis:contentStreamFileName

  String getContentStreamId(); // cmis:contentStreamId

  /**
   * Shortcut for ObjectFactory.createDocumentFromSource(this, ...).
   * 
   * @param properties
   * @param versioningState
   * @param policies
   * @param addACEs
   * @param removeACEs
   * @return
   */
  Document copy(List<Property<?>> properties, VersioningState versioningState,
      List<Policy> policies, List<Ace> addACEs, List<Ace> removeACEs);

}
