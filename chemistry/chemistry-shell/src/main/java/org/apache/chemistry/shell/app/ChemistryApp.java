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

import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.chemistry.shell.cmds.cmis.Cat;
import org.apache.chemistry.shell.cmds.cmis.CreateFile;
import org.apache.chemistry.shell.cmds.cmis.CreateFolder;
import org.apache.chemistry.shell.cmds.cmis.DumpProps;
import org.apache.chemistry.shell.cmds.cmis.DumpTree;
import org.apache.chemistry.shell.cmds.cmis.Get;
import org.apache.chemistry.shell.cmds.cmis.PropGet;
import org.apache.chemistry.shell.cmds.cmis.Put;
import org.apache.chemistry.shell.cmds.cmis.Remove;
import org.apache.chemistry.shell.cmds.cmis.SetProp;
import org.apache.chemistry.shell.cmds.cmis.SetStream;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ChemistryApp extends AbstractApplication {

    protected APPContentManager cm;

    public ChemistryApp() {
        registry.registerCommand(new DumpTree());
        registry.registerCommand(new SetProp());
        registry.registerCommand(new PropGet());
        registry.registerCommand(new DumpProps());
        registry.registerCommand(new Get());
        registry.registerCommand(new SetStream());
        registry.registerCommand(new CreateFile());
        registry.registerCommand(new CreateFolder());
        registry.registerCommand(new Remove());
        registry.registerCommand(new Cat());
        registry.registerCommand(new Put());
    }

    @Override
    protected void doConnect() {
        cm = new APPContentManager(serverUrl.toExternalForm());
        if (username != null) {
            cm.login(username, new String(password));
        }
    }

    public void disconnect() {
        cm = null;
    }

    public boolean isConnected() {
        return cm != null;
    }

    public Context getRootContext() {
        return new ChemistryRootContext(this);
    }

    public APPContentManager getContentManager() {
        return cm;
    }

}
