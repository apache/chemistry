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

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;


/**
 * CMIS Allowable Action for the Abdera ATOM library.
 */
public abstract class CMISAllowableAction extends ElementWrapper {
    
    /**
     * @param internal
     */
    public CMISAllowableAction(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISAllowableAction(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * Gets the Allowable Action name
     * 
     * @return name
     */
    public String getName() {
        return getQName().getLocalPart();
    }

    /**
     * Is the Action Allowed
     * 
     * @return
     */
    public boolean isAllowed() {
        return Boolean.getBoolean(this.getText());
    }

}
