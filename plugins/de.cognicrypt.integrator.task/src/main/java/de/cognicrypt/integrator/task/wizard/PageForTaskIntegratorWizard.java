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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.widgets.CompositeBrowseForFile;
import de.cognicrypt.integrator.task.widgets.CompositeChoiceForModeOfWizard;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldGranularUIElements;

public class PageForTaskIntegratorWizard extends WizardPage {

	private CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard = null;
	protected CompositeToHoldGranularUIElements compositeToHoldGranularUIElements = null;

	int counter = 0;// TODO for testing only.

	TreeViewer treeViewer;

	/**
	 * Create the wizard.
	 */
	public PageForTaskIntegratorWizard(final String name, final String title, final String description) {
		super(name);
		setTitle(title);
		setDescription(description);
		setPageComplete(false);
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		switch (getName()) {
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				setCompositeChoiceForModeOfWizard(new CompositeChoiceForModeOfWizard(container, SWT.NONE, this));
				break;
			case Constants.PAGE_NAME_FOR_LINK_ANSWERS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				break;
		}
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

		if (getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD) && !getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode().isGuidedModeChosen()) {
			return null;
		}
		/*
		 * This is for debugging only. To be removed for the final version. TODO Please add checks on the pages after mode selection to mark those pages as completed, or restrict the
		 * finish button.
		 */
		final IWizardPage nextPage = super.getNextPage();
		if (nextPage != null) {
			((WizardPage) nextPage).setPageComplete(true);
		}

		return nextPage;

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
						for (final Question question : listOfAllQuestions) {
							comp.getListOfAllQuestions().add(question);
							comp.addQuestionUIElements(question, true);
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
		return ValidateNextPress;
	}

	@Override
	public boolean canFlipToNextPage() {

		// each case needs to be handled separately. By default all cases will return false.
		/*
		 * switch(this.getName()){ case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD: if(((boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN) == true
		 * || (boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED) == true) && !this.isPageComplete()){ return true; } case
		 * Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION: return false; case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION: return false; case Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS:
		 * return false; default: return false; }
		 */
		return super.canFlipToNextPage();

	}

	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR.
	 */
	public void checkIfModeSelectionPageIsComplete() {
		boolean errorOnFileWidgets = false;
		// The first child of the composite is a group. Get the children of this group to iterated over.
		for (final Control control : ((Composite) getCompositeChoiceForModeOfWizard().getChildren()[0]).getChildren()) {
			// Check if the child is an instance of group and is visible.
			if (control instanceof Composite && control.isVisible()) {

				// Get the children of this group and iterate over them. These are the widgets that get the file data. This loop generalizes for all these widgets.
				for (final Control subGroup : ((Composite) control).getChildren()) {
					if (subGroup instanceof CompositeBrowseForFile) {
						final CompositeBrowseForFile tempVaraiable = (CompositeBrowseForFile) subGroup;
						if ((tempVaraiable).getDecFilePath().getDescriptionText().contains(Constants.ERROR)) {
							errorOnFileWidgets = true;
						}
					}

				}

			}
		}

		// Check if validation failed on the task name.
		final boolean errorOnTaskName = getCompositeChoiceForModeOfWizard().getDecNameOfTheTask().getDescriptionText().contains(Constants.ERROR);

		// Set the page to incomplete if the validation failed on any of the text boxes.
		if (errorOnTaskName || errorOnFileWidgets) {
			setPageComplete(false);

		} else {
			setPageComplete(true);
		}
	}

	/**
	 * Return the composite for the first page, i.e. to choose the mode of the wizard.
	 *
	 * @return the compositeChoiceForModeOfWizard
	 */
	public CompositeChoiceForModeOfWizard getCompositeChoiceForModeOfWizard() {
		return this.compositeChoiceForModeOfWizard;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 *
	 * @param compositeChoiceForModeOfWizard the compositeChoiceForModeOfWizard to set
	 */
	private void setCompositeChoiceForModeOfWizard(final CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard) {
		this.compositeChoiceForModeOfWizard = compositeChoiceForModeOfWizard;
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

	public int getCounter() {
		return this.counter;
	}

}
