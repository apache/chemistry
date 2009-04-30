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

/**
 * Provides CMIS namespace, prefix, local and qualified names.
 */
public interface CMIS {

    /* Namespace */
    public static final String NS = "http://www.cmis.org/2008/05";

    /* Prefix */
    public static final String PREFIX = "cmis";

    /* Local names */
    public static final String LN_REPOSITORY_INFO = "repositoryInfo";
    public static final String LN_REPOSITORY_ID = "repositoryId";
    public static final String LN_REPOSITORY_NAME = "repositoryName";
    public static final String LN_REPOSITORY_RELATIONSHIP = "repositoryRelationship";
    public static final String LN_REPOSITORY_DESCRIPTION = "repositoryDescription";
    public static final String LN_VENDOR_NAME = "vendorName";
    public static final String LN_PRODUCT_NAME = "productName";
    public static final String LN_PRODUCT_VERSION = "productVersion";
    public static final String LN_ROOT_FOLDER_ID = "rootFolderId";
    public static final String LN_CAPABILITIES = "capabilities";
    public static final String LN_CAPABILITY_MULTIFILING = "capabilityMultifiling";
    public static final String LN_CAPABILITY_UNFILING = "capabilityUnfiling";
    public static final String LN_CAPABILITY_VERSION_SPECIFIC_FILING = "capabilityVersionSpecificFiling";
    public static final String LN_CAPABILITY_PWC_UPDATEABLE = "capabilityPWCUpdateable";
    public static final String LN_CAPABILITY_PWC_SEARCHABLE = "capabilityPWCSearchable";
    public static final String LN_CAPABILITY_ALL_VERSIONS_SEARCHABLE = "capabilityAllVersionsSearchable";
    public static final String LN_CAPABILITY_QUERY = "capabilityQuery";
    public static final String LN_CAPABILITY_JOIN = "capabilityJoin";
    public static final String LN_CAPABILITY_FULL_TEXT = "capabilityFullText";
    public static final String LN_VERSIONS_SUPPORTED = "cmisVersionsSupported";
    public static final String LN_REPOSITORY_SPECIFIC_INFORMATION = "repositorySpecificInformation";

    /* Qualified names */
    public static final QName REPOSITORY_INFO = new QName(NS, LN_REPOSITORY_INFO, PREFIX);
    public static final QName REPOSITORY_ID = new QName(NS, LN_REPOSITORY_ID, PREFIX);
    public static final QName REPOSITORY_NAME = new QName(NS, LN_REPOSITORY_NAME, PREFIX);
    public static final QName REPOSITORY_RELATIONSHIP = new QName(NS, LN_REPOSITORY_RELATIONSHIP, PREFIX);
    public static final QName REPOSITORY_DESCRIPTION = new QName(NS, LN_REPOSITORY_DESCRIPTION, PREFIX);
    public static final QName VENDOR_NAME = new QName(NS, LN_VENDOR_NAME, PREFIX);
    public static final QName PRODUCT_NAME = new QName(NS, LN_PRODUCT_NAME, PREFIX);
    public static final QName PRODUCT_VERSION = new QName(NS, LN_PRODUCT_VERSION, PREFIX);
    public static final QName ROOT_FOLDER_ID = new QName(NS, LN_ROOT_FOLDER_ID, PREFIX);
    public static final QName CAPABILITIES = new QName(NS, LN_CAPABILITIES, PREFIX);
    public static final QName CAPABILITY_MULTIFILING = new QName(NS, LN_CAPABILITY_MULTIFILING, PREFIX);
    public static final QName CAPABILITY_UNFILING = new QName(NS, LN_CAPABILITY_UNFILING, PREFIX);
    public static final QName CAPABILITY_VERSION_SPECIFIC_FILING = new QName(NS, LN_CAPABILITY_VERSION_SPECIFIC_FILING, PREFIX);
    public static final QName CAPABILITY_PWC_UPDATEABLE = new QName(NS, LN_CAPABILITY_PWC_UPDATEABLE, PREFIX);
    public static final QName CAPABILITY_PWC_SEARCHABLE = new QName(NS, LN_CAPABILITY_PWC_SEARCHABLE, PREFIX);
    public static final QName CAPABILITY_ALL_VERSIONS_SEARCHABLE = new QName(NS, LN_CAPABILITY_ALL_VERSIONS_SEARCHABLE, PREFIX);
    public static final QName CAPABILITY_QUERY = new QName(NS, LN_CAPABILITY_QUERY, PREFIX);
    public static final QName CAPABILITY_JOIN = new QName(NS, LN_CAPABILITY_JOIN, PREFIX);
    public static final QName CAPABILITY_FULL_TEXT = new QName(NS, LN_CAPABILITY_FULL_TEXT, PREFIX);
    public static final QName VERSIONS_SUPPORTED = new QName(NS, LN_VERSIONS_SUPPORTED, PREFIX);
    public static final QName REPOSITORY_SPECIFIC_INFORMATION = new QName(NS, LN_REPOSITORY_SPECIFIC_INFORMATION, PREFIX);
}
