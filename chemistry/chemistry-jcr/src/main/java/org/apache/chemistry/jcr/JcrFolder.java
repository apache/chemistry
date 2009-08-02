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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.ChangeInfo;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Unfiling;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrFolder extends JcrObjectEntry implements Folder {

    private static final Log log = LogFactory.getLog(JcrFolder.class);

    public JcrFolder(Node node) {
        super(node);
    }

    protected JcrFolder() {
    }

    public List<CMISObject> getChildren() {
        try {
            List<CMISObject> result = new ArrayList<CMISObject>();

            NodeIterator iter = node.getNodes();
            while (iter.hasNext()) {
                Node child = iter.nextNode();
                CMISObject entry = null;
                if (child.isNodeType(JcrConstants.NT_FOLDER)) {
                    entry = new JcrFolder(child);
                } else if (child.isNodeType(JcrConstants.NT_FILE)) {
                    entry = new JcrDocument(child);
                } else {
                    continue;
                }
                result.add(entry);
            }
            return result;
        } catch (RepositoryException e) {
            String msg = "Unable to get children.";
            log.error(msg, e);
        }
        return null;
    }

    public Document newDocument(String typeId) {
        return new JcrNewDocument(node);
    }

    public Folder newFolder(String typeId) {
        return new JcrNewFolder(node);
    }

    public Folder getParent() {
        try {
            if (node.getDepth() > 0) {
                return new JcrFolder(node.getParent());
            }
        } catch (RepositoryException e) {
            String msg = "Unable to get parent.";
            log.error(msg, e);
        }
        return null;
    }

    public void save() {
        throw new UnsupportedOperationException();
    }

    public void setName(String id) {
        throw new UnsupportedOperationException();
    }

    public void setValue(String id, Serializable value) {
        throw new UnsupportedOperationException();
    }

    public void setValues(Map<String, Serializable> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Folder getFolder() {
        return this;
    }

    public void add(CMISObject object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void remove(CMISObject object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectId> deleteTree(Unfiling unfiling) {
        delete();
        return Collections.emptySet();
    }

    public List<Folder> getAncestors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public BaseType getBaseType() {
        return BaseType.FOLDER;
    }

    public ChangeInfo getChangeInfo() {
        return null;
    }

    public ContentStream getContentStream(String contentStreamId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
