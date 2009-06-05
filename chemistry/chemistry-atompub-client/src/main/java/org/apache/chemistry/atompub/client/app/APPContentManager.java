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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.Credentials;
import org.apache.chemistry.atompub.client.CredentialsProvider;
import org.apache.chemistry.atompub.client.NoSuchRepositoryException;
import org.apache.chemistry.atompub.client.app.httpclient.HttpClientConnector;
import org.apache.chemistry.atompub.client.common.atom.ReadContext;

/**
 *
 */
public class APPContentManager implements ContentManager {

    protected String baseUrl;

    protected Connector connector;

    protected Repository[] repos;

    protected CredentialsProvider login;

    protected IOProvider ioProvider;

    protected static ThreadLocal<List<CredentialsProvider>> loginStack = new ThreadLocal<List<CredentialsProvider>>();

    protected static Map<Class<?>, Class<?>> services = new Hashtable<Class<?>, Class<?>>();

    public APPContentManager(String url) {
        this(url, null);
    }

    protected APPContentManager(String url, Connector connector) {
        this.baseUrl = url;
        this.connector = connector;
        initialize();
    }

    protected void initialize() {
        if (connector == null) {
            connector = createConnector();
        }
    }

    protected Connector createConnector() {
        return new HttpClientConnector(this);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Connector getConnector() {
        return connector;
    }

    public Repository[] getRepositories() throws ContentManagerException {
        if (repos == null) {
            Request req = new Request(getBaseUrl());
            Response resp = connector.get(req);
            if (!resp.isOk()) {
                throw new ContentManagerException(
                        "Remote server returned error code: "
                                + resp.getStatusCode());
            }
            ReadContext ctx = new ReadContext();
            ctx.put(APPContentManager.class, this);
            repos = resp.getServiceDocument(ctx);
        }
        return repos;
    }

    public Repository getRepository(String id)
            throws NoSuchRepositoryException, ContentManagerException {
        for (Repository repository : getRepositories()) {
            if (repository.getId().equals(id)) {
                return repository;
            }
        }
        throw new NoSuchRepositoryException(baseUrl, id);
    }

    public Repository getDefaultRepository() throws ContentManagerException {
        Repository[] repos = getRepositories();
        if (repos != null && repos.length > 0) {
            return repos[0];
        }
        throw new NoSuchRepositoryException(baseUrl, "default");
    }

    public void refresh() {
        repos = null;
    }

    public void login(String username, String pass) {
        login = new DefaultCredentialsProvider(username, pass.toCharArray());
    }

    public void pushLogin(String username, String pass) {
        List<CredentialsProvider> stack = loginStack.get();
        if (stack == null) {
            stack = new ArrayList<CredentialsProvider>();
            loginStack.set(stack);
        }
        stack.add(new DefaultCredentialsProvider(username, pass.toCharArray()));
    }

    public void popLogin() {
        List<CredentialsProvider> stack = loginStack.get();
        if (stack != null && !stack.isEmpty()) {
            stack.remove(stack.size() - 1);
        }
    }

    public void logout() {
        login = null;
    }

    public Credentials getCurrentLogin() {
        List<CredentialsProvider> stack = loginStack.get();
        return stack == null || stack.isEmpty() ? login.getCredentials()
                : stack.get(stack.size() - 1).getCredentials();
    }

    public CredentialsProvider getCredentialsProvider() {
        return login;
    }

    public void setCredentialsProvider(CredentialsProvider provider) {
        login = provider;
    }

    public IOProvider getIO() {
        if (ioProvider == null) {
            ioProvider = new DefaultIOProvider();
        }
        return ioProvider;
    }

    public void setIO(IOProvider readers) {
        this.ioProvider = readers;
    }

    public static void registerService(Class<?> itf, Class<?> impl) {
        services.put(itf, impl);
    }

    public static void unregisterService(Class<?> itf) {
        services.remove(itf);
    }

    public static Class<?> getServiceClass(Class<?> itf) {
        return services.get(itf);
    }

}
