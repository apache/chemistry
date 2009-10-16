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
package org.apache.chemistry.tck.atompub.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Request
 */
public class Request {
    
    private String method;
    private String uri;
    private Map<String, String> args;
    private Map<String, String> headers;
    private byte[] body;
    private String encoding = "UTF-8";
    private String contentType;

    public Request(Request req) {
        this.method = req.method;
        this.uri = req.uri;
        this.args = req.args;
        this.headers = req.headers;
        this.body = req.body;
        this.encoding = req.encoding;
        this.contentType = req.contentType;
    }

    public Request(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getFullUri() {
        // calculate full uri
        String fullUri = uri == null ? "" : uri;
        if (args != null && args.size() > 0) {
            char prefix = (uri.indexOf('?') == -1) ? '?' : '&';
            for (Map.Entry<String, String> arg : args.entrySet()) {
                // TODO: fix up url encoding
                try {
                    fullUri += prefix + arg.getKey() + "=" + (arg.getValue() == null ? "" : URLEncoder.encode(arg.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {}
                prefix = '&';
            }
        }

        return fullUri;
    }

    public Request setArgs(Map<String, String> args) {
        this.args = args;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public Request setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Request setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public Request setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public Request setType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getType() {
        return contentType;
    }
}
