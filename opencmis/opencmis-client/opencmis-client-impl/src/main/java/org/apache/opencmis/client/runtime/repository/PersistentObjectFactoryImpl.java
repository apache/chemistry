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
package org.apache.opencmis.client.runtime.repository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.opencmis.client.api.Ace;
import org.apache.opencmis.client.api.Acl;
import org.apache.opencmis.client.api.AllowableActions;
import org.apache.opencmis.client.api.ContentStream;
import org.apache.opencmis.client.api.Document;
import org.apache.opencmis.client.api.Folder;
import org.apache.opencmis.client.api.Policy;
import org.apache.opencmis.client.api.Property;
import org.apache.opencmis.client.api.Relationship;
import org.apache.opencmis.client.api.repository.ObjectFactory;
import org.apache.opencmis.client.runtime.AceImpl;
import org.apache.opencmis.client.runtime.AclImpl;
import org.apache.opencmis.client.runtime.AllowableActionsImpl;
import org.apache.opencmis.client.runtime.PersistentFolderImpl;
import org.apache.opencmis.client.runtime.PersistentSessionImpl;
import org.apache.opencmis.commons.enums.VersioningState;
import org.apache.opencmis.commons.exceptions.CmisRuntimeException;

public class PersistentObjectFactoryImpl implements ObjectFactory {

  private PersistentSessionImpl session = null;

  protected PersistentObjectFactoryImpl(PersistentSessionImpl session) {
    this.session = session;
  }

  public static ObjectFactory newInstance(PersistentSessionImpl session) {
    ObjectFactory f = new PersistentObjectFactoryImpl(session);
    return f;
  }

  public AllowableActions createAllowableAction(Map<String, Boolean> actions) {
    return new AllowableActionsImpl(actions);
  }

  public Ace createAce(String principalId, List<String> permissions, boolean isDirect) {
    return new AceImpl(principalId, permissions, isDirect);
  }

  public Acl createAcl(List<Ace> aces, Boolean isExact) {
    return new AclImpl(aces, isExact);
  }

  public ContentStream createContentStream(int length, String mimetype, String filename,
      InputStream stream) {
    throw new CmisRuntimeException("not implemented");
  }

  public Document createDocument(Folder parentfolder, String name) {
    throw new CmisRuntimeException("not implemented");
  }

  public Document createDocument(List<Property<?>> properties, Folder parentfolder,
      ContentStream contentstream, VersioningState versioningState, List<Policy> policies,
      List<Ace> addACEs, List<Ace> removeACEs) {
    throw new CmisRuntimeException("not implemented");
  }

  public Document createDocumentFromSource(Document source, List<Property<?>> properties,
      Folder parentfolder, VersioningState versioningState, List<Policy> policies,
      List<Ace> addACEs, List<Ace> removeACEs) {
    throw new CmisRuntimeException("not implemented");
  }

  public Folder createFolder(Folder parent, List<Property<?>> properties, List<Policy> policies,
      List<Ace> addACEs, List<Ace> removeACEs) {

    PersistentFolderImpl f = new PersistentFolderImpl(this.session);

    /* create folder in backend */
    f.create(parent, properties, policies, addACEs, removeACEs);

    return f;
  }

  public Policy createPolicy(List<Property<?>> properties, Folder parentfolder,
      List<Policy> policies, List<Ace> addACEs, List<Ace> removeACEs) {
    throw new CmisRuntimeException("not implemented");
  }

  public Relationship createRelationship(List<Property<?>> properties, List<Policy> policies,
      List<Ace> addACEs, List<Ace> removeACEs) {
    throw new CmisRuntimeException("not implemented");
  }
}
