/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     David Caruana, Alfresco
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.abdera.ext;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;


/**
 * CMIS Type Definition for the Abdera ATOM library.
 */
public class CMISTypeDefinition extends ExtensibleElementWrapper {
    
    /**
     * @param internal
     */
    public CMISTypeDefinition(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     */
    public CMISTypeDefinition(Factory factory) {
        super(factory, CMISConstants.TYPE_DEFINITION);
    }

    /**
     * @return the type identifier
     */
    public String getId() {
        return getFirstChild(CMISConstants.TYPE_ID).getText();
    }
    
    /**
     * Gets all Property Definitions for this CMIS Type
     * 
     * @return property definitions
     */
    public List<CMISPropertyDefinition> getPropertyDefinitions() {
        List<Element> elements = getElements();
        List<CMISPropertyDefinition> propertyDefs = new ArrayList<CMISPropertyDefinition>(elements.size());
        for (Element element : elements) {
            if (element instanceof CMISPropertyDefinition) {
                propertyDefs.add((CMISPropertyDefinition)element);
            }
        }
        return propertyDefs;
    }

    /**
     * Gets Property Definition
     * 
     * @param id property definition id
     * @return property definition
     */
    public CMISPropertyDefinition getPropertyDefinition(String id) {
        List<Element> elements = getElements();
        for (Element element : elements) {
            if (element instanceof CMISPropertyDefinition) {
                CMISPropertyDefinition propDef = (CMISPropertyDefinition)element;
                if (id.equals(propDef.getId())) {
                    return propDef;
                }
            }
        }
        return null;
    }
    
    /**
     * Gets the type local name 
     * @return local name
     */
    public String getLocalName() {
      return getFirstChild(CMISConstants.TYPE_LOCAL_NAME).getText();
    }
    
    
    /**
     * Gets the type local namespace 
     * @return local name
     */
    
    public String getLocalNamespace() {
      return getFirstChild(CMISConstants.TYPE_LOCAL_NAMESPACE).getText();
    }
    
    /**
     * Gets the type CMIS SQL query name (virtual table type is mapped into)
     * @return local name
     */
    
    public String getQueryName() {
      return getFirstChild(CMISConstants.TYPE_QUERY_NAME).getText();
    }
    
    /**
     * Gets the type display name 
     * @return local name
     */
    
    public String getDisplayName() {
      return getFirstChild(CMISConstants.TYPE_DISPLAY_NAME).getText();
    }
    
    /**
     * Gets a value that indicates whether the base type for this Object-Type is
     * the Document, Folder, Relationship, or Policy base type.
     * 
     * @return "cmis:document", "cmis:folder", "cmis:relationship" or
     *         "cmis:policy"
     */
    public String getBaseId() {
        return getFirstChild(CMISConstants.TYPE_BASE_ID).getText();
    }
    
    
    /**
     * Gets the ID of the Object-Type’s immediate parent type.   
     * It MUST be “not set” for a base type.
     *  
     * @return the parent Id
     */
    public String getParentId() {
        return getFirstChild(CMISConstants.TYPE_BASE_ID).getText();
    }
    
    /**
     * Gets Description of this object-type, such as the nature of content, or its intended use. 
     * Used for presentation by application362 
     * 
     * @return the type description
     */
    public String getDescription() {
        return getFirstChild(CMISConstants.TYPE_DESCRIPTION).getText();
    }
    
    /**
     * Determines whether objects of this type are creatable.
     * TODO: Shouldn't this be a predicate? (e.g. isCreatable? )
     * 
     * @return <code>true</code> if objects of this type are creatable
     */
    public boolean getCreatable() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_CREATABLE).getText());
    }
    
    /**
     * Determines whether objects of this type are fileable.
     * TODO: Shouldn't this be a predicate? (e.g. isFileable? ) 
     * 
     * @return <code>true</code> if objects of this type are fileable
     */
    public boolean getFileable() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_FILEABLE).getText());
    }
    
    /**
     * Determines whether objects of this type are versionable
     * TODO: Shouldn't this be a predicate? (e.g. isVersionable? ) 
     * 
     * @return <code>true</code> if objects of this type are queryable
     */
    public boolean getVersionable() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_VERSIONABLE).getText());
    }
    
    /**
     * Determines whether objects of this type are queryable.
     * TODO: Shouldn't this be a predicate? (e.g. isQueryable? ) 
     * 
     * @return <code>true</code> if objects of this type are queryable
     */
    public boolean getQueryable() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_QUERYABLE).getText());
    }
    
    /**
     * Determines whether objects of this type allow a content stream
     * TODO: Shouldn't this be a predicate? (e.g. isContentStreamAllowed? ) 
     * 
     * @return <code>true</code> if objects of this type are queryable
     */
    public String getContentStreamAllowed() {
        return getFirstChild(CMISConstants.TYPE_CONTENT_STREAM_ALLOWED).getText();
    }
    
    /**
     * Determines whether objects of this type are controllable via policies
     * 
     * @return <code>true</code> if objects of this type are controllable via policies
     */
    public boolean getControllablePolicy() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_CONTROLLABLE_POLICY).getText());
    }
    
    /**
     * Determines whether objects of this type are controllable by ACLs
     * 
     * @return <code>true</code> if objects of this type are controllable by ACLs
     */
    public boolean getControllableACL() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_CONTROLLABLE_ACL).getText());
    }
    
    
    /**
     * Determines whether objects of this type are indexed for full-text search for querying via the  CONTAINS() query predicate. 
     *
     * @return <code>true</code> if objects of this type are full text indexed 
     *         
     */
    public boolean getFullTextIndexed() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_FULL_TEXT_INDEXED).getText());
    }
    
    /**
     * Determines whether objects of this type should be included in supertype query
     * @return <code>true</code> if objects of this type have to be included in supertype query
     *         
     */
    public boolean getIncludeInSupertypeQuery() {
        return Boolean.parseBoolean(getFirstChild(CMISConstants.TYPE_INCLUDED_IN_SUPERTYPE_QUERY).getText());
    }
    
    
}
