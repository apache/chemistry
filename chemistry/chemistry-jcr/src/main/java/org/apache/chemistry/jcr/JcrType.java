package org.apache.chemistry.jcr;

import java.util.Collection;
import java.util.Collections;

import javax.jcr.nodetype.NodeType;

import org.apache.chemistry.property.PropertyDefinition;
import org.apache.chemistry.type.BaseType;
import org.apache.chemistry.type.ContentStreamPresence;
import org.apache.chemistry.type.Type;

public class JcrType implements Type {

    private NodeType nodeType;
    private BaseType baseType;

    public JcrType(NodeType nodeType, BaseType baseType) {
        this.nodeType = nodeType;
        this.baseType = baseType;
    }

    public String[] getAllowedSourceTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getAllowedTargetTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public BaseType getBaseType() {
    	return baseType;
    }

    public String getBaseTypeQueryName() {
    	return baseType.toString();
    }

    public ContentStreamPresence getContentStreamAllowed() {
        if (baseType == BaseType.DOCUMENT) {
            return ContentStreamPresence.ALLOWED;
        } else {
            return ContentStreamPresence.NOT_ALLOWED;
        }
    }

    public String getDescription() {
        return nodeType.getName();
    }

    public String getDisplayName() {
        return nodeType.getName();
    }

    public String getId() {
        return nodeType.getName();
    }

    public String getParentId() {
        return null;
    }

    public PropertyDefinition getPropertyDefinition(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<PropertyDefinition> getPropertyDefinitions() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    public String getQueryName() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isControllable() {
        return false;
    }

    public boolean isCreatable() {
        return true;
    }

    public boolean isFileable() {
        return baseType == BaseType.DOCUMENT;
    }

    public boolean isIncludedInSuperTypeQuery() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isQueryable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isVersionable() {
        // TODO Auto-generated method stub
        return false;
    }



}
