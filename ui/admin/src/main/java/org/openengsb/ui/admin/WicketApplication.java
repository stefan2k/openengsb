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

package org.openengsb.ui.admin;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.openengsb.ui.admin.index.Index;
import org.openengsb.ui.admin.loginPage.LoginPage;
import org.openengsb.ui.common.OpenEngSBWicketApplication;

public class WicketApplication extends OpenEngSBWicketApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return Index.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }
}
