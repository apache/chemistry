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
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectInFolderContainerType;
import org.apache.chemistry.ws.CmisObjectInFolderListType;
import org.apache.chemistry.ws.CmisObjectListType;
import org.apache.chemistry.ws.CmisObjectParentsType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.EnumIncludeRelationships;
import org.apache.chemistry.ws.NavigationServicePort;
import org.apache.chemistry.ws.ObjectFactory;

@WebService(name = "NavigationService", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "NavigationService", //
portName = "NavigationServicePort", //
endpointInterface = "org.apache.chemistry.ws.NavigationServicePort")
public class NavigationServicePortImpl implements NavigationServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public CmisObjectListType getCheckedOutDocs(String repositoryId,
            String folderId, String filter, String orderBy,
            Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisObjectInFolderListType getChildren(String repositoryId,
            String folderId, String filter, String orderBy,
            Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment,
            BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CmisObjectInFolderContainerType> getDescendants(
            String repositoryId, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisObjectType getFolderParent(String repositoryId, String folderId,
            String filter, CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CmisObjectInFolderContainerType> getFolderTree(
            String repositoryId, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CmisObjectParentsType> getObjectParents(String repositoryId,
            String objectId, String filter, Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includeRelativePathSegment,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
