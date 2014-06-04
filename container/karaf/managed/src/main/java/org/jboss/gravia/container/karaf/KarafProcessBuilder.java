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

package org.jboss.gravia.container.karaf;

import org.jboss.gravia.process.spi.AbstractProcessBuilder;



/**
 * The managed container configuration builder
 *
 * @author thomas.diesler@jboss.com
 * @since 14-Apr-2014
 */
public final class KarafProcessBuilder extends AbstractProcessBuilder<KarafProcessBuilder, KarafProcessOptions> {

    public static KarafProcessBuilder create() {
        return new KarafProcessBuilder();
    }

    private KarafProcessBuilder() {
        super(new KarafProcessOptions());
    }

    public KarafProcessBuilder rmiServerPort(int serverPort) {
        options.setRmiServerPort(serverPort);
        return this;
    }

    public KarafProcessBuilder rmiRegistryPort(int registryPort) {
        options.setRmiRegistryPort(registryPort);
        return this;
    }

    public KarafProcessBuilder httpPort(int httpPort) {
        options.setHttpPort(httpPort);
        return this;
    }

    public KarafProcessBuilder httpsPort(int httpsPort) {
        options.setHttpsPort(httpsPort);
        return this;
    }
}
