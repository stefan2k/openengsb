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

package org.openengsb.connector.plaintextreport.internal;

import org.openengsb.domain.report.model.ReportPart;

public class StringReportPart extends ReportPart {

    private String content;

    public StringReportPart(String partName, String contentType, String content) {
        super(partName, contentType);
        this.content = content;
    }

    @Override
    public byte[] getContent() {
        return content.getBytes();
    }

}
