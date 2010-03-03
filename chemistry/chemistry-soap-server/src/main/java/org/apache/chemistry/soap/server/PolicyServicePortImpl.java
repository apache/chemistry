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
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.ObjectFactory;
import org.apache.chemistry.ws.PolicyServicePort;

@WebService(name = "PolicyServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "PolicyService", //
portName = "PolicyServicePort", //
endpointInterface = "org.apache.chemistry.ws.PolicyServicePort")
public class PolicyServicePortImpl implements PolicyServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public CmisExtensionType applyPolicy(String repositoryId, String policyId,
            String objectId, CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CmisObjectType> getAppliedPolicies(String repositoryId,
            String objectId, String filter, CmisExtensionType extension)
            throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CmisExtensionType removePolicy(String repositoryId, String policyId,
            String objectId, CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
