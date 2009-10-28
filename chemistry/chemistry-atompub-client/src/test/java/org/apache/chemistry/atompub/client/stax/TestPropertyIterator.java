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
 *     Bogdan Stefanescu, Nuxeo
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client.stax;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public class TestPropertyIterator extends TestCase {

    public void testPropertyIterator() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("entry.xml");
        StaxReader sr = StaxReader.newReader(url.openStream());
        sr.getFirstTag(CMIS.PROPERTIES);
        PropertyIterator pi = new PropertyIterator(sr);
        List<String> ids = new LinkedList<String>();
        while (pi.hasNext()) {
            ids.add(pi.next().getId());
        }
        assertEquals(Arrays.asList("string_null", "string", "date",
                "string_array", "date_array"), ids);
    }

}
