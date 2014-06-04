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
package org.jboss.gravia.process.spi;

import org.jboss.gravia.process.api.ManagedProcess;
import org.jboss.gravia.process.api.ProcessIdentity;
import org.jboss.gravia.process.api.ProcessOptions;
import org.jboss.gravia.runtime.LifecycleException;


/**
 * The process handler
 *
 * @author thomas.diesler@jboss.com
 * @since 29-May-2014
 */
public interface ProcessHandler {

    boolean accept(ProcessOptions options);

    ManagedProcess create(ProcessOptions options, ProcessIdentity identity);

    void start(ManagedProcess process) throws LifecycleException;

    void stop(ManagedProcess process) throws LifecycleException;

    void destroy(ManagedProcess process);
}
