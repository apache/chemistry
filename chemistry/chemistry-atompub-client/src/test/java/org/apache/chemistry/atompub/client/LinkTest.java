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

import junit.framework.TestCase;

import org.apache.chemistry.atompub.client.APPObjectEntry.Link;

public class LinkTest extends TestCase {

    public void testLink() throws Exception {
        assertNull(Link.canonicalType(null));
        assertEquals("foo/bar", Link.canonicalType("foo/bar"));
        assertEquals("foo/bar;type=gee", Link.canonicalType("foo/bar; TYPE = \"gee\""));
    }

}
