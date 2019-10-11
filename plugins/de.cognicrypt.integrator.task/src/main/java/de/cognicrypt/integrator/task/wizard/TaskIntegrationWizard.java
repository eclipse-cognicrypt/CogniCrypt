/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ModelAdvancedMode;

public class TaskIntegrationWizard extends Wizard {

	public TaskIntegrationWizard() {

	}

	@Override
	public void addPages() {
		addPage(new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD));

		addPage(new ClaferPage());

		addPage(new QuestionsPage());

		addPage(new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_LINK_ANSWERS, Constants.PAGE_TITLE_FOR_LINK_ANSWERS, Constants.PAGE_DESCIPTION_FOR_LINK_ANSWERS));

		addPage(new XslPage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		final ModelAdvancedMode objectForDataInNonGuidedMode =
				getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD).getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
		objectForDataInNonGuidedMode.setTask();
		final FileUtilities fileUtilities = new FileUtilities(objectForDataInNonGuidedMode.getNameOfTheTask());
		if (getContainer().getCurrentPage().getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
			if (objectForDataInNonGuidedMode.isGuidedModeChosen() == false // && this.objectForDataInNonGuidedMode.isGuidedModeForced() == false
			) {

				final String fileWriteAttemptResult = fileUtilities.writeFiles(objectForDataInNonGuidedMode.getLocationOfClaferFile(), objectForDataInNonGuidedMode.getLocationOfJSONFile(),
						objectForDataInNonGuidedMode.getLocationOfXSLFile(), objectForDataInNonGuidedMode.getLocationOfCustomLibrary(),
						objectForDataInNonGuidedMode.getLocationOfHelpXMLFile());
				// Check if the contents of the provided files are valid.
				if (fileWriteAttemptResult.equals("")) {
					fileUtilities.writeTaskToJSONFile(objectForDataInNonGuidedMode.getTask());
					fileUtilities.updateThePluginXMLFileWithHelpData(objectForDataInNonGuidedMode.getNameOfTheTask());
					return true;
				} else {
					final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
					errorBox.setText("Problems with the provided files.");
					errorBox.setMessage(fileWriteAttemptResult);
					errorBox.open();
					return false;
				}

			}
		} else {

			// collect input to task-related files from individual pages
			final ClaferModel claferModel = ((PageForTaskIntegratorWizard) getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompositeToHoldGranularUIElements().getClaferModel();
			final ArrayList<Question> questions =
					((PageForTaskIntegratorWizard) getPage(Constants.PAGE_NAME_FOR_LINK_ANSWERS)).getCompositeToHoldGranularUIElements().getListOfAllQuestions();
			final String xslFileContents = ((XslPage) getPage(Constants.PAGE_NAME_FOR_XSL_FILE_CREATION)).getCompositeForXsl().getXslTxtBox().getText();

			final File customLibLocation = null;

			final ModelAdvancedMode objectForDataInGuidedMode =
					getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD).getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
			objectForDataInGuidedMode.setTask();

			final String fileWriteAttemptResult = fileUtilities.writeFiles(claferModel, questions, xslFileContents, customLibLocation, null);
			if (fileWriteAttemptResult.equals("")) {
				fileUtilities.writeTaskToJSONFile(objectForDataInNonGuidedMode.getTask());
				return true;
			} else {
				final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
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
	 * @param needle name of the page to be found
	 * @return if found, wizard page of type {@link PageForTaskIntegratorWizard}, else null
	 */
	public PageForTaskIntegratorWizard getTIPageByName(final String needle) {
		final IWizardPage page = getPage(needle);
		if (PageForTaskIntegratorWizard.class.isInstance(page)) {
			return (PageForTaskIntegratorWizard) page;
		}

		return null;
	}

}
