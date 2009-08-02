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

import org.apache.chemistry.ChangeCapability;
import org.apache.chemistry.JoinCapability;
import org.apache.chemistry.QueryCapability;
import org.apache.chemistry.RenditionCapability;
import org.apache.chemistry.RepositoryCapabilities;

/**
 *
 */
public class APPRepositoryCapabilities implements RepositoryCapabilities {

    protected JoinCapability joinCapability;

    protected QueryCapability queryCapability;

    protected RenditionCapability renditionCapability;

    protected ChangeCapability changeCapability;

    protected boolean hasUnfiling;

    protected boolean hasMultifiling;

    protected boolean hasVersionSpecificFiling;

    protected boolean isAllVersionsSearchable;

    protected boolean hasGetDescendants;

    protected boolean isPWCSearchable;

    protected boolean isPWCUpdatable;

    public JoinCapability getJoinCapability() {
        return joinCapability;
    }

    public QueryCapability getQueryCapability() {
        return queryCapability;
    }

    public RenditionCapability getRenditionCapability() {
        return renditionCapability;
    }

    public ChangeCapability getChangeCapability() {
        return changeCapability;
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

    public void setHasMultifiling(boolean hasMultifiling) {
        this.hasMultifiling = hasMultifiling;
    }

    public void setHasUnfiling(boolean hasUnfiling) {
        this.hasUnfiling = hasUnfiling;
    }

    public void setHasVersionSpecificFiling(boolean hasVersionSpecificFiling) {
        this.hasVersionSpecificFiling = hasVersionSpecificFiling;
    }

    public void setJoinCapability(JoinCapability joinCapability) {
        this.joinCapability = joinCapability;
    }

    public void setPWCSearchable(boolean isPWCSearchable) {
        this.isPWCSearchable = isPWCSearchable;
    }

    public void setPWCUpdatable(boolean isPWCUpdatable) {
        this.isPWCUpdatable = isPWCUpdatable;
    }

    public void setQueryCapability(QueryCapability queryCapability) {
        this.queryCapability = queryCapability;
    }

    public void setRenditionCapability(RenditionCapability renditionCapability) {
        this.renditionCapability = renditionCapability;
    }

    public void setChangeCapability(ChangeCapability changeCapability) {
        this.changeCapability = changeCapability;
    }

}
