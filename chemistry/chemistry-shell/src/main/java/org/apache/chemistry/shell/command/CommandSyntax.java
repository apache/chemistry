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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.chemistry.shell.jline.CompositeCompletor;
import org.apache.chemistry.shell.util.StringUtils;

/**
 * cmd [-opt|-o:type?defValue] [name:type]
 * <p>
 * Supported types: file, dir, command, item. See {@link CompositeCompletor}
 */
public class CommandSyntax {

    protected final List<CommandToken> tokens = new ArrayList<CommandToken>();
    protected final List<CommandToken> args = new ArrayList<CommandToken>();
    protected final HashMap<String, CommandToken> map = new HashMap<String, CommandToken>();

    /**
     * Static factory.
     */
    public static CommandSyntax parse(String text) {
        String[] tokens = StringUtils.tokenize(text);
        if (tokens.length == 0) {
            throw new IllegalArgumentException("cannot parse empty command lines");
        }
        CommandSyntax syntax = new CommandSyntax();
        if (tokens.length == 0) {
            return syntax;
        }
        CommandToken tok = CommandToken.parseCommand(tokens[0]);
        syntax.addToken(tok);
        for (int i=1; i<tokens.length; i++) {
            tok = CommandToken.parseArg(tokens[i]);
            syntax.addToken(tok);
        }
        return syntax;
    }

    public CommandToken getCommandToken() {
        return tokens.get(0);
    }

    public List<CommandToken> getArguments() {
        return args;
    }

    public CommandToken getArgument(int index) {
        if (index >= args.size()) {
            return null;
        }
        return args.get(index);
    }

    public List<CommandToken> getTokens() {
        return tokens;
    }

    public CommandToken getToken(int i) {
        return tokens.get(i);
    }

    public CommandToken getToken(String key) {
        return map.get(key);
    }

    /**
     * Gets all parameter keys.
     */
    public String[] getParameterKeys() {
        ArrayList<String> keys = new ArrayList<String>();
        for (int i=1,len=tokens.size(); i<len; i++) { // skip first token
            CommandToken token = tokens.get(i);
            if (!token.isArgument()) {
                keys.addAll(Arrays.asList(token.getNames()));
            }
        }
        return keys.toArray(new String[keys.size()]);
    }

    public void addToken(CommandToken tok) {
        tokens.add(tok);
        for (int i=0; i<tok.getNames().length; i++) {
            map.put(tok.getNames()[i], tok);
        }
        if (tok.isArgument()) {
            args.add(tok);
        }
    }

}
