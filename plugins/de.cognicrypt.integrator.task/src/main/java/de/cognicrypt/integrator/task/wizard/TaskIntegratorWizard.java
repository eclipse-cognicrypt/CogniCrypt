/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.awt.image.RescaleOp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.omg.CORBA.FREE_MEM;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.controllers.FileUtilitiesImportMode;
import de.cognicrypt.integrator.task.controllers.Validator;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class TaskIntegratorWizard extends Wizard {
	
	private TaskIntegratorWizardPage taskInformation;
	
	public TaskIntegratorWizard() {
		setWindowTitle("CogniCrypt Task Integrator");

		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "platform:/plugin/de.cognicrypt.core/icons/cognicrypt-medium.png");
		setDefaultPageImageDescriptor(image);
	}

	@Override
	public void addPages() {
		taskInformation = new TaskIntegratorWizardPage(Constants.PAGE_TASK_INFORMATION, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD);
		addPage(taskInformation);
		
		addPage(new QuestionsPage());
	}

	
	@Override
	public boolean canFinish() {
		
		if(IntegratorModel.getInstance().isImportModeChosen()) {
			return taskInformation.checkImportModeFinish();
		}
		
		if(!IntegratorModel.getInstance().isGuidedModeChosen())
			return taskInformation.checkNonGuidedFinish();
	
		if(IntegratorModel.getInstance().getIdentifiers().size() == 1)
			return taskInformation.checkMandatoryFields();
		
		return super.canFinish();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		File ressourceFolder = new File(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR);

		if (!ressourceFolder.exists()) {
			// make resource directory for Code Generation Templates if it doesn't exist
			ressourceFolder.mkdirs();
			initlocalResourceDir(); // initialize needed sub-directories
		}

		final IntegratorModel integratorModel = IntegratorModel.getInstance();
		final FileUtilities fileUtilities = new FileUtilities();
		final FileUtilitiesImportMode fileUtilitiesImportMode = new FileUtilitiesImportMode();

		
		String fileWriteAttemptResult;
		
		Task task;

		if (integratorModel.isImportModeChosen()) {
			FileUtilities.unzipFile();
			fileWriteAttemptResult = fileUtilitiesImportMode.writeDataImportMode();
			if (fileWriteAttemptResult.equals("")) {
				task = integratorModel.getTask();
				task.setImage(task.getName().replaceAll("[^A-Za-z0-9]", ""));
				task.setCodeGen(CodeGenerators.CrySL);
				fileUtilitiesImportMode.writeTaskToJSONFile(task);
				return true;
			} else {
				final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
				errorBox.setText("Problems with the provided ZIP (Most likly has a wrong stucture).");
				errorBox.setMessage(fileWriteAttemptResult);
				errorBox.open();
				return false;
			}
		}else{
			integratorModel.setTask();
			task = integratorModel.getTask();

			File taskDir = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + task.getName());
			taskDir.mkdir();

			File templateDir = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + task.getName() + "/template");
			templateDir.mkdir();

			File resourceDir = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + task.getName() + "/res");
			resourceDir.mkdir();

			if (integratorModel.isGuidedModeChosen()) {
				fileWriteAttemptResult = fileUtilities.writeData();
				if (integratorModel.getIdentifiers().size() == 1) {
					fileUtilities.writeJSONFile(new ArrayList<Question>());
				} else {

					if (Validator.checkForUnusedIdentifiers()) {
						final MessageBox confirmationMessageBox = new MessageBox(getShell(),
								SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						confirmationMessageBox.setMessage(
								"A template was added but is not used. Do you want to generate the task anyway?");
						confirmationMessageBox.setText("Warning: Unused Template");
						final int response = confirmationMessageBox.open();
						if (response == SWT.CANCEL) // abort
							return false;
					}

					fileUtilities.writeJSONFile(integratorModel.getQuestions());
				}
			} else {
				fileWriteAttemptResult = fileUtilities.writeDataNonGuidedMode();
			}

			if (fileWriteAttemptResult.equals("")) {
				task.setImage(task.getName().replaceAll("[^A-Za-z0-9]", ""));
				task.setCodeGen(CodeGenerators.CrySL);
				fileUtilities.writeTaskToJSONFile(task);

				try {
					String sourceFile = Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + task.getName();
					FileOutputStream fos = new FileOutputStream(
							Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + task.getName() + ".zip");
					ZipOutputStream zipOut = new ZipOutputStream(fos);
					File fileToZip = new File(sourceFile);

					FileUtilities.zipFile(fileToZip, fileToZip.getName(), zipOut);
					zipOut.close();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				FileUtilities.deleteDirectory(new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + task.getName()));
				return true;
			} else {
				final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
				errorBox.setText("Problems with the provided files.");
				errorBox.setMessage(fileWriteAttemptResult);
				errorBox.open();
				return false;
			}
		}
	}
	
	
	public void initlocalResourceDir() {
		File resourceCCTemp = new File(Constants.ECLIPSE_LOC_TEMP_DIR); 
		File resourceCCres = new File(Constants.ECLIPSE_LOC_RES_DIR);
		
		
		resourceCCTemp.mkdir(); // make local directory for Code Generation Templates
		resourceCCres.mkdir();  //// make local directory for Resources for Code Generation Templates
		
		
		File resourceCCaddres = new File(Constants.ECLIPSE_LOC_ADDRES_DIR);
		File resourceCCcla = new File(Constants.ECLIPSE_LOC_CLA_DIR);
		File resourceCCimg = new File(Constants.ECLIPSE_LOC_IMG_DIR);
		File resourceCCtaskdesc = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR);
		File resourceCCtasks = new File(Constants.ECLIPSE_LOC_TASKS_DIR);
		File resourceCCXSL = new File(Constants.ECLIPSE_LOC_XSL_DIR);
		File resourceCCtasksjson = new File(Constants.customjsonTaskFile);
		File resourceExport = new File(Constants.ECLIPSE_LOC_EXPORT_DIR);
		
		resourceCCaddres.mkdir();
		resourceCCcla.mkdir();
		resourceCCimg.mkdir();
		resourceCCtaskdesc.mkdir();
		resourceCCtasks.mkdir();
		resourceCCXSL.mkdir();
		resourceExport.mkdir();
		try {
			resourceCCtasksjson.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(resourceCCtasksjson));
			writer.write("[]");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
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
