package org.apache.chemistry.jcr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.type.BaseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrFolder extends JcrObjectEntry implements Folder {

    private static final Log log = LogFactory.getLog(JcrFolder.class);

    public JcrFolder(Node node) {
        super(node);
    }

    protected JcrFolder() {}

    public List<ObjectEntry> getChildren(BaseType type, String orderBy) {
        try {
            List<ObjectEntry> result = new ArrayList<ObjectEntry>();

            NodeIterator iter = node.getNodes();
            while (iter.hasNext()) {
                Node child = iter.nextNode();
                JcrObjectEntry entry = null;
                if (child.isNodeType(JcrConstants.NT_FOLDER)) {
                    entry = new JcrFolder(child);
                } else if (child.isNodeType(JcrConstants.NT_FILE)) {
                    entry = new JcrDocument(child);
                } else {
                    continue;
                }
                if (type == null || type == entry.getType().getBaseType()) {
                    result.add(entry);
                }
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
    public Folder getFolder() {
        return this;
    }

    public void delete() {
        try {
            Node parent = node.getParent();
            node.remove();
            parent.save();
        } catch (RepositoryException e) {
            String msg = "Unable to delete folder.";
            log.error(msg, e);
        }
    }
    
    @Override
    protected BaseType getBaseType() {
    	return BaseType.FOLDER;
    }
}
