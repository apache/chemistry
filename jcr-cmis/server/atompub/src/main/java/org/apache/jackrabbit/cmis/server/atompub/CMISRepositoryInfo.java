/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis.server.atompub;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.jackrabbit.cmis.Repository;

public class CMISRepositoryInfo extends ElementWrapper {

    public CMISRepositoryInfo(Element elem) {
        super(elem);
    }

    public String getCmisVersionsSupported() {
        return getChildText(CMIS.VERSIONS_SUPPORTED);
    }

    public String getDescription() {
        return getChildText(CMIS.REPOSITORY_DESCRIPTION);
    }

    public String getId() {
        return getChildText(CMIS.REPOSITORY_ID);
    }

    public String getName() {
        return getChildText(CMIS.REPOSITORY_NAME);
    }

    public String getProductName() {
        return getChildText(CMIS.PRODUCT_NAME);
    }

    public String getProductVersion() {
        return getChildText(CMIS.PRODUCT_VERSION);
    }

    public String getRootFolderId() {
        return getChildText(CMIS.ROOT_FOLDER_ID);
    }

    public String getVendorName() {
        return getChildText(CMIS.VENDOR_NAME);
    }

    protected String getChildText(QName name) {
        Element elem = getFirstChild(name);
        return elem != null ? elem.getText() : null;
    }
}
