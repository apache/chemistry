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

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ws.CmisAccessControlListType;
import org.apache.chemistry.ws.CmisContentStreamType;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.CmisPropertiesType;
import org.apache.chemistry.ws.EnumIncludeRelationships;
import org.apache.chemistry.ws.ObjectFactory;
import org.apache.chemistry.ws.VersioningServicePort;

@WebService(name = "VersioningServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "VersioningService", //
portName = "VersioningServicePort", //
endpointInterface = "org.apache.chemistry.ws.VersioningServicePort")
public class VersioningServicePortImpl implements VersioningServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public CmisExtensionType cancelCheckOut(String repositoryId,
            String objectId, CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void checkIn(String repositoryId, Holder<String> objectId,
            Boolean major, CmisPropertiesType properties,
            CmisContentStreamType contentStream, String checkinComment,
            List<String> policies, CmisAccessControlListType addACEs,
            CmisAccessControlListType removeACEs,
            Holder<CmisExtensionType> extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void checkOut(String repositoryId, Holder<String> objectId,
            Holder<CmisExtensionType> extension, Holder<Boolean> contentCopied)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CmisObjectType> getAllVersions(String repositoryId,
            String objectId, String filter, Boolean includeAllowableActions,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisObjectType getObjectOfLatestVersion(String repositoryId,
            String objectId, Boolean major, String filter,
            Boolean includeAllowableActions,
            EnumIncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePolicyIds,
            Boolean includeACL, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisPropertiesType getPropertiesOfLatestVersion(String repositoryId,
            String objectId, Boolean major, String filter,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
