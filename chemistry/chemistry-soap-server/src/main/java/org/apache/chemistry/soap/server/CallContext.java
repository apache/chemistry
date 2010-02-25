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
package org.apache.chemistry.soap.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;

/**
 * Holds context data about the current call.
 */
public class CallContext extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String CALL_CONTEXT = "org.apache.chemistry.callcontext";

    public void setUsername(String username) {
        put(USERNAME, username);
    }

    public String getUsername() {
        return get(USERNAME);
    }

    public void setPassword(String password) {
        put(PASSWORD, password);
    }

    public String getPassword() {
        return get(PASSWORD);
    }

    public Map<String, Serializable> toMap() {
        return new HashMap<String, Serializable>(this);
    }

    /**
     * Stores this call context in a {@link MessageContext}.
     *
     * @param mcontext the message context in which to store
     */
    public void setInMessageContext(MessageContext mcontext) {
        mcontext.put(CALL_CONTEXT, this);
        mcontext.setScope(CALL_CONTEXT, Scope.APPLICATION);
    }

    /**
     * Retrieves the call context from the {@link MessageContext}.
     *
     * @param messageContext the message context
     * @return the call context
     */
    public static CallContext fromMessageContext(MessageContext messageContext) {
        CallContext callContext = (CallContext) messageContext.get(CALL_CONTEXT);
        if (callContext == null) {
            callContext = new CallContext();
            // Principal principal = context.getUserPrincipal();
            // if (principal != null) {
            // callContext.setUsername(principal.getName());
            // }
        }
        return callContext;
    }

    public static Map<String, Serializable> mapFromWebServiceContext(
            WebServiceContext wscontext) {
        return CallContext.fromMessageContext(wscontext.getMessageContext()).toMap();
    }

}
