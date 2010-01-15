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

import java.util.Stack;

import org.apache.chemistry.shell.app.Application;
import org.apache.chemistry.shell.app.Context;
import org.apache.chemistry.shell.command.Cmd;
import org.apache.chemistry.shell.command.Command;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;

@Cmd(syntax="popd", synopsis="Pop directory stack")
public class Popd extends Command {

    public static final String CTX_STACK_KEY = "ctx.stack";

    @Override
    @SuppressWarnings("unchecked")
    public void run(Application app, CommandLine cmdLine) throws Exception {
        Stack<Context> stack = (Stack<Context>) app.getData(CTX_STACK_KEY);
        if (stack == null || stack.isEmpty()) {
            throw new CommandException("Context stack is empty");
        }

        Context ctx = stack.pop();
        app.setContext(ctx);
        if (stack.isEmpty()) {
            app.setData(CTX_STACK_KEY, null);
        }
    }

}
