/*
 * #%L
 * Gravia :: Runtime :: API
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
package org.jboss.gravia.runtime.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The logger provider
 *
 * @author thomas.diesler@jboss.com
 * @since 27-Sep-2013
 */
public final class RuntimeLogger {

    public static final Logger LOGGER = LoggerFactory.getLogger("org.jboss.gravia.runtime");

    // Hide ctor
    private RuntimeLogger() {
    }

}
