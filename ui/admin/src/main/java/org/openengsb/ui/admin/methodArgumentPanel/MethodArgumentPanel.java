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

package org.openengsb.ui.admin.methodArgumentPanel;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.openengsb.core.common.descriptor.AttributeDefinition;
import org.openengsb.core.common.descriptor.AttributeDefinition.Builder;
import org.openengsb.core.common.l10n.PassThroughStringLocalizer;
import org.openengsb.ui.admin.model.Argument;
import org.openengsb.ui.common.editor.AttributeEditorUtil;
import org.openengsb.ui.common.editor.BeanEditorPanel;
import org.openengsb.ui.common.editor.fields.AbstractField;
import org.openengsb.ui.common.util.MethodUtil;

@SuppressWarnings("serial")
public class MethodArgumentPanel extends Panel {

    public MethodArgumentPanel(String id, Argument arg) {
        super(id);

        add(new Label("index", String.format("Argument #%d", arg.getIndex())));

        Class<?> type = arg.getType();
        if (type.isPrimitive() || type.isEnum() || hasStringOnlyConstructor(type)) {
            Builder builder = AttributeDefinition.builder(new PassThroughStringLocalizer());
            MethodUtil.addEnumValues(type, builder);
            builder.id("value").name("value");
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                builder.asBoolean();
            }
            AbstractField<?> field =
                AttributeEditorUtil.createEditorField("valueEditor", new PropertyModel<String>(arg, "value"),
                    builder.build());
            add(field);
        } else {
            Map<String, String> beanAttrs = new HashMap<String, String>();
            arg.setValue(beanAttrs);
            arg.setBean(true);
            BeanEditorPanel field = new BeanEditorPanel("valueEditor", type, beanAttrs);
            add(field);
        }
    }

    private boolean hasStringOnlyConstructor(Class<?> type) {
        return MethodUtil.getStringOnlyConstructor(type) != null;
    }

}
