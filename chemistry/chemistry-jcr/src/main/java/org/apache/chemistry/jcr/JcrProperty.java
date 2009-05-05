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
