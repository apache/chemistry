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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsProvider;

/**
 *
 */
public class APPContentManager implements ContentManager {

    protected final String baseUrl;

    protected final HttpClient client;

    protected Repository[] repos;

    protected String username;

    public APPContentManager(String url) {
        this.baseUrl = url;
        client = new HttpClient();
        // client.setHttpConnectionManager(new
        // MultiThreadedHttpConnectionManager());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpClient getClient() {
        return client;
    }

    public Repository[] getRepositories() throws ContentManagerException {
        if (repos == null) {
            Connector connector = new Connector(client, new ReadContext(this));
            repos = connector.getServiceDocument(getBaseUrl());
        }
        return repos;
    }

    public Repository getRepository(String id) throws ContentManagerException {
        for (Repository repository : getRepositories()) {
            if (repository.getId().equals(id)) {
                return repository;
            }
        }
        throw new ContentManagerException("No such repository: " + id + " in: "
                + baseUrl);
    }

    public Repository getDefaultRepository() throws ContentManagerException {
        Repository[] repos = getRepositories();
        if (repos != null && repos.length > 0) {
            return repos[0];
        }
        throw new ContentManagerException("No default repository in: "
                + baseUrl);
    }

    public void refresh() {
        repos = null;
    }

    // TODO have another login method with more generic Credentials
    public void login(String username, String password) {
        this.username = username;
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
                username, password);
        client.getParams().setAuthenticationPreemptive(true);
        client.getParams().setParameter(CredentialsProvider.PROVIDER, cp);
    }

    public void logout() {
        username = null;
        client.getParams().setAuthenticationPreemptive(true);
        client.getParams().setParameter(CredentialsProvider.PROVIDER, null);
    }

    public String getCurrentLogin() {
        return username;
    }

    /**
     * Simple credentials provider using fixed username/password credentials.
     * Other implementations could query the user through a GUI.
     */
    public static class UsernamePasswordCredentialsProvider implements
            CredentialsProvider {

        protected final Credentials credentials;

        public UsernamePasswordCredentialsProvider(String username,
                String password) {
            this.credentials = new UsernamePasswordCredentials(username,
                    password);
        }

        public Credentials getCredentials(AuthScheme scheme, String host,
                int port, boolean proxy) {
            return credentials;
        }
    }

}
