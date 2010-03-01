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
package org.apache.opencmis.inmemory.clientprovider;

import org.apache.opencmis.commons.api.ExtensionsData;
import org.apache.opencmis.commons.provider.MultiFilingService;
import org.apache.opencmis.inmemory.server.InMemoryMultiFilingServiceImpl;

public class MultiFilingServiceImpl  extends AbstractService implements MultiFilingService {

  private InMemoryMultiFilingServiceImpl fMultiSvc;

  public MultiFilingServiceImpl(InMemoryMultiFilingServiceImpl multiSvc) {
    fMultiSvc = multiSvc;
  }

  public void addObjectToFolder(String repositoryId, String objectId, String folderId,
      Boolean allVersions, ExtensionsData extension) {

    fMultiSvc.addObjectToFolder(fDummyCallContext, repositoryId, objectId, folderId, allVersions, extension, null);
  }

  public void removeObjectFromFolder(String repositoryId, String objectId, String folderId,
      ExtensionsData extension) {
    
    fMultiSvc.removeObjectFromFolder(fDummyCallContext, repositoryId, objectId, folderId, extension, null);    
  }
}
