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

import org.apache.opencmis.commons.provider.ObjectData;
import org.apache.opencmis.commons.provider.ObjectInFolderData;

/**
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public class ObjectInFolderDataImpl extends AbstractExtensionData implements ObjectInFolderData {

  private ObjectData fObject;
  private String fPathSegment;

  /**
   * Constructor.
   */
  public ObjectInFolderDataImpl() {
  }

  /**
   * Constructor.
   */
  public ObjectInFolderDataImpl(ObjectData object) {
    setObject(object);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.ObjectInFolderData#getObject()
   */
  public ObjectData getObject() {
    return fObject;
  }

  public void setObject(ObjectData object) {
    fObject = object;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.ObjectInFolderData#getPathSegment()
   */
  public String getPathSegment() {
    return fPathSegment;
  }

  public void setPathSegment(String pathSegment) {
    fPathSegment = pathSegment;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ObjectInFolder [object=" + fObject + ", path segment=" + fPathSegment + "]"
        + super.toString();
  }
}
