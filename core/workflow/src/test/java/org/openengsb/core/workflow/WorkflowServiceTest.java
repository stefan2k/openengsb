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

package org.openengsb.core.workflow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.openengsb.core.common.Event;
import org.openengsb.core.common.workflow.RuleBaseException;
import org.openengsb.core.common.workflow.model.InternalWorkflowEvent;
import org.openengsb.core.common.workflow.model.ProcessBag;
import org.openengsb.core.common.workflow.model.RuleBaseElementId;
import org.openengsb.core.common.workflow.model.RuleBaseElementType;
import org.openengsb.core.test.NullEvent3;
import org.openengsb.core.workflow.model.TestEvent;

public class WorkflowServiceTest extends AbstractWorkflowServiceTest {

    @Test
    public void testProcessEvent() throws Exception {
        Event event = new Event();
        service.processEvent(event);
    }

    @Test
    public void testProcessInternalWorkflowEvent_shouldNotFail() throws Exception {
        InternalWorkflowEvent event = new InternalWorkflowEvent();
        event.getProcessBag().setProcessId("0");
        service.processEvent(event);
    }

    @Test
    public void testProcessEventTriggersHelloWorld() throws Exception {
        Event event = new Event();
        service.processEvent(event);
        verify(notification, atLeast(1)).notify("Hello");
        verify(logService, atLeast(1)).doSomething("Hello World");
        verify(myservice, atLeast(1)).call();
    }

    @Test
    public void testUseLog() throws Exception {
        Event event = new Event("test-context");
        service.processEvent(event);
        verify(logService).doSomething("42");
    }

    @Test
    public void testUpdateRule() throws Exception {
        manager.update(new RuleBaseElementId(RuleBaseElementType.Rule, "hello1"),
            "when\n Event ( name == \"test-context\")\n then \n example.doSomething(\"21\");");
        Event event = new Event("test-context");
        service.processEvent(event);
        verify(logService).doSomething("21");
    }

    @Test
    public void testUseLogContent() throws Exception {
        Event event = new Event("test-context");
        service.processEvent(event);
        Mockito.verify(logService, Mockito.times(2)).doSomething(Mockito.anyString());
    }

    @Test
    public void addInvalidRule_shouldNotModifyRulebase() throws Exception {
        try {
            manager.add(new RuleBaseElementId(RuleBaseElementType.Rule, "hello"), "this*is_invalid");
            fail("expected Exception");
        } catch (RuleBaseException e) {
            // expected
        }
        Event event = new Event("test-context");
        service.processEvent(event);
        Mockito.verify(logService, Mockito.times(2)).doSomething(Mockito.anyString());
    }

    @Test
    public void invalidMofidyRule_shouldNotModifyRulebase() throws Exception {
        try {
            manager.update(new RuleBaseElementId(RuleBaseElementType.Rule, "hello1"), "this*is_invalid");
            fail("expected Exception");
        } catch (RuleBaseException e) {
            assertThat(e.getCause(), nullValue());
        }
        Event event = new Event("test-context");
        service.processEvent(event);
        Mockito.verify(logService, Mockito.times(2)).doSomething(Mockito.anyString());
    }

    @Test
    public void testStartProcess_shouldRunScriptNodes() throws Exception {
        long id = service.startFlow("flowtest");
        service.waitForFlowToFinish(id);
        verify(logService).doSomething("flow42");
    }

    @Test
    public void testStartProcessWithEvents_shouldRunScriptNodes() throws Exception {
        long id = service.startFlow("floweventtest");
        service.processEvent(new Event());
        service.processEvent(new TestEvent());
        service.waitForFlowToFinish(id);
        InOrder inOrder2 = inOrder(logService);
        inOrder2.verify(logService).doSomething("start testflow");
        inOrder2.verify(logService).doSomething("first event received");
    }

    @Test
    public void testStart2Processes_shouldOnlyTriggerSpecificEvents() throws Exception {
        long id1 = service.startFlow("floweventtest");
        long id2 = service.startFlow("floweventtest");

        service.processEvent(new Event("event", id1));
        service.processEvent(new TestEvent(id1));
        service.waitForFlowToFinish(id1);

        assertThat(service.getRunningFlows(), hasItem(id2));
        assertThat(service.getRunningFlows(), not(hasItem(id1)));
    }

    @Test
    public void testCiWorkflow() throws Exception {
        long id = service.startFlow("ci");
        service.processEvent(new Event() {
            @Override
            public String getType() {
                return "BuildSuccess";
            }
        });
        service.processEvent(new Event() {
            @Override
            public String getType() {
                return "TestSuccess";
            }
        });
        service.waitForFlowToFinish(id);
        verify(report, times(1)).collectData();
        verify(notification, atLeast(1)).notify(anyString());
        verify(deploy, times(1)).deployProject();
    }

