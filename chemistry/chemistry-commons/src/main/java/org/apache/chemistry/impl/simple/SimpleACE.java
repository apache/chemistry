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
package org.apache.chemistry.impl.simple;

import org.apache.chemistry.ACE;

public class SimpleACE implements ACE {

    protected final String permission;

    protected final String principal;

    protected final boolean direct;

    public SimpleACE(String permission, String principal, boolean direct) {
        this.permission = permission;
        this.principal = principal;
        this.direct = direct;
    }

    public String getPermission() {
        return permission;
    }

    public String getPrincipal() {
        return principal;
    }

    public boolean isDirect() {
        return direct;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + permission + ','
                + principal + ',' + direct + ')';
    }

}
