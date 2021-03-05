/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
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
		
		if (!TaskIntegratorWizard.class.isInstance(getWizard())) {
			Activator.getDefault().logError(Constants.INSTANTIATED_BY_WRONG_WIZARD_ERROR);
		}

		final Button addQuestionBtn = new Button(container, SWT.NONE);
		addQuestionBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		addQuestionBtn.setText(Constants.ADD_QUESTION);

		addQuestionBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int questionID = IntegratorModel.getInstance().getQuestions().size();
				final Question questionDetails = new Question();
				questionDetails.setQuestionText("");
				questionDetails.setHelpText("");
				questionDetails.setAnswers(new ArrayList<Answer>());
				questionDetails.setId(questionID);

				// Update the array list.
				IntegratorModel.getInstance().getQuestions().add(questionDetails);
				// rebuild the UI
				questionsDisplayComposite.updateQuestionContainer();
			}
		});
	}

	
	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR
	 * Is used to determine whether wizard can flip to next page
	 */
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