    @Test
    public void testStartInBackground() throws Exception {
        Object lock = new Object();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lock", lock);
        Future<Long> processIdFuture = service.startFlowInBackground("blockingFlowtest", params);
        Thread.sleep(200);
        assertThat(processIdFuture.isDone(), is(false));
        synchronized (lock) {
            lock.notify();
        }
        processIdFuture.get(2, TimeUnit.SECONDS);
        assertThat(processIdFuture.isDone(), is(true));
    }

    @Test
    public void testStartInBackgroundWithoutParams() throws Exception {
        Future<Long> processIdFuture = service.startFlowInBackground("flowtest");
        Thread.sleep(200);
        processIdFuture.get(2, TimeUnit.SECONDS);
        assertThat(processIdFuture.isDone(), is(true));
    }

    @Test
    public void testStartWorkflowTriggeredByEvent() throws Exception {
        manager.add(new RuleBaseElementId(RuleBaseElementType.Rule, "test42"), "when\n" + "  Event()\n" + "then\n"
                + "  kcontext.getKnowledgeRuntime().startProcess(\"ci\");\n");
        service.processEvent(new Event());
        assertThat(service.getRunningFlows().isEmpty(), is(false));
    }

    @Test
    public void testRegisterWorkflowTrigger() throws Exception {
        service.registerFlowTriggerEvent(new Event("triggerEvent"), "ci");
        service.processEvent(new Event());
        service.processEvent(new Event("triggerEvent"));
        assertThat(service.getRunningFlows().size(), is(1));
    }

    @Test
    public void testRegisterWorkflowTriggerWithSubclass() throws Exception {
        NullEvent3 testEvent = new NullEvent3();
        testEvent.setName("triggerEvent");
        testEvent.setTestProperty("foo");
        testEvent.setTestStringProp("bar");
        testEvent.setTestBoolProp(true);
        testEvent.setTestIntProp(42);
        service.registerFlowTriggerEvent(testEvent, "ci");
        service.processEvent(new Event());
        service.processEvent(testEvent);
        assertThat(service.getRunningFlows().size(), is(1));
    }

    @Test
    public void testRegisterWorkflowTriggerIgnoreNullFields() throws Exception {
        NullEvent3 testEvent = new NullEvent3();
        testEvent.setName("triggerEvent");
        service.registerFlowTriggerEvent(testEvent, "ci");
        service.processEvent(new Event());
        service.processEvent(testEvent);
        assertThat(service.getRunningFlows().size(), is(1));
    }

    @Test
    public void testRegisterWorkflowTriggerIgnoreNullFieldsMixed() throws Exception {
        NullEvent3 testEvent = new NullEvent3();
        testEvent.setName("triggerEvent");
        testEvent.setTestStringProp("bar");
        testEvent.setTestIntProp(42);
        service.registerFlowTriggerEvent(testEvent, "ci");
        service.processEvent(new Event());
        service.processEvent(testEvent);
        assertThat(service.getRunningFlows().size(), is(1));
    }

    @Test(timeout = 3000)
    public void testRegisterWorkflowTriggerWithFlowStartedEvent() throws Exception {
        service.registerFlowTriggerEvent(new Event("triggerEvent"), "flowStartedEvent");
        service.processEvent(new Event("triggerEvent"));
        for (Long id : service.getRunningFlows()) {
            service.waitForFlowToFinish(id);
        }
    }

    @Test
    public void testIfEventIsRetracted() throws Exception {
        Event event = new Event();
        service.processEvent(event);
        event = new Event("test-context");
        service.processEvent(event);
        verify(logService, times(2)).doSomething("Hello World");
    }

    @Test
    public void testStartProcessWithProperyBag_ChangePropertyByScriptNode_shouldChangeProperty() throws Exception {
        ProcessBag processBag = new ProcessBag();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("processBag", processBag);

        long id = service.startFlow("propertybagtest", parameterMap);
        service.waitForFlowToFinish(id);

        assertThat((String) processBag.getProperty("test"), is(String.valueOf(id)));
    }

    @Test
    public void processEventsConcurrently_shouldProcessBothEvents() throws Exception {
        manager.addImport(TestEvent.class.getName());
        manager.add(new RuleBaseElementId(RuleBaseElementType.Rule, "concurrent test"), "when\n"
                + "TestEvent(value == \"0\")\n"
                + "then\n"
                + "example.doSomething(\"concurrent\");");
        manager.add(new RuleBaseElementId(RuleBaseElementType.Rule, "concurrent test1"), "when\n"
                + "TestEvent(value == \"1\")\n"
                + "then\n"
                + "Thread.sleep(1000);");
        Callable<Void> task = makeProcessEventTask(new TestEvent("1"));
        Callable<Void> task2 = makeProcessEventTask(new TestEvent("0"));
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Void> future1 = executor.submit(task);
        Thread.sleep(300);
        Future<Void> future2 = executor.submit(task2);
        future1.get();
        future2.get();
        verify(logService).doSomething("concurrent");
    }

    private Callable<Void> makeProcessEventTask(final Event event) {
        Callable<Void> task = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                service.processEvent(event);
                return null;
            }
        };
        return task;
    }
}
