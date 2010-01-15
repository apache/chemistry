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

import org.junit.Assert;
import org.junit.Test;
import org.apache.chemistry.shell.command.CommandSyntax;
import org.apache.chemistry.shell.command.CommandToken;

public class TestCommandSyntax extends Assert {

    @Test
    public void testNoArgument() {
        CommandSyntax cs = CommandSyntax.parse("cmd");
        assertEquals(0, cs.getArguments().size());
        assertEquals(1, cs.getTokens().size());
        assertEquals("cmd", cs.getCommandToken().getName());
    }

    @Test
    public void testOneMandatoryArgument() {
        CommandSyntax cs = CommandSyntax.parse("cmd arg1");
        assertEquals(1, cs.getArguments().size());
        assertEquals(2, cs.getTokens().size());

        CommandToken cmd = cs.getCommandToken();
        assertEquals("cmd", cmd.getName());
        assertTrue(cmd.isCommand());
        assertFalse(cmd.isArgument());

        CommandToken arg1 = cs.getArgument(0);
        CommandToken arg1ByName = cs.getToken("arg1");
        assertEquals(arg1ByName, arg1);

        assertEquals("arg1", arg1.getName());
        assertTrue(arg1.isArgument());
        assertFalse(arg1.isCommand());
        assertFalse(arg1.isOptional());
        assertFalse(arg1.isFlag());
        assertNull(arg1.getValueType());
    }

    @Test
    public void testOneMandatoryWithType() {
        CommandSyntax cs = CommandSyntax.parse("cmd arg1:file");
        assertEquals(1, cs.getArguments().size());
        assertEquals(2, cs.getTokens().size());

        CommandToken arg1 = cs.getArgument(0);
        CommandToken arg1ByName = cs.getToken("arg1");
        assertEquals(arg1ByName, arg1);

        assertEquals("arg1", arg1.getName());
        assertTrue(arg1.isArgument());
        assertFalse(arg1.isCommand());
        assertFalse(arg1.isFlag());

        assertEquals("file", arg1.getValueType());
    }

    @Test
    public void testOneOptionalArgument() {
        CommandSyntax cs = CommandSyntax.parse("cmd [arg1]");
        assertEquals(1, cs.getArguments().size());
        assertEquals(2, cs.getTokens().size());

        CommandToken cmd = cs.getCommandToken();
        assertEquals("cmd", cmd.getName());
        assertTrue(cmd.isCommand());
        assertFalse(cmd.isArgument());

        CommandToken arg1 = cs.getArgument(0);
        CommandToken arg1ByName = cs.getToken("arg1");
        assertEquals(arg1ByName, arg1);

        assertEquals("arg1", arg1.getName());
        assertTrue(arg1.isArgument());
        assertFalse(arg1.isCommand());
        assertTrue(arg1.isOptional());
        assertFalse(arg1.isFlag());
    }

    @Test
    public void testOneOptionalFlag() {
        CommandSyntax cs = CommandSyntax.parse("cmd [-s] arg1");
        assertEquals(1, cs.getArguments().size());
        assertEquals(3, cs.getTokens().size());

        CommandToken cmd = cs.getCommandToken();
        assertEquals("cmd", cmd.getName());
        assertTrue(cmd.isCommand());
        assertFalse(cmd.isArgument());

        CommandToken arg1 = cs.getToken("arg1");
        assertEquals("arg1", arg1.getName());

        CommandToken flag = cs.getToken("-s");

        assertEquals("-s", flag.getName());
        assertFalse(flag.isArgument());
        assertFalse(flag.isCommand());
        assertTrue(flag.isOptional());
        assertTrue(flag.isFlag());
    }

    @Test
    public void testDefaultValue() {
        CommandSyntax cs = CommandSyntax.parse("cmd [arg1:file?toto]");
        CommandToken arg1 = cs.getToken("arg1");
        assertEquals("arg1", arg1.getName());
        assertEquals("file", arg1.getValueType());
        assertEquals("toto", arg1.getDefaultValue());
        assertTrue(arg1.isArgument());
        assertFalse(arg1.isCommand());
        assertTrue(arg1.isOptional());
        assertFalse(arg1.isFlag());
    }
}
