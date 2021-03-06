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

package org.openengsb.domain.report.model;

public abstract class ReportPart {

    private String partName;

    private String contentType;

    public ReportPart(String partName, String contentType) {
        this.partName = partName;
        this.contentType = contentType;
    }

    public abstract byte[] getContent();

    public String getContentType() {
        return contentType;
    }

    public String getPartName() {
        return partName;
    }

}
