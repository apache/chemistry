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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.APPConnection;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.chemistry.shell.util.ColorHelper;
import org.apache.chemistry.shell.util.Path;

public class ChemistryContext extends AbstractContext {

    //public static final String CONN_KEY = "chemistry.connection";

    protected final APPContentManager cm;
    protected final APPConnection conn;
    protected final CMISObject entry;

    protected String[] keys;
    protected String[] ls;
    protected Map<String,CMISObject> children;

    public ChemistryContext(ChemistryApp app, Path path, APPConnection conn, CMISObject entry) {
        super(app, path);
        this.conn = conn;
        this.entry = entry;
        cm = app.getContentManager();
    }

    @Override
    public ChemistryApp getApplication() {
        return (ChemistryApp)app;
    }

    // Not used
    public APPConnection getConnection() {
        return conn;
    }

    // Not used
    public CMISObject getEntry() {
        return entry;
    }

    // Not used
    public APPContentManager getContentManager() {
        return cm;
    }

    // Not used
    public Repository getRepository() {
        return conn.getRepository();
    }

    public Context getContext(String name) {
        load();
        CMISObject e = children.get(name);
        if (e != null) {
            return new ChemistryContext((ChemistryApp) app, path.append(name), conn, e);
        }
        return null;
    }

    public String[] ls() {
        load();
        return ls;
    }

    public String[] entries() {
        load();
        return keys;
    }

    public void reset() {
        children = null;
        keys = null;
        ls = null;
    }

    public boolean isFolder() {
        return entry instanceof Folder;
    }

    protected void load() {
        if (children == null) {
            if (!isFolder()) {
                return;
            }
            Folder folder = (Folder) entry;
            List<CMISObject> feed =  folder.getChildren();
            children = new LinkedHashMap<String, CMISObject>();
            keys = new String[feed.size()];
            ls = new String[keys.length];
            int i = 0;
            for (CMISObject entry : feed) {
                children.put(entry.getName(), entry);
                keys[i] = entry.getName();
                ls[i++] = ColorHelper.decorateNameByType(entry.getName(), entry.getTypeId());
            }
        }
    }

    public <T> T as(Class<T> type) {
        if (type.isAssignableFrom(entry.getClass())) {
            return type.cast(entry);
        }
        return null;
    }

    // Not used
    public CMISObject getObjectByAbsolutePath(String path) {
        ObjectEntry entry = conn.getObjectByPath(path, null);
        if (entry!=null) {
            return conn.getObject(entry);
        } else {
            return null;
        }
    }

    // Not used
    public CMISObject resolveObject(String path) {
        Path p = resolvePath(path);
        ObjectEntry entry = conn.getObjectByPath(p.toString(), null);
        if (entry!=null) {
            return conn.getObject(entry);
        } else {
            return null;
        }
    }

    public String id() {
        return "Object "+entry.getId()+" of type "+entry.getTypeId();
    }

}
