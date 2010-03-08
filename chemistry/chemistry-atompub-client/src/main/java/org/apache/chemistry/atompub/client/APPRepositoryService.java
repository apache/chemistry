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
 *     Chris Hubick
 */
package org.apache.chemistry.atompub.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryService;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.CredentialsProvider;

/**
 * A {@link RepositoryService} providing the repositories found through an
 * AtomPub Service Document, configured through a URL.
 */
public class APPRepositoryService implements RepositoryService {

    protected final String url;

    protected final Map<String, Serializable> urlParams;

    protected final List<HttpClient> clients = new ArrayList<HttpClient>(2);

    protected final List<Map<String, Serializable>> clientParams = new ArrayList<Map<String, Serializable>>(
            2);

    protected List<APPRepository> repos;

    /**
     * Constructs an AtomPub RepositoryService given a URL
     * <p>
     * The optional parameters are used to fetch the initial AtomPub Service
     * Document, but will not be used for subsequent connections.
     *
     * @param url the AtomPub Service Document URL
     * @param params the parameters
     */
    public APPRepositoryService(String url, Map<String, Serializable> params) {
        this.url = url;
        this.urlParams = params;
    }

    public Collection<RepositoryEntry> getRepositories() {
        loadRepositories();
        List<RepositoryEntry> res = new ArrayList<RepositoryEntry>(repos.size());
        res.addAll(repos);
        return res;
    }

    protected void loadRepositories() {
        if (repos != null) {
            return;
        }
        Connector connector = new Connector(getDefaultClient(), new APPContext(
                this));
        repos = connector.getServiceDocument(url);
    }

    public Repository getDefaultRepository() {
        loadRepositories();
        return repos.size() == 0 ? null : repos.get(0);
    }

    public Repository getRepository(String id) {
        loadRepositories();
        for (Repository repository : repos) {
            if (repository.getId().equals(id)) {
                return repository;
            }
        }
        return null;
    }

    /**
     * Gets a {@link HttpClient} for the given connection parameters.
     *
     * @param params the connection parameters
     * @return the client, which may be multi-threaded and retrieved from a
     *         cache
     */
    public synchronized HttpClient getClient(Map<String, Serializable> params) {
        int i = 0;
        for (Map<String, Serializable> p : clientParams) {
            if ((params == null && p == null)
                    || (params != null && params.equals(p))) {
                return clients.get(i);
            }
            i++;
        }
        // create a new client
        HttpClient client = newClient(params);
        clients.add(client);
        clientParams.add(params);
        return client;
    }

    // used to load types
    protected HttpClient getDefaultClient() {
        return getClient(urlParams);
    }

    protected HttpClient newClient(Map<String, Serializable> params) {
        HttpClient client = new HttpClient();
        client.setHttpConnectionManager(new MultiThreadedHttpConnectionManager());
        Credentials credentials = getCredentials(params);
        if (credentials != null) {
            CredentialsProvider cp = new FixedCredentialsProvider(credentials);
            client.getState().setCredentials(AuthScope.ANY, credentials);
            client.getParams().setParameter(CredentialsProvider.PROVIDER, cp);
            client.getParams().setAuthenticationPreemptive(true);
        }
        return client;
    }

    protected Credentials getCredentials(Map<String, Serializable> params) {
        if (params == null) {
            return null;
        }
        String username = (String) params.get(Repository.PARAM_USERNAME);
        if (username == null) {
            return null;
        }
        String password = (String) params.get(Repository.PARAM_PASSWORD);
        return new UsernamePasswordCredentials(username, password);
    }

    /**
     * Simple credentials provider using fixed credentials. Other
     * implementations could query the user through a GUI.
     */
    public static class FixedCredentialsProvider implements CredentialsProvider {

        protected final Credentials credentials;

        public FixedCredentialsProvider(Credentials credentials) {
            this.credentials = credentials;
        }

        public Credentials getCredentials(AuthScheme scheme, String host,
                int port, boolean proxy) {
            return credentials;
        }
    }

}
