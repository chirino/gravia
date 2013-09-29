/*
 * #%L
 * JBossOSGi Provision: Core
 * %%
 * Copyright (C) 2013 JBoss by Red Hat
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.jboss.gravia.provision.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.gravia.provision.Environment;
import org.jboss.gravia.provision.ProvisionException;
import org.jboss.gravia.provision.ProvisionResult;
import org.jboss.gravia.provision.Provisioner;
import org.jboss.gravia.repository.Repository;
import org.jboss.gravia.resolver.DefaultResolveContext;
import org.jboss.gravia.resolver.PreferencePolicy;
import org.jboss.gravia.resolver.ResolutionException;
import org.jboss.gravia.resolver.ResolveContext;
import org.jboss.gravia.resolver.Resolver;
import org.jboss.gravia.resource.Capability;
import org.jboss.gravia.resource.IdentityNamespace;
import org.jboss.gravia.resource.Requirement;
import org.jboss.gravia.resource.Resource;
import org.jboss.gravia.resource.Wire;
import org.jboss.logging.Logger;

/**
 * An abstract {@link Provisioner}
 *
 * @author thomas.diesler@jboss.com
 * @since 06-May-2013
 */
public abstract class AbstractProvisioner implements Provisioner {

    static final Logger LOGGER = Logger.getLogger(Provisioner.class.getPackage().getName());

    private final Resolver resolver;
    private final Repository repository;
    private PreferencePolicy preferencePolicy;
    private Environment environment;

    public AbstractProvisioner(Resolver resolver, Repository repository) {
        this.resolver = resolver;
        this.repository = repository;
    }

    protected abstract Environment createEnvironment();

    private Environment getEnvironment() {
        if (environment == null) {
            environment = createEnvironment();
        }
        return environment;
    }

    protected abstract Environment cloneEnvironment(Environment env);

    @Override
    public final Resolver getResolver() {
        return resolver;
    }

    @Override
    public final Repository getRepository() {
        return repository;
    }

    protected abstract PreferencePolicy createPreferencePolicy();

    private PreferencePolicy getPreferencePolicyInternal() {
        if (preferencePolicy == null) {
            preferencePolicy = createPreferencePolicy();
        }
        return preferencePolicy;
    }

    @Override
    public ProvisionResult findResources(Set<Requirement> reqs) {
        return findResources(getEnvironment(), reqs);
    }

    @Override
    public final ProvisionResult findResources(Environment env, Set<Requirement> reqs) {
        if (env == null)
            throw new IllegalArgumentException("Null env");
        if (reqs == null)
            throw new IllegalArgumentException("Null reqs");

        LOGGER.debugf("START findResources: %s", reqs);

        // Install the unresolved resources into the cloned environment
        List<Resource> unresolved = new ArrayList<Resource>();
        Environment envclone = cloneEnvironment(env);
        for (Requirement req : reqs) {
            Resource res = req.getResource();
            if (env.getResource(res.getIdentity()) == null) {
                envclone.addResource(res);
                unresolved.add(res);
            }
        }

        // Find the resources in the cloned environment
        List<Resource> resources = new ArrayList<Resource>();
        Set<Requirement> unstatisfied = new HashSet<Requirement>(reqs);
        Map<Requirement, Resource> mapping = new HashMap<Requirement, Resource>();
        findResources(envclone, unresolved, mapping, unstatisfied, resources);

        // Remove abstract resources
        Iterator<Resource> itres = resources.iterator();
        while (itres.hasNext()) {
            Resource res = itres.next();
            if (isAbstract(res)) {
                itres.remove();
            }
        }

        // Sort the provisioner result
        List<Resource> sorted = new ArrayList<Resource>();
        for (Resource res : resources) {
            sortResultResources(res, mapping, sorted);
        }

        AbstractProvisionResult result = new AbstractProvisionResult(mapping, unstatisfied, sorted);
        LOGGER.debugf("END findResources");
        LOGGER.debugf("  resources: %s", result.getResources());
        LOGGER.debugf("  unsatisfied: %s", result.getUnsatisfiedRequirements());

        // Sanity check that we can resolve all result resources
        Set<Resource> mandatory = new LinkedHashSet<Resource>();
        mandatory.addAll(result.getResources());
        try {
            ResolveContext context = new DefaultResolveContext(envclone, mandatory, null);
            resolver.resolve(context).entrySet();
        } catch (ResolutionException ex) {
            LOGGER.warnf(ex, "Cannot resolve provisioner result");
        }

        return result;
    }

    // Sort mapping targets higher in the list. This should result in resource installations
    // without dependencies on resources from the same provioner result set.
    private void sortResultResources(Resource res, Map<Requirement, Resource> mapping, List<Resource> result) {
        if (!result.contains(res)) {
            for (Requirement req : res.getRequirements(null)) {
                Resource target = mapping.get(req);
                if (target != null) {
                    sortResultResources(target, mapping, result);
                }
            }
            result.add(res);
        }
    }

