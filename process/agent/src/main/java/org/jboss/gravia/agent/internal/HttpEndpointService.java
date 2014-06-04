/*
 * #%L
 * Gravia :: Resolver
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
package org.jboss.gravia.agent.internal;

import static org.jboss.gravia.agent.internal.AgentLogger.LOGGER;

import java.util.Map;

import org.jboss.gravia.agent.Agent;
import org.jboss.gravia.runtime.ModuleContext;
import org.jboss.gravia.runtime.RuntimeLocator;
import org.jboss.gravia.runtime.ServiceReference;
import org.jboss.gravia.runtime.ServiceTracker;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

/**
 * The agent Http endpoint service
 *
 * @author thomas.diesler@jboss.com
 * @since 29-May-2014
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true)
public final class HttpEndpointService {

    private Agent agent;
    private ServiceTracker<?, ?> httpTracker;

    @Activate
    void activate(Map<String, Object> config) {
        activateInternal();
    }

    @Deactivate
    void deactivate() {
        if (httpTracker != null) {
            httpTracker.close();
        }
    }

    private void activateInternal() {
        ModuleContext syscontext = RuntimeLocator.getRequiredRuntime().getModuleContext();
        httpTracker = new ServiceTracker<HttpService, HttpService>(syscontext, HttpService.class.getName(), null) {

            public HttpService addingService(ServiceReference<HttpService> sref) {
                HttpService service = super.addingService(sref);
                try {
                    service.registerServlet("/agent", new AgentServlet(agent), null, null);
                    // [TODO] compute actual endpoint url
                    LOGGER.info("Agent HttpEndpoint registered: http://localhost:8080/agent");
                } catch (Exception ex) {
                    LOGGER.error("Cannot register agent servlet", ex);
                }
                return service;
            }

            public void removedService(ServiceReference<HttpService> sref, HttpService service) {
                service.unregister("/agent");
                LOGGER.info("Agent HttpEndpoint unregistered");
            }
        };
        httpTracker.open();
    }

    @Reference
    void bindAgent(Agent service) {
        agent = service;
    }
    void unbindAgent(Agent service) {
        agent = null;
    }
}
