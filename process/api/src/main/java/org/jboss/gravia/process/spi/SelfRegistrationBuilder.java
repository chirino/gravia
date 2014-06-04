/*
 * #%L
 * Fabric8 :: SPI
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
package org.jboss.gravia.process.spi;


/**
 * The self registration process builder
 *
 * @author thomas.diesler@jboss.com
 * @since 04-Jun-2014
 */
public class SelfRegistrationBuilder extends AbstractProcessBuilder<SelfRegistrationBuilder, SelfRegistrationOptions> {

    public SelfRegistrationBuilder() {
        super(new SelfRegistrationOptions());
    }

}
