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

import java.util.Map;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.templates.Template;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;


/**
 * CMIS URI Template for the Abdera ATOM library.
 */
public class CMISUriTemplate extends ElementWrapper {
    
    public CMISUriTemplate(Element internal) {
        super(internal);
    }

    public CMISUriTemplate(Factory factory) {
        super(factory, CMISConstants.URI_TEMPLATE);
    }

    public String getTemplate() {
        Element child = getFirstChild(CMISConstants.URI_TEMPLATE_PATH);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getType() {
        Element child = getFirstChild(CMISConstants.URI_TEMPLATE_TYPE);
        if (child != null) {
            return child.getText();
        }
        return null;
    }

    public String getMediaType() {
        Element child = getFirstChild(CMISConstants.URI_TEMPLATE_MEDIATYPE);
        if (child != null) {
            return child.getText();
        }
        return null;
    }
    
    public IRI generateUri(Map<String, Object> variables)
    {
        String template = getTemplate();
        if (template != null)
        {
            Template uriTemplate = new Template(template);
            return new IRI(uriTemplate.expand(variables));
        }
        return null;
    }
}
