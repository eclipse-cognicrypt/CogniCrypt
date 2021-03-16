/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.widgets.QuestionInformationComposite;
import de.cognicrypt.integrator.task.widgets.QuestionsDisplayComposite;

public class QuestionsPage extends TaskIntegratorWizardPage {
	
	QuestionsDisplayComposite questionsDisplayComposite;
	QuestionInformationComposite questionInformationComposite;
	
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

		questionsDisplayComposite = new QuestionsDisplayComposite(container, this);
		
		// fill the available space with the big composite
		questionsDisplayComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Button addQuestionBtn = new Button(container, SWT.NONE);
		addQuestionBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		addQuestionBtn.setText(Constants.ADD_QUESTION);

		addQuestionBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				addQuestion();
			}
		});
	}
	
	
	/**
	 * Adds a question to the plugin state
	 */
	private void addQuestion() {
		try {
			IntegratorModel.getInstance().addQuestion();
			
			questionsDisplayComposite.updateQuestionContainer();
		}catch(ErrorMessageException e) {
			MessageDialog.openError(getShell(), "Warning", e.getMessage());
		}
	}

	
	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR
	 * Is used to determine whether wizard can flip to next page
	 */
	@Override
	public void checkPageComplete() {
		
		if(questionsDisplayComposite.getQuestionsInformationComposites().isEmpty())
			return;
		
		boolean isPageComplete = true;
		
		// Iterate over all question composites and check if there is any error
		for(QuestionInformationComposite questionInformationComposite : questionsDisplayComposite.getQuestionsInformationComposites()) {
			if (questionInformationComposite.questionDec.getDescriptionText().contains(Constants.ERROR)
					|| questionInformationComposite.answersDec.getDescriptionText().contains(Constants.ERROR)) {
				isPageComplete = false;
				break;
			}
			
		}
		
		setPageComplete(isPageComplete);
	}
	
	public QuestionsDisplayComposite getQuestionsDisplayComposite() {
		return questionsDisplayComposite;
	}
}
