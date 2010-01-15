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
package org.apache.chemistry.shell.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * In java 6 there is direct support for reading passwords form a console
 * <pre>
 *  if ((cons = System.console()) != null &&
 *    (passwd = cons.readPassword("[%s]", "Password:")) != null) {
 *    ...
 *  }
 * </pre>
 * This can be for java &lt; 6.
 * <p>
 * A separate thread is used to send to the console the backspace character to erase the last-typed character.
 */
public class PasswordReader {

    private PasswordReader() {
    }

    public static String read() throws IOException {
        ConsoleEraser consoleEraser = new ConsoleEraser();
        System.out.print("Password:  ");
        BufferedReader stdin = new BufferedReader(new
                InputStreamReader(System.in));
        consoleEraser.start();
        String pass = stdin.readLine();
        consoleEraser.halt();
        System.out.print("\b");
        return pass;
    }

    static class ConsoleEraser extends Thread {

        private boolean running = true;

        @Override
        public void run() {
            while (running) {
                System.out.print("\b ");
            }
        }

        public synchronized void halt() {
            running = false;
        }
    }

}
