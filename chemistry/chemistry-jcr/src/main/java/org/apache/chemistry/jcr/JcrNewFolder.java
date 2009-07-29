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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Unfiling;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrNewFolder extends JcrFolder {

    private static final Log log = LogFactory.getLog(JcrNewFolder.class);

    private final Node parent;
    private final Map<String, Serializable> values = new HashMap<String, Serializable>();
    private String name;
    private boolean saved;

    public JcrNewFolder(Node parent) {
        this.parent = parent;
    }

    @Override
    public List<CMISObject> getChildren(BaseType type) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.getChildren(type);
    }

    @Override
    public Document newDocument(String typeId) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.newDocument(typeId);
    }

    @Override
    public Folder newFolder(String typeId) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.newFolder(typeId);
    }

    @Override
    public Folder getParent() {
        if (!saved) {
            return new JcrFolder(parent);
        }
        return super.getParent();
    }

    @Override
    public void add(CMISObject object) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        super.add(object);
    }

    @Override
    public void remove(CMISObject object) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        super.remove(object);
    }

    @Override
    public void delete() {
        // TODO delete of an unsaved folder?
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        super.delete();
    }

    @Override
    public Collection<ObjectId> deleteTree(Unfiling unfiling) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.deleteTree(unfiling);
    }

    @Override
    public List<Folder> getAncestors() {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.getAncestors();
    }

    @Override
    public void save() {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        try {
            Node node = parent.addNode(name, JcrConstants.NT_FOLDER);
            node.addMixin(MIX_UNSTRUCTURED);

            for (String key : values.keySet()) {
                node.setProperty(key, values.get(key).toString());
            }
            parent.save();
            setNode(node);
            saved = true;
        } catch (RepositoryException e) {
            String msg = "Unable to save folder.";
            log.error(msg, e);
        }
    }

    @Override
    public void setName(String value) {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        this.name = value;
    }

    @Override
    public void setValue(String id, Serializable value) {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        values.put(id, value);
    }

    @Override
    public void setValues(Map<String, Serializable> values) {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        this.values.putAll(values);
    }
}
