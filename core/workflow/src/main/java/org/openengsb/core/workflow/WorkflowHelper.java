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

import org.drools.runtime.KnowledgeRuntime;
import org.openengsb.core.workflow.internal.WorkflowStarter;

public final class WorkflowHelper {

    public static long startFlow(KnowledgeRuntime session, String processId) {
        return new WorkflowStarter(session, processId).call();
    }

    private WorkflowHelper() {
    }

}
