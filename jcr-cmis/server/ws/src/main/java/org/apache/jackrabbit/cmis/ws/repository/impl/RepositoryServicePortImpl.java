/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis.ws.repository.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.jcr.Repository;
import javax.jws.WebService;

import org.apache.jackrabbit.cmis.ws.repository.CmisRepositoryCapabilitiesType;
import org.apache.jackrabbit.cmis.ws.repository.CmisRepositoryEntryType;
import org.apache.jackrabbit.cmis.ws.repository.CmisRepositoryInfoType;
import org.apache.jackrabbit.cmis.ws.repository.ConstraintViolationException;
import org.apache.jackrabbit.cmis.ws.repository.EnumCapabilityFullText;
import org.apache.jackrabbit.cmis.ws.repository.EnumCapabilityJoin;
import org.apache.jackrabbit.cmis.ws.repository.EnumCapabilityQuery;
import org.apache.jackrabbit.cmis.ws.repository.EnumRepositoryRelationship;
import org.apache.jackrabbit.cmis.ws.repository.GetRepositoryInfo;
import org.apache.jackrabbit.cmis.ws.repository.GetTypeDefinition;
import org.apache.jackrabbit.cmis.ws.repository.GetTypeDefinitionResponse;
import org.apache.jackrabbit.cmis.ws.repository.GetTypes;
import org.apache.jackrabbit.cmis.ws.repository.GetTypesResponse;
import org.apache.jackrabbit.cmis.ws.repository.InvalidArgumentException;
import org.apache.jackrabbit.cmis.ws.repository.ObjectNotFoundException;
import org.apache.jackrabbit.cmis.ws.repository.OperationNotSupportedException;
import org.apache.jackrabbit.cmis.ws.repository.PermissionDeniedException;
import org.apache.jackrabbit.cmis.ws.repository.RepositoryServicePort;
import org.apache.jackrabbit.cmis.ws.repository.RuntimeException;
import org.apache.jackrabbit.cmis.ws.repository.TypeNotFoundException;
import org.apache.jackrabbit.cmis.ws.repository.UpdateConflictException;
import org.apache.jackrabbit.core.TransientRepository;


@WebService(name = "RepositoryServicePort", serviceName = "RepositoryService", portName = "RepositoryServicePort", targetNamespace = "http://www.cmis.org/ns/1.0", endpointInterface = "org.apache.jackrabbit.cmis.ws.repository.RepositoryServicePort")
public class RepositoryServicePortImpl implements RepositoryServicePort{

	public List<CmisRepositoryEntryType> getRepositories()
			throws PermissionDeniedException, UpdateConflictException,
			OperationNotSupportedException, InvalidArgumentException,
			RuntimeException {
        CmisRepositoryEntryType repositoryEntryType = new CmisRepositoryEntryType();
        repositoryEntryType.setRepositoryID("ID");
        repositoryEntryType.setRepositoryName("Jackrabbit");
        return Collections.singletonList(repositoryEntryType);
	}

	public CmisRepositoryInfoType getRepositoryInfo(GetRepositoryInfo parameters)
			throws PermissionDeniedException, UpdateConflictException,
			ObjectNotFoundException, OperationNotSupportedException,
			InvalidArgumentException, RuntimeException,
			ConstraintViolationException {
		CmisRepositoryInfoType repositoryInfo = new CmisRepositoryInfoType();
		CmisRepositoryCapabilitiesType capabilities = new CmisRepositoryCapabilitiesType();
		repositoryInfo.setCapabilities(capabilities);
		
		/** FIXME remove this Repository instantiation; get the repository from parameters info **/
		Repository repository = null;
		try {
			 repository = new TransientRepository();
		} catch (IOException e) {
			e.printStackTrace();
		}

        repositoryInfo.setRepositoryName(repository.getDescriptor(Repository.REP_NAME_DESC));
        repositoryInfo.setRepositoryRelationship(EnumRepositoryRelationship.SELF.toString());
        repositoryInfo.setRepositoryDescription(repository.getDescriptor(Repository.REP_NAME_DESC));
        repositoryInfo.setVendorName(repository.getDescriptor(Repository.REP_VENDOR_DESC));
        repositoryInfo.setProductName(repository.getDescriptor(Repository.REP_NAME_DESC));
        repositoryInfo.setProductVersion(repository.getDescriptor(Repository.REP_VERSION_DESC));
        repositoryInfo.setCmisVersionsSupported("0.5");
        
        capabilities.setCapabilityMultifiling(true);
        capabilities.setCapabilityUnfiling(true);
        /** FIXME validate this **/
        capabilities.setCapabilityVersionSpecificFiling(false);
        /** FIXME validate this **/
        capabilities.setCapabilityPWCUpdateable(false);
        /** FIXME validate this **/
        capabilities.setCapabilityPWCSearchable(false);
        capabilities.setCapabilityQuery(EnumCapabilityQuery.BOTH);
        capabilities.setCapabilityJoin(EnumCapabilityJoin.NOJOIN);
        capabilities.setCapabilityFullText(EnumCapabilityFullText.FULLTEXTANDSTRUCTURED);

        return repositoryInfo;
	}

	public GetTypeDefinitionResponse getTypeDefinition(
			GetTypeDefinition parameters) throws PermissionDeniedException,
			UpdateConflictException, ObjectNotFoundException,
			OperationNotSupportedException, TypeNotFoundException,
			InvalidArgumentException, RuntimeException,
			ConstraintViolationException {
		// TODO Auto-generated method stub
		return null;
	}

	public GetTypesResponse getTypes(GetTypes parameters)
			throws PermissionDeniedException, UpdateConflictException,
			ObjectNotFoundException, OperationNotSupportedException,
			InvalidArgumentException, RuntimeException,
			ConstraintViolationException {
		// TODO Auto-generated method stub
		return null;
	}

}
