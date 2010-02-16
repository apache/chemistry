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

package org.apache.opencmis.inmemory;

public class NameValidator {
  public static final String ERROR_ILLEGAL_ID = 
    "Id contains illegal characters, allowed are 'a'..'z', 'A'..'Z', '0'..'9', '-', '_'";

  public static final String ERROR_ILLEGAL_NAME = 
    "Id contains illegal characters, allowed are 'a'..'z', 'A'..'Z', '0'..'9', '-', '_', '.', ' '";

  /**
   * check whether id contains only valid characters Allowed are 'a'..'z', 'A'..'Z', '0'..'9', '.', '-', ' ',
   * '_';
   * 
   * @param s
   *          string to verify
   */
  static public boolean isValidId(String s) {
    if (s.startsWith("cmis:"))
        s = s.substring(5);
    		
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.'
          || c == '-' || c == '_' || c == ' '))
        return false;
    }
    return true;
  }

  static public boolean isValidLocalName(String s) {
    return isValidId(s);
  }

  /**
   * check whether id contains only valid characters Allowed are 'a'..'z', 'A'..'Z', '0'..'9', '.', '-', ' ',
   * '_';
   * 
   * @param s
   *          string to verify
   */
  static public boolean isValidName(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.'
          || c == '-' || c == '_' || c == ' '))
        return false;
    }
    return true;
  }

  static public boolean isValidNamespace(String s) {
    return isValidId(s);
  }

  static public boolean isValidQueryName(String s) {
    return isValidId(s);
  }

}
