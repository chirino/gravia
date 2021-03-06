/*
 * #%L
 * Gravia :: Container :: Tomcat :: Extension
 * %%
 * Copyright (C) 2010 - 2014 JBoss by Red Hat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.jboss.gravia.container.tomcat.extension;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.jboss.gravia.resource.Capability;
import org.jboss.gravia.resource.ContentCapability;
import org.jboss.gravia.resource.ContentNamespace;
import org.jboss.gravia.resource.Resource;
import org.jboss.gravia.utils.IllegalArgumentAssertion;

/**
 * The shared module {@link ClassLoader}.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 13-Jan-2014
 */
public final class SharedModuleClassLoader extends ClassLoader {

    private final static List<URL> urls = new ArrayList<URL>();

    SharedModuleClassLoader(ClassLoader parent) {
        super(getExtendedParent(parent));
    }

    private static ClassLoader getExtendedParent(ClassLoader parent) {
        synchronized (urls) {
            if (urls.isEmpty()) {
                return parent;
            } else {
                URL[] urlarr = urls.toArray(new URL[urls.size()]);
                return new URLClassLoader(urlarr, parent);
            }
        }
    }

    public static void addSharedModule(Resource resource) {
        URL contentURL = getRequiredContentURL(resource);
        synchronized (urls) {
            urls.add(contentURL);
        }
    }

    public static void removeSharedModule(Resource resource) {
        URL contentURL = getContentURL(resource);
        if (contentURL != null) {
            synchronized (urls) {
                urls.remove(contentURL);
            }
        }
    }

    private static URL getContentURL(Resource resource) {
        IllegalArgumentAssertion.assertNotNull(resource, "resource");

        List<Capability> ccaps = resource.getCapabilities(ContentNamespace.CONTENT_NAMESPACE);
        if (ccaps.size() != 1)
            return null;

        return ccaps.get(0).adapt(ContentCapability.class).getContentURL();
    }

    private static URL getRequiredContentURL(Resource resource) {
        IllegalArgumentAssertion.assertNotNull(resource, "resource");

        List<Capability> ccaps = resource.getCapabilities(ContentNamespace.CONTENT_NAMESPACE);
        IllegalArgumentAssertion.assertFalse(ccaps.isEmpty(), "Cannot obtain content capability from: " + resource);
        IllegalArgumentAssertion.assertFalse(ccaps.size() > 1, "Cannot process multiple content capabilities in: " + resource);

        URL contentURL = ccaps.get(0).adapt(ContentCapability.class).getContentURL();
        IllegalArgumentAssertion.assertTrue(contentURL != null, "Cannot obtain content URL from: " + resource);
        return contentURL;
    }
}
