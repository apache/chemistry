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
package org.apache.chemistry.atompub.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;

/**
 * Regex target resolver that doesn't need an explicit servlet path in the
 * patterns.
 * <p>
 * It checks the patterns using only the part of the path *after* the servlet
 * path.
 */
public class ServletRegexTargetResolver extends RegexTargetResolver {

    @Override
    public Target resolve(Request request) {
        RequestContext context = (RequestContext) request;
        String uri = context.getUri().toString();
        String spath = context.getTargetBasePath();
        String path = spath == null ? uri : uri.substring(spath.length());
        for (Pattern pattern : patterns.keySet()) {
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                TargetType type = this.patterns.get(pattern);
                String[] fields = this.fields.get(pattern);
                return getTarget(type, context, matcher, fields);
            }
        }
        return null;
    }

}
