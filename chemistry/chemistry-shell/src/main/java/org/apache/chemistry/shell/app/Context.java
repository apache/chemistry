/*
 * (C) Copyright 2009-2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   Bogdan Stefanescu (bs@nuxeo.com), Nuxeo
 *   Stefane Fermigier (sf@nuxeo.com), Nuxeo
 *   Florent Guillaume (fg@nuxeo.com), Nuxeo
 */

package org.apache.chemistry.shell.app;

import org.apache.chemistry.shell.util.Path;

/**
 * A context represents the current state of the shell application and is usually wrapping
 * the current selected remote object.
 * <p>
 * The context can be adapted to the wrapped object by calling {@link Context#as(Class)}
 * and providing the desired type.
 * If the context cannot be represented as the given type it will return null,
 * otherwise will return the instance of the desired object.
 * <p>
 * A context may wrap a "folder" object (thus may contain sub contexts) or it may be a leaf context.
 * To change a context to another remote object the Context#cd
 * <p>
 * A context is providing several basic operations like:
 * <ul>
 * <li> ls  - list available sub contexts
 * <li> pwd - get the context absolute path
 * <li> id  - show more information about the current context
 * <li> cd  - change the context to another context given a context path.
 * If the path starts with a '/' it will be assumed to be an absolute path otherwise it will be resolved relative to the current context
 * </ul>
 */
public interface Context {

    /**
     * Gets the current application.
     *
     * @return
     */
    Application getApplication();

    /**
     * Gets the context path.
     *
     * @return
     */
    Path getPath();

    /**
     * Gets the context absolute path as a string.
     *
     * @return
     */
    String pwd();

    /**
     * Lists the keys of the available sub contexts.
     * This is used by the command line completor.
     *
     * @return an empty array if no sub contexts are available, otherwise return the array of sub context names
     */
    String[] entries();

    /**
     * Lists sub contexts names. The returned names are colored (may contain color code characters).
     *
     * @return
     */
    String[] ls(); //colored entries

    /**
     * Gets a child context given its name.
     *
     * @param name
     * @return null if no such sub context exists, otherwise returns the sub context
     */
    Context getContext(String name);

    Path resolvePath(String path);

    /**
     * Clears any cache associated with the context.
     */
    void reset();

    /**
     * Adapts the context to the given type.
     *
     * @param <T>
     * @param type
     * @return null if the context cannot be adapted, otherwise an instance of the given type
     */
    <T> T as(Class<T> type);

    /**
     * Gets a string identifying this context.
     * (Can be the object title and path or other useful information).
     *
     * @return
     */
    String id();

}
