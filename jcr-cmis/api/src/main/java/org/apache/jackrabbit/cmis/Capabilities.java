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

/**
 * Describes the capabilities of a repository.
 */
public interface Capabilities {

    /**
     * Ability to file a document (or other fileable object that is not a
     * folder) in more than a folder.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean hasMultifiling();

    /**
     * Ability to leave a document (or other fileable object that is not a
     * folder) not filed in a any folder.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean hasUnfiling();

    /**
     * Ability to file a particular version (i.e., not all versions) of a
     * document in a folder.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean hasVersionSpecificFiling();

    /**
     * Ability to update the "Private Working Copy" of a checked-out document.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean isPWCUpdatable();

    /**
     * Ability to include non-latest versions of document in query search
     * scope; otherwise only the latest version of each document is searchable.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean areAllVersionsSearchable();

    /**
     * Ability to include the "Private Working Copy" of checked-out documents
     * in query search scope; otherwise PWC's are not searchable.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean isPWCSearchable();

    /**
     * Ability to provide query.
     *
     * @return query support enum
     */
    public QuerySupport getQuerySupport();

    /**
     * Join support level in query.
     *
     * @return join support enum
     */
    public JoinSupport getJoinSupport();

    /**
     * Full-text search support level in query.
     *
     * @return full text support enum
     */
    public FullTextSupport getFullTextSupport();
}
