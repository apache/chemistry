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
package org.apache.chemistry.atompub.client.app.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An extension service implementation must have a constructor that takes one
 * argument of type {@link ServiceContext} used by the service to initialize
 * itself.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionService {

    /**
     * Whether or not the service is a singleton (relative to its scope). For
     * connection bound services the singleton will live in the scope of the
     * connection. This means a new different will be used for each connection.
     * If not bound to a connection a singleton service will live as long the
     * repository which is bound id used.. Non singleton services will be
     * instantiated at each lookup.
     *
     * @return true if singleton, false otherwise.
     */
    boolean singleton() default true;

    /**
     * Whether or not this service require to be bound on a connection
     *
     * @return true if the service requires a connection, false otherwise
     */
    boolean requiresConnection() default false;

}
