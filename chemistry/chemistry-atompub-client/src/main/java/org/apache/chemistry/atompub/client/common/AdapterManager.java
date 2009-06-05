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
package org.apache.chemistry.atompub.client.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("unchecked")
public class AdapterManager implements ClassRegistry {

    // adaptableClass => { adapterClass => adapterFactory }
    protected Map<Class<?>, Map<Class<?>, AdapterFactory>> registry;

    public AdapterManager() {
        this.registry = new HashMap<Class<?>, Map<Class<?>, AdapterFactory>>();
    }

    public synchronized void registerAdapters(Class<?> clazz,
            AdapterFactory factory) {
        Map<Class<?>, AdapterFactory> adapters = registry.get(clazz);
        if (adapters == null) {
            adapters = new HashMap<Class<?>, AdapterFactory>();
        }
        for (Class<?> adapterType : factory.getAdapterTypes()) {
            adapters.put(adapterType, factory);
        }
        registry.put(clazz, adapters);
    }

    public void unregisterAdapters(Class<?> clazz) {
        // TODO implement
    }

    public void unregisterAdapterFactory(Class<?> factory) {
        // TODO implement
    }

    public synchronized AdapterFactory getAdapterFactory(Class<?> adaptee,
            Class<?> adapter) {
        Map<Class<?>, AdapterFactory> adapters = (Map<Class<?>, AdapterFactory>) ClassLookup.lookup(
                adaptee, this);
        if (adapters != null) {
            return adapters.get(adapter);
        }
        return null;
    }

    public <T> T getAdapter(Object adaptee, Class<T> adapter) {
        AdapterFactory factory = getAdapterFactory(adaptee.getClass(), adapter);
        return factory == null ? null : factory.getAdapter(adaptee, adapter);
    }

    public Object get(Class<?> clazz) {
        return registry.get(clazz);
    }

    public void put(Class<?> clazz, Object value) {
        registry.put(clazz, (Map<Class<?>, AdapterFactory>) value);
    }

}
