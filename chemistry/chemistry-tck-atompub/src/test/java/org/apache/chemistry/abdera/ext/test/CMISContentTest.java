package org.apache.chemistry.abdera.ext.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.activation.MimeType;

import junit.framework.TestCase;

import org.apache.abdera.model.Entry;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISContent;
import org.apache.chemistry.tck.atompub.client.CMISAppModel;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;

public class CMISContentTest extends TestCase {
    private CMISAppModel appModel;
    private ResourceLoader examples;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        appModel = new CMISAppModel();
        examples = new ResourceLoader("/org/apache/chemistry/abdera/ext/examples/");
    }

    public void testCMISContent() throws Exception {
        String atomentry = examples.load("cmiscontent.xml");
        Entry entry = appModel.parseEntry(new StringReader(atomentry), null);
        CMISContent content = entry.getExtension(CMISConstants.CONTENT);
        MimeType mediaType = content.getMediaType();
        assertNotNull(mediaType);
        assertEquals("text/plain", mediaType.toString());
        InputStream contentStream = content.getContentStream();
        assertNotNull(contentStream);
        InputStreamReader reader = new InputStreamReader(contentStream);
        StringWriter writer = new StringWriter();
        try {
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            writer.flush();
        } finally {
            reader.close();
            writer.close();
        }
        assertEquals("Hello World", writer.toString());
    }

}
