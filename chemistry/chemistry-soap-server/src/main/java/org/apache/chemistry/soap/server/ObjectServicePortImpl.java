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
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisContentStreamType getContentStream(String repositoryId,
            String objectId, String streamId, BigInteger offset,
            BigInteger length, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisObjectType getObject(String repositoryId, String objectId,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePolicyIds,
            Boolean includeACL, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisObjectType getObjectByPath(String repositoryId, String path,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePolicyIds,
            Boolean includeACL, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisPropertiesType getProperties(String repositoryId,
            String objectId, String filter, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
