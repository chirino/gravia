package org.jboss.test.gravia.repository;
/*
 * #%L
 * JBossOSGi Repository
 * %%
 * Copyright (C) 2010 - 2012 JBoss by Red Hat
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.jboss.gravia.repository.DefaultRepositoryXMLReader;
import org.jboss.gravia.repository.DefaultRepositoryXMLWriter;
import org.jboss.gravia.repository.RepositoryReader;
import org.jboss.gravia.repository.RepositoryWriter;
import org.jboss.gravia.resource.Resource;
import org.junit.Test;

/** 
 * Test the repository reader/writer
 *
 * @author thomas.diesler@jboss.com
 * @since 21-May-2012
 */
public class AbstractResourcesWriterTestCase extends AbstractRepositoryTest {

    @Test
    public void testXMLWriter() throws Exception {

        RepositoryReader reader = getRepositoryReader("xml/abstract-resources.xml");
        Map<String, String> attributes = reader.getRepositoryAttributes();
        List<Resource> resources = getResources(reader);

        File file = new File("target/abstract-resources.xml");
        RepositoryWriter writer = new DefaultRepositoryXMLWriter(new FileOutputStream(file));
        writer.writeRepositoryElement(attributes);
        for (Resource res : resources) {
            writer.writeResource(res);
        }
        writer.close();

        reader = new DefaultRepositoryXMLReader(new FileInputStream(file));
        AbstractResourcesReaderTestCase.verifyContent(reader.getRepositoryAttributes(), getResources(reader));
    }
}