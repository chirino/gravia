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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;

import org.jboss.gravia.agent.Agent;
import org.jboss.gravia.process.api.ManagedProcess;
import org.jboss.gravia.process.api.MutableManagedProcess;
import org.jboss.gravia.process.api.ProcessIdentity;
import org.jboss.gravia.process.api.ProcessOptions;
import org.jboss.gravia.process.spi.ImmutableManagedProcess;
import org.jboss.gravia.process.spi.ProcessHandler;
import org.jboss.gravia.process.spi.SelfRegistrationHandler;
import org.jboss.gravia.runtime.LifecycleException;
import org.jboss.gravia.utils.IllegalStateAssertion;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * The agent controller
 *
 * @author thomas.diesler@jboss.com
 * @since 29-May-2014
 */
@Component(service = { Agent.class }, configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true)
public final class AgentService implements Agent {

    private final AtomicInteger processCount = new AtomicInteger();
    private final Set<ProcessHandler> processHandlers = new CopyOnWriteArraySet<>();
    private final Map<ProcessIdentity, Registration> registrations = new ConcurrentHashMap<>();
    private ConfigurationAdmin configAdmin;
    private MBeanServer mbeanServer;

    @Activate
    void activate(Map<String, Object> config) {
        activateInternal();
    }

    @Deactivate
    void deactivate() {
    }

    private void activateInternal() {

        // Register the self registration handler
        processHandlers.add(new SelfRegistrationHandler());
    }

    @Override
    public Set<String> getProcessHandlers() {
        Set<String> fqnames = new HashSet<>();
        for (ProcessHandler handler : processHandlers) {
            fqnames.add(handler.getClass().getName());
        }
        return fqnames;
    }

    @Override
    public Set<ProcessIdentity> getProcessIdentities() {
        return registrations.keySet();
    }

    @Override
    public ManagedProcess getManagedProcess(ProcessIdentity processId) {
        Registration preg = getRequiredRegistration(processId);
        return new ImmutableManagedProcess(preg.getManagedProcess());
    }

    @Override
    public ManagedProcess createProcess(ProcessOptions options) {
        MutableManagedProcess process = null;
        for (ProcessHandler handler : processHandlers) {
            if (handler.accept(options)) {
                String identitySpec = options.getIdentityPrefix();
                if (identitySpec.endsWith("#")) {
                    identitySpec += processCount.incrementAndGet();
                }
                process = handler.create(options, ProcessIdentity.create(identitySpec));
                registrations.put(process.getIdentity(), new Registration(handler, process));
                break;
            }
        }
        IllegalStateAssertion.assertNotNull(process, "No handler for: " + options);
        return process;
    }

    @Override
    public void startProcess(ProcessIdentity processId) throws LifecycleException {
        Registration preg = getRequiredRegistration(processId);
        MutableManagedProcess process = preg.getManagedProcess();
        preg.getProcessHandler().start(process);
    }

    @Override
    public void stopProcess(ProcessIdentity processId) throws LifecycleException {
        Registration preg = getRequiredRegistration(processId);
        MutableManagedProcess process = preg.getManagedProcess();
        preg.getProcessHandler().stop(process);
    }

    @Override
    public void destroyProcess(ProcessIdentity processId) {
        Registration preg = getRequiredRegistration(processId);
        MutableManagedProcess process = preg.getManagedProcess();
        preg.getProcessHandler().destroy(process);
    }

    private Registration getRequiredRegistration(ProcessIdentity processId) {
        Registration preg = registrations.get(processId);
        IllegalStateAssertion.assertNotNull(preg, "Process not registered: " + processId);
        return preg;
    }

    @Reference
    void bindConfigurationAdmin(ConfigurationAdmin service) {
        configAdmin = service;
    }

    void unbindConfigurationAdmin(ConfigurationAdmin service) {
        configAdmin = null;
    }

    @Reference
    void bindMBeanServer(MBeanServer service) {
        mbeanServer = service;
    }

    void unbindMBeanServer(MBeanServer service) {
        mbeanServer = null;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    void bindProcessHandler(ProcessHandler service) {
        processHandlers.add(service);
    }
    void unbindProcessHandler(ProcessHandler service) {
        processHandlers.remove(service);
    }

    static class Registration {

        private final ProcessHandler handler;
        private MutableManagedProcess process;

        Registration(ProcessHandler handler, MutableManagedProcess process) {
            this.handler = handler;
            this.process = process;
        }

        ProcessHandler getProcessHandler() {
            return handler;
        }

        MutableManagedProcess getManagedProcess() {
            return process;
        }
    }
}
