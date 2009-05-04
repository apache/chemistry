package org.apache.chemistry.jcr;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.apache.chemistry.property.Choice;
import org.apache.chemistry.property.PropertyDefinition;
import org.apache.chemistry.property.PropertyType;
import org.apache.chemistry.property.Updatability;

public class JcrPropertyDefinition implements PropertyDefinition {

    private javax.jcr.nodetype.PropertyDefinition propDef;

    public JcrPropertyDefinition(javax.jcr.nodetype.PropertyDefinition propDef) {
        this.propDef = propDef;
    }

    public List<Choice> getChoices() {
        // TODO Auto-generated method stub
        return null;
    }

    public Serializable getDefaultValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription() {
        return propDef.getName();
    }

    public String getDisplayName() {
        return propDef.getName();
    }

    public String getEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        return propDef.getName();
    }

    public int getMaxLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Integer getMaxValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getMinValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        return propDef.getName();
    }

    public int getPrecision() {
        // TODO Auto-generated method stub
        return 0;
    }

    public URI getSchemaURI() {
        // TODO Auto-generated method stub
        return null;
    }

    public PropertyType getType() {
        switch (propDef.getRequiredType()) {
            case javax.jcr.PropertyType.STRING:
                return PropertyType.STRING;
            case javax.jcr.PropertyType.LONG:
                return PropertyType.INTEGER;
            case javax.jcr.PropertyType.DOUBLE:
                return PropertyType.DECIMAL;
            case javax.jcr.PropertyType.DATE:
                return PropertyType.DATETIME;
            case javax.jcr.PropertyType.BOOLEAN:
                return PropertyType.BOOLEAN;
            case javax.jcr.PropertyType.REFERENCE:
                return PropertyType.ID;
            case javax.jcr.PropertyType.NAME:
            case javax.jcr.PropertyType.BINARY:
            case javax.jcr.PropertyType.PATH:
            case javax.jcr.PropertyType.UNDEFINED:
                return PropertyType.STRING;
            default:
                return null;
        }
    }

    public Updatability getUpdatability() {
        return propDef.isProtected() ? Updatability.READ_ONLY : Updatability.READ_WRITE;
    }

    public boolean isInherited() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMultiValued() {
        return propDef.isMultiple();
    }

    public boolean isOpenChoice() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isOrderable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isQueryable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequired() {
        return propDef.isMandatory();
    }

    public boolean validates(Serializable value) {
        // TODO Auto-generated method stub
        return false;
    }
}
