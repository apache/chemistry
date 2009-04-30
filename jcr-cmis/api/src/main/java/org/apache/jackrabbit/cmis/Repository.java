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
 * Describes a repository.
 */
public interface Repository {

    /**
     * Return the repository id.
     *
     * @return repository id
     */
    public String getId();

    /**
     * Return the repository name.
     *
     * @return repository name
     */
    public String getName();

    /**
     * Return the repository URI.
     *
     * @return repository URI
     */
    public URI getURI();

    /**
     * Return the description.
     *
     * @return description
     */
    public String getDescription();

    /**
     * Return the vendor name.
     *
     * @return vendor name
     */
    public String getVendorName();

    /**
     * Return the product name.
     *
     * @return product name
     */
    public String getProductName();

    /**
     * Return the product version.
     *
     * @return product version
     */
    public String getProductVersion();

    /**
     * Return the root folder id.
     *
     * @return root folder id
     */
    public String getRootFolderId();

    /**
     * Return the version of CMIS standard supported.
     *
     * @return version of CMIS standard supported
     */
    public String getVersionsSupported();

    /**
     * Return the capabilities of this repository.
     *
     * @return capabilities
     */
    public Capabilities getCapabilities();

    /**
     * Return an entry, given its id.
     *
     * @param id entry id
     * @return entry
     */
    public Entry getEntry(String id);

    /**
     * Query elements, given a query string.
     *
     * @param query query string
     * @return list of items
     */
    public Iterable<Entry> query(String query);
}
