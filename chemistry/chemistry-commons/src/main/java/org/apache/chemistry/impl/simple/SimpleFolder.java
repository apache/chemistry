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
package org.apache.chemistry.impl.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Unfiling;

public class SimpleFolder extends SimpleObject implements Folder {

    public SimpleFolder(SimpleObjectEntry entry) {
        super(entry);
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
        return entry.connection.deleteTree(this, unfiling, true);
    }

    public List<CMISObject> getChildren() {
        SimpleRepository repository = entry.connection.repository;
        Set<String> ids = repository.children.get(getId());
        List<CMISObject> children = new ArrayList<CMISObject>(ids.size());
        for (String id : ids) {
            SimpleData d = repository.datas.get(id);
            children.add(SimpleObject.construct(new SimpleObjectEntry(d,
                    entry.connection)));
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
