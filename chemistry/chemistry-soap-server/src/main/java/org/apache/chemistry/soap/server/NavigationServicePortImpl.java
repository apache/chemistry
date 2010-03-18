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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Paging;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Tree;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectInFolderContainerType;
import org.apache.chemistry.ws.CmisObjectInFolderListType;
import org.apache.chemistry.ws.CmisObjectListType;
import org.apache.chemistry.ws.CmisObjectParentsType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.EnumIncludeRelationships;
import org.apache.chemistry.ws.NavigationServicePort;

@WebService(name = "NavigationService", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "NavigationService", //
portName = "NavigationServicePort", //
endpointInterface = "org.apache.chemistry.ws.NavigationServicePort")
public class NavigationServicePortImpl implements NavigationServicePort {

    @Resource
    private WebServiceContext wscontext;

    public CmisObjectListType getCheckedOutDocs(String repositoryId,
            String folderId, String filter, String orderBy,
            Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }
            RelationshipDirection inclrel = ChemistryHelper.convert(includeRelationships);
            boolean inclaa = Boolean.TRUE.equals(includeAllowableActions);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, false, false);
            int max = maxItems == null || maxItems.intValue() < 0 ? 0
                    : maxItems.intValue();
            int skip = skipCount == null || skipCount.intValue() < 0 ? 0
                    : skipCount.intValue();
            Paging paging = new Paging(max, skip);

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            ListPage<ObjectEntry> res = spi.getCheckedOutDocuments(
                    spi.newObjectId(folderId), inclusion, paging);
            return ChemistryHelper.convert(res);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public CmisObjectInFolderListType getChildren(String repositoryId,
            String folderId, String filter, String orderBy,
            Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment,
            BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }
            RelationshipDirection inclrel = ChemistryHelper.convert(includeRelationships);
            boolean inclaa = Boolean.TRUE.equals(includeAllowableActions);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, false, false);
            int max = maxItems == null || maxItems.intValue() < 0 ? 0
                    : maxItems.intValue();
            int skip = skipCount == null || skipCount.intValue() < 0 ? 0
                    : skipCount.intValue();
            Paging paging = new Paging(max, skip);
            // includePathSegment

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            ListPage<ObjectEntry> res = spi.getChildren(
                    spi.newObjectId(folderId), inclusion, orderBy, paging);
            return ChemistryHelper.convertInFolder(res);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public List<CmisObjectInFolderContainerType> getDescendants(
            String repositoryId, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment,
            CmisExtensionType extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }
            int d = depth == null ? -1 : depth.intValue();
            RelationshipDirection inclrel = ChemistryHelper.convert(includeRelationships);
            boolean inclaa = Boolean.TRUE.equals(includeAllowableActions);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, false, false);
            // includePathSegment

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            String orderBy = null; // TODO
            Tree<ObjectEntry> res = spi.getDescendants(
                    spi.newObjectId(folderId), d, orderBy, inclusion);
            return ChemistryHelper.convertForest(res);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public CmisObjectType getFolderParent(String repositoryId, String folderId,
            String filter, CmisExtensionType extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            ObjectEntry entry = spi.getFolderParent(spi.newObjectId(folderId),
                    filter);
            return ChemistryHelper.convert(entry);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public List<CmisObjectInFolderContainerType> getFolderTree(
            String repositoryId, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment,
            CmisExtensionType extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }
            int d = depth == null ? -1 : depth.intValue();
            RelationshipDirection inclrel = ChemistryHelper.convert(includeRelationships);
            boolean inclaa = Boolean.TRUE.equals(includeAllowableActions);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, false, false);
            // includePathSegment

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            Tree<ObjectEntry> res = spi.getFolderTree(
                    spi.newObjectId(folderId), d, inclusion);
            return ChemistryHelper.convertForest(res);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public List<CmisObjectParentsType> getObjectParents(String repositoryId,
            String objectId, String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includeRelativePathSegment,
            CmisExtensionType extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }
            RelationshipDirection inclrel = ChemistryHelper.convert(includeRelationships);
            boolean inclaa = Boolean.TRUE.equals(includeAllowableActions);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, false, false); // TODO unused
            // includeRelativePathSegment

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            Collection<ObjectEntry> res = spi.getObjectParents(
                    spi.newObjectId(objectId), renditionFilter);
            return ChemistryHelper.convertParents(res);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

}
