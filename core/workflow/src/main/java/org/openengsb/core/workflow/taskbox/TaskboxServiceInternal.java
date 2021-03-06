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

package org.openengsb.core.workflow.taskbox;

import org.openengsb.core.common.persistence.PersistenceException;
import org.openengsb.core.common.workflow.model.ProcessBag;

/**
 * This service is used internally by the human subtask 'humantask' to create new tasks for human interaction.
 */
public interface TaskboxServiceInternal {
    /**
     * Creates a new Task object from the ProcessBag passed and stores it so it can be obtained by the
     * {@link org.openengsb.core.common.taskbox.TaskboxService TaskboxService}.
     * 
     * @throws PersistenceException when saving the task failed
     */
    void createNewTask(ProcessBag bag) throws PersistenceException;
}
