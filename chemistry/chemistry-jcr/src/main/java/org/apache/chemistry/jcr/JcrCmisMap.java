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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyType;
import org.apache.jackrabbit.JcrConstants;

/**
 * This is a helper class that helps mapping JCR and CMIS properties to each
 * other.
 *
 * @see org.apache.jackrabbit.JcrConstants
 * @see org.apache.chemistry.Property
 *
 * @author Michael Mertins
 * @author Florent Guillaume
 */
public class JcrCmisMap {

    /**
     * Table of CMIS names to JCR equivalent.
     */
    private static Hashtable<String, String> cmisDict = new Hashtable<String, String>();
    static {
        cmisDict.put(Property.ID, "jcr:name");
        cmisDict.put(Property.CREATED_BY, "jcr:uuid");
        cmisDict.put(Property.CREATION_DATE, "jcr:created");
        cmisDict.put(Property.TYPE_ID, "jcr:primaryType");
        cmisDict.put(Property.LAST_MODIFICATION_DATE, "jcr:created");
    }

    /**
     * Table of JCR names to CMIS equivalent
     */
    private static Hashtable<String, String> jcrDict = new Hashtable<String, String>();
    static {
        jcrDict.put("jcr:name", Property.ID);
        jcrDict.put("jcr:uuid", Property.CREATED_BY);
        jcrDict.put("jcr:created", Property.CREATION_DATE);
        jcrDict.put("jcr:primaryType", Property.TYPE_ID);
        jcrDict.put("jcr:created", Property.LAST_MODIFICATION_DATE);
    }

    private static ArrayList<String> folderNtList = new ArrayList<String>();
    static {
        folderNtList.add(JcrConstants.NT_FOLDER);
    }

    protected static ArrayList<String> documentNtList = new ArrayList<String>();
    static {
        documentNtList.add(JcrConstants.NT_FILE);
    }

    /**
     * Converts from CMIS to JCR name.
     *
     * @param cmisName the CMIS name
     * @return the JCR equivalent of the CMIS name
     */
    public static String cmisToJcr(String cmisName) {
        String jcrName = cmisDict.get(cmisName);
        if (jcrName == null) {
            jcrName = cmisName;
        }
        return jcrName;
    }

    /**
     * Converts from JCR to CMIS name.
     *
     * @param jcrName the JCR name
     * @return the CMIS equivalent of the JCR name
     */
    public static String jcrToCmis(String jcrName) {
        String cmisName = jcrDict.get(jcrName);
        if (cmisName == null) {
            cmisName = jcrName;
        }
        return cmisName;
    }

    /**
     * Checks if a JCR node type is a folder.
     *
     * @param nodeTypeName the JCR node type name
     * @return {@code true} if the node type is a folder
     */
    public static boolean isBaseTypeFolder(String nodeTypeName) {
        return folderNtList.contains(nodeTypeName);
    }

    /**
     * Checks if a JCR node type is a document.
     *
     * @param nodeTypeName the JCR node type name
     * @return {@code true} if the node type is a document
     */
    public static boolean isBaseTypeDocument(String nodeTypeName) {
        return documentNtList.contains(nodeTypeName);
    }

    /**
     * Checks if a JCR node is a document.
     *
     * @param node the JCR node
     * @return {@code true} if the node is a document
     */
    public static boolean isNodeDocument(Node node) {
        for (String nodeTypeName : documentNtList) {
            try {
                if (node.isNodeType(nodeTypeName)) {
                    return true;
                }
            } catch (RepositoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks if a JCR node is a document.
     *
     * @param node the JCR node
     * @return {@code true} if the node is a document
     */
    public static boolean isNodeFolder(Node node) {
        for (String nodeTypeName : folderNtList) {
            try {
                if (node.isNodeType(nodeTypeName)) {
                    return true;
                }
            } catch (RepositoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks whether a JCR node is an internal node, e.g. <b>jcr:system</b>
     *
     * @param node node
     * @return <code>true</code> if the node is internal;
     *         <code>false</code> otherwise
     */
    public static boolean isInternal(Node node) {
        try {
            return node.getName().equals(JcrConstants.JCR_SYSTEM);
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a JCR name corresponds to a date.
     */
    public static boolean isDate(String s) {
        if (s.equals("jcr:created")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a JCR name corresponds to an integer.
     */
    public static boolean isInt(String s) {
        if (s.equals("whatever")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a JCR name corresponds to a boolean.
     */
    public static boolean isBool(String s) {
        if (s.equals("whatever")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a JCR name is multi-valued.
     */
    public static boolean isArray(String s) {
        if (s.equals("whatever")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convert a JCR value to a serializable
     *
     * @param pd property definition
     * @param value JCR value
     * @return serializable
     * @throws RepositoryException if accessing the value fails
     */
    public static Serializable valueToSerializable(PropertyType pd, javax.jcr.Value value)
            throws RepositoryException {

        switch (pd.ordinal()) {
        case PropertyType.BOOLEAN_ORD:
            return Boolean.valueOf(value.getBoolean());
        case PropertyType.DATETIME_ORD:
            return value.getDate();
        case PropertyType.DECIMAL_ORD:
            return value.getDecimal();
        case PropertyType.INTEGER_ORD:
            return Integer.valueOf((int) value.getLong());
        case PropertyType.HTML_ORD:
        case PropertyType.ID_ORD:
        case PropertyType.STRING_ORD:
        case PropertyType.URI_ORD:
            return value.getString();
        default:
            throw new AssertionError("Unexpected property type ordinal: " + pd.ordinal());
        }
    }

    /**
     * Convert a JCR value to a serializable
     *
     * @param pd property definition
     * @param value JCR value
     * @return serializable
     * @throws RepositoryException if accessing the value fails
     */
    public static javax.jcr.Value serializableToValue(PropertyType pd, Serializable v,
                javax.jcr.ValueFactory f)
            throws RepositoryException {

        switch (pd.ordinal()) {
        case PropertyType.BOOLEAN_ORD:
            return f.createValue(((Boolean) v).booleanValue());
        case PropertyType.DATETIME_ORD:
            return f.createValue((Calendar) v);
        case PropertyType.DECIMAL_ORD:
            return f.createValue((BigDecimal) v);
        case PropertyType.INTEGER_ORD:
            return f.createValue(((Integer) v).longValue());
        case PropertyType.HTML_ORD:
        case PropertyType.ID_ORD:
        case PropertyType.STRING_ORD:
        case PropertyType.URI_ORD:
            return f.createValue((String) v);
        default:
            throw new AssertionError("Unexpected property type ordinal: " + pd.ordinal());
        }
    }
}
