/*
 * #%L
 * Gravia :: Runtime :: API
 * %%
 * Copyright (C) 2013 - 2014 JBoss by Red Hat
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
package org.jboss.gravia.runtime.spi;

import static org.jboss.gravia.Constants.URL_HANDLER_PROTOCOL;
import static org.jboss.gravia.runtime.spi.RuntimeLogger.LOGGER;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.gravia.runtime.ModuleContext;
import org.jboss.gravia.runtime.ServiceReference;
import org.jboss.gravia.runtime.ServiceTracker;
import org.jboss.gravia.utils.IllegalStateAssertion;

/**
 * Allows dynamic registration of URL stream handlers.
 *
 * @see URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)
 *
 * @author thomas.diesler@jboss.com
 * @since 16-May-2014
 *
 * @ThreadSafe
 */
public class URLStreamHandlerTracker implements URLStreamHandlerFactory {

    // The VM maitains a static mapping of protocol to handler. We register a proxy that can get updated
    private static final Map<String, URLStreamHandlerProxy> handlers = new ConcurrentHashMap<>();

    private final ServiceTracker<?, ?> tracker;

    /**
     * Get an instance URLStreamHandlerTracker
     */
    public URLStreamHandlerTracker(ModuleContext context) {
        tracker = new ServiceTracker<URLStreamHandler, URLStreamHandler>(context, URLStreamHandler.class, null) {

            @Override
            public URLStreamHandler addingService(ServiceReference<URLStreamHandler> sref) {
                URLStreamHandler handler = super.addingService(sref);
                String protocol = getRequiredProtocol(sref);
                URLStreamHandlerProxy proxy = handlers.get(protocol);
                if (proxy == null) {
                    proxy = new URLStreamHandlerProxy(protocol, sref, handler);
                    handlers.put(protocol, proxy);
                } else {
                    proxy.update(sref, handler);
                }
                addingHandler(protocol, sref, handler);
                return handler;
            }

            @Override
            public void removedService(ServiceReference<URLStreamHandler> sref, URLStreamHandler handler) {
                String protocol = getRequiredProtocol(sref);
                URLStreamHandlerProxy proxy = handlers.get(protocol);
                if (proxy != null && proxy.getServiceReference().equals(sref)) {
                    removingHandler(protocol, sref, handler);
                    proxy.update(null,  null);
                    ServiceReference<URLStreamHandler>[] srefs = getServiceReferences();
                    if (srefs != null) {
                        for (ServiceReference<URLStreamHandler> aux : srefs) {
                            if (aux != sref && protocol.equals(getProtocol(aux))) {
                                proxy.update(aux, getService(aux));
                                break;
                            }
                        }
                    }
                }
                super.removedService(sref, handler);
            }
        };
    }

    protected void addingHandler(String protocol, ServiceReference<URLStreamHandler> sref, URLStreamHandler handler) {
        // do nothing
    }

    protected void removingHandler(String protocol, ServiceReference<URLStreamHandler> sref, URLStreamHandler handler) {
        // do nothing
    }

    public void open() {
        tracker.open();
    }

    public void close() {
        tracker.close();
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        URLStreamHandlerProxy proxy = handlers.get(protocol);
        return proxy != null && proxy.getDelegate() != null ? proxy : null;
    }

    private String getProtocol(ServiceReference<URLStreamHandler> sref) {
        return (String) sref.getProperty(URL_HANDLER_PROTOCOL);
    }

    private String getRequiredProtocol(ServiceReference<URLStreamHandler> sref) {
        String protocol = getProtocol(sref);
        IllegalStateAssertion.assertNotNull(protocol, "Cannot obtain property '" + URL_HANDLER_PROTOCOL + "' from: " + sref);
        return protocol;
    }

    /**
     * The URL class caches the handler associated with a given protocol.
     * We therfore need to provide a proxy that can get updated as
     * URLStreamHandler service come/go.
     *
     * Note, this proxy does not delegate all URLStreamHandler methods
     */
    static class URLStreamHandlerProxy extends URLStreamHandler {

        private final String protocol;
        private ServiceReference<URLStreamHandler> sref;
        private URLStreamHandler delegate;

        URLStreamHandlerProxy(String protocol, ServiceReference<URLStreamHandler> sref, URLStreamHandler delegate) {
            this.protocol = protocol;
            this.delegate = delegate;
            this.sref = sref;
        }

        synchronized void update(ServiceReference<URLStreamHandler> sref, URLStreamHandler delegate) {
            LOGGER.debug("Update URLStreamHandler for '{}' with: {}", protocol, delegate);
            this.delegate = delegate;
            this.sref = sref;
        }

        synchronized URLStreamHandler getDelegate() {
            return delegate;
        }

        synchronized ServiceReference<URLStreamHandler> getServiceReference() {
            return sref;
        }

        @Override
        protected synchronized URLConnection openConnection(URL url) throws IOException {
            if (delegate == null)
                throw new IOException("URLStreamHandler unregistered");
            return new URL(null, url.toExternalForm(), delegate).openConnection();
        }
    }
}
