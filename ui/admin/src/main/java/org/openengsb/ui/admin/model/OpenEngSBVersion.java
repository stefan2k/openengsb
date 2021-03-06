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

package org.openengsb.ui.admin.model;

/**
 * Container filled by spring cm-prop service to contain the properties about the openengsb wich are set via a config
 * file, loaded via the fileadmin and mapped via the admin service.
 */
public class OpenEngSBVersion {

    private String versionNumber;
    private String nameAdjective;
    private String nameNoun;

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getNameAdjective() {
        return nameAdjective;
    }

    public void setNameAdjective(String nameAdjective) {
        this.nameAdjective = nameAdjective;
    }

    public String getNameNoun() {
        return nameNoun;
    }

    public void setNameNoun(String nameNoun) {
        this.nameNoun = nameNoun;
    }

}
