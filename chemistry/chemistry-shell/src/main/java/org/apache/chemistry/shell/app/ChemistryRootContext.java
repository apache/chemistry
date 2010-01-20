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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.APPConnection;
import org.apache.chemistry.atompub.client.APPContentManager;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.shell.util.ColorHelper;
import org.apache.chemistry.shell.util.Path;

public class ChemistryRootContext extends AbstractContext {

    protected Map<String, Repository> repos;
    protected String[] keys;
    protected String[] ls;

    public ChemistryRootContext(ChemistryApp app) {
        super(app, Path.ROOT);
    }

    public APPContentManager getContentManager() {
        return ((ChemistryApp) app).getContentManager();
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
        ContentManager cm = getContentManager();
        if (cm == null) {
            Console.getDefault().error("Not connected: cannot browse repository");
            return null;
        }
        Repository r = repos.get(name); // TODO  atompub client is using IDs to get repositories ...
        Repository repo = cm.getRepository(r.getId());
        if (repo != null) {
            APPConnection conn = (APPConnection) repo.getConnection(null);
            CMISObject entry = conn.getRootFolder();
            return new ChemistryContext((ChemistryApp) app, path.append(name), conn, entry);
        }
        return null;
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
        if (keys == null) {
            ContentManager cm = getContentManager();
            if (cm == null) {
                Console.getDefault().error("Not connected: cannot browse repository");
                return false;
            }
            Repository[] repos = cm.getRepositories();
            this.repos = new HashMap<String, Repository>();
            keys = new String[repos.length];
            ls = new String[repos.length];
            for (int i=0; i<repos.length; i++) {
                keys[i] = repos[i].getName();
                this.repos.put(repos[i].getName(), repos[i]);
                ls[i] = ColorHelper.decorateNameByType(repos[i].getName(), "Repository");
            }
        }
        return true;
    }

    public void reset() {
        keys = null;
        ls = null;
        APPContentManager cm = getContentManager();
        if (cm != null) {
            cm.refresh();
        }
    }

    public String id() {
        return "CMIS server: "+app.getServerUrl();
    }

}
