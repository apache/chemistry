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
package org.apache.chemistry.atompub.client;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.ServiceDocumentReader;
import org.apache.chemistry.impl.simple.SimpleObjectId;

public class TestServiceDocumentReader extends TestCase {

    public void testReadServiceDocument() throws Exception {
        InputStream is = getClass().getResourceAsStream("/service-document.xml");
        ServiceDocumentReader<APPRepository> reader = new APPServiceDocumentReader();
        ReadContext ctx = new ReadContext(new APPContentManager(""));
        Repository[] repos = reader.read(ctx, is);
        assertEquals(1, repos.length);
        Repository repo = repos[0];
        assertEquals("test", repo.getId());
        assertEquals("testname", repo.getName());
        RepositoryInfo info = repo.getInfo();
        assertEquals("Repository test", info.getDescription());
        assertEquals("Apache Test", info.getVendorName());
        assertEquals("Chemistry Test", info.getProductName());
        assertEquals("1.0-test", info.getProductVersion());
        assertEquals(new SimpleObjectId("1234567890"), info.getRootFolderId());
        assertEquals("20091027-test", info.getLatestChangeLogToken());
        assertEquals("1.0-test", info.getVersionSupported());
        assertFalse(info.isChangeLogIncomplete());
        Set<BaseType> clbt = info.getChangeLogBaseTypes();
        Set<BaseType> clbtExpected = new HashSet<BaseType>(Arrays.asList(
                BaseType.FOLDER, BaseType.DOCUMENT));
        assertEquals(clbtExpected, clbt);
        RepositoryCapabilities cap = info.getCapabilities();
        assertEquals(CapabilityACL.MANAGE, cap.getACLCapability());
        assertFalse(cap.isAllVersionsSearchable());
        assertEquals(CapabilityChange.OBJECT_IDS_ONLY,
                cap.getChangeCapability());
        assertTrue(cap.isContentStreamUpdatableAnytime());
        assertTrue(cap.hasGetDescendants());
        assertTrue(cap.hasGetFolderTree());
        assertFalse(cap.hasMultifiling());
        assertFalse(cap.isPWCSearchable());
        assertTrue(cap.isPWCUpdatable());
        assertEquals(CapabilityQuery.BOTH_COMBINED, cap.getQueryCapability());
        assertEquals(CapabilityRendition.READ, cap.getRenditionCapability());
        assertFalse(cap.hasUnfiling());
        assertFalse(cap.hasVersionSpecificFiling());
        assertEquals(CapabilityJoin.INNER_AND_OUTER, cap.getJoinCapability());
    }

}
