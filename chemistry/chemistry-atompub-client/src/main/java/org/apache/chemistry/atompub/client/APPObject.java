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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.impl.base.BaseObject;
import org.apache.commons.httpclient.Header;

/**
 *
 */
public abstract class APPObject extends BaseObject {

    protected static final String UNINITIALIZED_STRING = "__UNINITIALIZED__\0\0\0";

    protected static final URI UNINITIALIZED_URI;
    static {
        try {
            UNINITIALIZED_URI = new URI("http://__UNINITIALIZED__/%00%00%00");
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

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

    public Connection getConnection() {
        return entry.connection;
    }

    /*
     * ----- Object Services -----
     */

    public void move(Folder targetFolder, Folder sourceFolder) {
        entry.connection.moveObject(entry, targetFolder, sourceFolder);
    }

    public void delete() {
        entry.connection.deleteObject(entry, false);
    }

    public void unfile() {
        entry.connection.removeObjectFromFolder(entry, null);
    }

    public ContentStream getContentStream(String contentStreamId)
            throws IOException {
        ContentStream contentStream = entry.getContentStream();
        if (contentStream != APPObjectEntry.REMOTE_CONTENT_STREAM) {
            return contentStream;
        }
        String url = entry.getContentHref();
        return url == null ? null : new APPContentStream(url);
    }

    /*
     * ----- Navigation Services -----
     */

    public Folder getParent() {
        APPObjectEntry parentEntry;
        if (getBaseType() == BaseType.FOLDER) {
            parentEntry = (APPObjectEntry) entry.connection.getFolderParent(
                    this, null);
            if (parentEntry == null) {
                return null;
            }
        } else {
            String href = entry.getLink(AtomPub.LINK_UP); // feed or entry
            if (href == null) {
                return null;
            }
            Collection<ObjectEntry> parents = entry.connection.getObjectParents(
                    this, null);
            if (parents.isEmpty()) {
                return null;
            }
            parentEntry = (APPObjectEntry) parents.iterator().next();
        }
        return (Folder) construct(parentEntry);
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
        // this link value is local, set by APPConnection#newDocument
        String href = entry.getLink(AtomPub.LINK_UP);
        if (href == null) {
            throw new CMISRuntimeException("Missing up link");
        }
        // TODO hardcoded Chemistry URL pattern here...
        href = href.replaceAll("/object/([0-9a-f-]{36}$)", "/children/$1");

        entry = entry.connection.connector.postEntry(href, null, entry);
    }

    protected void update() throws ContentManagerException {
        String href = entry.getEditLink();
        if (href == null) {
            throw new CMISRuntimeException("Missing edit link");
        }
        Header header = new Header("Content-Type",
                AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        entry = entry.connection.connector.putEntry(href, header, entry);
    }

    /**
     * ContentStream class that fetches a remote URL when needed.
     */
    public class APPContentStream implements ContentStream {

        protected final String url;

        protected String mimeType = UNINITIALIZED_STRING;

        protected String filename = UNINITIALIZED_STRING;

        protected URI uri = UNINITIALIZED_URI;

        protected long length = -1;

        public APPContentStream(String url) {
            this.url = url;
        }

        public String getMimeType() {
            if (mimeType == UNINITIALIZED_STRING) {
                mimeType = getString(Property.CONTENT_STREAM_MIME_TYPE);
            }
            return mimeType;
        }

        public String getFileName() {
            if (filename == UNINITIALIZED_STRING) {
                filename = getString(Property.CONTENT_STREAM_FILE_NAME);
            }
            return filename;
        }

        public long getLength() {
            if (length == -1) {
                Integer value = getInteger(Property.CONTENT_STREAM_LENGTH);
                return length = value == null ? -1 : value.longValue();
            }
            return length;
        }

        // TODO this could save the stream in a side object and put it back in
        // the entry's local content stream when done, to allow reuse
        public InputStream getStream() throws IOException {
            ContentStream cs = entry.connection.connector.getContentStream(url,
                    null, null);
            if (length == -1) {
                // ((HttpMethodBase) method).getResponseContentLength();
                length = cs.getLength();
            }
            return cs.getStream();
        }
    }

}
