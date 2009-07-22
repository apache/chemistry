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
 */
package org.apache.chemistry.atompub.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;
import org.apache.chemistry.atompub.client.stax.ReadContext;

/**
 *
 */
public class APPFolder extends APPDocument implements Folder {

    public APPFolder(APPObjectEntry entry, Type type) {
        super(entry, type);
    }

    public void add(CMISObject object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void remove(CMISObject object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectId> deleteTree(Unfiling unfiling) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<Folder> getAncestors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<CMISObject> getChildren(BaseType type) {
        String href = entry.getLink(CMIS.LINK_CHILDREN);
        Response resp = entry.connection.getConnector().get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        List<ObjectEntry> feed = resp.getObjectFeed(new ReadContext(
                entry.connection));
        List<CMISObject> children = new ArrayList<CMISObject>(feed.size());
        for (ObjectEntry child : feed) {
            if (type != null && !child.getBaseType().equals(type)) {
                continue;
            }
            children.add(APPObject.construct((APPObjectEntry) child));
        }
        return children;
    }

    public Document newDocument(String typeId) {
        return entry.connection.newDocument(typeId, this);
    }

    public Folder newFolder(String typeId) {
        return entry.connection.newFolder(typeId, this);
    }

}
