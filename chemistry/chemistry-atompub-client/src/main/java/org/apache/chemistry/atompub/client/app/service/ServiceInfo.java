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
package org.apache.chemistry.atompub.client.app.service;

import java.lang.reflect.Constructor;

import org.apache.chemistry.atompub.client.app.APPContentManager;

/**
 *
 */
public class ServiceInfo {

    public String id;

    public String title;

    public String summary;

    public String href;

    protected Class<?> itf;

    protected Constructor<?> ctor;

    protected boolean isSingleton;

    protected boolean requiresConnection;

    public ServiceInfo() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getHref() {
        return href;
    }

    public boolean isSingleton() {
        getServiceCtor();
        return isSingleton;
    }

    public boolean requiresConnection() {
        getServiceCtor();
        return requiresConnection;
    }

    public Constructor<?> getServiceCtor() {
        if (ctor == null) {
            try {
                Class<?> itf = Class.forName(id);
                Class<?> clazz = APPContentManager.getServiceClass(itf);
                if (clazz == null) { // no service registered for the given
                                     // interface
                    return null;
                }
                ExtensionService anno = clazz.getAnnotation(ExtensionService.class);
                if (anno == null) {
                    throw new IllegalStateException("Class " + clazz
                            + " is not an extension service!");
                }
                requiresConnection = anno.requiresConnection();
                isSingleton = anno.singleton();
                ctor = clazz.getConstructor(new Class<?>[] { ServiceContext.class });
            } catch (Exception e) {
                e.printStackTrace(); // TODO handle errors
            }
        }
        return ctor;
    }

    public Class<?> getServiceClass() {
        Constructor<?> ctor = getServiceCtor();
        return ctor == null ? null : ctor.getDeclaringClass();
    }

    public Class<?> getInterfaceClass() {
        return itf;
    }

    public Object newInstance(ServiceContext ctx) {
        Constructor<?> ctor = getServiceCtor();
        if (ctor == null) {
            return null;
        }
        try {
            return ctor.newInstance(new Object[] { ctx });
        } catch (Exception e) {
            e.printStackTrace();// TODO handle errors
            return null;
        }
    }
}
