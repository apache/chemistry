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

/**
 * A Class keyed map sensible to class hierarchy. This map provides an
 * additional method {@link #lookup(Class)} that can be used to lookup
 * compatible to the given one depending on the class hierarchy.
 */
public class ClassLookup {

    private static final long serialVersionUID = 1L;

    public static Object lookup(Class<?> key, ClassRegistry registry) {
        Object v = registry.get(key);
        if (v == null) {
            Class<?> sk = key.getSuperclass();
            if (sk != null) {
                v = registry.get(sk);
            }
            Class<?>[] itfs = null;
            if (v == null) { // try interfaces
                itfs = key.getInterfaces();
                for (Class<?> itf : itfs) {
                    v = registry.get(itf);
                    if (v != null) {
                        break;
                    }
                }
            }
            if (v == null) {
                if (sk != null) { // superclass
                    v = lookup(sk, registry);
                }
                if (v == null) { // interfaces
                    for (Class<?> itf : itfs) {
                        v = lookup(itf, registry);
                        if (v != null) {
                            break;
                        }
                    }
                }
            }
            if (v != null) {
                registry.put(key, v);
            }
        }
        return v;
    }

    public static Object lookup(Class<?> key, ClassNameRegistry registry) {
        Object v = registry.get(key.getName());
        if (v == null) {
            Class<?> sk = key.getSuperclass();
            if (sk != null) {
                v = registry.get(sk.getName());
            }
            Class<?>[] itfs = null;
            if (v == null) { // try interfaces
                itfs = key.getInterfaces();
                for (Class<?> itf : itfs) {
                    v = registry.get(itf.getName());
                    if (v != null) {
                        break;
                    }
                }
            }
            if (v == null) {
                if (sk != null) { // superclass
                    v = lookup(sk, registry);
                }
                if (v == null) { // interfaces
                    for (Class<?> itf : itfs) {
                        v = lookup(itf, registry);
                        if (v != null) {
                            break;
                        }
                    }
                }
            }
            if (v != null) {
                registry.put(key.getName(), v);
            }
        }
        return v;
    }

}
