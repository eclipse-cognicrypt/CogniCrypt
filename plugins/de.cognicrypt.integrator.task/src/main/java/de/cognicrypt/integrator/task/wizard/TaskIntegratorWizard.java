/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class TaskIntegratorWizard extends Wizard {

	private TaskIntegratorWizardPage taskInformation;

	public TaskIntegratorWizard() {
		setWindowTitle("CogniCrypt Task Integrator");

		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "platform:/plugin/de.cognicrypt.core/icons/cognicrypt-medium.png");
		setDefaultPageImageDescriptor(image);
	}

	/**
	 * Adds the task information and question page
	 */
	@Override
	public void addPages() {
		taskInformation = new TaskIntegratorWizardPage(Constants.PAGE_TASK_INFORMATION, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD);
		addPage(taskInformation);

		addPage(new QuestionsPage());
	}


	@Override
	/**
	 * Checks if the task integrator wizard can finish (determines if Generate button is enabled)
	 */
	public boolean canFinish() {

		if(IntegratorModel.getInstance().isImportModeChosen()) {
			return taskInformation.checkImportModeFinish();
		}

		if(!IntegratorModel.getInstance().isGuidedModeChosen()) {
			return taskInformation.checkNonGuidedFinish();
		}
			
		if(IntegratorModel.getInstance().getIdentifiers().size() == 1) {
			return taskInformation.checkMandatoryFields();
		}
			
		return super.canFinish();
	}

	/**
	 * Finishes the task integrator by generating the local file structure and copying the specified tasks
	 * @return true if task could be generated successfully
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IntegratorModel integratorModel = IntegratorModel.getInstance();

		if (integratorModel.checkForUnusedIdentifiers()) {
			final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			confirmationMessageBox.setMessage(Constants.WARNING_TEMPLATE_NOT_USED);
			confirmationMessageBox.setText(Constants.WARNING_TEMPLATE_NOT_USED_TITLE);
			final int response = confirmationMessageBox.open();
			if (response == SWT.CANCEL) { // abort
				return false;
			}
		}

		try {
			integratorModel.copyTask();

			final MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
			msgBox.setText(Constants.TASK_SUCCESFULLY_IMPORTED);
			msgBox.setMessage(integratorModel.isImportModeChosen() ? Constants.IMPORT_SUCCESSFUL : Constants.EXPORT_SUCCESSFUL);
			msgBox.open();

			return true;
		}catch(ErrorMessageException e) {
			final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
			errorBox.setText(e.getText());
			errorBox.setMessage(e.getMessage());
			errorBox.open();

			return false;
		}
	}



	/**
	 * Get the first page of this wizard that is of type {@link TaskIntegratorWizardPage} and matches the given page name
	 *
	 * @param needle name of the page to be found
	 * @return if found, wizard page of type {@link TaskIntegratorWizardPage}, else null
	 */
	public TaskIntegratorWizardPage getTIPageByName(final String needle) {
		final IWizardPage page = getPage(needle);
		if (TaskIntegratorWizardPage.class.isInstance(page)) {
			return (TaskIntegratorWizardPage) page;
		}

		return null;
	}

}
