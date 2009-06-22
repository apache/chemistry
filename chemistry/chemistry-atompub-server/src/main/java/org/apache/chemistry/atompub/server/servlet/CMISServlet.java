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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.server.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryFactory;
import org.apache.chemistry.atompub.server.CMISProvider;

public class CMISServlet extends AbderaServlet {

    private static final long serialVersionUID = 1L;

    private Repository repository;

    public CMISServlet(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void init() throws ServletException {
        if (repository == null) {
            Map<String, String> params = new HashMap<String, String>();
            @SuppressWarnings("unchecked")
            Enumeration<String> names = getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                params.put(name, getInitParameter(name));
            }
            repository = createRepository(getServletContext(), params);
        }
        super.init();
    }

    private Repository createRepository(ServletContext context,
            Map<String, String> params) throws ServletException {

        String className = params.get("class");
        if (className == null) {
            String msg = "Repository factory expected in 'class' parameter.";
            throw new ServletException(msg);
        }
        RepositoryFactory factory = null;

        try {
            Class<?> c = Class.forName(className);
            factory = (RepositoryFactory) c.newInstance();
        } catch (Exception e) {
            String msg = "Unable to create repository factory class: "
                    + className;
            throw new ServletException(msg, e);
        }

        try {
            return factory.create(context, params);
        } catch (Exception e) {
            String msg = "Unable to create repository.";
            throw new ServletException(msg, e);
        }
    }

    @Override
    protected Provider createProvider() {
        Provider provider = new CMISProvider(repository);
        Abdera abdera = new Abdera();
        Map<String, String> properties = new HashMap<String, String>();
        provider.init(abdera, properties);
        return provider;
    }

}
