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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import javax.jcr.nodetype.NodeType;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Type;

public class JcrType implements Type {

    private final NodeType nodeType;

    private final BaseType baseType;

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

    public String getLocalName() {
        return nodeType.getName();
    }

    public URI getLocalNamespace() {
        return null;
    }

    public PropertyDefinition getPropertyDefinition(String id) {
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

    public boolean isControllablePolicy() {
        return false;
    }

    public boolean isControllableACL() {
        return false;
    }

    public boolean isFulltextIndexed() {
        return false; // TODO
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
