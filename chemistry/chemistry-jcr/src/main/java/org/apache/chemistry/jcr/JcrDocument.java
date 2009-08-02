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
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ChangeInfo;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Property;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrDocument extends JcrObjectEntry implements Document {

    private static final Log log = LogFactory.getLog(JcrDocument.class);

    public JcrDocument(Node node) {
        super(node);
    }

    protected JcrDocument() {
    }

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

    public ContentStream getContentStream(String contentStreamId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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

    @Override
    public String getString(String id) {
        if (id.equals(Property.CONTENT_STREAM_MIME_TYPE)) {
            return getContentStream().getMimeType();
        }
        return super.getString(id);
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {

        throw new UnsupportedOperationException();
    }

    public void save() {
        throw new UnsupportedOperationException();
    }

    public void setName(String value) {
        throw new UnsupportedOperationException();
    }

    public void setValue(String id, Serializable value) {
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

    public void cancelCheckOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkIn(boolean major, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Document> getAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document getLatestVersion(boolean major) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public BaseType getBaseType() {
        return BaseType.DOCUMENT;
    }

    public ChangeInfo getChangeInfo() {
        return null;
    }

}
