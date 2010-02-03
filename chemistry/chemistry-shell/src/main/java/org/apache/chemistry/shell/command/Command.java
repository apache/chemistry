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

package org.apache.chemistry.shell.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.chemistry.shell.app.Application;
import org.apache.chemistry.shell.app.Console;

public abstract class Command {

    protected CommandSyntax syntax;
    protected String[] aliases;
    protected String synopsis;

    public Command() {
        Cmd anno = getClass().getAnnotation(Cmd.class);
        synopsis = anno.synopsis();
        syntax = CommandSyntax.parse(anno.syntax());
        aliases = syntax.getCommandToken().getNames();
    }

    public String getName() {
        return aliases[0];
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public CommandSyntax getSyntax() {
        return syntax;
    }

    public void ensureConnected(Application app) throws CommandException {
        if (!app.isConnected()) {
            throw new CommandException("Not connected");
        }
    }

    public String getHelp() {
        URL url = getClass().getResource("/help/"+getName()+".help");
        if (url == null) {
            return "";
        }
        InputStream in = null;
        try {
            in = url.openStream();
            return IOUtils.toString(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (in != null) in.close(); } catch (IOException e) {}
        }
        return "";
    }

    public void print(InputStream in) throws IOException {
        Console.getDefault().print(in);
    }

    public void println(String str) {
        Console.getDefault().println(str);
    }

    public boolean isLocal() {
        return true;
    }

    public abstract void run(Application app, CommandLine cmdLine) throws Exception;

}
