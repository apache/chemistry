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
package org.apache.jackrabbit.cmis;

import java.net.URI;

/**
 * Simple repository used for testing.
 */
public class SimpleRepository implements Repository {

    private Capabilities capabilities;
    private String description;
    private String id;
    private String name;
    private String productName;
    private String productVersion;
    private String rootFolderId;
    private URI uri;
    private String vendorName;
    private String versionsSupported;

    /**
     * Create a new instance of this class.
     */
    public SimpleRepository() {
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getCapabilities()
     */
    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getDescription()
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getId()
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getName()
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getProductName()
     */
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getProductVersion()
     */
    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getRootFolderId()
     */
    public String getRootFolderId() {
        return rootFolderId;
    }

    public void setRootFolderId(String rootFolderId) {
        this.rootFolderId = rootFolderId;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getURI()
     */
    public URI getURI() {
        return uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getVendorName()
     */
    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Repository#getVersionsSupported()
     */
    public String getVersionsSupported() {
        return versionsSupported;
    }

    public void setVersionsSupported(String versionsSupported) {
        this.versionsSupported = versionsSupported;
    }

    public Entry getEntry(String id) {
        // TODO Auto-generated method stub
	return null;
    }

    public Iterable<Entry> query(String query) {
        // TODO Auto-generated method stub
        return null;
    }
}
