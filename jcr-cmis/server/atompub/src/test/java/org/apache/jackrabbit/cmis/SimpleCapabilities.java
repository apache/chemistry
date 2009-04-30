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
 * Simple capabilities.
 */
public class SimpleCapabilities implements Capabilities {

    private boolean allVersionsSearchable;
    private FullTextSupport fullTextSupport;
    private JoinSupport joinSupport;
    private QuerySupport querySupport;
    private boolean multiFiling;
    private boolean unfiling;
    private boolean versionSpecificFiling;
    private boolean pwcSearchable;
    private boolean pwcUpdateable;

    /**
     * Create an instance of this class.
     */
    public SimpleCapabilities() {
        fullTextSupport = FullTextSupport.NONE;
        joinSupport = JoinSupport.NO;
        querySupport = QuerySupport.NONE;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#areAllVersionsSearchable()
     */
    public boolean areAllVersionsSearchable() {
        // TODO Auto-generated method stub
        return allVersionsSearchable;
    }

    public void setAllVersionsSearchable(boolean allVersionsSearchable) {
        this.allVersionsSearchable = allVersionsSearchable;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#getFullTextSupport()
     */
    public FullTextSupport getFullTextSupport() {
        return fullTextSupport;
    }

    public void setFullTextSupport(FullTextSupport fullTextSupport) {
        this.fullTextSupport = fullTextSupport;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#getJoinSupport()
     */
    public JoinSupport getJoinSupport() {
        return joinSupport;
    }

    public void setJoinSupport(JoinSupport joinSupport) {
        this.joinSupport = joinSupport;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#getQuerySupport()
     */
    public QuerySupport getQuerySupport() {
        return querySupport;
    }

    public void setQuerySupport(QuerySupport querySupport) {
        this.querySupport = querySupport;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#hasMultifiling()
     */
    public boolean hasMultifiling() {
        return multiFiling;
    }

    public void setMultiFiling(boolean multiFiling) {
        this.multiFiling = multiFiling;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#hasUnfiling()
     */
    public boolean hasUnfiling() {
        return unfiling;
    }

    public void setUnfiling(boolean unfiling) {
        this.unfiling = unfiling;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#hasVersionSpecificFiling()
     */
    public boolean hasVersionSpecificFiling() {
        return versionSpecificFiling;
    }

    public void setVersionSpecificFiling(boolean versionSpecificFiling) {
        this.versionSpecificFiling = versionSpecificFiling;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#isPWCSearchable()
     */
    public boolean isPWCSearchable() {
        return pwcSearchable;
    }

    public void setPWCSearchable(boolean pwcSearchable) {
        this.pwcSearchable = pwcSearchable;
    }

    /* (non-Javadoc)
     * @see org.apache.jackrabbit.cmis.Capabilities#isPWCUpdatable()
     */
    public boolean isPWCUpdatable() {
        return pwcUpdateable;
    }

    public void setPwcUpdateable(boolean pwcUpdateable) {
        this.pwcUpdateable = pwcUpdateable;
    }
}
