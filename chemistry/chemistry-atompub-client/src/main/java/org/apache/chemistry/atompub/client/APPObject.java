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
 *     Bogdan Stefanescu, Nuxeo
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.connector.Connector;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.impl.base.BaseObject;

/**
 *
 */
public abstract class APPObject extends BaseObject {

    protected final APPObjectEntry entry;

    private final Type type;

    public APPObject(APPObjectEntry entry, Type type) {
        this.entry = entry;
        this.type = type;
    }

    protected static APPObject construct(APPObjectEntry entry) {
        Type type = entry.connection.repository.getType(entry.getTypeId());
        BaseType baseType = type.getBaseType();
        switch (baseType) {
        case DOCUMENT:
            return new APPDocument(entry, type);
        case FOLDER:
            return new APPFolder(entry, type);
        case POLICY:
            // return new APPPolicy(entry, type);
        case RELATIONSHIP:
            // return new APPRelationship(entry, type);
        default:
            throw new AssertionError(baseType.toString());
        }
    }

    /*
     * ----- Object Services -----
     */

    public void move(Folder targetFolder, Folder sourceFolder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void delete() {
        Request req = new Request(entry.getEditLink());
        Response resp = entry.connection.getConnector().delete(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
    }

    public void unfile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Navigation Services -----
     */
    public Folder getParent() {
        String href = entry.getLink(CMIS.LINK_PARENTS); // TODO check this
        if (href == null) {
            return null;
        }
        APPObjectEntry e = (APPObjectEntry) entry.connection.getConnector().getObject(
                new ReadContext(entry.getConnection()), href);
        Type t = entry.getConnection().getRepository().getType(e.getTypeId());
        APPFolder f = new APPFolder(e, t);
        return f;
    }

    public Collection<Folder> getParents() {
        // TODO
        return Collections.singleton(getParent());
    }

    /*
     * ----- Relationship Services -----
     */

    public List<Relationship> getRelationships(RelationshipDirection direction,
            String typeId, boolean includeSubRelationshipTypes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Policy Services -----
     */

    public void applyPolicy(Policy policy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(Policy policy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Policy> getPolicies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- data access -----
     */

    public Type getType() {
        return type;
    }

    public Property getProperty(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Serializable getValue(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void save() {
        try {
            if (getId() == null) {
                create();
            } else {
                update();
            }
        } catch (ContentManagerException e) { // TODO
            throw new RuntimeException(e);
        }
    }

    protected void create() throws ContentManagerException {
        String href = entry.getLink(CMIS.LINK_PARENTS); // TODO check this
        if (href == null) {
            throw new IllegalArgumentException(
                    "Cannot create entry: no 'cmis-parents' link is present");
        }
        Request req = new Request(href);
        req.setHeader("Content-Type", "application/atom+xml;type=entry");
        Response resp = entry.connection.getConnector().postObject(req, entry);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        // TODO get the response to update the content of the posted document
        // resp.getEntity(get, APPDocument.class);
    }

    protected void update() throws ContentManagerException {
        String href = entry.getEditLink();
        if (href == null) {
            throw new IllegalArgumentException(
                    "Cannot edit entry: no 'edit' link is present");
        }
        Request req = new Request(href);
        req.setHeader("Content-Type", "application/atom+xml;type=entry");
        Response resp = entry.connection.getConnector().putObject(req, entry);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
    }

    // ----- overriden from base class -----

    // TODO make sure getProperties returns that too
    @Override
    public URI getURI() {
        String value = entry.getEditLink();
        if (value == null) {
            value = entry.getLink("self");
            if (value == null) {
                value = entry.getLink("alternate");
            }
        }
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Not an URI: " + value);
        }
    }

}
