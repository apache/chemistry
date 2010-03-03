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

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.MultiFilingServicePort;
import org.apache.chemistry.ws.ObjectFactory;

@WebService(name = "MultiFilingServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "MultiFilingService", //
portName = "MultiFilingServicePort", //
endpointInterface = "org.apache.chemistry.ws.MultiFilingServicePort")
public class MultiFilingServicePortImpl implements MultiFilingServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public CmisExtensionType addObjectToFolder(String repositoryId,
            String objectId, String folderId, Boolean allVersions,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisExtensionType removeObjectFromFolder(String repositoryId,
            String objectId, String folderId, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
