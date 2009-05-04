package org.apache.chemistry.jcr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.type.BaseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrNewFolder extends JcrFolder {

    private static final Log log = LogFactory.getLog(JcrNewFolder.class);

    private Node parent;
    private String name;
    private Map<String, Serializable> values = new HashMap<String, Serializable>();
    private boolean saved;

    public JcrNewFolder(Node parent) {
        this.parent = parent;
    }

    public List<ObjectEntry> getChildren(BaseType type, String orderBy) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.getChildren(type, orderBy);
    }

    public Document newDocument(String typeId) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.newDocument(typeId);
    }

    public Folder newFolder(String typeId) {
        if (!saved) {
            throw new UnsupportedOperationException();
        }
        return super.newFolder(typeId);
    }

    public Folder getParent() {
        if (!saved) {
            return new JcrFolder(parent);
        }
        return super.getParent();
    }

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

    public void setName(String name) {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        this.name = name;
    }

    public void setValue(String name, Serializable value) {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        values.put(name, value);
    }

    public void setValues(Map<String, Serializable> values) {
        if (saved) {
            throw new UnsupportedOperationException();
        }
        this.values.putAll(values);
    }
}
