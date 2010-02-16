/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.opencmis.server.impl.webservices;

import static org.apache.opencmis.commons.impl.Converter.convert;
import static org.apache.opencmis.commons.impl.Converter.convertHolder;
import static org.apache.opencmis.commons.impl.Converter.setHolderValue;

import java.math.BigInteger;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

import org.apache.opencmis.commons.enums.IncludeRelationships;
import org.apache.opencmis.commons.impl.jaxb.CmisException;
import org.apache.opencmis.commons.impl.jaxb.CmisExtensionType;
import org.apache.opencmis.commons.impl.jaxb.CmisObjectListType;
import org.apache.opencmis.commons.impl.jaxb.DiscoveryServicePort;
import org.apache.opencmis.commons.impl.jaxb.EnumIncludeRelationships;
import org.apache.opencmis.commons.provider.ObjectList;
import org.apache.opencmis.server.spi.AbstractServicesFactory;
import org.apache.opencmis.server.spi.CallContext;
import org.apache.opencmis.server.spi.CmisDiscoveryService;

/**
 * CMIS Discovery Service.
 * 
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
@WebService(endpointInterface = "org.apache.opencmis.commons.impl.jaxb.DiscoveryServicePort")
public class DiscoveryService extends AbstractService implements DiscoveryServicePort {
  @Resource
  WebServiceContext fContext;

  public void getContentChanges(String repositoryId, Holder<String> changeLogToken,
      Boolean includeProperties, String filter, Boolean includePolicyIds, Boolean includeAcl,
      BigInteger maxItems, CmisExtensionType extension, Holder<CmisObjectListType> objects)
      throws CmisException {
    try {
      AbstractServicesFactory factory = getServicesFactory(fContext);
      CmisDiscoveryService service = factory.getDiscoveryService();
      CallContext context = createContext(fContext);

      org.apache.opencmis.commons.provider.Holder<String> changeLogTokenHolder = convertHolder(changeLogToken);

      ObjectList changesList = service.getContentChanges(context, repositoryId,
          changeLogTokenHolder, includeProperties, filter, includePolicyIds, includeAcl, maxItems,
          convert(extension), null);

      if (objects != null) {
        objects.value = convert(changesList);
      }

      setHolderValue(changeLogTokenHolder, changeLogToken);
    }
    catch (Exception e) {
      throw convertException(e);
    }
  }

  public CmisObjectListType query(String repositoryId, String statement, Boolean searchAllVersions,
      Boolean includeAllowableActions, EnumIncludeRelationships includeRelationships,
      String renditionFilter, BigInteger maxItems, BigInteger skipCount, CmisExtensionType extension)
      throws CmisException {
    try {
      AbstractServicesFactory factory = getServicesFactory(fContext);
      CmisDiscoveryService service = factory.getDiscoveryService();
      CallContext context = createContext(fContext);

      return convert(service.query(context, repositoryId, statement, searchAllVersions,
          includeAllowableActions, convert(IncludeRelationships.class, includeRelationships),
          renditionFilter, maxItems, skipCount, convert(extension)));
    }
    catch (Exception e) {
      throw convertException(e);
    }
  }

}
