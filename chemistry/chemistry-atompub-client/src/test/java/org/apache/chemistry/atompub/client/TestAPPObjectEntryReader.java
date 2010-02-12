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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.client.APPObjectEntry.Link;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.chemistry.impl.simple.SimpleTypeManager;

public class TestAPPObjectEntryReader extends TestCase {

    protected ReadContext getReadContext() {
        Set<BaseType> changeLogBaseTypes = new HashSet<BaseType>();
        changeLogBaseTypes.add(BaseType.DOCUMENT);
        changeLogBaseTypes.add(BaseType.FOLDER);
        APPRepositoryInfo ri = new APPRepositoryInfo(
                new APPRepositoryCapabilities(), new HashMap<String, Object>(),
                changeLogBaseTypes);
        APPRepository repo = new APPRepository(new APPContentManager(null), ri);

        SimpleTypeManager tm = new SimpleTypeManager();
        PropertyDefinition psn = new SimplePropertyDefinition("string_null",
                "def:string_null", null, "string_null", "string_null", "",
                false, PropertyType.STRING, false, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition ps = new SimplePropertyDefinition("string",
                "def:string", null, "string", "string", "", false,
                PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition psa = new SimplePropertyDefinition("string_array",
                "def:string_array", null, "string_array", "string_array", "",
                false, PropertyType.STRING, true, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition pd = new SimplePropertyDefinition("date",
                "def:date", null, "date", "date", "", false,
                PropertyType.DATETIME, false, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        PropertyDefinition pda = new SimplePropertyDefinition("date_array",
                "def:date_array", null, "date_array", "date_array", "", false,
                PropertyType.DATETIME, true, null, false, false, null,
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null);
        SimpleType t = new SimpleType("doc", BaseType.DOCUMENT.getId(), "doc",
                null, "Doc", "My Doc Type", BaseType.DOCUMENT, "", true, true,
                true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(psn,
                        ps, psa, pd, pda));
        tm.addType(t);
        repo.typeManager = tm;

        return new ReadContext(new APPConnection(repo));
    }

    public void testReadObjectEntry() throws Exception {
        InputStream is = getClass().getResourceAsStream("/entry.xml");
        APPObjectEntry entry = new APPObjectEntryReader().read(
                getReadContext(), is);
        Link[] links = entry.getLinks();
        assertEquals(2, links.length);
        Link link = links[0];
        assertEquals("self", link.rel);
        assertEquals("http://host/self", link.href);
        link = links[1];
        assertEquals("up", link.rel);
        assertEquals("http://host/parents", link.href);
        assertEquals("application/atom+xml;type=feed", link.type);
        assertEquals("seg", entry.getPathSegment());
    }

}
