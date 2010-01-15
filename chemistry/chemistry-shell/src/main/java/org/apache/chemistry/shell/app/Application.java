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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.chemistry.shell.command.CommandRegistry;
import org.apache.chemistry.shell.util.Path;

/**
 * An application represents the global context of the shell.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface Application {

    /**
     * Login using the given account.
     */
    void login(String username, char[] password);

    /**
     * Gets the connection URL.
     */
    URL getServerUrl();

    /**
     * Gets he username used for the connection.
     */
    String getUsername();

    /**
     * Gets the host where the application is connected.
     */
    String getHost();

    /**
     * Gets the working directory.
     */
    File getWorkingDirectory();

    /**
     * Sets the working directory (will be used to resolve relative file paths).
     */
    void setWorkingDirectory(File file);

    /**
     * Gets a file given its path.
     * <p>
     * If the path is absolute (starts with '/') it will be resolved as an absolute path
     * otherwise it will be resolved against the current working directory.
     */
    File resolveFile(String path);

    /**
     * Gets the current context.
     */
    Context getContext();

    /**
     * Sets the current context to the given one.
     */
    void setContext(Context ctx);

    /**
     * Resolves the given path to a context.
     */
    Context resolveContext(Path path);

    /**
     * Gets the root context.
     */
    Context getRootContext();

    /**
     * Gets the command registry.
     */
    CommandRegistry getCommandRegistry();

    /**
     * Sets a global variable. Can be used by commands to preserve their state.
     */
    void setData(String key, Object data);

    /**
     * Gets a global variable given its key.
     */
    Object getData(String key);

    /**
     * Connects to the given url. The current context will be reset.
     */
    void connect(String uri) throws IOException;

    /**
     * Connects to the given url. The current context will be reset.
     */
    void connect(URL uri) throws IOException;

    /**
     * Disconnects if already connected. The current context will be reset.
     */
    void disconnect();

    /**
     * Tests if connected.
     */
    boolean isConnected();

}
