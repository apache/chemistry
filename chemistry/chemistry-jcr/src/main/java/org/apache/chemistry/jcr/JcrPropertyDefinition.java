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
import java.net.URI;
import java.util.List;

import org.apache.chemistry.Choice;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Updatability;

public class JcrPropertyDefinition implements PropertyDefinition {

    private final javax.jcr.nodetype.PropertyDefinition propDef;

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

    public String getId() {
        return propDef.getName();
    }

    public String getLocalName() {
        return propDef.getName(); // TODO
    }

    public URI getLocalNamespace() {
        return null; // TODO
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

    public String getQueryName() {
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
        return validationError(value) == null;
    }

    public String validationError(Serializable value) {
        // TODO Auto-generated method stub
        return "error TODO";
    }

}
