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
package org.apache.opencmis.client.runtime;

import java.io.Serializable;
import java.util.List;

import org.apache.opencmis.client.api.repository.RepositoryAclCapabilities;
import org.apache.opencmis.client.api.repository.RepositoryCapabilities;
import org.apache.opencmis.client.api.repository.RepositoryInfo;
import org.apache.opencmis.client.runtime.repository.RepositoryAclCapabilitiesImpl;
import org.apache.opencmis.commons.enums.BaseObjectTypeIds;
import org.apache.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.opencmis.commons.provider.RepositoryInfoData;

public class RepositoryInfoImpl implements RepositoryInfo, Serializable {

	/**
	 * serialization
	 */
	private static final long serialVersionUID = -1297274972722405445L;

	/*
	 * provider data (serializable)
	 */
	private RepositoryInfoData riData;

	/*
	 * session (serializable)
	 */
	private PersistentSessionImpl session;

	/*
	 * acl capabilities (serializable)
	 */
	private RepositoryAclCapabilities aclCapabilites = null;

	public RepositoryInfoImpl(PersistentSessionImpl session,
			RepositoryInfoData riData) {
		this.riData = riData;
		this.session = session;
	}

	public boolean changesIncomplete() {
		return this.riData.changesIncomplete();
	}

	public RepositoryAclCapabilities getAclCapabilities() {
		if (this.aclCapabilites == null) {
			this.aclCapabilites = new RepositoryAclCapabilitiesImpl(this.riData
					.getAclCapabilities());
		}
		return this.aclCapabilites;
	}

	public RepositoryCapabilities getCapabilities() {
		throw new CmisRuntimeException("not implemented");
	}

	public List<BaseObjectTypeIds> getChangesOnType() {
		return this.riData.getChangesOnType();
	}

	public String getCmisVersionSupported() {
		return this.riData.getCmisVersionSupported();
	}

	public String getDescription() {
		return this.riData.getRepositoryDescription();
	}

	public String getId() {
		return this.riData.getRepositoryId();
	}

	public String getLatestChangeLogToken() {
		return this.riData.getLatestChangeLogToken();
	}

	public String getName() {
		return this.riData.getRepositoryName();
	}

	public String getPrincipalIdAnonymous() {
		return this.riData.getPrincipalAnonymous();
	}

	public String getPrincipalIdAnyone() {
		return this.riData.getPrincipalAnyone();
	}

	public String getProductName() {
		return this.riData.getProductName();
	}

	public String getProductVersion() {
		return this.getProductVersion();
	}

	public String getRootFolderId() {
		return this.riData.getRootFolderId();
	}

	public String getThinClientUri() {
	  return this.riData.getThinClientUri();
	}

	public String getVendorName() {
		return this.riData.getVendorName();
	}

}
