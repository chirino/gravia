/*
 * #%L
 * JBossOSGi Resolver API
 * %%
 * Copyright (C) 2012 - 2013 JBoss by Red Hat
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

package org.jboss.gravia.repository;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The artifact coordinates.
 *
 * @author thomas.diesler@jboss.com
 * @since 16-Jan-2012
 */
public final class MavenCoordinates {

    private final String groupId;
    private final String artifactId;
    private final String type;
    private final String version;
    private final String classifier;

    public static MavenCoordinates parse(String coordinates) {
        MavenCoordinates result;
        String[] parts = coordinates.split(":");
        if (parts.length == 3) {
            result = new MavenCoordinates(parts[0], parts[1], null, parts[2], null);
        } else if (parts.length == 4) {
            result = new MavenCoordinates(parts[0], parts[1], parts[2], parts[3], null);
        } else if (parts.length == 5) {
            result = new MavenCoordinates(parts[0], parts[1], parts[2], parts[3], parts[4]);
        } else {
            throw new IllegalArgumentException("Invalid coordinates: " + coordinates);
        }
        return result;
    }

    public static MavenCoordinates create(String groupId, String artifactId, String version, String type, String classifier) {
        return new MavenCoordinates(groupId, artifactId, type, version, classifier);
    }

    private MavenCoordinates(String groupId, String artifactId, String type, String version, String classifier) {
        if (groupId == null)
            throw new IllegalArgumentException("Null groupId");
        if (artifactId == null)
            throw new IllegalArgumentException("Null artifactId");
        if (version == null)
            throw new IllegalArgumentException("Null version");

        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = (type != null ? type : "jar");
        this.version = version;
        this.classifier = classifier;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getClassifier() {
        return classifier;
    }

    public String toExternalForm() {
        String clstr = classifier != null ? ":" + classifier : "";
        return groupId + ":" + artifactId + ":" + type + ":" + version + clstr;
    }

    public URL getArtifactURL(URL baseURL) {
        String base = baseURL.toExternalForm();
        if (base.endsWith("/") == false)
            base += "/";
        String urlstr = base + getArtifactPath();
        try {
            return new URL(urlstr);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid artifact URL: " + urlstr);
        }
    }

    public String getArtifactPath() {
        String dirstr = groupId.replace('.', '/') + "/" + artifactId + "/" + version;
        String clstr = classifier != null ? "-" + classifier : "";
        String path = dirstr + "/" + artifactId + "-" + version + clstr + "." + type;
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MavenCoordinates)) return false;
        MavenCoordinates other = (MavenCoordinates) obj;
        return toExternalForm().equals(other.toExternalForm());
    }

    @Override
    public int hashCode() {
        return toExternalForm().hashCode();
    }

    @Override
    public String toString() {
        return "MavenCoordinates[" + toExternalForm() + "]";
    }
}
