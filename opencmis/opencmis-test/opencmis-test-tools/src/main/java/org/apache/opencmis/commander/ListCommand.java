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
package org.apache.opencmis.commander;

import java.io.PrintWriter;

import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.provider.CmisProvider;
import org.apache.opencmis.commons.provider.ObjectInFolderData;
import org.apache.opencmis.commons.provider.ObjectInFolderList;
import org.apache.opencmis.commons.provider.PropertyData;

public class ListCommand implements Command {

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.commander.Command#getCommandName()
   */
  public String getCommandName() {
    return "list";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.commander.Command#getUsage()
   */
  public String getUsage() {
    return "LIST <repository id> <folder id>";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.commander.Command#execute(org.apache.opencmis.commons.provider.CmisProvider,
   * java.lang.String[], java.io.PrintWriter)
   */
  public void execute(CmisProvider provider, String[] args, PrintWriter output) {
    if (args.length < 2) {
      output.println(getUsage());
      return;
    }

    String repositoryId = args[0];
    String folderId = args[1];

    ObjectInFolderList list = provider.getNavigationService().getChildren(repositoryId, folderId,
        null, null, null, null, null, null, null, null, null);

    for (ObjectInFolderData object : list.getObjects()) {
      output.println(getPropertyValue(object, PropertyIds.CMIS_OBJECT_ID) + "\t"
          + getPropertyValue(object, PropertyIds.CMIS_NAME) + "\t"
          + getPropertyValue(object, PropertyIds.CMIS_OBJECT_TYPE_ID));
    }
  }

  /**
   * Returns a property value as string.
   */
  private String getPropertyValue(ObjectInFolderData object, String name) {
    if ((object == null) || (object.getObject() == null)
        || (object.getObject().getProperties() == null)
        || (object.getObject().getProperties().getProperties() == null)) {
      return "?";
    }

    PropertyData<?> property = object.getObject().getProperties().getProperties().get(name);
    if ((property == null) || (property.getFirstValue() == null)) {
      return "<not set>";
    }

    return property.getFirstValue().toString();
  }
}
