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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.chemistry.shell.app.Application;
import org.apache.chemistry.shell.command.Cmd;
import org.apache.chemistry.shell.command.Command;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;

@Cmd(syntax="help [command:command]", synopsis="Help")
public class Help extends Command {

    @Override
    public void run(Application app, CommandLine cmdLine) throws Exception {
        String param = cmdLine.getParameterValue("command");
        if (param != null) {
            printHelpForCommand(app, param);
        } else {
            printHelpForAllCommands(app);
        }
    }

    private void printHelpForCommand(Application app, String cmdName) throws CommandException {
        Command cmd = app.getCommandRegistry().getCommand(cmdName);
        if (cmd != null) {
            println(cmd.getHelp());
        } else {
            throw new CommandException("Unknown command: " + cmdName);
        }
    }

    private void printHelpForAllCommands(Application app) {
        println(getHelp());

        Command[] cmds = app.getCommandRegistry().getCommands();
        Arrays.sort(cmds, new CommandComparator());
        Set<String> seen = new HashSet<String>();
        StringBuilder buf = new StringBuilder();
        for (Command cmd : cmds) {
            String name = cmd.getName();
            if (seen.contains(name)) {
                continue;
            }
            seen.add(name);
            buf.setLength(0);
            buf.append(name);
            String[] aliases = cmd.getAliases();
            if (aliases.length > 1) {
                buf.append(" [");
                for (int i=1; i<aliases.length; i++) {
                    buf.append(aliases[i]).append("|");
                }
                buf.setLength(buf.length()-1);
                buf.append("]");
            }
            buf.append(" - ").append(cmd.getSynopsis());
            println(buf.toString());
        }
    }

    private static class CommandComparator implements Comparator<Command> {
        public int compare(Command o1, Command o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

}
