/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeBrowseForFile;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeChoiceForModeOfWizard;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.core.Constants;

public class PageForTaskIntegratorWizard extends WizardPage {

	private CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard = null;
	protected CompositeToHoldGranularUIElements compositeToHoldGranularUIElements = null;

	int counter = 0;// TODO for testing only.
	protected ArrayList<ClaferFeature> cfrFeatures;

	TreeViewer treeViewer;

	/**
	 * Create the wizard.
	 */
	public PageForTaskIntegratorWizard(String name, String title, String description) {
		super(name);
		setTitle(title);
		setDescription(description);
		this.setPageComplete(false);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		switch (this.getName()) {
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				setCompositeChoiceForModeOfWizard(new CompositeChoiceForModeOfWizard(container, SWT.NONE, this));
				break;
			case Constants.PAGE_NAME_FOR_LINK_ANSWERS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, this.getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				break;
		}
	}

	/**
	 * Get the location of the compiled Javascript file.
	 * 
	 * @return the location of the JS file in the form of a string.
	 */
	public String getJSFilePath() {
		return ((ClaferPage) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompiledClaferModelPath();
	}

	/**
	 * Overwriting the getNextPage method to extract the list of all questions from highLevelQuestion page and forward the data to pageForLinkAnswers at runtime
	 */
	public IWizardPage getNextPage() {
		boolean isNextPressed = "nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[2].getMethodName());
		if (isNextPressed) {
			boolean validatedNextPress = this.nextPressed(this);
			if (!validatedNextPress) {
				return this;
			}
		}

		if (this.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD) && !getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode().isGuidedModeChosen()) {
			return null;
		}

		if (this.getName().equals(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)) {

		}
		/*
		 * This is for debugging only. To be removed for the final version. TODO Please add checks on the pages after mode selection to mark those pages as completed, or restrict
		 * the finish button.
		 */
		IWizardPage nextPage = super.getNextPage();
		if (nextPage != null) {
			((WizardPage) nextPage).setPageComplete(true);

			// refresh the TreeViewer when coming to the XSL page 
			if (nextPage.getName().equals(Constants.PAGE_NAME_FOR_XSL_FILE_CREATION)) {
				if (((PageForTaskIntegratorWizard) nextPage).treeViewer != null) {
					((PageForTaskIntegratorWizard) nextPage).treeViewer.refresh();
					((XslPage) nextPage).setTreeViewerInput();
				}
			} else if (nextPage.getName().equals(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)) {
				((ClaferPage) nextPage).initializeClaferModel();
			}
		}

		return nextPage;

	}

	/**
	 * Extract data from highLevelQuestions page and forward it to pageForLinkAnswers at runtime
	 * 
	 * @param page
	 *        highLevelQuestions page is received
	 * @return true always
	 */
	protected boolean nextPressed(IWizardPage page) {
		boolean ValidateNextPress = true;
		try {
			if (page.getName().equals(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS)) {
				PageForTaskIntegratorWizard highLevelQuestionPage = (PageForTaskIntegratorWizard) page;
				CompositeToHoldGranularUIElements highLevelQuestionPageComposite = (CompositeToHoldGranularUIElements) highLevelQuestionPage.getCompositeToHoldGranularUIElements();
				IWizardPage nextPage = super.getNextPage();
				ArrayList<Question> listOfAllQuestions = highLevelQuestionPageComposite.getListOfAllQuestions();
				if (nextPage instanceof PageForTaskIntegratorWizard) {
					PageForTaskIntegratorWizard pftiw = (PageForTaskIntegratorWizard) nextPage;
					if (pftiw.getCompositeToHoldGranularUIElements() instanceof CompositeToHoldGranularUIElements) {
						CompositeToHoldGranularUIElements comp = (CompositeToHoldGranularUIElements) pftiw.getCompositeToHoldGranularUIElements();
						if (comp.getListOfAllQuestions().size() > 0) {
							comp.deleteAllQuestion();
						}
						for (Question question : listOfAllQuestions) {
							comp.getListOfAllQuestions().add(question);
							comp.addQuestionUIElements(question, null, true);
							//to rebuild the UI
							comp.updateLayout();
						}

					}
				}
			}

		} catch (Exception ex) {
			Activator.getDefault().logError(ex);

		}
		return ValidateNextPress;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {

		// each case needs to be handled separately. By default all cases will return false. 
		/*
		 * switch(this.getName()){ case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD: if(((boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN) ==
		 * true || (boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED) == true) && !this.isPageComplete()){ return true; } case
		 * Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION: return false; case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION: return false; case
		 * Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS: return false; default: return false; }
		 */
		return super.canFlipToNextPage();

	}

	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR.
	 */
	public void checkIfModeSelectionPageIsComplete() {
		boolean errorOnFileWidgets = false;
		// The first child of the composite is a group. Get the children of this group to iterated over.
		for (Control control : ((Composite) getCompositeChoiceForModeOfWizard().getChildren()[0]).getChildren()) {
			// Check if the child is an instance of group and is visible.
			if (control instanceof Composite && control.isVisible()) {

				// Get the children of this group and iterate over them. These are the widgets that get the file data. This loop generalizes for all these widgets.
				for (Control subGroup : ((Composite) control).getChildren()) {
					if (subGroup instanceof CompositeBrowseForFile) {
						CompositeBrowseForFile tempVaraiable = (CompositeBrowseForFile) subGroup;
						if ((tempVaraiable).getDecFilePath().getDescriptionText().contains(Constants.ERROR)) {
							errorOnFileWidgets = true;
						}
					}

				}

			}
		}

		// Check if validation failed on the task name.
		boolean errorOnTaskName = getCompositeChoiceForModeOfWizard().getDecNameOfTheTask().getDescriptionText().contains(Constants.ERROR);

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
		return compositeChoiceForModeOfWizard;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * 
	 * @param compositeChoiceForModeOfWizard
	 *        the compositeChoiceForModeOfWizard to set
	 */
	private void setCompositeChoiceForModeOfWizard(CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard) {
		this.compositeChoiceForModeOfWizard = compositeChoiceForModeOfWizard;
	}

	/**
	 * @return the compositeToHoldGranularUIElements
	 */
	public CompositeToHoldGranularUIElements getCompositeToHoldGranularUIElements() {
		return compositeToHoldGranularUIElements;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * 
	 * @param compositeToHoldGranularUIElements
	 *        the compositeToHoldGranularUIElements to set
	 */
	public void setCompositeToHoldGranularUIElements(CompositeToHoldGranularUIElements compositeToHoldGranularUIElements) {
		this.compositeToHoldGranularUIElements = compositeToHoldGranularUIElements;
	}

	public int getCounter() {
		return counter;
	}

}
