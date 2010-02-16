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
package org.apache.opencmis.client.api.objecttype;

import org.apache.opencmis.commons.enums.ContentStreamAllowed;

/**
 * Document Object Type.
 * 
 * See CMIS Domain Model - section 2.1.4.3.
 */
public interface DocumentType extends ObjectType {

  /**
   * Get the {@code isVersionable} flag.
   * 
   * @return {@code true} if this document type is versionable, {@code false} if documents of this
   *         type cannot be versioned.
   */
  boolean isVersionable();

  /**
   * Get the enum that describes, how content streams have to be handled with this document type.
   * 
   * @return the mode of content stream support ({@code notallowed}, {@code allowed}, or {@code
   *         required}).
   */
  ContentStreamAllowed getContentStreamAllowed();

}
