package org.apache.chemistry.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.ContentStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrContentStream implements ContentStream {

    private static final Log log = LogFactory.getLog(JcrFolder.class);
    private Node content;

    public JcrContentStream(Node content) {
        this.content = content;
    }

    public String getFilename() {
        try {
            return content.getName();
        } catch (RepositoryException e) {
            String msg = "Unable to get node name.";
            log.error(msg, e);
        }
        return null;
    }

    public long getLength() {
        try {
            javax.jcr.Property data = content.getProperty(JcrConstants.JCR_DATA);
            return data.getLength();
        } catch (RepositoryException e) {
            String msg = "Unable to get length.";
            log.error(msg, e);
        }
        return 0;
    }

    public String getMimeType() {
        try {
            javax.jcr.Property mimetype = content.getProperty(JcrConstants.JCR_MIMETYPE);
            return mimetype.getString();
        } catch (RepositoryException e) {
            String msg = "Unable to get mime type.";
            log.error(msg, e);
        }
        return null;
    }

    public InputStream getStream() throws IOException {
        try {
            javax.jcr.Property data = content.getProperty(JcrConstants.JCR_DATA);
            return data.getStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get stream.";
            log.error(msg, e);
        }
        return null;
    }

    public URI getURI() {
        // TODO Auto-generated method stub
        return null;
    }
}
