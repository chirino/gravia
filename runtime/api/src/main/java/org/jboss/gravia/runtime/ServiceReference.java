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
package org.jboss.gravia.runtime;

import java.util.Dictionary;


/**
 * A reference to a service.
 *
 * <p>
 * The Runtime returns {@code ServiceReference} objects from the
 * {@code ModuleContext.getServiceReference} and
 * {@code ModuleContext.getServiceReferences} methods.
 * <p>
 * A {@code ServiceReference} object may be shared between modules and can be
 * used to examine the properties of the service and to get the service object.
 * <p>
 * Every service registered in the Runtime has a unique
 * {@code ServiceRegistration} object and may have multiple, distinct
 * {@code ServiceReference} objects referring to it. {@code ServiceReference}
 * objects associated with a {@code ServiceRegistration} object have the same
 * {@code hashCode} and are considered equal (more specifically, their
 * {@code equals()} method will return {@code true} when compared).
 * <p>
 * If the same service object is registered multiple times,
 * {@code ServiceReference} objects associated with different
 * {@code ServiceRegistration} objects are not equal.
 *
 * @param <T> Type of Service.
 *
 * @see ModuleContext#getServiceReference(Class)
 * @see ModuleContext#getServiceReference(String)
 * @see ModuleContext#getServiceReferences(Class, String)
 * @see ModuleContext#getServiceReferences(String, String)
 * @see ModuleContext#getService(ServiceReference)
 *
 * @author thomas.diesler@jboss.com
 * @since 27-Sep-2013
 *
 * @ThreadSafe
 */
public interface ServiceReference<T> extends Comparable<T> {

    /**
     * Returns the property value to which the specified property key is mapped
     * in the properties {@code Dictionary} object of the service referenced by
     * this {@code ServiceReference} object.
     *
     * <p>
     * Property keys are case-insensitive.
     *
     * <p>
     * This method must continue to return property values after the service has
     * been unregistered. This is so references to unregistered services (for
     * example, {@code ServiceReference} objects stored in the log) can still be
     * interrogated.
     *
     * @param key The property key.
     * @return The property value to which the key is mapped; {@code null} if
     *         there is no property named after the key.
     */
    Object getProperty(String key);

    /**
     * Returns an array of the keys in the properties {@code Dictionary} object
     * of the service referenced by this {@code ServiceReference} object.
     *
     * <p>
     * This method will continue to return the keys after the service has been
     * unregistered. This is so references to unregistered services (for
     * example, {@code ServiceReference} objects stored in the log) can still be
     * interrogated.
     *
     * <p>
     * This method is <i>case-preserving</i>; this means that every key in the
     * returned array must have the same case as the corresponding key in the
     * properties {@code Dictionary} that was passed to the
     * {@link ModuleContext#registerService(String[],Object,Dictionary)} or
     * {@link ServiceRegistration#setProperties(Dictionary)} methods.
     *
     * @return An array of property keys.
     */
    String[] getPropertyKeys();

    /**
     * Returns the module that registered the service referenced by this
     * {@code ServiceReference} object.
     *
     * <p>
     * This method must return {@code null} when the service has been
     * unregistered. This can be used to determine if the service has been
     * unregistered.
     *
     * @return The module that registered the service referenced by this
     *         {@code ServiceReference} object; {@code null} if that service has
     *         already been unregistered.
     * @see ModuleContext#registerService(String[],Object,Dictionary)
     */
    Module getModule();

    /**
     * Tests if the module that registered the service referenced by this
     * {@code ServiceReference} and the specified module use the same source for
     * the package of the specified class name.
     * <p>
     * This method performs the following checks:
     * <ol>
     * <li>Get the package name from the specified class name.</li>
     * <li>For the module that registered the service referenced by this
     * {@code ServiceReference} (registrant module); find the source for the
     * package. If no source is found then return {@code true} if the registrant
     * module is equal to the specified module; otherwise return {@code false}.</li>
     * <li>If the package source of the registrant module is equal to the
     * package source of the specified module then return {@code true};
     * otherwise return {@code false}.</li>
     * </ol>
     *
     * @param module The {@code Module} object to check.
     * @param className The class name to check.
     * @return {@code true} if the module which registered the service
     *         referenced by this {@code ServiceReference} and the specified
     *         module use the same source for the package of the specified class
     *         name. Otherwise {@code false} is returned.
     */
    boolean isAssignableTo(Module module, String className);
}
