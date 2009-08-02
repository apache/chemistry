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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.Atom;
import org.apache.chemistry.atompub.client.connector.Connector;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.impl.base.BaseObject;

/**
 *
 */
public abstract class APPObject extends BaseObject {

    protected APPObjectEntry entry;

    private final Type type;

    public APPObject(APPObjectEntry entry, Type type) {
        this.entry = entry;
        this.type = type;
    }

    protected static APPObject construct(APPObjectEntry entry) {
        Type type = entry.connection.repository.getType(entry.getTypeId());
        switch (entry.getBaseType()) {
        case DOCUMENT:
            return new APPDocument(entry, type);
        case FOLDER:
            return new APPFolder(entry, type);
        case POLICY:
            // return new APPPolicy(entry, type);
        case RELATIONSHIP:
            // return new APPRelationship(entry, type);
        default:
            throw new AssertionError(entry.getBaseType().getId());
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

    public ContentStream getContentStream(String contentStreamId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Navigation Services -----
     */

    public Folder getParent() {
        String href = entry.getLink(Atom.LINK_UP); // usually a feed entry
        if (href == null) {
            return null;
        }
        // can read an entry directly, or the first one from a feed
        APPObjectEntry e = (APPObjectEntry) entry.connection.getConnector().getObject(
                new ReadContext(entry.connection), href);
        if (e == null) {
            return null; // no parent
        }
        Type t = entry.connection.getRepository().getType(e.getTypeId());
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

    @Override
    public String getId() {
        return entry.getId();
    }

    @Override
    public String getTypeId() {
        return entry.getTypeId();
    }

    public Type getType() {
        return type;
    }

    public BaseType getBaseType() {
        return type.getBaseType();
    }

    public Property getProperty(String id) {
        PropertyDefinition pd = getType().getPropertyDefinition(id);
        if (pd == null) {
            throw new IllegalArgumentException(id);
        }
        return entry.getProperty(pd);
    }

    public Serializable getValue(String id) {
        PropertyDefinition pd = getType().getPropertyDefinition(id);
        if (pd == null) {
            throw new IllegalArgumentException(id);
        }
        // TODO deal with unfetched properties
        Serializable value = entry.getValue(id);
        if (value == null) {
            value = pd.getDefaultValue();
        }
        return value;
    }

    public void save() {
        try {
            if (entry.isCreation()) {
                create();
            } else {
                update();
            }
        } catch (ContentManagerException e) { // TODO
            throw new RuntimeException(e);
        }
    }

    protected void create() throws ContentManagerException {
        Connector connector = entry.connection.getConnector();
        ReadContext ctx = new ReadContext(entry.connection);

        // this link value is local, set by APPConnection#newDocument
        String href = entry.getLink(Atom.LINK_UP);
        if (href == null) {
            throw new IllegalArgumentException(
                    "Cannot create entry: no 'cmis-parents' link is present");
        }
        // TODO hardcoded Chemistry URL pattern here...
        href = href.replaceAll("/object/([0-9a-f-]{36}$)", "/children/$1");
        Request req = new Request(href);
        req.setHeader("Content-Type", Atom.MEDIA_TYPE_ATOM_ENTRY);
        Response resp = connector.postObject(req, entry);
        if (resp.getStatusCode() != 201) { // Created
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        APPObjectEntry newEntry = (APPObjectEntry) resp.getObject(ctx);
        // newEntry SHOULD be returned (AtomPub 9.2)...
        String loc = resp.getHeader("Location");
        if (loc == null) {
            throw new ContentManagerException(
                    "Remote server failed to return a Location header");
        }
        if (newEntry == null || !loc.equals(resp.getHeader("Content-Location"))) {
            // (Content-Location defined by AtomPub 9.2)
            // fetch actual new entry from Location header
            // TODO could fetch only a subset of the properties, if deemed ok
            newEntry = (APPObjectEntry) connector.getObject(ctx, loc);
            if (newEntry == null) {
                throw new ContentManagerException(
                        "Remote server failed to return an entry for Location: "
                                + loc);
            }
        }
        entry = newEntry;
    }

    protected void update() throws ContentManagerException {
        String href = entry.getEditLink();
        if (href == null) {
            throw new IllegalArgumentException(
                    "Cannot edit entry: no 'edit' link is present");
        }
        Request req = new Request(href);
        req.setHeader("Content-Type", Atom.MEDIA_TYPE_ATOM_ENTRY);
        Response resp = entry.connection.getConnector().putObject(req, entry);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
    }

}
