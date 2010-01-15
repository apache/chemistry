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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.shell.app.Application;
import org.apache.chemistry.shell.app.Console;
import org.apache.chemistry.shell.util.StringUtils;

public class CommandLine {

    protected List<CommandParameter> params;
    protected Map<String, CommandParameter> map;
    protected Command cmd;

    public CommandLine(CommandRegistry registry, String cmd) throws CommandException {
        this(registry, StringUtils.tokenize(cmd));
    }

    public CommandLine(CommandRegistry registry, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new IllegalArgumentException("CommandLine cannot be empty");
        }
        map = new HashMap<String, CommandParameter>();
        params = new ArrayList<CommandParameter>();
        cmd = registry.getCommand(args[0]);
        if (cmd == null) {
            throw new NoSuchCommandException(args[0]);
        }

        // build params
        CommandParameter param = new CommandParameter(args[0], cmd.syntax.tokens.get(0));
        params.add(param);

        int k = 0;
        for (int i=1; i<args.length; i++) {
            String key = args[i];
            if (param != null && param.token.isValueRequired()) {
                param.setValue(key);
                param = null;
            } else {
                CommandToken token = cmd.syntax.getToken(key);
                if (token == null) {
                    token = cmd.syntax.getArgument(k++);
                    if (token == null) {
                        throw new CommandSyntaxException(
                                cmd, "Syntax Error: Extra argument found on position "+i);
                    }
                }
                if (token.isArgument()) {
                    param = new CommandParameter(token.getName(), token);
                    param.setValue(key);
                } else {
                    param = new CommandParameter(key, token);
                }
                params.add(param);
                map.put(param.token.getName(), param);
            }
        }
        // check if the last parameter has a value if it requires it
        if (param != null && param.getValue() == null && param.token.isValueRequired()) {
            throw new CommandSyntaxException(
                    cmd, "Syntax Error: Value for parameter "+param.key+" is required");
        }
        // check if all required options are present - ignore first token which is the command token
        for (int i=1, len=cmd.syntax.tokens.size(); i<len; i++) {
            CommandToken token = cmd.syntax.tokens.get(i);
            if (!token.isOptional()) {
                if (!map.containsKey(token.getName())) {
                    throw new CommandSyntaxException(
                            cmd, "Syntax Error: Missing required parameter: "+token.getName());
                }
            }
        }
    }

    public CommandParameter getParameter(String key) {
        return map.get(key);
    }

    public String getParameterValue(String key) {
        CommandParameter param = map.get(key);
        return param == null ? null : param.getValue();
    }

    public void run(Application app) throws Exception {
        cmd.run(app, this);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (CommandParameter param : params) {
            buf.append(param.key).append(" ");
            if (param.token.isValueRequired()) {
                String value = param.getValue();
                if (value != null) {
                    buf.append(value).append(" ");
                }
            }
        }
        buf.setLength(buf.length()-1);
        return buf.toString();
    }

    /**
     * Not used. Will probably be removed.
     */
    public Command getCommand() {
        return cmd;
    }

    /**
     * Not used. Will probably be removed.
     */
    public CommandParameter getLastParameter() {
        if (params.isEmpty()) {
            return null;
        }
        if (params.size() == 1) {
            return null;
        }
        return params.get(params.size()-1);
    }

    /**
     * Not used. Will probably be removed.
     */
    public List<CommandParameter> getParameters() {
        return params;
    }

    /**
     * Not used. Will probably be removed.
     */
    public List<CommandParameter> getArguments() {
        List<CommandParameter> result = new ArrayList<CommandParameter>();
        for (CommandParameter arg : params) {
            if (arg.token.isArgument()) {
                result.add(arg);
            }
        }
        return result;
    }

    /**
     * Not used. Will probably be removed.
     */
    public Map<String, Object> toMap() {
        // preserve params order
        LinkedHashMap<String, Object> args = new LinkedHashMap<String, Object>();
        int k = 0;
        for (CommandParameter param : params) {
            String key = param.getKey();
            String value = param.getValue();
            Object val = value;
            if (key == null) {
                key = "_"+(k++);
            }
            if (CommandToken.FILE.equals(param.token.getValueType())) {
                val = Console.getDefault().getApplication().resolveFile(value);
            }
            args.put(key, val);
        }
        return args;
    }

}
