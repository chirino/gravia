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

import org.apache.catalina.loader.WebappClassLoader;

/**
 * The shared module aware {@link WebappClassLoader}.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 13-Jan-2014
 */
public class SharedModuleAwareWebappClassLoader extends WebappClassLoader {

    public SharedModuleAwareWebappClassLoader(ClassLoader parent) {
        super(new SharedModuleClassLoader(parent));
    }
}
