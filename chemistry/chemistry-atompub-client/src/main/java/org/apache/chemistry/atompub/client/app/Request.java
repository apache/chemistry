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
import java.util.List;

/**
 * An HTTP operation.
 */
public class Request {

    protected String url;

    protected List<String> headers;

    protected List<String> params;

    public Request(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setHeader(String key, String value) {
        if (headers == null) {
            headers = new ArrayList<String>();
        }
        headers.add(key);
        headers.add(value);
    }

    public void setParameter(String key, String value) {
        if (params == null) {
            params = new ArrayList<String>();
        }
        params.add(key);
        params.add(value);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getParameters() {
        return params;
    }

}
