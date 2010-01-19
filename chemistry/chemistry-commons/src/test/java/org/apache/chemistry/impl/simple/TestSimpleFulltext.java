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
 *     Amelie Avramo, EntropySoft
 */
package org.apache.chemistry.impl.simple;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class TestSimpleFulltext extends TestCase {

    public static void checkSplit(String string, String... expected) {
        assertEquals(new HashSet<String>(Arrays.asList(expected)),
                SimpleFulltext.split(string, ' '));
    }

    public static void checkSplit(char sep, String string, String... expected) {
        assertEquals(new HashSet<String>(Arrays.asList(expected)),
                SimpleFulltext.split(string, sep));
    }

    public void testSplit() {
        checkSplit("", new String[0]);
        checkSplit("A", "A");
        checkSplit("A B C", "A", "B", "C");
        checkSplit("A  B", "A", "B", "");
        checkSplit(" A B C", "A", "B", "C", "");
        checkSplit("A B C ", "A", "B", "C", "");
        checkSplit("  ", "");
        checkSplit('-', "A-B-C", "A", "B", "C");
    }

    public void testParse() {
        assertNull(SimpleFulltext.parseWord("gr"));
        assertNull(SimpleFulltext.parseWord("are"));
        assertNull(SimpleFulltext.parseWord("THE"));
        assertEquals("foo", SimpleFulltext.parseWord("foo"));
        assertEquals("foo", SimpleFulltext.parseWord("fOoS"));
    }

    protected static void checkParseFullText(String expected, String text) {
        Set<String> set = new HashSet<String>();
        SimpleFulltext.parseFullText(text, set);
        assertEquals(new HashSet<String>(Arrays.asList(expected.split(" "))),
                set);
    }

    public void testParseFullText() throws Exception {
        checkParseFullText("brown dog fail fox jump lazy over quick",
                "The quick brown fox jumps over the lazy dog -- and fails!");
        checkParseFullText("aime cafe jure pas",
                "J'aime PAS le caf\u00e9, je te jure.");
        checkParseFullText("007 bond jame thx1138", "James Bond 007 && THX1138");
    }

}
