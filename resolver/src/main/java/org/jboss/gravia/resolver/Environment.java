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
package org.jboss.gravia.resolver;

import java.util.Map;

import org.jboss.gravia.resource.Resource;
import org.jboss.gravia.resource.ResourceStore;
import org.jboss.gravia.runtime.Wiring;

/**
 * A resource store that represents the runtime wiring
 * information of installed resources.
 *
 * @author thomas.diesler@jboss.com
 * @since 06-May-2013
 */
public interface Environment extends ResourceStore {

    Environment cloneEnvironment();

    Map<Resource, Wiring> getWirings();
}
