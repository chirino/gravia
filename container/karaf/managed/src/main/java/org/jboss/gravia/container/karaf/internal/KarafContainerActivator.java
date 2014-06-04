/*
 * #%L
 * Fabric8 :: Container :: Karaf :: Managed
 * %%
 * Copyright (C) 2014 Red Hat
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

package org.jboss.gravia.container.karaf.internal;

import org.jboss.gravia.container.karaf.KarafProcessHandler;
import org.jboss.gravia.process.spi.ProcessHandler;
import org.jboss.gravia.runtime.ModuleActivator;
import org.jboss.gravia.runtime.ModuleContext;
import org.jboss.gravia.runtime.ServiceRegistration;

/**
 * The Karaf container activator
 *
 * @author thomas.diesler@jboss.com
 * @since 14-Apr-2014
 */
public final class KarafContainerActivator implements ModuleActivator {

    private ServiceRegistration<?> registration;

    @Override
    public void start(ModuleContext context) throws Exception {
        registration = context.registerService(ProcessHandler.class, new KarafProcessHandler(), null);
    }

    @Override
    public void stop(ModuleContext context) throws Exception {
        registration.unregister();
    }
}
