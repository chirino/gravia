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
package org.jboss.gravia.repository.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Privileged actions used by this package.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 19-Jan-2010
 */
class SecurityActions {

    private SecurityActions() {
    }

    static String getSystemProperty(final String key, final String defaultValue) {
        if (System.getSecurityManager() == null) {
            String value = System.getProperty(key);
            return value != null ? value : defaultValue;
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    String value = System.getProperty(key);
                    return value != null ? value : defaultValue;
                }
            });
        }
    }
}
