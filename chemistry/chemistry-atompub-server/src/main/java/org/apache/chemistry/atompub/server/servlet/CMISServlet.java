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

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.server.CMISProvider;

public class CMISServlet extends AbderaServlet {

    private static final long serialVersionUID = 1L;

    protected Repository repository;

    /**
     * Empty constructor required by servlet spec.
     */
    public CMISServlet() {
    }

    public CMISServlet(Repository repository) {
        this.repository = repository;
    }

    @Override
    protected Provider createProvider() {
        Provider provider = new CMISProvider(getRepository());
        Abdera abdera = new Abdera();
        Map<String, String> properties = new HashMap<String, String>();
        provider.init(abdera, properties);
        return provider;
    }

    /**
     * Return the repository. Allows subclasses to create the repository if necessary.
     *
     * @return repository
     */
    protected Repository getRepository() {
        return repository;
    }
}
