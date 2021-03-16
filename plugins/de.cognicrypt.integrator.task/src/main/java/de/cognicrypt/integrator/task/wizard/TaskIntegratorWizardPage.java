/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.wizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.widgets.TaskInformationComposite;

public class TaskIntegratorWizardPage extends WizardPage {

	private TaskInformationComposite taskInformationComposite;
	
	/**
	 * Create the wizard.
	 */
	public TaskIntegratorWizardPage(final String name, final String title, final String description) {
		super(name);
		setTitle(title);
		setDescription(description);
		setPageComplete(false);
	}

	/**
	 * 
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		
		taskInformationComposite = new TaskInformationComposite(container, SWT.NONE, this);

		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(1, false));
	}

	
	/**
	 * Overwriting the getNextPage method to extract the list of all questions from highLevelQuestion page and forward the data to pageForLinkAnswers at runtime
	 */
	@Override
	public IWizardPage getNextPage() {
		final boolean isNextPressed = "nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[2].getMethodName());
		if (isNextPressed) {
			final boolean validatedNextPress = nextPressed(this);
			if (!validatedNextPress) {
				return this;
			}
		}

		return super.getNextPage();
	}

	/**
	 * Extract data from highLevelQuestions page and forward it to pageForLinkAnswers at runtime
	 *
	 * @param page highLevelQuestions page is received
	 * @return true always
	 */
	protected boolean nextPressed(final IWizardPage page) {
		final boolean ValidateNextPress = true;
		try {
			if (page.getName().equals(Constants.PAGE_TASK_INFORMATION)) {
				final IWizardPage nextPage = super.getNextPage();
				if (nextPage instanceof QuestionsPage) {
					
					if(IntegratorModel.getInstance().getQuestions().isEmpty()) {
						final Question firstQuestion = new Question();
						firstQuestion.setQuestionText("");
						firstQuestion.setHelpText("");
						
						ArrayList<Answer> answers = new ArrayList<>();
						for(String id : IntegratorModel.getInstance().getIdentifiers()) {
							Answer a = new Answer();
							a.setOption(id);
							a.setValue("");
							answers.add(a);
						}
						
						answers.get(0).setDefaultAnswer(true);
						
						firstQuestion.setAnswers(answers);
						firstQuestion.setId(0);
						
						IntegratorModel.getInstance().getQuestions().add(firstQuestion);
					}
					
					final QuestionsPage questionsPage = (QuestionsPage) nextPage;
					questionsPage.getQuestionsDisplayComposite().updateQuestionContainer();
				}
			}
		}catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}
		
		return ValidateNextPress;
	}

	
	/**
	 * Determines whether the next button is enabled
	 * Sets page to complete if there are no errors on mandatory fields, guided mode is chosen and multiple templates exist
	 */
	public void checkPageComplete() {
		if(taskInformationComposite == null)
			return;
		
		boolean mandatoryFields = checkMandatoryFields();
		
		boolean guidedMode = IntegratorModel.getInstance().isGuidedModeChosen();
		boolean multipleTemplatesExist = IntegratorModel.getInstance().getIdentifiers().size() > 1;
		
		// Set the page to incomplete if the validation failed on any of the text boxes
		setPageComplete(mandatoryFields && guidedMode && multipleTemplatesExist);
	}
	
	/**
	 * 
	 * @return true if there are no errors on zip file
	 */
	public boolean checkImportModeFinish() {		
		return !taskInformationComposite.getImportZIP().getDecFilePath().getDescriptionText().contains(Constants.ERROR);
	}
	
	/**
	 * 
	 * @return true if there are no errors on mandatory fields and json file
	 */
	public boolean checkNonGuidedFinish() {
		
		boolean mandatoryFields = checkMandatoryFields();
		boolean errorOnJSONFile = taskInformationComposite.getCompJSON().getDecFilePath().getDescriptionText().contains(Constants.ERROR);		
		
		return (mandatoryFields && !errorOnJSONFile);
	}
	
	/**
	 * 
	 * @return true if there are no errors on templates and icon file
	 */
	public boolean checkMandatoryFields() {
		boolean errorOnIconFile = taskInformationComposite.getCompPNG().getDecFilePath().getDescriptionText().contains(Constants.ERROR);
		boolean errorOnTemplates = taskInformationComposite.getDecTemplates().getDescriptionText().contains(Constants.ERROR);
		
		return !errorOnTemplates && !errorOnIconFile;
	}	
}
