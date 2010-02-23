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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.atompub.client.APPConnection;
import org.apache.chemistry.shell.util.ColorHelper;
import org.apache.chemistry.shell.util.Path;

public class ChemistryRootContext extends AbstractContext {

    protected String[] keys;

    protected String[] ls;

    public ChemistryRootContext(ChemistryApp app) {
        super(app, Path.ROOT);
    }

    @Override
    public ChemistryApp getApplication() {
        return (ChemistryApp) app;
    }

    public <T> T as(Class<T> type) {
        return null;
    }

    public Context getContext(String name) {
        load();
        ChemistryApp app = getApplication();
        if (!app.isConnected()) {
            Console.getDefault().error(
                    "Not connected: cannot browse repository");
            return null;
        }
        Repository repo = RepositoryManager.getInstance().getRepository(name);
        if (repo == null) {
            return null;
        }
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(Repository.PARAM_USERNAME, app.username);
        params.put(Repository.PARAM_PASSWORD, new String(app.password));
        APPConnection conn = (APPConnection) repo.getConnection(params);
        CMISObject entry = conn.getRootFolder();
        return new ChemistryContext((ChemistryApp) app, path.append(name),
                conn, entry);
    }

    public String[] ls() {
        if (load()) {
            return ls;
        }
        return new String[0];
    }

    public String[] entries() {
        if (load()) {
            return keys;
        }
        return new String[0];
    }

    protected boolean load() {
        if (keys != null) {
            return true;
        }
        if (!getApplication().isConnected()) {
            Console.getDefault().error(
                    "Not connected: cannot browse repository");
            return false;
        }
        Collection<RepositoryEntry> repos = RepositoryManager.getInstance().getRepositories();
        int size = repos.size();
        keys = new String[size];
        ls = new String[size];
        int i = 0;
        for (RepositoryEntry repo : repos) {
            String name = repo.getName();
            keys[i] = name;
            ls[i] = ColorHelper.decorateNameByType(name, "Repository");
            i++;
        }
        return true;
    }

    public void reset() {
        keys = null;
        ls = null;
    }

    public String id() {
        return "CMIS server: " + app.getServerUrl();
    }

}
