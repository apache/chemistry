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
 *     Florian Mueller, Open Text
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.soap.server;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * Extracts a username and password from the WSS UsernameToken of the SOAP
 * request.
 * <p>
 * This information is then stored in a CallContext object of the global
 * SOAPMessageContext for later retrieval by methods that need it.
 * <p>
 * This class should be registered in the handler-chains for each SOAP endpoint.
 * This often happens in the {@code sun-jaxws.xml} file.
 */
public class AuthHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    public static final QName WSSE_SECURITY = new QName(WSSE_NS, "Security");

    public static final QName WSSE_USERNAME_TOKEN = new QName(WSSE_NS,
            "UsernameToken");

    public static final QName WSSE_USERNAME = new QName(WSSE_NS, "Username");

    public static final QName WSSE_PASSWORD = new QName(WSSE_NS, "Password");

    public static final Set<QName> HEADERS = Collections.singleton(WSSE_SECURITY);

    public Set<QName> getHeaders() {
        return HEADERS;
    }

    public boolean handleMessage(SOAPMessageContext soapContext) {
        if (Boolean.TRUE.equals(soapContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
            return handleOutboundMessage(soapContext);
        } else {
            return handleInboundMessage(soapContext);
        }
    }

    protected boolean handleInboundMessage(SOAPMessageContext soapContext) {
        CallContext callContext = new CallContext();
        try {
            extractUsernamePassword(soapContext, callContext);
        } catch (NoSuchElementException e) {
            // cannot get UsernameToken
            callContext.setUsername("");
            callContext.setPassword("");
        }
        callContext.setInMessageContext(soapContext);
        return true; // continue processing
    }

    /**
     * Gets the username and password from the UsernameToken on the
     * {@link SOAPMessageContext} and stores them in the {@link CallContext}.
     *
     * @throws NoSuchElementException if the token cannot be found
     */
    protected void extractUsernamePassword(SOAPMessageContext soapContext,
            CallContext callContext) throws NoSuchElementException {
        SOAPHeader sh;
        try {
            sh = soapContext.getMessage().getSOAPHeader();
        } catch (SOAPException e) {
            throw new RuntimeException("Cannot get SOAP header", e);
        }
        // NoSuchElementException may be thrown by next()
        SOAPElement security = (SOAPElement) sh.getChildElements(WSSE_SECURITY).next();
        SOAPElement token = (SOAPElement) security.getChildElements(
                WSSE_USERNAME_TOKEN).next();
        SOAPElement usernameElement = (SOAPElement) token.getChildElements(
                WSSE_USERNAME).next();
        SOAPElement passwordElement = (SOAPElement) token.getChildElements(
                WSSE_PASSWORD).next();
        String username = usernameElement.getValue();
        String password = passwordElement.getValue();
        if (username == null || password == null) {
            throw new NoSuchElementException();
        }
        callContext.setUsername(username);
        callContext.setPassword(password);
    }

    protected boolean handleOutboundMessage(SOAPMessageContext soapContext) {
        return true; // continue processing
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true; // continue processing
    }

    public void close(MessageContext context) {
    }

}
