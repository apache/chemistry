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
package org.apache.jackrabbit.cmis.ws.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.jcr.Repository;
import javax.xml.ws.Endpoint;

import junit.framework.TestCase;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.jackrabbit.cmis.ws.repository.impl.RepositoryServicePortImpl;
import org.apache.jackrabbit.core.TransientRepository;

public class RepositoryServiceTest extends TestCase {
	
	private RepositoryServicePort client;
	private Repository repository;
	private GetRepositoryInfo parameters;
	
	protected void setUp() {
        // Starting a Server
        RepositoryServicePortImpl implementor = new RepositoryServicePortImpl();
        String address = "http://localhost:9000/repositoryService";
        Endpoint.publish(address, implementor);
        
        // Creating a Jax-ws client
    	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
    	factory.getInInterceptors().add(new LoggingInInterceptor());
    	factory.getOutInterceptors().add(new LoggingOutInterceptor());
    	factory.setServiceClass(RepositoryServicePort.class);
    	factory.setAddress
    	("http://localhost:9000/repositoryService");
    	client = (RepositoryServicePort) factory.create();

    	// Instantiate a Repository
		try {
			repository = new TransientRepository("target/test-classes/repository.xml", "target/test-classes/repository");
		} catch (IOException e) {
			this.fail("Some exception occurred instantiating a TransientRepository object");
			e.printStackTrace();
		}
    	
    	// init a GetRepositoryInfo object
    	parameters = new GetRepositoryInfo();
    	parameters.setRepositoryId("ID");
	}
	
	protected void tearDown() {
		// Drop the Repository created in setUp
		File repositoryRoot = new File("target/test-classes/repository");
		repositoryRoot.delete();
	}

	public void testGetRepositories() {
    	List repositories = null;
		try {
			repositories = client.getRepositories();
		} catch (Exception e) {
			this.fail("Some exception occurred getting repositories");
			e.printStackTrace();
		}
    	assertEquals(1, repositories.size());
	}
	
	public void testGetRepositoryInfo() {
		CmisRepositoryInfoType repositoryInfo = null;
		try {
			repositoryInfo = client.getRepositoryInfo(parameters);
		} catch (Exception e) {
			this.fail("Some exception occurred getting repositoryInfo");
			e.printStackTrace();
		}
		assertEquals("Jackrabbit", repositoryInfo.getRepositoryName());
		assertEquals("SELF", repositoryInfo.getRepositoryRelationship());
		assertEquals("Jackrabbit", repositoryInfo.getRepositoryDescription());
		assertEquals("Apache Software Foundation", repositoryInfo.getVendorName());
		assertEquals("Jackrabbit", repositoryInfo.getProductName());
		assertEquals("1.6-SNAPSHOT", repositoryInfo.getProductVersion());
		assertEquals(true, repositoryInfo.getCapabilities().isCapabilityMultifiling());
		assertEquals(true, repositoryInfo.getCapabilities().isCapabilityUnfiling());
		assertEquals(EnumCapabilityQuery.BOTH, repositoryInfo.getCapabilities().getCapabilityQuery());
		assertEquals(EnumCapabilityJoin.NOJOIN, repositoryInfo.getCapabilities().getCapabilityJoin());
		assertEquals(EnumCapabilityFullText.FULLTEXTANDSTRUCTURED, repositoryInfo.getCapabilities().getCapabilityFullText());
	}

}
