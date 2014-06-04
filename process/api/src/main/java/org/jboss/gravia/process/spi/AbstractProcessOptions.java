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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.gravia.process.api.ProcessOptions;
import org.jboss.gravia.resource.AttributeKey;
import org.jboss.gravia.resource.MavenCoordinates;
import org.jboss.gravia.resource.spi.AttributeSupport;
import org.jboss.gravia.utils.IllegalStateAssertion;


public abstract class AbstractProcessOptions implements ProcessOptions {

    private AttributeSupport attributes = new AttributeSupport();
    private List<MavenCoordinates> mavenCoordinates = new ArrayList<MavenCoordinates>();
    private boolean outputToConsole;
    private String identityPrefix;
    private String javaVmArguments;
    private Path targetPath;

    @Override
    public String getIdentityPrefix() {
        return identityPrefix;
    }

    @Override
    public List<MavenCoordinates> getMavenCoordinates() {
        return Collections.unmodifiableList(mavenCoordinates);
    }

    @Override
    public Path getTargetPath() {
        return targetPath;
    }

    @Override
    public String getJavaVmArguments() {
        return javaVmArguments;
    }

    @Override
    public boolean isOutputToConsole() {
        return outputToConsole;
    }

    /*
     * Setters are protected
     */

    @Override
    public Set<AttributeKey<?>> getAttributeKeys() {
        return attributes.getAttributeKeys();
    }

    @Override
    public <T> T getAttribute(AttributeKey<T> key) {
        return attributes.getAttribute(key);
    }

    @Override
    public <T> boolean hasAttribute(AttributeKey<T> key) {
        return attributes.hasAttribute(key);
    }

    @Override
    public Map<AttributeKey<?>, Object> getAttributes() {
        return attributes.getAttributes();
    }

    protected void setIdentityPrefix(String identityPrefix) {
        this.identityPrefix = identityPrefix;
    }

    protected void addMavenCoordinates(MavenCoordinates coordinates) {
        mavenCoordinates.add(coordinates);
    }

    protected void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    protected void setJavaVmArguments(String javaVmArguments) {
        this.javaVmArguments = javaVmArguments;
    }

    protected void setOutputToConsole(boolean outputToConsole) {
        this.outputToConsole = outputToConsole;
    }

    protected <V> void addAttribute(AttributeKey<V> key, V value) {
        attributes.putAttribute(key, value);
    }

    protected void addAttributes(Map<AttributeKey<?>, Object> atts) {
        attributes.putAllAttributes(atts);
    }

    protected void validate() {
        IllegalStateAssertion.assertNotNull(identityPrefix, "identityPrefix");
        IllegalStateAssertion.assertNotNull(targetPath, "targetPath");
    }
}
