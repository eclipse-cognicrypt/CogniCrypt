/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldGranularUIElements;

public class QuestionsPage extends PageForTaskIntegratorWizard {

	public QuestionsPage() {
		super(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_TITLE_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS);
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, getName()));
		// fill the available space on the with the big composite
		getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TaskIntegrationWizard tiWizard = null;

		if (TaskIntegrationWizard.class.isInstance(getWizard())) {
			tiWizard = (TaskIntegrationWizard) getWizard();
		} else {
			Activator.getDefault().logError("PageForTaskIntegratorWizard was instantiated by a wizard other than TaskIntegrationWizard");
		}


		final QuestionDialog questionDialog = new QuestionDialog(parent.getShell());
		final Button qstnDialog = new Button(container, SWT.NONE);
		qstnDialog.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		qstnDialog.setText("Add Question");

		qstnDialog.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int response = questionDialog.open();
				final int qID = QuestionsPage.this.compositeToHoldGranularUIElements.getListOfAllQuestions().size();
				if (response == Window.OK) {
					QuestionsPage.this.counter++;
					// Question questionDetails = getDummyQuestion(questionDialog.getQuestionText(),questionDialog.getquestionType(),questionDialog.getAnswerValue());
					final Question questionDetails = questionDialog.getQuestionDetails();
					questionDetails.setId(qID);

					// Update the array list.
					QuestionsPage.this.compositeToHoldGranularUIElements.getListOfAllQuestions().add(questionDetails);
					// rebuild the UI
					QuestionsPage.this.compositeToHoldGranularUIElements.updateQuestionContainer();
				}
			}
		});
	}

}
