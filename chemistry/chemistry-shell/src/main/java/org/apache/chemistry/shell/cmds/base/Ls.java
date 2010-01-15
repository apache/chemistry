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

package org.apache.chemistry.shell.cmds.base;

import org.apache.chemistry.Document;
import org.apache.chemistry.shell.app.Application;
import org.apache.chemistry.shell.app.Context;
import org.apache.chemistry.shell.command.Cmd;
import org.apache.chemistry.shell.command.Command;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;
import org.apache.chemistry.shell.util.Path;

@Cmd(syntax = "ls [target:item]", synopsis = "List entries in working directory")
public class Ls extends Command {

    @Override
    public void run(Application app, CommandLine cmdLine) throws Exception {
        ensureConnected(app);

        String param = cmdLine.getParameterValue("target");

        Context ctx;
        if (param == null) {
            ctx = app.getContext();
            for (String line : ctx.ls()) {
                println(line);
            }
        } else {
            ctx = app.resolveContext(new Path(param));
            if (ctx == null) {
                throw new CommandException("Cannot resolve target: " + param);
            }
            Document document = ctx.as(Document.class);
            if (document != null) {
                println(document.getName());
            } else {
                for (String line : ctx.ls()) {
                    println(line);
                }
            }
        }
    }

}
