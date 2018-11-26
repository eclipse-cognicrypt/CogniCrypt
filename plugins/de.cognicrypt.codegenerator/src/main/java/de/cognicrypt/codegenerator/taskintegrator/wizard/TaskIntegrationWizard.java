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

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeForXsl;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.core.Constants;

public class TaskIntegrationWizard extends Wizard {

	public TaskIntegrationWizard() {

	}

	@Override
	public void addPages() {
		this.addPage(
			new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD));

		this.addPage(new ClaferPage());

		this.addPage(new QuestionsPage());

		this.addPage(new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_LINK_ANSWERS, Constants.PAGE_TITLE_FOR_LINK_ANSWERS, Constants.PAGE_DESCIPTION_FOR_LINK_ANSWERS));

		this.addPage(new XslPage());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		ModelAdvancedMode objectForDataInNonGuidedMode = getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD).getCompositeChoiceForModeOfWizard()
			.getObjectForDataInNonGuidedMode();
		objectForDataInNonGuidedMode.setTask();
		FileUtilities fileUtilities = new FileUtilities(objectForDataInNonGuidedMode.getNameOfTheTask());
		if (this.getContainer().getCurrentPage().getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
			if (objectForDataInNonGuidedMode.isGuidedModeChosen() == false //&& this.objectForDataInNonGuidedMode.isGuidedModeForced() == false
			) {

				String fileWriteAttemptResult = fileUtilities.writeFiles(objectForDataInNonGuidedMode.getLocationOfClaferFile(),
					objectForDataInNonGuidedMode.getLocationOfJSONFile(), objectForDataInNonGuidedMode.getLocationOfXSLFile(),
					objectForDataInNonGuidedMode.getLocationOfCustomLibrary(), objectForDataInNonGuidedMode.getLocationOfHelpXMLFile());
				// Check if the contents of the provided files are valid.
				if (fileWriteAttemptResult.equals("")) {
					fileUtilities.writeTaskToJSONFile(objectForDataInNonGuidedMode.getTask());
					fileUtilities.updateThePluginXMLFileWithHelpData(objectForDataInNonGuidedMode.getNameOfTheTask());
					return true;
				} else {
					MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
					errorBox.setText("Problems with the provided files.");
					errorBox.setMessage(fileWriteAttemptResult);
					errorBox.open();
					return false;
				}

			}
		} else {

			// collect input to task-related files from individual pages
			ClaferModel claferModel = ((CompositeToHoldGranularUIElements) ((PageForTaskIntegratorWizard) getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION))
				.getCompositeToHoldGranularUIElements()).getClaferModel();
			ArrayList<Question> questions = ((CompositeToHoldGranularUIElements) ((PageForTaskIntegratorWizard) getPage(Constants.PAGE_NAME_FOR_LINK_ANSWERS))
				.getCompositeToHoldGranularUIElements()).getListOfAllQuestions();
			String xslFileContents = ((CompositeForXsl) ((XslPage) getPage(Constants.PAGE_NAME_FOR_XSL_FILE_CREATION)).getCompositeForXsl()).getXslTxtBox().getText();

			File customLibLocation = null;

			ModelAdvancedMode objectForDataInGuidedMode = getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD).getCompositeChoiceForModeOfWizard()
				.getObjectForDataInNonGuidedMode();
			objectForDataInGuidedMode.setTask();

			String fileWriteAttemptResult = fileUtilities.writeFiles(claferModel, questions, xslFileContents, customLibLocation, null);
			if (fileWriteAttemptResult.equals("")) {
				fileUtilities.writeTaskToJSONFile(objectForDataInNonGuidedMode.getTask());
				return true;
			} else {
				MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
				errorBox.setText("Problems with the provided data.");
				errorBox.setMessage(fileWriteAttemptResult);
				errorBox.open();
				return false;
			}
		}
		return false;
	}

	/**
	 * Get the first page of this wizard that is of type {@link PageForTaskIntegratorWizard} and matches the given page name
	 * 
	 * @param needle
	 *        name of the page to be found
	 * @return if found, wizard page of type {@link PageForTaskIntegratorWizard}, else null
	 */
	public PageForTaskIntegratorWizard getTIPageByName(String needle) {
		IWizardPage page = getPage(needle);
		if (PageForTaskIntegratorWizard.class.isInstance(page)) {
			return (PageForTaskIntegratorWizard) page;
		}

		return null;
	}

}
