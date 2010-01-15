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

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.shell.app.ChemistryApp;
import org.apache.chemistry.shell.app.ChemistryCommand;
import org.apache.chemistry.shell.app.Context;
import org.apache.chemistry.shell.command.Cmd;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;
import org.apache.chemistry.shell.util.Path;
import org.apache.chemistry.shell.util.SimplePropertyManager;

@Cmd(syntax="propget target:item [key]", synopsis="Print the value of the given property on the current context object")
public class PropGet extends ChemistryCommand {

    @Override
    protected void execute(ChemistryApp app, CommandLine cmdLine)
            throws Exception {

        String target = cmdLine.getParameterValue("target");
        String key = cmdLine.getParameterValue("key");

        Context ctx = app.resolveContext(new Path(target));
        if (ctx == null) {
            throw new CommandException("Cannot resolve "+target);
        }

        CMISObject obj = ctx.as(CMISObject.class);
        if (obj == null) {
            throw new CommandException("Cannot resolve "+target);
        }

        if (key != null) {
            String propValue = new SimplePropertyManager(obj).getPropertyAsString(key);
            println(propValue);
        } else {
            new SimplePropertyManager(obj).dumpProperties();
        }
    }

}
