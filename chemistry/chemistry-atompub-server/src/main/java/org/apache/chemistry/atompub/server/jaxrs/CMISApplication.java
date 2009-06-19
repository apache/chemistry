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
package org.apache.chemistry.atompub.server.jaxrs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * A JAX-RS Application registering the relevant classes.
 * <p>
 * This is registered through implementation mechanism depending on the JAX-RS
 * implementation.
 * <p>
 * For CXF, a CXFNonSpringJaxrsServlet servlet must be registered with this
 * class as value of parameter javax.ws.rs.Application.
 */
public class CMISApplication extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList( //
        AbderaResource.class //
        ));
    }

    /*
     * CXFNonSpringJaxrsServlet expects Providers only as singletons...
     */
    @Override
    public Set<Object> getSingletons() {
        return new HashSet<Object>(Arrays.asList( //
        new AbderaResponseProvider() //
        ));
    }

}
