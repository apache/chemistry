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

package org.apache.chemistry.shell.app;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.chemistry.shell.command.CommandException;
import org.apache.chemistry.shell.command.CommandLine;
import org.apache.chemistry.shell.command.CommandRegistry;
import org.apache.chemistry.shell.util.PasswordReader;

public class Console {

    protected static Console instance;

    protected Application app;

    protected StringBuffer buffer = new StringBuffer();
    protected String lastResult;

    public static Console getDefault() {
        return instance;
    }

    public static void setDefault(Console console) {
        instance = console;
    }

    /**
     * Starts the console.
     */
    public void start(Application app) throws IOException {
        if (this.app != null) {
            throw new IllegalStateException("Console already started");
        }
        this.app = app;
    }

    /**
     * Gets the current client
     */
    public Application getApplication() {
        return app;
    }

    /**
     * Gets the result of the last command.
     */
    public String getLastResult() {
        return lastResult;
    }

    public void runCommand(String line) throws Exception {
        CommandLine commandLine = parseCommandLine(app.getCommandRegistry(), line);
        lastResult = buffer.toString();
        buffer = new StringBuffer();
        commandLine.run(app);
        if ("match".equals(commandLine.getCommand().getName())) {
            // Keep previous result in case we have several 'match' commands
            buffer = new StringBuffer(lastResult);
        }
    }

    public static CommandLine parseCommandLine(CommandRegistry reg, String line) throws CommandException {
        return new CommandLine(reg, line);
    }

    /**
     * Update the current context of the console.
     * Overridden in the JLine console.
     */
    public void updatePrompt() {
        // do nothing
    }

    /**
     * Reads the stream an prints the result on the screen.
     */
    public void print(InputStream in) throws IOException {
        IOUtils.copy(in, System.out);
        System.out.flush();
    }

    public void println(String str) {
        buffer.append(str + "\n");
        System.out.println(str);
    }

    /**
     * Print a new line.
     * On non text console does nothing
     */
    public void println() throws IOException {
        System.out.println();
    }

    public void error(String message) {
        System.err.println(message);
    }

    public String promptPassword() throws IOException {
        return PasswordReader.read();
    }

}
