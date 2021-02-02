/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.ArrayList;
import java.util.List;

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
		
		//getCompositeToHoldGranularUIElements().updateQuestionContainer(listCryslTemplatesIdentifier);

		if (!TaskIntegrationWizard.class.isInstance(getWizard())) {
			Activator.getDefault().logError(Constants.INSTANTIATED_BY_WRONG_WIZARD_ERROR);
		}

		final QuestionDialog questionDialog = new QuestionDialog(parent.getShell());
		final Button addQuestionBtn = new Button(container, SWT.NONE);
		addQuestionBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		addQuestionBtn.setText(Constants.ADD_QUESTION);

		addQuestionBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int response = questionDialog.open();
				final int questionID = QuestionsPage.this.compositeToHoldGranularUIElements.getListOfAllQuestions().size();
				if (response == Window.OK) {
					QuestionsPage.this.counter++;
					// Question questionDetails = getDummyQuestion(questionDialog.getQuestionText(),questionDialog.getquestionType(),questionDialog.getAnswerValue());
					final Question questionDetails = questionDialog.getQuestionDetails();
					questionDetails.setId(questionID);

					// Update the array list.
					QuestionsPage.this.compositeToHoldGranularUIElements.getListOfAllQuestions().add(questionDetails);
					// rebuild the UI
					QuestionsPage.this.compositeToHoldGranularUIElements.updateQuestionContainer();
				}
			}
		});
	}
}
