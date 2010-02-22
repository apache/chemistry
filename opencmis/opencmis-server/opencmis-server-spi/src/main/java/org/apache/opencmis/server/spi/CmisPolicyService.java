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
package org.apache.opencmis.server.spi;

import java.util.List;

import org.apache.opencmis.commons.api.ExtensionsData;
import org.apache.opencmis.commons.provider.ObjectData;

/**
 * CMIS Policy Service interface. Please refer to the CMIS specification and the OpenCMIS
 * documentation for details.
 * 
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public interface CmisPolicyService {

  /**
   * Applies a policy to an object.
   * 
   * <p>
   * Bindings: AtomPub, Web Services
   * </p>
   */
  public ObjectData applyPolicy(CallContext context, String repositoryId, String policyId,
      String objectId, ExtensionsData extension, ObjectInfoHolder objectInfos);

  /**
   * Removes a policy to an object.
   * 
   * <p>
   * Bindings: AtomPub, Web Services
   * </p>
   */
  public void removePolicy(CallContext context, String repositoryId, String policyId,
      String objectId, ExtensionsData extension);

  /**
   * Get all applied policies of an object.
   * 
   * <p>
   * Bindings: AtomPub, Web Services
   * </p>
   */
  public List<ObjectData> getAppliedPolicies(CallContext context, String repositoryId,
      String objectId, String filter, ExtensionsData extension, ObjectInfoHolder objectInfos);

}
