/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.chemistry.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.property.Property;
import org.apache.chemistry.type.BaseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrDocument extends JcrObjectEntry implements Document {

    private static final Log log = LogFactory.getLog(JcrDocument.class);

    public JcrDocument(Node node) {
        super(node);
    }

    protected JcrDocument() {}

    public ContentStream getContentStream() {
        try {
            Node content = node.getNode(JcrConstants.JCR_CONTENT);
            return new JcrContentStream(content);
        } catch (RepositoryException e) {
            String msg = "Unable to get parent.";
            log.error(msg, e);
        }
        return null;
    }

    public Folder getParent() {
        try {
            return new JcrFolder(node.getParent());
        } catch (RepositoryException e) {
            String msg = "Unable to get parent.";
            log.error(msg, e);
        }
        return null;
    }

    public InputStream getStream() throws IOException {
        try {
            Node content = node.getNode(JcrConstants.JCR_CONTENT);
            javax.jcr.Property prop = content.getProperty(JcrConstants.JCR_DATA);
            return prop.getStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get stream.";
            log.error(msg, e);
        }
        return null;
    }

    @Override
    public String getString(String name) {
        if (name.equals(Property.CONTENT_STREAM_MIME_TYPE)) {
            return getContentStream().getMimeType();
        }
        return super.getString(name);
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {

        throw new UnsupportedOperationException();
    }

    public void save() {
        throw new UnsupportedOperationException();
    }

    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public void setValue(String name, Serializable value) {
        throw new UnsupportedOperationException();
    }

    public void setValues(Map<String, Serializable> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document getDocument() {
        return this;
    }

    @Override
    public boolean hasContentStream() {
        try {
            return node.hasNode(JcrConstants.JCR_CONTENT);
        } catch (RepositoryException e) {
            String msg = "Unable to get sub node: " + JcrConstants.JCR_CONTENT;
            log.error(msg, e);
        }
        return false;
    }

    @Override
    protected BaseType getBaseType() {
    	return BaseType.DOCUMENT;
    }
}
