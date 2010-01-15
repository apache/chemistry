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

package org.apache.chemistry.shell.cmds.cmis;

import java.io.File;
import java.io.FileInputStream;

import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.shell.app.ChemistryApp;
import org.apache.chemistry.shell.app.ChemistryCommand;
import org.apache.chemistry.shell.app.Context;
import org.apache.chemistry.shell.command.Cmd;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;
import org.apache.chemistry.shell.util.Path;
import org.apache.chemistry.shell.util.SimpleCreator;
import org.apache.chemistry.shell.util.SimplePropertyManager;

@Cmd(syntax="put [-t|--type:*] source:file [target:item]", synopsis="Uploads the stream of the target document")
public class Put extends ChemistryCommand {

    @Override
    protected void execute(ChemistryApp app, CommandLine cmdLine)
            throws Exception {

        String source = cmdLine.getParameterValue("source");
        String target = cmdLine.getParameterValue("target");
        if (target == null) {
            target = new Path(source).getLastSegment();
        }
        String typeName = cmdLine.getParameterValue("-t");
        if (typeName == null) {
            typeName = "cmis:document";
        }

        Context targetCtx = app.resolveContext(new Path(target));

        // Create document if it doesn't exist
        if (targetCtx == null) {
            Context currentCtx = app.getContext();
            Folder folder =  currentCtx.as(Folder.class);
            if (folder != null) {
                new SimpleCreator(folder).createFile(typeName, target);
                currentCtx.reset();
                targetCtx = app.resolveContext(new Path(target));
            }
        }
        if (targetCtx == null) {
            throw new CommandException("Cannot create target document");
        }

        Document obj = targetCtx.as(Document.class);
        if (obj == null) {
            throw new CommandException("Your target must be a document");
        }

        File file = app.resolveFile(source);
        FileInputStream in = new FileInputStream(file);
        try {
            new SimplePropertyManager(obj).setStream(in, file.getName());
        } finally {
            in.close();
        }
    }

}
