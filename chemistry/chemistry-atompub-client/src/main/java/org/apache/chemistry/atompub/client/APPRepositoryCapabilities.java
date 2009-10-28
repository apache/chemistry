/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.RepositoryCapabilities;

/**
 *
 */
public class APPRepositoryCapabilities implements RepositoryCapabilities {

    protected CapabilityJoin joinCapability;

    protected CapabilityQuery queryCapability;

    protected CapabilityRendition renditionCapability;

    protected CapabilityChange changeCapability;

    protected CapabilityACL aclCapability;

    protected boolean hasUnfiling;

    protected boolean hasMultifiling;

    protected boolean hasVersionSpecificFiling;

    protected boolean isAllVersionsSearchable;

    protected boolean hasGetDescendants;

    protected boolean hasGetFolderTree;

    protected boolean isContentStreamUpdatableAnytime;

    protected boolean isPWCSearchable;

    protected boolean isPWCUpdatable;

    public CapabilityJoin getJoinCapability() {
        return joinCapability;
    }

    public CapabilityQuery getQueryCapability() {
        return queryCapability;
    }

    public CapabilityRendition getRenditionCapability() {
        return renditionCapability;
    }

    public CapabilityChange getChangeCapability() {
        return changeCapability;
    }

    public CapabilityACL getACLCapability() {
        return aclCapability;
    }

    public boolean hasMultifiling() {
        return hasMultifiling;
    }

    public boolean hasUnfiling() {
        return hasUnfiling;
    }

    public boolean hasVersionSpecificFiling() {
        return hasVersionSpecificFiling;
    }

    public boolean isAllVersionsSearchable() {
        return isAllVersionsSearchable;
    }

    public boolean hasGetDescendants() {
        return hasGetDescendants;
    }

    public boolean hasGetFolderTree() {
        return hasGetFolderTree;
    }

    public boolean isContentStreamUpdatableAnytime() {
        return isContentStreamUpdatableAnytime;
    }

    public boolean isPWCSearchable() {
        return isPWCSearchable;
    }

    public boolean isPWCUpdatable() {
        return isPWCUpdatable;
    }

    public void setAllVersionsSearchable(boolean isAllVersionsSearchable) {
        this.isAllVersionsSearchable = isAllVersionsSearchable;
    }

    public void setHasGetDescendants(boolean hasGetDescendants) {
        this.hasGetDescendants = hasGetDescendants;
    }

    public void setHasGetFolderTree(boolean hasGetFolderTree) {
        this.hasGetFolderTree = hasGetFolderTree;
    }

    public void setContentStreamUpdatableAnytime(
            boolean isContentStreamUpdatableAnytime) {
        this.isContentStreamUpdatableAnytime = isContentStreamUpdatableAnytime;
    }

    public void setHasMultifiling(boolean hasMultifiling) {
        this.hasMultifiling = hasMultifiling;
    }

    public void setHasUnfiling(boolean hasUnfiling) {
        this.hasUnfiling = hasUnfiling;
    }

    public void setHasVersionSpecificFiling(boolean hasVersionSpecificFiling) {
        this.hasVersionSpecificFiling = hasVersionSpecificFiling;
    }

    public void setJoinCapability(CapabilityJoin joinCapability) {
        this.joinCapability = joinCapability;
    }

    public void setPWCSearchable(boolean isPWCSearchable) {
        this.isPWCSearchable = isPWCSearchable;
    }

    public void setPWCUpdatable(boolean isPWCUpdatable) {
        this.isPWCUpdatable = isPWCUpdatable;
    }

    public void setQueryCapability(CapabilityQuery queryCapability) {
        this.queryCapability = queryCapability;
    }

    public void setRenditionCapability(CapabilityRendition renditionCapability) {
        this.renditionCapability = renditionCapability;
    }

    public void setChangeCapability(CapabilityChange changeCapability) {
        this.changeCapability = changeCapability;
    }

    public void setACLCapability(CapabilityACL aclCapability) {
        this.aclCapability = aclCapability;
    }

}
