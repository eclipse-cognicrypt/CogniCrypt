/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.widgets.TaskInformationComposite;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldGranularUIElements;

public class TaskIntegratorWizardPage extends WizardPage {

	private TaskInformationComposite compositeTaskInformation;
	private CompositeToHoldGranularUIElements compositeToHoldGranularUIElements;
	
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
		
		compositeTaskInformation = new TaskInformationComposite(container, SWT.NONE, this);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));
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
					final QuestionsPage questionsPage = (QuestionsPage) nextPage;
					questionsPage.getCompositeToHoldGranularUIElements().updateQuestionContainer();
				}
			}
		}catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}
		/**
		try {
			if (page.getName().equals(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS)) {
				final PageForTaskIntegratorWizard highLevelQuestionPage = (PageForTaskIntegratorWizard) page;
				final CompositeToHoldGranularUIElements highLevelQuestionPageComposite = highLevelQuestionPage.getCompositeToHoldGranularUIElements();
				final IWizardPage nextPage = super.getNextPage();
				final ArrayList<Question> listOfAllQuestions = highLevelQuestionPageComposite.getListOfAllQuestions();
				if (nextPage instanceof PageForTaskIntegratorWizard) {
					final PageForTaskIntegratorWizard pftiw = (PageForTaskIntegratorWizard) nextPage;
					if (pftiw.getCompositeToHoldGranularUIElements() instanceof CompositeToHoldGranularUIElements) {
						final CompositeToHoldGranularUIElements comp = pftiw.getCompositeToHoldGranularUIElements();
						if (comp.getListOfAllQuestions().size() > 0) {
							comp.deleteAllQuestion();
						}
						for(CompositeBrowseForFile b : getCompositeChoiceForModeOfWizard().getLstCryslTemplates()) {
							listCryslTemplatesIdentifier.add(b.getText());
						}
						for (final Question question : listOfAllQuestions) {
							comp.getListOfAllQuestions().add(question);
							comp.addQuestionUIElements(question, true, listCryslTemplatesIdentifier);
							// to rebuild the UI
							comp.updateLayout();
						}
					}
				}
				
				
			}
		}
		catch (final Exception ex) {
			Activator.getDefault().logError(ex);

		}
		**/
		return ValidateNextPress;
	}

	
	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR
	 * Is used to determine whether wizard can flip to next page
	 */
	public void checkPageComplete() {
		if(compositeTaskInformation == null)
			return;
		
		boolean mandatoryFields = checkMandatoryFields();
		
		// Validity of template files should be checked during add routine
		
		boolean guidedMode = IntegratorModel.getInstance().isGuidedModeChosen();
		boolean multipleTemplatesExist = IntegratorModel.getInstance().getIdentifiers().size() > 1;
		
		// Set the page to incomplete if the validation failed on any of the text boxes
		setPageComplete(mandatoryFields && guidedMode && multipleTemplatesExist);
	}
		
	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR.
	 * Is used to determine wheter wizard can finish early
	 */
	public boolean checkNonGuidedFinish() {
		
		boolean mandatoryFields = checkMandatoryFields();
		
		boolean guidedMode = IntegratorModel.getInstance().isGuidedModeChosen();
		boolean errorOnJSONFile = compositeTaskInformation.getCompJSON().getDecFilePath().getDescriptionText().contains(Constants.ERROR);		
		
		return mandatoryFields && !guidedMode && !errorOnJSONFile;
	}
	
	public boolean checkSingleFinish() {
		
		boolean mandatoryFields = checkMandatoryFields();
		
		boolean identifierEqualsTaskName = IntegratorModel.getInstance().getIdentifiers().get(0).contentEquals(IntegratorModel.getInstance().getTaskName());	
		
		return mandatoryFields && identifierEqualsTaskName;
	}
	
	public boolean checkMandatoryFields() {
		boolean errorOnIconFile = compositeTaskInformation.getCompPNG().getDecFilePath().getDescriptionText().contains(Constants.ERROR);
		boolean templatesEmpty = IntegratorModel.getInstance().isTemplatesEmpty(); 
		
		return !errorOnIconFile && !templatesEmpty;
	}	


	/**
	 * @return the compositeToHoldGranularUIElements
	 */
	public CompositeToHoldGranularUIElements getCompositeToHoldGranularUIElements() {
		return this.compositeToHoldGranularUIElements;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 *
	 * @param compositeToHoldGranularUIElements the compositeToHoldGranularUIElements to set
	 */
	public void setCompositeToHoldGranularUIElements(final CompositeToHoldGranularUIElements compositeToHoldGranularUIElements) {
		this.compositeToHoldGranularUIElements = compositeToHoldGranularUIElements;
	}
}
