/*
 * #%L
 * Gravia :: Container :: WildFly :: Extension
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
package org.wildfly.extension.gravia.service;

import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.gravia.resource.Attachable;
import org.jboss.gravia.resource.AttachmentKey;
import org.jboss.gravia.runtime.Module;
import org.jboss.gravia.runtime.embedded.internal.EmbeddedRuntime;
import org.jboss.gravia.runtime.spi.ModuleEntriesProvider;
import org.jboss.gravia.runtime.spi.PropertiesProvider;

/**
 * The WildFly {@link Runtime}
 *
 * @author thomas.diesler@jboss.com
 * @since 27-Sep-2013
 */
public class WildflyRuntime extends EmbeddedRuntime {

    public static AttachmentKey<ResourceRoot> DEPLOYMENT_ROOT_KEY = AttachmentKey.create(ResourceRoot.class);

    public WildflyRuntime(PropertiesProvider propertiesProvider, Attachable context) {
        super(propertiesProvider, context);
    }

    @Override
    protected ModuleEntriesProvider getDefaultEntriesProvider(Module module, Attachable context) {
        ResourceRoot resourceRoot = context.getAttachment(WildflyRuntime.DEPLOYMENT_ROOT_KEY);
        return resourceRoot != null ? new VirtualFileEntriesProvider(resourceRoot) : null;
    }
}
