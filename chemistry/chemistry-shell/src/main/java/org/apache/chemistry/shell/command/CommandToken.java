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

import org.apache.chemistry.shell.util.StringUtils;

public class CommandToken {

    public static final String COMMAND = "command";
    public static final String ANY = "*";
    public static final String FILE = "file";
    public static final String DOCUMENT = "document";

    private String[] names;
    private String valueType; // null | string | command | file | doc
    private String defaultValue;
    private boolean isArgument;
    private boolean isOptional;

    public static CommandToken parseCommand(String text) {
        CommandToken tok = new CommandToken();
        tok.names = StringUtils.split(text, '|', false);
        tok.valueType = COMMAND;
        return tok;
    }

    public static CommandToken parseArg(String text) {
        CommandToken tok = new CommandToken();
        if (text.startsWith("[")) {
            tok.isOptional = true;
            text = text.substring(1, text.length()-1);
        }
        int p = text.indexOf(':');
        if (p > -1) {
            tok.valueType = text.substring(p+1);
            text = text.substring(0, p);
            p = tok.valueType.indexOf('?');
            if (p > -1) {
                tok.defaultValue = tok.valueType.substring(p+1);
                tok.valueType = tok.valueType.substring(0, p);
            }
        }
        // parse names in text
        tok.names = StringUtils.split(text, '|', true);
        tok.isArgument = !tok.names[0].startsWith("-");
        return tok;
    }

    public boolean isValueRequired() {
        return valueType != COMMAND && valueType != null && !isArgument;
    }

    public boolean isCommand() {
        return valueType == COMMAND;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isFlag() {
        return names[0].startsWith("-");
    }

    public boolean isArgument() {
        return isArgument;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getValueType() {
        return valueType;
    }

    public String getName() {
        return names[0];
    }

    public String[] getNames() {
        return names;
    }

}
