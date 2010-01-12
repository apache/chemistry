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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.chemistry.CMIS;

import junit.framework.TestCase;

/**
 *
 */
public class TestXMLWriter extends TestCase {

    public static final QName OBJECT = new QName(CMIS.CMIS_NS, "object",
            CMIS.CMIS_PREFIX);

    public void testXMLWriter() throws Exception {
        Writer w = new StringWriter();
        XMLWriter x = new XMLWriter(w, 2);
        String s = "abcdefghij";
        InputStream in = new ByteArrayInputStream(
                (s + s + s + s + s + s + s).getBytes("UTF-8"));
        x.start();
        {
            x.element("service");
            x.xmlns("cmis", CMIS.CMIS_NS).attr("version", "1.0");
            x.start();
            {
                x.element("ws1").attr("k", "v").content("test");
                x.element("ws2").attr("key", "val");
                x.start();
                {
                    x.element(OBJECT);
                }
                x.end();
                x.element("ws3").attr("key", "val");
                x.element("ws4").eattr("key", "a&b<c>d\"e'f");
                x.element("ws5").econtent("a&b<c>d\"e'f");
                x.element("ws6").contentBase64(in);
            }
            x.end();
        }
        x.end();
        String actual = w.toString();

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(
                "xmlwriter-output.xml");
        String expected = toString(new InputStreamReader(stream, "UTF-8"));
        stream.close();

        assertEquals(expected.trim(), actual.trim());
    }

    // Regression test: test corner case when stream length = 3*19.
    public void testEncodeBase64CornerCase() throws Exception {
        Writer w = new StringWriter();
        XMLWriter x = new XMLWriter(w, 2);
        String s = "abcdefghij" + "abcdefghij" + "abcdefghij" + "abcdefghij" + "abcdefghij" + "abcdefg";
        assertEquals(3*19, s.length());

        InputStream in = new ByteArrayInputStream(s.getBytes("UTF-8"));
        x.start();
        x.element("root").contentBase64(in);
        x.end();
        String actual = w.toString();
        String expected = "YWJjZGVmZ2hpamFiY2RlZmdoaWphYmNkZWZnaGlqYWJjZGVmZ2hpamFiY2RlZmdoaWphYmNkZWZn";
    }

    public void testFormatDate() {
        String s = XMLWriter.formatDate(new Date(0));
        assertEquals("1970-01-01T00:00:00.000Z", s);
    }

    private static String toString(Reader r) throws IOException {
        char[] chars = new char[1000]; // big enough for this test
        int pos = 0;
        int n = 0;
        do {
            pos += n;
            n = r.read(chars, pos, chars.length - pos);
        } while (n > 0);
        return new String(chars, 0, pos);
    }

}
