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

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectListType;
import org.apache.chemistry.ws.EnumRelationshipDirection;
import org.apache.chemistry.ws.ObjectFactory;
import org.apache.chemistry.ws.RelationshipServicePort;

@WebService(name = "RelationshipServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "RelationshipService", //
portName = "RelationshipServicePort", //
endpointInterface = "org.apache.chemistry.ws.RelationshipServicePort")
public class RelationshipServicePortImpl implements RelationshipServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public CmisObjectListType getObjectRelationships(String repositoryId,
            String objectId, Boolean includeSubRelationshipTypes,
            EnumRelationshipDirection relationshipDirection, String typeId,
            String filter, Boolean includeAllowableActions,
            BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
