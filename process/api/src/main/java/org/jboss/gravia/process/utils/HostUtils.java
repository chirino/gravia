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
package org.jboss.gravia.process.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.jboss.gravia.utils.IllegalStateAssertion;


/**
 * Host utilities
 *
 * @author thomas.diesler@jboss.com
 * @since 18-Apr-2014
 */
public final class HostUtils {

    // Hide ctor
    private HostUtils() {
    }

    public static int nextAvailablePort(int portValue, InetAddress bindAddr) {
        ServerSocket socket = null;
        int endPort = portValue + 100;
        while (socket == null && portValue < endPort) {
            try {
                socket = new ServerSocket(portValue, 0, bindAddr);
            } catch (IOException ex) {
                portValue++;
            }
        }
        IllegalStateAssertion.assertNotNull(socket, "Cannot obtain next available port");
        int resultPort = socket.getLocalPort();
        try {
            socket.close();
        } catch (IOException e) {
            // ignore
        }
        return resultPort;
    }
}
