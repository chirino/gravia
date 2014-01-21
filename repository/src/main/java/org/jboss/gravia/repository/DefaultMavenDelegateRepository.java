/*
 * #%L
 * Gravia :: Repository
 * %%
 * Copyright (C) 2012 - 2014 JBoss by Red Hat
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
package org.jboss.gravia.repository;

import org.jboss.gravia.repository.spi.AbstractMavenDelegateRepository;
import org.jboss.gravia.runtime.spi.PropertiesProvider;

/**
 * A simple {@link Repository} that delegates to maven repositories.
 *
 * @author thomas.diesler@jboss.com
 * @since 16-Jan-2012
 */
public class DefaultMavenDelegateRepository extends AbstractMavenDelegateRepository {

    public DefaultMavenDelegateRepository(PropertiesProvider propertyProvider) {
        super(propertyProvider);
    }
}
