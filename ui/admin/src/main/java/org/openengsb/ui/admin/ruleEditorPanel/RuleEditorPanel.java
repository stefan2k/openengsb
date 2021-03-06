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

package org.openengsb.ui.admin.ruleEditorPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.openengsb.core.common.workflow.RuleBaseException;
import org.openengsb.core.common.workflow.model.RuleBaseElementId;
import org.openengsb.core.common.workflow.model.RuleBaseElementType;

@SuppressWarnings("serial")
public class RuleEditorPanel extends Panel {

    private RuleManagerProvider ruleManagerProvider;
    private TextArea<String> textArea;
    private DropDownChoice<RuleBaseElementId> ruleChoice;
    private TextField<String> newRuleTextField;
    private DropDownChoice<RuleBaseElementType> typeChoice;
    private AjaxButton newButton;
    private AjaxButton cancelButton;
    private IndicatingAjaxButton saveButton;
    private boolean newRuleMode;
    private Form<Object> form;
    private FeedbackPanel feedbackPanel;

    public RuleEditorPanel(String id, RuleManagerProvider ruleManagerProvider) {
        super(id);
        this.ruleManagerProvider = ruleManagerProvider;
        initPage();
    }

    private void initPage() {
        form = new Form<Object>("form");
        initTypeChoice(form);
        initRuleChoice(form);
        newRuleTextField = new TextField<String>("ruleName", new Model<String>("rulename"));
        newRuleTextField.setOutputMarkupId(true);
        newRuleTextField.setVisible(false);
        form.add(newRuleTextField);
        initButtons(form);
        initTextArea(form);
        add(form);
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
    }

    private void initTextArea(Form<Object> form) {
        Model<String> textAreaModel = new Model<String>();
        textArea = new TextArea<String>("text", textAreaModel);
        textArea.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                enableButtons(target);
            }
        });
        textArea.setEnabled(false);
        textArea.setOutputMarkupId(true);
        form.add(textArea);
    }

    private void enableButtons(AjaxRequestTarget target) {
        saveButton.setEnabled(true);
        target.addComponent(saveButton);
        cancelButton.setEnabled(true);
        target.addComponent(cancelButton);
    }

    private void disableButtons(AjaxRequestTarget target) {
        saveButton.setEnabled(false);
        target.addComponent(saveButton);
        cancelButton.setEnabled(false);
        target.addComponent(cancelButton);
    }

    private void initButtons(Form<Object> form) {
        saveButton = new IndicatingAjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(textArea);
                if (newRuleMode) {
                    RuleBaseElementId ruleBaseElementId = new RuleBaseElementId(typeChoice.getModelObject(),
                            newRuleTextField.getModelObject());
                    try {
                        ruleManagerProvider.getRuleManager().add(ruleBaseElementId, textArea.getModelObject());
                    } catch (RuleBaseException e) {
                        target.addComponent(feedbackPanel);
                        error(e.getLocalizedMessage());
                    }
                    resetAfterNew(target);
                    ruleChoice.setModelObject(ruleBaseElementId);
                } else {
                    updateRule(target);
                }
            }
        };
        saveButton.setEnabled(false);
        saveButton.setOutputMarkupId(true);
        form.add(saveButton);
        cancelButton = new AjaxButton("cancel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (newRuleMode) {
                    resetAfterNew(target);
                } else {
                    reloadTextArea(target);
                }
            }
        };
        cancelButton.setEnabled(false);
        cancelButton.setOutputMarkupId(true);
        form.add(cancelButton);
        newButton = new AjaxButton("new") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                enterNewRuleMode(target);
            }
        };
        form.add(newButton);
    }

    private void initRuleChoice(Form<Object> form) {
        ruleChoice = new DropDownChoice<RuleBaseElementId>("ruleChoice");
        loadRuleChoice();
        ruleChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                reloadTextArea(target);
            }
        });
        ruleChoice.setOutputMarkupId(true);
        form.add(ruleChoice);
    }

    private void initTypeChoice(Form<Object> form) {
        List<RuleBaseElementType> ruleTypes = Arrays.asList(RuleBaseElementType.values());
        typeChoice = new DropDownChoice<RuleBaseElementType>("typeChoice", ruleTypes);
        typeChoice.setModel(new Model<RuleBaseElementType>());
        typeChoice.getModel().setObject(RuleBaseElementType.Rule);
        typeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (!newRuleMode) {
                    reloadTextArea(target);
                    reloadRuleChoice(target);

                }
            }
        });
        form.add(typeChoice);
    }

    private void reloadRuleChoice(AjaxRequestTarget target) {
        target.addComponent(ruleChoice);
        loadRuleChoice();
    }

    private void loadRuleChoice() {
        RuleBaseElementType selectedType = typeChoice.getModelObject();
        List<RuleBaseElementId> choices = new ArrayList<RuleBaseElementId>(ruleManagerProvider.getRuleManager().list(
                selectedType));
        ruleChoice.setChoices(choices);
        ruleChoice.setModel(new Model<RuleBaseElementId>());
    }

    private void reloadTextArea(AjaxRequestTarget target) {
        target.addComponent(textArea);
        RuleBaseElementId selection = ruleChoice.getModelObject();
        if (selection != null) {
            textArea.setModel(new Model<String>(ruleManagerProvider.getRuleManager().get(selection)));
            textArea.setEnabled(true);
        } else {
            textArea.setModel(new Model<String>());
            textArea.setEnabled(false);
        }
        disableButtons(target);
    }

    private void updateRule(AjaxRequestTarget target) {
        RuleBaseElementId selection = ruleChoice.getModelObject();
        String text = textArea.getModelObject();
        if (selection != null && text != null) {
            try {
                ruleManagerProvider.getRuleManager().update(selection, text);
            } catch (RuleBaseException e) {
                error(e.getLocalizedMessage());
            }
        }
        disableButtons(target);
    }

    private void resetAfterNew(AjaxRequestTarget target) {
        newRuleMode = false;
        newButton.setEnabled(true);
        newRuleTextField.setVisible(false);
        ruleChoice.setVisible(true);
        loadRuleChoice();
        target.addComponent(form);
        target.addComponent(newButton);
        target.addComponent(ruleChoice);
        disableButtons(target);
    }

    private void enterNewRuleMode(AjaxRequestTarget target) {
        newRuleMode = true;
        newRuleTextField.setVisible(true);
        ruleChoice.setVisible(false);
        textArea.setEnabled(true);
        if (textArea.getModelObject() == null) {
            textArea.setModel(new Model<String>(""));
        }
        newButton.setEnabled(false);
        saveButton.setEnabled(true);
        cancelButton.setEnabled(true);
        target.addComponent(form);
        target.addComponent(newRuleTextField);
        target.addComponent(newRuleTextField);
        target.addComponent(ruleChoice);
        target.addComponent(textArea);
        target.addComponent(newButton);
    }
}
