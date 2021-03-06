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

package org.openengsb.connector.example.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openengsb.core.common.AbstractOpenEngSBService;
import org.openengsb.core.common.AliveState;
import org.openengsb.domain.example.ExampleDomain;
import org.openengsb.domain.example.ExampleDomainEvents;
import org.openengsb.domain.example.event.LogEvent;
import org.openengsb.domain.example.event.LogEvent.Level;

public class LogService extends AbstractOpenEngSBService implements ExampleDomain {

    private final Log log = LogFactory.getLog(getClass());
    private String outputMode;
    private AliveState aliveState = AliveState.OFFLINE;
    private final ExampleDomainEvents domainEventInterface;

    public LogService(String instanceId, ExampleDomainEvents domainEventInterface) {
        super(instanceId);
        this.domainEventInterface = domainEventInterface;
        aliveState = AliveState.CONNECTING;
    }

    @Override
    public String doSomething(String message) {
        message = instanceId + ": " + message;
        Level level = Level.INFO;
        if ("DEBUG".equals(outputMode)) {
            log.debug(message);
            level = Level.DEBUG;
        } else if ("INFO".equals(outputMode)) {
            log.info(message);
            level = Level.INFO;
        } else if ("WARN".equals(outputMode)) {
            log.warn(message);
            level = Level.WARN;
        } else if ("ERROR".equals(outputMode)) {
            log.error(message);
            level = Level.ERROR;
        }
        raiseEvent(message, level);
        return "LogServiceCalled with: " + message;
    }

    private void raiseEvent(String message, Level level) {
        LogEvent event = new LogEvent();
        event.setMessage(message);
        event.setLevel(level);
        domainEventInterface.raiseEvent(event);
    }

    public void setOutputMode(String outputMode) {
        this.outputMode = outputMode;
        this.aliveState = AliveState.ONLINE;
    }

    @Override
    public AliveState getAliveState() {
        return this.aliveState;
    }

    @Override
    public String doSomething(ExampleEnum exampleEnum) {
        log.info(exampleEnum);
        return "Called with: " + exampleEnum.toString();
    }

    @Override
    public String doSomethingWithLogEvent(LogEvent event) {
        return "Called: " + event.getMessage() + " " + event.getLevel();
    }
}
