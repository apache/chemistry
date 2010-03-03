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
package org.apache.chemistry.atompub.server;

import java.io.Serializable;
import java.util.Map;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.axiom.om.OMContainer;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.abdera.AllowableActionsDocument;

/**
 * CMIS allowableActions document (fetched through {@link #getEntry}).
 */
public class CMISAllowableActionsEntry implements CollectionAdapter {

    protected final Repository repository;

    public CMISAllowableActionsEntry(Repository repository) {
        this.repository = repository;
    }

    public ResponseContext getEntry(RequestContext request) {
        SPI spi = getSPI(request);
        try {
            String id = getResourceName(request);
            Inclusion inclusion = new Inclusion(null, null, null, true, false,
                    false);
            ObjectEntry object = spi.getProperties(spi.newObjectId(id),
                    inclusion);
            if (object == null) {
                return new EmptyResponseContext(404);
            }
            // create resulting document
            Factory factory = request.getAbdera().getFactory();
            Document<Element> doc = factory.newDocument();
            AllowableActionsDocument el = new AllowableActionsDocument(
                    (OMContainer) doc, factory);
            el.setAllowableActions(object.getAllowableActions());
            return new BaseResponseContext<Document<Element>>(doc);
        } catch (Exception e) {
            return new EmptyResponseContext(500, e.toString());
        } finally {
            spi.close();
        }
    }

    // duplicated in CMISCollection
    public SPI getSPI(RequestContext request) {
        return repository.getSPI(getConnectionParams(request));
    }

    // duplicated in CMISCollection
    protected Map<String, Serializable> getConnectionParams(
            RequestContext request) {
        return null; // TODO username, password
    }

    // duplicated in CMISCollection
    protected String getResourceName(RequestContext request) {
        String resourceName = request.getTarget().getParameter("objectid");
        return UrlEncoding.decode(resourceName);
    }

    public ResponseContext headEntry(RequestContext request) {
        // TODO just in case
        return getEntry(request);
    }

    public ResponseContext deleteEntry(RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public ResponseContext extensionRequest(RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public ResponseContext getCategories(RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public ResponseContext getFeed(RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public ResponseContext optionsEntry(RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public ResponseContext postEntry(RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public ResponseContext putEntry(RequestContext request) {
        throw new UnsupportedOperationException();
    }

}
