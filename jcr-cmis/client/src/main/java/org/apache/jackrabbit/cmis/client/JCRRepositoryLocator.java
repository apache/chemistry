/*
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
 */
package org.apache.jackrabbit.cmis.client;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.jackrabbit.cmis.Repository;
import org.apache.jackrabbit.cmis.jcr.JCRRepository;
import org.apache.jackrabbit.rmi.client.ClientRepositoryFactory;

public abstract class JCRRepositoryLocator {

    public static Repository getRepository(ServletContext context)
            throws ServletException {

        String url = context.getInitParameter("url");
        String workspace = context.getInitParameter("workspace");

        if (url == null || workspace == null) {
            String msg = "Init parameter 'url' or 'workspace' are not specified.";
            throw new UnavailableException(msg);
        }

        try {
            return getRepository(url, workspace);
        } catch (Exception e) {
            String msg = "Unable to obtain repository.";
            UnavailableException ue = new UnavailableException(msg);
            ue.initCause(e);
            throw ue;
        }
    }

    public static Repository getRepository(String url, String workspace)
            throws Exception {

        if (url.startsWith("rmi:")) {
            return getRMIRepository(url, workspace);
        }
        String msg = "Unknown protocol: " + url;
        throw new IllegalArgumentException(msg);
    }

    private static Repository getRMIRepository(String url, String workspace)
            throws Exception {

        ClientRepositoryFactory factory = new ClientRepositoryFactory();
        return new JCRRepository(factory.getRepository(url), workspace);
    }
}
