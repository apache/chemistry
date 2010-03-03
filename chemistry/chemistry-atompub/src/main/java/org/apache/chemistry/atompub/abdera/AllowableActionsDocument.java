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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.abdera;

import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.chemistry.CMIS;

/**
 * FOM extensible element for a cmis:allowableActions document.
 */
public class AllowableActionsDocument extends FOMExtensibleElement {

    public AllowableActionsDocument(OMContainer parent, Factory factory) {
        super(CMIS.ALLOWABLE_ACTIONS, parent, (OMFactory) factory);
    }

    public void setAllowableActions(Set<QName> set) {
        for (QName qname : set) {
            Element el = addExtension(qname);
            el.setText("true");
        }
    }

}
