package org.apache.chemistry.jcr;

import java.io.Serializable;

import javax.jcr.RepositoryException;

import org.apache.chemistry.property.Property;
import org.apache.chemistry.property.PropertyDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JcrProperty implements Property {

    private static final Log log = LogFactory.getLog(JcrProperty.class);

    private javax.jcr.Property property;

    public JcrProperty(javax.jcr.Property property) {
        this.property = property;
    }

    public PropertyDefinition getDefinition() {
        try {
            return new JcrPropertyDefinition(property.getDefinition());
        } catch (RepositoryException e) {
            String msg = "Unable to get property definition.";
            log.error(msg, e);
        }
        return null;
    }

    public Serializable getValue() {
        try {
            return property.getValue().getString();
        } catch (RepositoryException e) {
            String msg = "Unable to get property value.";
            log.error(msg, e);
        }
        return null;
    }

    public void setValue(Serializable value) {
        throw new UnsupportedOperationException();
    }

}
