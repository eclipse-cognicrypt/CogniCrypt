/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.models.ModelAdvancedMode;

public class TaskIntegrationWizard extends Wizard {

	public TaskIntegrationWizard() {

	}

	@Override
	public void addPages() {
		addPage(new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD));


		addPage(new QuestionsPage());

		addPage(new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_LINK_ANSWERS, Constants.PAGE_TITLE_FOR_LINK_ANSWERS, Constants.PAGE_DESCIPTION_FOR_LINK_ANSWERS));

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
		Task task = objectForDataInNonGuidedMode.getTask();
		HashMap<String, File> crylTemplatesWithOption = objectForDataInNonGuidedMode.getCrylTemplatesWithOption();
		if (getContainer().getCurrentPage().getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
			if (objectForDataInNonGuidedMode.isGuidedModeChosen() == false // && this.objectForDataInNonGuidedMode.isGuidedModeForced() == false
			) {

				final String fileWriteAttemptResult = fileUtilities.writeCryslTemplate(crylTemplatesWithOption, objectForDataInNonGuidedMode.getLocationOfJSONFile(), objectForDataInNonGuidedMode.getLocationOfIconFile());
				// Check if the contents of the provided files are valid.
				if (fileWriteAttemptResult.equals("")) {
					// Adding the trimmed task name to ensure it matches with the name of the image stored (refer FileUtilities)
					task.setImage(task.getName().replaceAll("[^A-Za-z0-9]", ""));
					task.setCodeGen(CodeGenerators.CrySL);
					fileUtilities.writeTaskToJSONFile(task);
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
			
			final String fileWriteAttemptResult = fileUtilities.writeCryslTemplate(objectForDataInNonGuidedMode.getCrylTemplatesWithOption(),  objectForDataInNonGuidedMode.getLocationOfIconFile());
			final ArrayList<Question> questions =
					((PageForTaskIntegratorWizard) getPage(Constants.PAGE_NAME_FOR_LINK_ANSWERS)).getCompositeToHoldGranularUIElements().getListOfAllQuestions();
			fileUtilities.writeJSONFile(questions);
			// Check if the contents of the provided files are valid.
			if (fileWriteAttemptResult.equals("")) {
				// Adding the trimmed task name to ensure it matches with the name of the image stored (refer FileUtilities)
				task.setImage(task.getName().replaceAll("[^A-Za-z0-9]", ""));
				task.setCodeGen(CodeGenerators.CrySL);
				task.setModelFile("");
				task.setAdditionalResources("");
				fileUtilities.writeTaskToJSONFile(task);
				fileUtilities.updateThePluginXMLFileWithHelpData(objectForDataInNonGuidedMode.getNameOfTheTask());
			} else {
				final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
				errorBox.setText("Problems with the provided files.");
				errorBox.setMessage(fileWriteAttemptResult);
				errorBox.open();
			}
			
			
			

			/*final File customLibLocation = null;

			final ModelAdvancedMode objectForDataInGuidedMode =
					getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD).getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
			objectForDataInGuidedMode.setTask();
			if (fileWriteAttemptResult.equals("")) {
				fileUtilities.writeTaskToJSONFile(task);
				return true;
			} else {
				final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
				errorBox.setText("Problems with the provided data.");
				errorBox.setMessage(fileWriteAttemptResult);
				errorBox.open();
				return false;
			}*/
		}
		return true;
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
