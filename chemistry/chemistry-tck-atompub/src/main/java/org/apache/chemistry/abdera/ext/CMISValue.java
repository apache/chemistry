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
 */
package org.apache.chemistry.abdera.ext;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.chemistry.tck.atompub.utils.ISO8601DateFormat;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;


/**
 * CMIS Property Value for the Abdera ATOM library.
 */
public class CMISValue extends ExtensibleElementWrapper {
    
    /**
     * @param internal
     */
    public CMISValue(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISValue(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * Gets property value (converting to appropriate Java type for the property
     * type)
     * 
     * @return property value (or null, if not specified)
     */
    public Object getNativeValue() {
        CMISProperty parent = (CMISProperty) getParentElement();
        String type = parent.getType();
        if (type.equals(CMISConstants.PROP_TYPE_STRING)) {
            return getStringValue();
        } else if (type.equals(CMISConstants.PROP_TYPE_INTEGER)) {
            return getIntegerValue();
        } else if (type.equals(CMISConstants.PROP_TYPE_DATETIME)) {
            return getDateValue();
        } else if (type.equals(CMISConstants.PROP_TYPE_BOOLEAN)) {
            return getBooleanValue();
        } else if (type.equals(CMISConstants.PROP_TYPE_DECIMAL)) {
            return getDecimalValue();
        }
        // TODO: Handle remaining property types
        return getStringValue();
    }

    /**
     * Gets String value
     * 
     * @return string value
     */
    public String getStringValue() {
        return getText();
    }

    /**
     * Gets Decimal value
     * 
     * @return decimal value
     */
    public BigDecimal getDecimalValue() {
        return new BigDecimal(getStringValue());
    }

    /**
     * Gets Integer value
     * 
     * @return integer value
     */
    public int getIntegerValue() {
        return new Integer(getStringValue());
    }

    /**
     * Gets Boolean value
     * 
     * @return boolean value
     */
    public boolean getBooleanValue() {
        return Boolean.valueOf(getStringValue());
    }

    /**
     * Gets Date value
     * 
     * @return date value
     */
    public Date getDateValue() {
        // TODO: Use mechanism not reliant on Alfresco code
        return ISO8601DateFormat.parse(getStringValue());
    }

}
