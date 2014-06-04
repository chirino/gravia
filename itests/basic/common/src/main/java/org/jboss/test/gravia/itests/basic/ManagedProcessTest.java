/*
 * #%L
 * Gravia :: Integration Tests :: Common
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
package org.jboss.test.gravia.itests.basic;

import java.io.InputStream;
import java.nio.file.Paths;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.osgi.StartLevelAware;
import org.jboss.gravia.container.karaf.KarafProcessBuilder;
import org.jboss.gravia.container.karaf.KarafProcessHandler;
import org.jboss.gravia.container.karaf.KarafProcessOptions;
import org.jboss.gravia.container.tomcat.TomcatProcessBuilder;
import org.jboss.gravia.container.tomcat.TomcatProcessHandler;
import org.jboss.gravia.container.tomcat.TomcatProcessOptions;
import org.jboss.gravia.process.api.ManagedProcess;
import org.jboss.gravia.process.api.ManagedProcess.State;
import org.jboss.gravia.process.api.MutableManagedProcess;
import org.jboss.gravia.process.api.ProcessIdentity;
import org.jboss.gravia.process.api.ProcessOptions;
import org.jboss.gravia.process.spi.AbstractProcessBuilder;
import org.jboss.gravia.process.spi.ProcessHandler;
import org.jboss.gravia.resource.ManifestBuilder;
import org.jboss.gravia.runtime.RuntimeLocator;
import org.jboss.gravia.runtime.RuntimeType;
import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.test.gravia.itests.support.AnnotatedContextListener;
import org.jboss.test.gravia.itests.support.ArchiveBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test manged processes
 *
 * @author thomas.diesler@jboss.com
 * @since 04-Jun-2014
 */
@RunWith(Arquillian.class)
public class ManagedProcessTest {

    @Deployment
    @StartLevelAware(autostart = true)
    public static Archive<?> deployment() {
        final ArchiveBuilder archive = new ArchiveBuilder("managed-process");
        archive.addClasses(ManagedProcessTest.class);
        archive.addClasses(RuntimeType.TOMCAT, AnnotatedContextListener.class);
        archive.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                if (ArchiveBuilder.getTargetContainer() == RuntimeType.KARAF) {
                    OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                    builder.addBundleManifestVersion(2);
                    builder.addBundleSymbolicName(archive.getName());
                    builder.addBundleVersion("1.0.0");
                    builder.addImportPackages(RuntimeLocator.class, ManagedProcess.class, AbstractProcessBuilder.class);
                    builder.addImportPackages(TomcatProcessBuilder.class, KarafProcessBuilder.class);
                    return builder.openStream();
                } else {
                    ManifestBuilder builder = new ManifestBuilder();
                    builder.addIdentityCapability(archive.getName(), "1.0.0");
                    return builder.openStream();
                }
            }
        });
        return archive.getArchive();
    }

    @Test
    public void testTomcatProcess() throws Exception {

        TomcatProcessOptions options = TomcatProcessBuilder.create()
                .targetPath(Paths.get("target", "process"))
                .identityPrefix("tomcatProc")
                .outputToConsole(true)
                .getProcessOptions();

        verifyManagedProcess(new TomcatProcessHandler(), options);
    }

    @Test
    public void testWildFlyProcess() throws Exception {

    }

    @Test
    public void testKarafProcess() throws Exception {

        KarafProcessOptions options = KarafProcessBuilder.create()
                .targetPath(Paths.get("target", "process"))
                .identityPrefix("karafProc")
                .outputToConsole(true)
                .getProcessOptions();

        verifyManagedProcess(new KarafProcessHandler(), options);
    }

    private void verifyManagedProcess(ProcessHandler handler, ProcessOptions options) {

        Assert.assertTrue(handler.accept(options));

        ProcessIdentity identity = ProcessIdentity.create(options.getIdentityPrefix());
        MutableManagedProcess process = handler.create(options, identity);
        Assert.assertEquals(State.CREATED, process.getState());

        handler.start(process);
        Assert.assertEquals(State.STARTED, process.getState());

        handler.stop(process);
        Assert.assertEquals(State.STOPPED, process.getState());

        handler.destroy(process);
        Assert.assertEquals(State.DESTROYED, process.getState());
    }
}
