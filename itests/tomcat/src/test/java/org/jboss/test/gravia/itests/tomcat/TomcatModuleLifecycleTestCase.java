/*
 * #%L
 * Gravia :: Integration Tests :: Tomcat
 * %%
 * Copyright (C) 2010 - 2013 JBoss by Red Hat
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
package org.jboss.test.gravia.itests.tomcat;

import java.io.InputStream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.gravia.resource.spi.ManifestBuilder;
import org.jboss.gravia.runtime.tomcat.ApplicationActivator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.gravia.itests.ModuleLifecycleTest;
import org.junit.runner.RunWith;

/**
 * Test webapp deployemnts
 *
 * @author thomas.diesler@jboss.com
 * @since 01-Oct-2013
 */
@RunWith(Arquillian.class)
public class TomcatModuleLifecycleTestCase extends ModuleLifecycleTest {

    @Deployment
    public static WebArchive deployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "simple.war");
        archive.addClasses(ApplicationActivator.class, ModuleLifecycleTest.class);
        archive.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                ManifestBuilder builder = new ManifestBuilder();
                builder.addIdentityCapability(archive.getName(), "1.0.0");
                return builder.openStream();
            }
        });
        return archive;
    }
}
