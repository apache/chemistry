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
 *     David Caruana, Alfresco
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.tck.atompub.http.httpclient;

import java.io.IOException;
import java.util.Map;

import org.apache.chemistry.tck.atompub.http.Connection;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;


/**
 * HttpClient implementation of Http Connection
 */
public class HttpClientConnection implements Connection {
    private HttpClient httpClient;

    public HttpClientConnection(String username, String password) {
        httpClient = new HttpClient();
        if (username != null) {
            httpClient.getParams().setBooleanParameter(HttpClientParams.PREEMPTIVE_AUTHENTICATION, true);
            httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(username, password));
        }
    }

    public Response executeRequest(Request req) throws IOException {
        // construct method
        HttpMethod httpMethod = null;
        String method = req.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            GetMethod get = new GetMethod(req.getFullUri());
            httpMethod = get;
        } else if (method.equalsIgnoreCase("POST")) {
            PostMethod post = new PostMethod(req.getFullUri());
            post.setRequestEntity(new ByteArrayRequestEntity(req.getBody(), req.getType()));
            httpMethod = post;
        } else if (method.equalsIgnoreCase("PATCH")) {
            HttpClientPatchMethod post = new HttpClientPatchMethod(req.getFullUri());
            post.setRequestEntity(new ByteArrayRequestEntity(req.getBody(), req.getType()));
            httpMethod = post;
        } else if (method.equalsIgnoreCase("PUT")) {
            PutMethod put = new PutMethod(req.getFullUri());
            put.setRequestEntity(new ByteArrayRequestEntity(req.getBody(), req.getType()));
            httpMethod = put;
        } else if (method.equalsIgnoreCase("DELETE")) {
            DeleteMethod del = new DeleteMethod(req.getFullUri());
            httpMethod = del;
        } else {
            throw new RuntimeException("Http Method " + method + " not supported");
        }
        if (req.getHeaders() != null) {
            for (Map.Entry<String, String> header : req.getHeaders().entrySet()) {
                httpMethod.setRequestHeader(header.getKey(), header.getValue());
            }
        }

        // execute
        httpClient.executeMethod(httpMethod);
        Response res = new HttpClientResponse(httpMethod);
        return res;
    }
}
