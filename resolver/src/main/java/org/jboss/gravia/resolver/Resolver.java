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

import java.util.List;
import java.util.Map;

import org.jboss.gravia.resource.Resource;
import org.jboss.gravia.runtime.Wire;
import org.jboss.gravia.runtime.Wiring;

/**
 * An extension of the {@link Resolver}
 *
 * @author thomas.diesler@jboss.com
 * @since 02-Apr-2012
 */
public interface Resolver {

    /**
     * Resolve the specified resolve context and return any new resources and
     * wires to the caller.
     *
     * <p>
     * The resolver considers two groups of resources:
     * <ul>
     * <li>Mandatory - any resource in the
     * {@link ResolveContext#getMandatoryResources() mandatory group} must be
     * resolved. A failure to satisfy any mandatory requirement for these
     * resources will result in throwing a {@link ResolutionException}</li>
     * <li>Optional - any resource in the
     * {@link ResolveContext#getOptionalResources() optional group} may be
     * resolved. A failure to satisfy a mandatory requirement for a resource in
     * this group will not fail the overall resolution but no resources or wires
     * will be returned for that resource.</li>
     * </ul>
     *
     * <p>
     * The resolve method returns the delta between the start state defined by
     * {@link ResolveContext#getWirings()} and the end resolved state. That is,
     * only new resources and wires are included.
     *
     * <p>
     * The behavior of the resolver is not defined if the specified resolve
     * context supplies inconsistent information.
     *
     * @param context The resolve context for the resolve operation. Must not be
     *        {@code null}.
     * @return The new resources and wires required to satisfy the specified
     *         resolve context. The returned map is the property of the caller
     *         and can be modified by the caller.
     * @throws ResolutionException If the resolution cannot be satisfied.
     */
    Map<Resource, List<Wire>> resolve(ResolveContext context) throws ResolutionException;

    /**
     * Resolve and apply the given {@link ResolveContext}.
     * <p>
     * This creates or updates the {@link Wiring} on each resolved {@link Resource}
     *
     * @param context The resolve context for the resolve operation. Must not be
     *        {@code null}.
     * @return The new resources and wires required to satisfy the specified
     *         resolve context. The returned map is the property of the caller
     *         and can be modified by the caller.
     * @throws ResolutionException If the resolution cannot be satisfied.
     */
    Map<Resource, List<Wire>> resolveAndApply(ResolveContext context) throws ResolutionException;
}
