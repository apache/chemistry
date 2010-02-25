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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.soap.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisRepositoryCapabilitiesType;
import org.apache.chemistry.ws.CmisRepositoryEntryType;
import org.apache.chemistry.ws.CmisRepositoryInfoType;
import org.apache.chemistry.ws.CmisTypeContainer;
import org.apache.chemistry.ws.CmisTypeDefinitionListType;
import org.apache.chemistry.ws.CmisTypeDefinitionType;
import org.apache.chemistry.ws.RepositoryServicePort;

@WebService(name = "RepositoryServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "RepositoryService", //
portName = "RepositoryServicePort", //
endpointInterface = "org.apache.chemistry.ws.RepositoryServicePort")
public class RepositoryServicePortImpl implements RepositoryServicePort {

    public List<CmisRepositoryEntryType> getRepositories(
            CmisExtensionType extension) throws CmisException {
        Collection<RepositoryEntry> repos = RepositoryManager.getInstance().getRepositories();
        List<CmisRepositoryEntryType> entries = new ArrayList<CmisRepositoryEntryType>(
                repos.size());
        for (RepositoryEntry repo : repos) {
            CmisRepositoryEntryType entry = new CmisRepositoryEntryType();
            entry.setRepositoryId(repo.getId());
            entry.setRepositoryName(repo.getName());
            entries.add(entry);
        }
        return entries;
    }

    public CmisRepositoryInfoType getRepositoryInfo(String repositoryId,
            CmisExtensionType extension) throws CmisException {
        Repository repo = RepositoryManager.getInstance().getRepository(
                repositoryId);
        if (repo == null) {
            return null; // TODO or fault?
        }
        RepositoryInfo info = repo.getInfo();
        RepositoryCapabilities cap = info.getCapabilities();

        CmisRepositoryInfoType repositoryInfo = new CmisRepositoryInfoType();
        repositoryInfo.setRepositoryId(info.getId());
        repositoryInfo.setRepositoryName(info.getName());
        repositoryInfo.setRepositoryDescription(info.getDescription());
        repositoryInfo.setVendorName(info.getVendorName());
        repositoryInfo.setProductName(info.getProductName());
        repositoryInfo.setProductVersion(info.getProductVersion());
        repositoryInfo.setCmisVersionSupported(info.getVersionSupported());
        repositoryInfo.setRootFolderId(info.getRootFolderId().getId());

        CmisRepositoryCapabilitiesType capabilities = new CmisRepositoryCapabilitiesType();
        repositoryInfo.setCapabilities(capabilities);
        capabilities.setCapabilityMultifiling(cap.hasMultifiling());
        capabilities.setCapabilityUnfiling(cap.hasUnfiling());
        capabilities.setCapabilityVersionSpecificFiling(cap.hasVersionSpecificFiling());
        capabilities.setCapabilityPWCUpdatable(cap.isPWCUpdatable());
        capabilities.setCapabilityPWCSearchable(cap.isPWCSearchable());
        capabilities.setCapabilityAllVersionsSearchable(cap.isAllVersionsSearchable());
        capabilities.setCapabilityQuery(ChemistryHelper.chemistryToJAXB(cap.getQueryCapability()));
        capabilities.setCapabilityJoin(ChemistryHelper.chemistryToJAXB(cap.getJoinCapability()));

        return repositoryInfo;
    }

    public CmisTypeDefinitionType getTypeDefinition(String repositoryId,
            String typeId, CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisTypeDefinitionListType getTypeChildren(String repositoryId,
            String typeId, Boolean includePropertyDefinitions,
            BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CmisTypeContainer> getTypeDescendants(String repositoryId,
            String typeId, BigInteger depth,
            Boolean includePropertyDefinitions, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
