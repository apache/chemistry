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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.shell.cmds.base.Cd;
import org.apache.chemistry.shell.cmds.base.Connect;
import org.apache.chemistry.shell.cmds.base.Disconnect;
import org.apache.chemistry.shell.cmds.base.Exit;
import org.apache.chemistry.shell.cmds.base.Help;
import org.apache.chemistry.shell.cmds.base.Id;
import org.apache.chemistry.shell.cmds.base.LCd;
import org.apache.chemistry.shell.cmds.base.LPopd;
import org.apache.chemistry.shell.cmds.base.LPushd;
import org.apache.chemistry.shell.cmds.base.LPwd;
import org.apache.chemistry.shell.cmds.base.Ll;
import org.apache.chemistry.shell.cmds.base.Ls;
import org.apache.chemistry.shell.cmds.base.Match;
import org.apache.chemistry.shell.cmds.base.Popd;
import org.apache.chemistry.shell.cmds.base.Pushd;
import org.apache.chemistry.shell.cmds.base.Pwd;
import org.apache.chemistry.shell.util.StringUtils;

public class CommandRegistry {

    protected static final CommandRegistry builtinCommands = new CommandRegistry(null);
    protected final Map<String, Command> commands;

    static {
        builtinCommands.registerCommand(new Help());
        builtinCommands.registerCommand(new Exit());
        builtinCommands.registerCommand(new Connect());
        builtinCommands.registerCommand(new Disconnect());
        builtinCommands.registerCommand(new Cd());
        builtinCommands.registerCommand(new Pushd());
        builtinCommands.registerCommand(new Popd());
        builtinCommands.registerCommand(new Ls());
        builtinCommands.registerCommand(new Pwd());
        builtinCommands.registerCommand(new Id());
        builtinCommands.registerCommand(new LCd());
        builtinCommands.registerCommand(new LPushd());
        builtinCommands.registerCommand(new LPopd());
        builtinCommands.registerCommand(new LPwd());
        builtinCommands.registerCommand(new Ll());
        builtinCommands.registerCommand(new Match());
    }

    public CommandRegistry(Map<String, Command> cmds) {
        commands = new HashMap<String, Command>();
        if (cmds != null) {
            commands.putAll(cmds);
        }
    }

    public CommandRegistry() {
        this(builtinCommands.commands);
    }

    public void registerCommand(Command cmd) {
        for (String alias : cmd.getAliases()) {
            commands.put(alias, cmd);
        }
    }

    public void unregisterCommand(String name) {
        Command cmd = commands.remove(name);
        if (cmd != null) {
            for (String alias : cmd.getAliases()) {
                commands.remove(alias);
            }
        }
    }

    public String[] getCommandNames() {
        return commands.keySet().toArray(new String[commands.size()]);
    }

    public Command[] getCommands() {
        return commands.values().toArray(new Command[commands.size()]);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Not used. Will probably be removed.
     */
    public String[] getCompletionInfo(String line, int offset) {
        if (offset == -1) {
            offset = line.length();
        }
        // get the word containing the offset
        int i = offset-1;
        while (i >=0) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c)) {
                break;
            }
            i--;
        }
        String word = offset > i && i>=0 ? line.substring(i+1, offset) : "";
        String prefix = line.substring(0, i).trim();
        if (prefix.length() == 0) {
           return new String[] {CommandToken.COMMAND, word};
        }
        String[] segments = StringUtils.tokenize(prefix);
        String cmdName = segments[0];
        Command cmd = getCommand(cmdName);
        if (cmd == null) {
            return null;
        }
        i = segments.length-1;
        CommandToken token = cmd.syntax.getToken(segments[i]);
        if (token == null) {
            i--;
            int k = 0;
            while (i > 0) {
                token = cmd.syntax.getToken(segments[i]);
                if (token != null) {

                }
                 i--;
                 k++;
             }
            token = cmd.syntax.getArgument(k);
        }
        if (token == null) {
            return null;
        }
        return new String[] {token.getValueType(), word} ;
    }

    /**
     * Not used. Will probably be removed.
     */
    public static CommandRegistry getBuiltinCommands() {
        return builtinCommands;
    }

    /**
     * Not used. Will probably be removed.
     */
    public static Command getBuiltinCommand(String name) {
        return builtinCommands.getCommand(name);
    }

    /**
     * Not used. Will probably be removed.
     */
    public static void registerBuiltinCommand(Command cmd) {
        builtinCommands.registerCommand(cmd);
    }

    /**
     * Not used. Will probably be removed.
     */
    public static void unregisterBuiltinCommand(Command cmd) {
        builtinCommands.unregisterCommand(cmd.getName());
    }

}
