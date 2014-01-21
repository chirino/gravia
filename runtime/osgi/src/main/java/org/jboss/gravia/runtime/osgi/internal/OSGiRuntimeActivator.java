/*
 * #%L
 * Gravia :: Runtime :: OSGi
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
package org.jboss.gravia.runtime.osgi.internal;

import org.jboss.gravia.runtime.Runtime;
import org.jboss.gravia.runtime.osgi.OSGiRuntimeLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;

/**
 * Activate the OSGi Runtime
 *
 * @author thomas.diesler@jboss.com
 * @since 27-Sep-2013
 */
public final class OSGiRuntimeActivator implements BundleActivator {

    private BundleTracker<Bundle> tracker;

    @Override
    public void start(BundleContext context) throws Exception {
        BundleContext syscontext = context.getBundle(0).getBundleContext();
        Runtime runtime = OSGiRuntimeLocator.createRuntime(syscontext);
        runtime.init();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (tracker != null) {
            tracker.close();
        }
        OSGiRuntimeLocator.releaseRuntime();
    }

}
