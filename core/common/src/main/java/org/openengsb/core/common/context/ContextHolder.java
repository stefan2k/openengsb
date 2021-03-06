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

package org.openengsb.core.common.context;

/**
 * Singleton Class, that provides access to thread-local context-attributes
 */
public final class ContextHolder {

    private static ContextHolder instance = new ContextHolder();

    private ThreadLocal<String> currentContextId = new InheritableThreadLocal<String>();

    /**
     * returns the singleton instance
     */
    public static ContextHolder get() {
        return instance;
    }

    /**
     * set the current Threads context Id (it is inherited by threads spawned by the current process)
     */
    public void setCurrentContextId(String value) {
        currentContextId.set(value);
    }

    /**
     * get the current Threads context id
     */
    public String getCurrentContextId() {
        return currentContextId.get();
    }

    private ContextHolder() {
    }

}
