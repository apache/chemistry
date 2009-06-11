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
package org.apache.chemistry.xml.stax;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

/**
 *
 */
public class TestXMLWriter extends TestCase {

    public static final String CMIS_NS = "http://docs.oasis-open.org/ns/cmis/core/200901";

    public static final String CMIS_PREFIX = "cmis";

    public static final QName OBJECT = new QName(CMIS_NS, "object", CMIS_PREFIX);

    public static String toString(Reader r) throws IOException {
        char[] chars = new char[1000]; // big enough for this test
        int pos = 0;
        int n = 0;
        do {
            pos += n;
            n = r.read(chars, pos, chars.length - pos);
        } while (n > 0);
        return new String(chars, 0, pos);
    }

    public void testXMLWriter() throws Exception {
        Writer w = new StringWriter();
        XMLWriter x = new XMLWriter(w, 2);
        x.start().element("service").xmlns("cmis", CMIS_NS).attr("version",
                "1.0").start().element("ws1").attr("k", "v").content("test").element(
                "ws2").attr("key", "val").start().element(OBJECT).end().element(
                "ws3").attr("key", "val").end().end();
        String actual = w.toString();

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(
                "xmlwriter-output.xml");
        String expected = toString(new InputStreamReader(stream, "UTF-8"));
        stream.close();

        assertEquals(expected.trim(), actual.trim());
    }
}
