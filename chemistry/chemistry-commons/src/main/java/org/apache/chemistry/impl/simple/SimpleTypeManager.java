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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;

public class SimpleTypeManager implements TypeManager {

    // linked so that values() returns things in a parent-before-children order
    protected final Map<String, Type> types = new LinkedHashMap<String, Type>();

    protected final Map<String, Collection<Type>> typesChildren;

    public SimpleTypeManager() {
        typesChildren = new HashMap<String, Collection<Type>>();
        // make sure base types are there
        for (String bid : BaseType.ALL_IDS) {
            typesChildren.put(bid, new LinkedList<Type>());
        }
    }

    public void addType(Type type) {
        String typeId = type.getId();
        if (types.containsKey(typeId)) {
            throw new RuntimeException("Type already defined: " + typeId);
        }
        types.put(typeId, type);
        typesChildren.put(typeId, new LinkedList<Type>());
        String parentId = type.getParentId();
        if (parentId == null) {
            // check it's a base type
            try {
                BaseType.get(typeId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Type: " + typeId
                        + " must have a parent type");
            }
        } else {
            Collection<Type> siblings = typesChildren.get(parentId);
            if (siblings == null) {
                throw new IllegalArgumentException("Type: " + typeId
                        + " refers to unknown parent: " + parentId);
            }
            siblings.add(type);
            // TODO check no cycle
        }
    }

    public Type getType(String typeId) {
        return types.get(typeId);
    }

    public Collection<Type> getTypes(String typeId) {
        return getTypes(typeId, -1, true);
    }

    /*
     * This implementation returns subtypes depth-first.
     */
    public Collection<Type> getTypes(String typeId, int depth,
            boolean returnPropertyDefinitions) {
        if (depth == 0) {
            throw new IllegalArgumentException("Depth 0 invalid");
        }
        List<Type> list = new LinkedList<Type>();
        Set<String> done = new HashSet<String>();
        if (typeId == null) {
            // ignore depth
            for (String tid : BaseType.ALL_IDS) {
                Type type = types.get(tid);
                if (type == null) {
                    // some optional base types may be absent
                    continue;
                }
                list.add(type);
                collectSubTypes(tid, -1, returnPropertyDefinitions, list, done);
            }
        } else {
            if (!types.containsKey(typeId)) {
                throw new IllegalArgumentException("No such type: " + typeId);
            }
            // TODO spec unclear on depth 0
            if (depth < 0) {
                list.add(types.get(typeId));
            }
            collectSubTypes(typeId, depth, returnPropertyDefinitions, list,
                    done);
        }
        return list;
    }

    protected void collectSubTypes(String typeId, int depth,
            boolean returnPropertyDefinitions, List<Type> list, Set<String> done) {
        // TODO returnPropertyDefinitions
        if (depth == 0) {
            return;
        }
        for (Type subType : typesChildren.get(typeId)) {
            String subTypeId = subType.getId();
            if (done.contains(subTypeId)) {
                // TODO move cycles check to addType
                throw new IllegalStateException(
                        "Types contain a cycle involving: " + subTypeId);
            }
            done.add(subTypeId);
            list.add(subType);
            collectSubTypes(subTypeId, depth - 1, returnPropertyDefinitions,
                    list, done);
        }
    }

}
