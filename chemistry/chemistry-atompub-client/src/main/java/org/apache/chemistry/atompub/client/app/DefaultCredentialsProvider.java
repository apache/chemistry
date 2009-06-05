/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client.app;

import org.apache.chemistry.atompub.client.Credentials;
import org.apache.chemistry.atompub.client.CredentialsProvider;

/**
 *
 */
public class DefaultCredentialsProvider implements CredentialsProvider {

    protected Credentials credentials;

    public DefaultCredentialsProvider(Credentials credentials) {
        this.credentials = credentials;
    }

    public DefaultCredentialsProvider(String username, char[] password) {
        this(new Credentials(username, password));
    }

    public DefaultCredentialsProvider(String username, String password) {
        this(username, password.toCharArray());
    }

    public Credentials getCredentials() {
        return credentials;
    }

}
