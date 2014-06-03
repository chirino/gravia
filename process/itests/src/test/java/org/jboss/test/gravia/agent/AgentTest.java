/*
 * #%L
 * Gravia :: Agent
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
package org.jboss.test.gravia.agent;

import java.util.concurrent.TimeUnit;

import org.jboss.gravia.agent.Agent;
import org.jboss.gravia.agent.ControllerService;
import org.jboss.gravia.runtime.ServiceLocator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the default resolver integration.
 *
 * @author thomas.diesler@jboss.com
 * @since 31-May-2010
 */
public class AgentTest {

    @Test
    public void testAgent() throws Exception {

        Agent agent = new Agent();
        agent.start();

        ServiceLocator.getRequiredService(ControllerService.class);

        Assert.assertTrue(agent.shutdown(30,  TimeUnit.SECONDS));
    }
}
