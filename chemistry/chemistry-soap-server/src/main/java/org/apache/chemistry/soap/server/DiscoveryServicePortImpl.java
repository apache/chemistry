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

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Paging;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.SPI;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisObjectListType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.DiscoveryServicePort;
import org.apache.chemistry.ws.EnumIncludeRelationships;
import org.apache.chemistry.ws.Query;
import org.apache.chemistry.ws.QueryResponse;

@WebService(name = "DiscoveryServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "DiscoveryService", //
portName = "DiscoveryServicePort", //
endpointInterface = "org.apache.chemistry.ws.DiscoveryServicePort")
public class DiscoveryServicePortImpl implements DiscoveryServicePort {

    @Resource
    WebServiceContext wscontext;

    public QueryResponse query(Query parameters) throws CmisException {
        // repository
        String repositoryId = parameters.getRepositoryId();
        Repository repository = RepositoryManager.getInstance().getRepository(
                repositoryId);
        if (repository == null) {
            return null; // TODO fault
        }

        // parameters
        String statement = parameters.getStatement();
        JAXBElement<Boolean> searchAllVersionsB = parameters.getSearchAllVersions();
        boolean searchAllVersions = searchAllVersionsB == null ? false
                : searchAllVersionsB.getValue().booleanValue();

        JAXBElement<BigInteger> maxItemsBI = parameters.getMaxItems();
        int maxItems = maxItemsBI == null || maxItemsBI.getValue() == null
                || maxItemsBI.getValue().intValue() < 0 ? 0
                : maxItemsBI.getValue().intValue();
        JAXBElement<BigInteger> skipCountBI = parameters.getSkipCount();
        int skipCount = skipCountBI == null || skipCountBI.getValue() == null
                || skipCountBI.getValue().intValue() < 0 ? 0
                : skipCountBI.getValue().intValue();
        Paging paging = new Paging(maxItems, skipCount);

        JAXBElement<Boolean> includeAllowableActions = parameters.getIncludeAllowableActions();
        boolean allowableActions = includeAllowableActions == null
                || includeAllowableActions.getValue() == null ? false
                : includeAllowableActions.getValue().booleanValue();
        JAXBElement<EnumIncludeRelationships> includeRelationships = parameters.getIncludeRelationships();
        RelationshipDirection relationships = includeRelationships == null
                || includeRelationships.getValue() == null ? null
                : RelationshipDirection.fromInclusion(includeRelationships.getValue().name());
        JAXBElement<String> renditionFilter = parameters.getRenditionFilter();
        String renditions = renditionFilter == null
                || renditionFilter.getValue() == null ? null
                : renditionFilter.getValue();
        Inclusion inclusion = new Inclusion(null, renditions, relationships,
                allowableActions, false, false);

        // response
        QueryResponse response = new QueryResponse();
        CmisObjectListType objects = new CmisObjectListType();
        response.setObjects(objects);

        Map<String, Serializable> params = CallContext.mapFromWebServiceContext(wscontext);
        SPI spi = repository.getSPI(params);
        try {
            ListPage<ObjectEntry> res = spi.query(statement, searchAllVersions,
                    inclusion, paging);
            objects.setHasMoreItems(res.getHasMoreItems());
            objects.setNumItems(BigInteger.valueOf(res.getNumItems()));
            List<CmisObjectType> objectList = objects.getObjects();
            for (ObjectEntry entry : res) {
                CmisObjectType object = new CmisObjectType();
                ChemistryHelper.chemistryToJAXB(entry, object);
                objectList.add(object);
            }
        } finally {
            spi.close();
        }

        return response;
    }

    public void getContentChanges(String repositoryId,
            Holder<String> changeLogToken, Boolean includeProperties,
            String filter, Boolean includePolicyIds, Boolean includeACL,
            BigInteger maxItems, CmisExtensionType extension,
            Holder<CmisObjectListType> objects) throws CmisException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
