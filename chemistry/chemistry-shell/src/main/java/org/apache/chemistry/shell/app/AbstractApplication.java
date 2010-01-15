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
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.shell.command.CommandRegistry;
import org.apache.chemistry.shell.util.Path;

public abstract class AbstractApplication implements Application {

    protected final CommandRegistry registry;
    protected final Map<String, Object> dataMap;

    protected Context ctx;
    protected URL serverUrl;
    protected File wd;
    protected String username;
    protected char[] password;


    public AbstractApplication() {
        registry = new CommandRegistry();
        dataMap = new HashMap<String, Object>();
        wd = new File(".");
        ctx = getRootContext();
    }

    public void login(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    protected void initServerURL(URL serverUrl) {
        String userInfo = serverUrl.getUserInfo();
        if (userInfo != null) {
            int p = userInfo.indexOf(':');
            if (p > -1) {
                username = userInfo.substring(0, p);
                password = userInfo.substring(p+1).toCharArray();
            } else {
                username = userInfo;
            }
        }
        // do URL cleanup
        try {
            this.serverUrl = new URL(serverUrl.getProtocol(), serverUrl.getHost(), serverUrl.getPort(), serverUrl.getPath());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String getHost() {
        return serverUrl.getHost();
    }

    public void connect(String uri) throws IOException {
        connect(new URL(uri));
    }

    public void connect(URL uri) throws IOException {
        initServerURL(uri);
        doConnect();
    }

    protected abstract void doConnect();

    public CommandRegistry getCommandRegistry() {
        return registry;
    }

    public URL getServerUrl() {
        return serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public File getWorkingDirectory() {
        return wd;
    }

    public void setWorkingDirectory(File file) {
        wd = file;
        Console.getDefault().updatePrompt();
    }

    public Context getContext() {
        return ctx;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
        Console.getDefault().updatePrompt();
    }

    public Context resolveContext(Path path) {
        Context c;
        if (path.isRelative()) {
            if (path.segmentCount() == 0) {
                return getContext();
            }
            boolean dotdot = false;
            while (path.segmentCount() > 0) {
                String seg = path.segment(0);
                if (seg.equals(".")) {
                    path = path.removeFirstSegments(1);
                } else if (seg.equals("..")) {
                    dotdot = true;
                    break;
                } else {
                    break;
                }
            }
            if (dotdot) {
                path = getContext().getPath().append(path);
                c = getRootContext();
            } else {
                c = getContext();
            }
        } else {
            c = getRootContext();
        }
        if (c == null) {
            return null;
        }
        for (int i=0,cnt=path.segmentCount(); i<cnt; i++) {
            c = c.getContext(path.segment(i));
            if (c == null) {
                return null;
            }
        }
        return c;
    }

    public File resolveFile(String path) {
        if (path.startsWith("/")) {
            return new File(path);
        } else {
            return new File(wd, path);
        }
    }

    public Object getData(String key) {
        return dataMap.get(key);
    }

    public void setData(String key, Object data) {
        if (data == null) {
            dataMap.remove(key);
        } else {
            dataMap.put(key, data);
        }
    }

}
