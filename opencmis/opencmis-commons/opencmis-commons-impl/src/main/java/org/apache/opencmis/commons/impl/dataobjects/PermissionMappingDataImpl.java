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
package org.apache.opencmis.commons.impl.dataobjects;

import java.util.List;

import org.apache.opencmis.commons.provider.PermissionMappingData;

/**
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public class PermissionMappingDataImpl extends AbstractExtensionData implements
    PermissionMappingData {

  private static final long serialVersionUID = 1L;

  private String fKey;
  private List<String> fPermissions;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.PermissionMappingData#getKey()
   */
  public String getKey() {
    return fKey;
  }

  public void setKey(String key) {
    fKey = key;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.PermissionMappingData#getPermissions()
   */
  public List<String> getPermissions() {
    return fPermissions;
  }

  public void setPermissions(List<String> permissions) {
    fPermissions = permissions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Permission Mapping [key=" + fKey + ", permissions=" + fPermissions + "]"
        + super.toString();
  }

}
