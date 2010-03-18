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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.SPI;
import org.apache.chemistry.ws.CmisAccessControlListType;
import org.apache.chemistry.ws.CmisAllowableActionsType;
import org.apache.chemistry.ws.CmisContentStreamType;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.CmisPropertiesType;
import org.apache.chemistry.ws.CmisRenditionType;
import org.apache.chemistry.ws.EnumIncludeRelationships;
import org.apache.chemistry.ws.EnumUnfileObject;
import org.apache.chemistry.ws.EnumVersioningState;
import org.apache.chemistry.ws.ObjectFactory;
import org.apache.chemistry.ws.ObjectServicePort;
import org.apache.chemistry.ws.DeleteTreeResponse.FailedToDelete;

@WebService(name = "ObjectServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "ObjectService", //
portName = "ObjectServicePort", //
endpointInterface = "org.apache.chemistry.ws.ObjectServicePort")
public class ObjectServicePortImpl implements ObjectServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public void createDocument(String repositoryId,
            CmisPropertiesType properties, String folderId,
            CmisContentStreamType contentStream,
            EnumVersioningState versioningState, List<String> policies,
            CmisAccessControlListType addACEs,
            CmisAccessControlListType removeACEs,
            Holder<CmisExtensionType> extension, Holder<String> objectId)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void createDocumentFromSource(String repositoryId, String sourceId,
            CmisPropertiesType properties, String folderId,
            EnumVersioningState versioningState, List<String> policies,
            CmisAccessControlListType addACEs,
            CmisAccessControlListType removeACEs,
            Holder<CmisExtensionType> extension, Holder<String> objectId)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void createFolder(String repositoryId,
            CmisPropertiesType properties, String folderId,
            List<String> policies, CmisAccessControlListType addACEs,
            CmisAccessControlListType removeACEs,
            Holder<CmisExtensionType> extension, Holder<String> objectId)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void createPolicy(String repositoryId,
            CmisPropertiesType properties, String folderId,
            List<String> policies, CmisAccessControlListType addACEs,
            CmisAccessControlListType removeACEs,
            Holder<CmisExtensionType> extension, Holder<String> objectId)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void createRelationship(String repositoryId,
            CmisPropertiesType properties, List<String> policies,
            CmisAccessControlListType addACEs,
            CmisAccessControlListType removeACEs,
            Holder<CmisExtensionType> extension, Holder<String> objectId)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteContentStream(String repositoryId,
            Holder<String> objectId, Holder<String> changeToken,
            Holder<CmisExtensionType> extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisExtensionType deleteObject(String repositoryId, String objectId,
            Boolean allVersions, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public FailedToDelete deleteTree(String repositoryId, String folderId,
            Boolean allVersions, EnumUnfileObject unfileObjects,
            Boolean continueOnFailure, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisAllowableActionsType getAllowableActions(String repositoryId,
            String objectId, CmisExtensionType extension) throws CmisException {
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
            Set<QName> res = spi.getAllowableActions(spi.newObjectId(objectId));
            return ChemistryHelper.convert(res);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public CmisContentStreamType getContentStream(String repositoryId,
            String objectId, String streamId, BigInteger offset,
            BigInteger length, CmisExtensionType extension)
            throws CmisException {
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
            ContentStream cs = spi.getContentStream(spi.newObjectId(objectId),
                    streamId);
            return ChemistryHelper.convert(cs);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public CmisObjectType getObject(String repositoryId, String objectId,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePolicyIds,
            Boolean includeACL, CmisExtensionType extension)
            throws CmisException {
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
            boolean incpol = Boolean.TRUE.equals(includePolicyIds);
            boolean incacls = Boolean.TRUE.equals(includeACL);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, incpol, incacls);

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);
            ObjectEntry entry = spi.getProperties(spi.newObjectId(objectId),
                    inclusion);
            return ChemistryHelper.convert(entry);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public CmisObjectType getObjectByPath(String repositoryId, String path,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePolicyIds,
            Boolean includeACL, CmisExtensionType extension)
            throws CmisException {
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
            boolean incpol = Boolean.TRUE.equals(includePolicyIds);
            boolean incacls = Boolean.TRUE.equals(includeACL);
            Inclusion inclusion = new Inclusion(filter, renditionFilter,
                    inclrel, inclaa, incpol, incacls);

            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);
            ObjectEntry entry = spi.getObjectByPath(path, inclusion);
            return ChemistryHelper.convert(entry);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public CmisPropertiesType getProperties(String repositoryId,
            String objectId, String filter, CmisExtensionType extension)
            throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }

            Inclusion inclusion = new Inclusion(filter, null, null, false,
                    false, false);
            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);
            ObjectEntry entry = spi.getProperties(spi.newObjectId(objectId),
                    inclusion);
            return ChemistryHelper.convertProperties(entry);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    public List<CmisRenditionType> getRenditions(String repositoryId,
            String objectId, String renditionFilter, BigInteger maxItems,
            BigInteger skipCount, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void moveObject(String repositoryId, Holder<String> objectId,
            String targetFolderId, String sourceFolderId,
            Holder<CmisExtensionType> extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setContentStream(String repositoryId, Holder<String> objectId,
            Boolean overwriteFlag, Holder<String> changeToken,
            CmisContentStreamType contentStream,
            Holder<CmisExtensionType> extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void updateProperties(String repositoryId, Holder<String> objectId,
            Holder<String> changeToken, CmisPropertiesType properties,
            Holder<CmisExtensionType> extension) throws CmisException {
        SPI spi = null;
        try {
            Repository repository = RepositoryManager.getInstance().getRepository(
                    repositoryId);
            if (repository == null) {
                String msg = "Unknown repository: " + repositoryId;
                throw new CmisException(msg, null, null);
            }
            if (objectId == null || objectId.value == null) {
                String msg = "Missing objectId";
                throw new CmisException(msg, null, null);
            }
            String id = objectId.value;
            String token = changeToken == null ? null : changeToken.value;
            Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
            spi = repository.getSPI(params);

            Map<String, Serializable> props = ChemistryHelper.convert(
                    properties, repository);

            spi.updateProperties(spi.newObjectId(id), token, props);
        } catch (Exception e) {
            throw ChemistryHelper.convert(e);
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

}
