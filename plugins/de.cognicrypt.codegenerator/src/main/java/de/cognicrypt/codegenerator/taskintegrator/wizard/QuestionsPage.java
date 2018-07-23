/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.wizard;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.core.Constants;

public class QuestionsPage extends PageForTaskIntegratorWizard {

	public QuestionsPage() {
		super(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_TITLE_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, this.getName()));
		// fill the available space on the with the big composite
		getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TaskIntegrationWizard tiWizard = null;

		if (TaskIntegrationWizard.class.isInstance(getWizard())) {
			tiWizard = (TaskIntegrationWizard) getWizard();
		} else {
			Activator.getDefault().logError("PageForTaskIntegratorWizard was instantiated by a wizard other than TaskIntegrationWizard");
		}

		PageForTaskIntegratorWizard claferPage = tiWizard.getTIPageByName(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION);
		CompositeToHoldGranularUIElements claferPageComposite = (CompositeToHoldGranularUIElements) claferPage.getCompositeToHoldGranularUIElements();

		QuestionDialog questionDialog = new QuestionDialog(parent.getShell());
		Button qstnDialog = new Button(container, SWT.NONE);
		qstnDialog.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		qstnDialog.setText("Add Question");

		qstnDialog.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int response = questionDialog.open();
				int qID = compositeToHoldGranularUIElements.getListOfAllQuestions().size();
				if (response == Window.OK) {
					counter++;
					//Question questionDetails = getDummyQuestion(questionDialog.getQuestionText(),questionDialog.getquestionType(),questionDialog.getAnswerValue());
					Question questionDetails = questionDialog.getQuestionDetails();
					questionDetails.setId(qID);

					// Update the array list.
					compositeToHoldGranularUIElements.getListOfAllQuestions().add(questionDetails);
					compositeToHoldGranularUIElements.addQuestionUIElements(questionDetails, claferPageComposite.getClaferModel(), false);
					// rebuild the UI
					compositeToHoldGranularUIElements.updateQuestionContainer();
				}
			}
		});
	}

}
