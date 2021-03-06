/*
 * #%L
 * Gravia :: Resource
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
package org.jboss.gravia.runtime;

import java.util.List;

import org.jboss.gravia.resource.Capability;
import org.jboss.gravia.resource.Namespace;
import org.jboss.gravia.resource.Requirement;
import org.jboss.gravia.resource.Resource;

/**
 * A wiring for a resource. A wiring is associated with a resource and
 * represents the dependencies with other wirings.
 *
 * @author thomas.diesler@jboss.com
 * @since 18-Feb-2013
 */
public interface Wiring {

    /**
     * Returns the capabilities provided by this wiring.
     *
     * <p>
     * Only capabilities considered by the resolver are returned.
     *
     * <p>
     * A capability may not be required by any wiring and thus there may be no
     * {@link #getProvidedResourceWires(String) wires} for the capability.
     *
     *
     * @param namespace The namespace of the capabilities to return or
     *        {@code null} to return the capabilities from all namespaces.
     * @return A list containing a snapshot of the {@link Capability}s, or an
     *         empty list if this wiring provides no capabilities in the
     *         specified namespace.
     */
    List<Capability> getResourceCapabilities(String namespace);

    /**
     * Returns the requirements of this wiring.
     *
     * <p>
     * Only requirements considered by the resolver are returned. For example,
     * requirements with {@link Namespace#REQUIREMENT_EFFECTIVE_DIRECTIVE
     * effective} directive not equal to {@link Namespace#EFFECTIVE_RESOLVE
     * resolve} are not returned.
     *
     * <p>
     * A wiring for a non-fragment resource has a subset of the declared
     * requirements from the resource and all attached fragment resources. Not
     * all declared requirements may be present since some may be discarded. For
     * example, if a package is declared to be optionally imported and is not
     * actually imported, the requirement must be discarded.
     *
     * @param namespace The namespace of the requirements to return or
     *        {@code null} to return the requirements from all namespaces.
     * @return A list containing a snapshot of the {@link Requirement}s, or an
     *         empty list if this wiring uses no requirements in the specified
     *         namespace.
     */
    List<Requirement> getResourceRequirements(String namespace);

    /**
     * Returns the {@link Wire}s to the provided {@link Capability capabilities}
     * of this wiring.
     *
     * @param namespace The namespace of the capabilities for which to return
     *        wires or {@code null} to return the wires for the capabilities in
     *        all namespaces.
     * @return A list containing a snapshot of the {@link Wire}s for the
     *         {@link Capability capabilities} of this wiring, or an empty list
     *         if this wiring has no capabilities in the specified namespace.
     */
    List<Wire> getProvidedResourceWires(String namespace);

    /**
     * Returns the {@link Wire}s to the {@link Requirement requirements} in use
     * by this wiring.
     *
     * @param namespace The namespace of the requirements for which to return
     *        wires or {@code null} to return the wires for the requirements in
     *        all namespaces.
     * @return A list containing a snapshot of the {@link Wire}s for the
     *         {@link Requirement requirements} of this wiring, or an empty list
     *         if this wiring has no requirements in the specified namespace.
     */
    List<Wire> getRequiredResourceWires(String namespace);

    /**
     * Returns the resource associated with this wiring.
     * @return The resource associated with this wiring.
     */
    Resource getResource();
}
