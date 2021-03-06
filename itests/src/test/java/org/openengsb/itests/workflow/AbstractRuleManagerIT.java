/**
 * Copyright 2010 OpenEngSB Division, Vienna University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.itests.workflow;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openengsb.core.common.AliveState;
import org.openengsb.core.common.Event;
import org.openengsb.core.common.workflow.RuleManager;
import org.openengsb.domain.example.ExampleDomain;
import org.openengsb.domain.example.event.LogEvent;

public abstract class AbstractRuleManagerIT {
    protected RuleManager ruleManager;
    protected KnowledgeBase rulebase;
    protected StatefulKnowledgeSession session;
    protected RuleListener listener;

    @Before
    public void setUp() throws Exception {
        ruleManager = PersistenceTestUtil.getRuleManager();
        rulebase = ruleManager.getRulebase();
    }

    @After
    public void tearDown() throws Exception {
        PersistenceTestUtil.cleanup();
        if (session != null) {
            session.dispose();
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        PersistenceTestUtil.createReferencePersistence();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        PersistenceTestUtil.cleanupReferenceData();
    }

    /**
     * create new stateful session from the rulebase and attach a listener to validate testresults
     */
    protected void createSession() {
        if (session != null) {
            session.dispose();
            session = null;
        }
        session = rulebase.newStatefulKnowledgeSession();
        listener = new RuleListener();
        session.addEventListener(listener);
        ExampleDomain exampleService = new ExampleDomain() {

            @Override
            public String getInstanceId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AliveState getAliveState() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String doSomethingWithLogEvent(LogEvent event) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String doSomething(ExampleEnum exampleEnum) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String doSomething(String message) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        session.setGlobal("example", exampleService);
    }

    /**
     * inserts an Event into the existing session and fires All rules
     */
    protected void executeTestSession() {
        Event event = new Event();
        session.insert(event);
        session.fireAllRules();
    }
}
