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
package org.apache.opencmis.commons.provider;

import java.util.List;

import org.apache.opencmis.commons.api.ExtensionsData;

/**
 * Base property interface.
 * 
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public interface PropertyData<T> extends ExtensionsData {

  /**
   * Returns the property id.
   * 
   * @return the property id
   */
  String getId();

  /**
   * Returns the local name.
   * 
   * @return the local name or <code>null</code>
   */
  String getLocalName();

  /**
   * Returns the display name.
   * 
   * @return the display name or <code>null</code>
   */
  String getDisplayName();

  /**
   * Returns the query name.
   * 
   * @return the query name or <code>null</code>
   */
  String getQueryName();

  /**
   * Returns the list of values of this property. For a single value property this is a list with
   * one entry.
   * 
   * @return the list of values or (in rare cases) <code>null</code>
   */
  List<T> getValues();

  /**
   * Returns the first entry of the list of values.
   * 
   * @return first entry of the list of values or (in rare cases) <code>null</code>
   */
  T getFirstValue();
}
