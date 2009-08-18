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
import java.io.UnsupportedEncodingException;

import org.apache.chemistry.tck.atompub.http.Response;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;


/**
 * HttpClient implementation of Response
 */
public class HttpClientResponse implements Response {
    private HttpMethod method;

    public HttpClientResponse(HttpMethod method) {
        this.method = method;
    }

    public byte[] getContentAsByteArray() {
        try {
            return method.getResponseBody();
        } catch (IOException e) {
            return null;
        }
    }

    public String getContentAsString() throws UnsupportedEncodingException {
        try {
            return method.getResponseBodyAsString();
        } catch (IOException e) {
            return null;
        }
    }

    public String getContentType() {
        return getHeader("Content-Type");
    }

    public int getContentLength() {
        try {
            return method.getResponseBody().length;
        } catch (IOException e) {
            return 0;
        }
    }

    public String getHeader(String name) {
        Header header = method.getResponseHeader(name);
        return (header != null) ? header.getValue() : null;
    }

    public int getStatus() {
        return method.getStatusCode();
    }

}