    @Override
    public ResourceHandle installResource(Resource resource, Map<Requirement, Resource> mapping) throws ProvisionException {
        throw new UnsupportedOperationException();
    }

    private boolean isAbstract(Resource res) {
        Object attval = res.getIdentityCapability().getAttribute(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE);
        return IdentityNamespace.TYPE_ABSTRACT.equals(attval);
    }

    private void findResources(Environment env, List<Resource> unresolved, Map<Requirement, Resource> mapping, Set<Requirement> unstatisfied, List<Resource> resources) {

        // Resolve the unsatisfied reqs in the environment
        resolveInEnvironment(env, unresolved, mapping, unstatisfied, resources);
        if (unstatisfied.isEmpty())
            return;

        boolean envModified = false;
        Set<Resource> installable = new HashSet<Resource>();

        LOGGER.debugf("Finding unsatisfied reqs");

        Iterator<Requirement> itun = unstatisfied.iterator();
        while (itun.hasNext()) {
            Requirement req = itun.next();

            // Ignore requirements that are already in the environment
            if (!env.findProviders(req).isEmpty()) {
                continue;
            }

            // Continue if we cannot find a provider for a given requirement
            Capability cap = findProviderInRepository(req);
            if (cap == null) {
                continue;
            }

            installable.add(cap.getResource());
        }

        // Install the resources that match the unsatisfied reqs
        for (Resource res : installable) {
            if (!resources.contains(res)) {
                Collection<Requirement> reqs = res.getRequirements(null);
                LOGGER.debugf("Adding %d unsatisfied reqs", reqs.size());
                unstatisfied.addAll(reqs);
                env.addResource(res);
                resources.add(res);
                envModified = true;
            }
        }

        // Recursivly find the missing resources
        if (envModified) {
            findResources(env, unresolved, mapping, unstatisfied, resources);
        }
    }

    private Capability findProviderInRepository(Requirement req) {

        // Find the providers in the repository
        LOGGER.debugf("Find in repository: %s", req);
        Collection<Capability> providers = repository.findProviders(req);

        // Remove abstract resources
        if (providers.size() > 1) {
            providers = new ArrayList<Capability>(providers);
            Iterator<Capability> itcap = providers.iterator();
            while (itcap.hasNext()) {
                Capability cap = itcap.next();
                if (isAbstract(cap.getResource())) {
                    itcap.remove();
                }
            }
        }

        Capability cap = null;
        if (providers.size() == 1) {
            cap = providers.iterator().next();
            LOGGER.debugf(" Found one: %s", cap);
        } else if (providers.size() > 1) {
            List<Capability> sorted = new ArrayList<Capability>(providers);
            getPreferencePolicyInternal().sort(sorted);
            LOGGER.debugf(" Found multiple: %s", sorted);
            cap = sorted.get(0);
        } else {
            LOGGER.debugf(" Not found: %s", req);
        }
        return cap;
    }

    private void resolveInEnvironment(Environment env, List<Resource> unresolved, Map<Requirement, Resource> mapping, Set<Requirement> unstatisfied, List<Resource> resources) {
        Set<Resource> mandatory = new LinkedHashSet<Resource>();
        mandatory.addAll(unresolved);
        mandatory.addAll(resources);
        try {
            ResolveContext context = new DefaultResolveContext(env, mandatory, null);
            Set<Entry<Resource, List<Wire>>> wiremap = resolver.resolve(context).entrySet();
            for (Entry<Resource, List<Wire>> entry : wiremap) {
                for (Wire wire : entry.getValue()) {
                    Requirement req = wire.getRequirement();
                    Resource provider = wire.getProvider();
                    mapping.put(req, provider);
                }
            }
            unstatisfied.clear();
        } catch (ResolutionException ex) {
            for (Requirement req : ex.getUnresolvedRequirements()) {
                LOGGER.debugf(" unresolved: %s", req);
            }
        }
    }

    static class AbstractProvisionResult implements ProvisionResult {

        private final Map<Requirement, Resource> mapping;
        private final Set<Requirement> unsatisfied;
        private final List<Resource> resources;

        public AbstractProvisionResult(Map<Requirement, Resource> mapping, Set<Requirement> unstatisfied, List<Resource> resources) {
            this.mapping = mapping;
            this.unsatisfied = unstatisfied;
            this.resources = resources;
        }

        @Override
        public Map<Requirement, Resource> getMapping() {
            return Collections.unmodifiableMap(mapping);
        }

        @Override
        public List<Resource> getResources() {
            return Collections.unmodifiableList(resources);
        }

        @Override
        public Set<Requirement> getUnsatisfiedRequirements() {
            return Collections.unmodifiableSet(unsatisfied);
        }
    }
}