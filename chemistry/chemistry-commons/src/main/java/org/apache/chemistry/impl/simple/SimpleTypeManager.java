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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.Paging;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleTypeManager implements TypeManager {

    private static final Log log = LogFactory.getLog(SimpleTypeManager.class);

    // linked so that values() returns things in a parent-before-children order
    protected final Map<String, Type> types = new LinkedHashMap<String, Type>();

    protected final Map<String, PropertyDefinition> propertyDefinitions = new HashMap<String, PropertyDefinition>();

    protected final Map<String, Collection<Type>> typesChildren;

    /**
     * Read-write lock protecting access to {@link #types},
     * {@link #propertyDefinitions} and {@link #typesChildren}.
     */
    private final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

    private final Lock rlock = rwlock.readLock();

    private final Lock wlock = rwlock.writeLock();

    public SimpleTypeManager() {
        typesChildren = new HashMap<String, Collection<Type>>();
        // make sure base types are there
        for (String bid : BaseType.ALL_IDS) {
            typesChildren.put(bid, new LinkedList<Type>());
        }
    }

    public void addType(Type type) {
        wlock.lock();
        try {
            String typeId = type.getId();
            if (types.containsKey(typeId)) {
                throw new RuntimeException("Type already defined: " + typeId);
            }
            types.put(typeId, type);
            for (PropertyDefinition pdef : type.getPropertyDefinitions()) {
                addPropertyDefinition(pdef);
            }
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
        } finally {
            wlock.unlock();
        }
    }

    protected void addPropertyDefinition(PropertyDefinition pdef) {
        PropertyDefinition old = propertyDefinitions.get(pdef.getId());
        if (old != null) {
            // some sanity checks
            if (!eq(old.getLocalName(), pdef.getLocalName())
                    || !eq(old.getDisplayName(), pdef.getDisplayName())
                    || !eq(old.getQueryName(), pdef.getQueryName())
                    || !old.getType().equals(pdef.getType())) {
                log.error("Property definition redefined differently: "
                        + pdef.getId());
            }
            return;
        }
        propertyDefinitions.put(pdef.getId(), pdef);
    }

    protected static boolean eq(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    public Type getType(String typeId) {
        rlock.lock();
        try {
            return types.get(typeId);
        } finally {
            rlock.unlock();
        }
    }

    public PropertyDefinition getPropertyDefinition(String id) {
        rlock.lock();
        try {
            return propertyDefinitions.get(id);
        } finally {
            rlock.unlock();
        }
    }

    public Collection<Type> getTypes() {
        rlock.lock();
        try {
            return new ArrayList<Type>(types.values());
        } finally {
            rlock.unlock();
        }
    }

    public Collection<Type> getTypeDescendants(String typeId) {
        Collection<Type> list = getTypeDescendants(typeId, -1, true);
        if (typeId != null) {
            // add the type itself as first element
            Type type = getType(typeId);
            ((LinkedList<Type>) list).addFirst(type);
        }
        return list;
    }

    public ListPage<Type> getTypeChildren(String typeId,
            boolean includePropertyDefinitions, Paging paging) {
        // TODO includePropertyDefinitions, paging
        rlock.lock();
        try {
            List<Type> list;
            if (typeId == null) {
                list = new ArrayList<Type>(4);
                for (String id : BaseType.ALL_IDS) {
                    Type type = types.get(id);
                    if (type != null) {
                        list.add(type);
                    }
                }
            } else {
                Collection<Type> children = typesChildren.get(typeId);
                if (children == null) {
                    throw new IllegalArgumentException("No such type: "
                            + typeId);
                }
                list = new ArrayList<Type>(children);
            }
            return new SimpleListPage<Type>(list);
        } finally {
            rlock.unlock();
        }
    }

    /*
     * This implementation returns subtypes depth-first.
     */
    public Collection<Type> getTypeDescendants(String typeId, int depth,
            boolean returnPropertyDefinitions) {
        rlock.lock();
        try {
            if (depth == 0) {
                throw new IllegalArgumentException("Depth 0 invalid");
            }
            List<Type> list = new LinkedList<Type>();
            Set<String> done = new HashSet<String>();
            if (typeId == null) {
                // return all types
                for (String tid : BaseType.ALL_IDS) {
                    Type type = types.get(tid);
                    if (type == null) {
                        // some optional base types may be absent
                        continue;
                    }
                    list.add(type);
                    collectSubTypes(tid, -1, returnPropertyDefinitions, list,
                            done);
                }
            } else {
                if (!types.containsKey(typeId)) {
                    throw new IllegalArgumentException("No such type: "
                            + typeId);
                }
                collectSubTypes(typeId, depth, returnPropertyDefinitions, list,
                        done);
            }
            return list;
        } finally {
            rlock.unlock();
        }
    }

    // rlock already held by caller
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
