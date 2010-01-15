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

public abstract class AbstractContext implements Context {

    protected final Application app;
    protected final Path path;

    public AbstractContext(Application app, Path path) {
        this.app = app;
        this.path = path;
    }

    public String pwd() {
        return path.toString();
    }

    public Path getPath() {
        return path;
    }

    public Application getApplication() {
        return app;
    }

    public Path resolvePath(String path) {
        if (!path.startsWith("/")) {
            return new Path(path);
        } else {
            return this.path.append(path);
        }
    }

}
