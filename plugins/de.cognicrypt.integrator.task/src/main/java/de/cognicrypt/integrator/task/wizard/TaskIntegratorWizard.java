/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.awt.image.RescaleOp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.omg.CORBA.FREE_MEM;

import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class TaskIntegratorWizard extends Wizard {
	
	private TaskIntegratorWizardPage taskInformationPage;
	
	public TaskIntegratorWizard() {
		setWindowTitle("CogniCrypt Task Integrator");

		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "platform:/plugin/de.cognicrypt.core/icons/cognicrypt-medium.png");
		setDefaultPageImageDescriptor(image);
	}

	@Override
	public void addPages() {
		taskInformationPage = new TaskIntegratorWizardPage(Constants.PAGE_TASK_INFORMATION, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD);
		addPage(taskInformationPage);
		
		addPage(new QuestionsPage());
	}

	
	@Override
	public boolean canFinish() {
		
		if(!IntegratorModel.getInstance().isGuidedModeChosen())
			return taskInformationPage.checkNonGuidedFinish();
	
		if(IntegratorModel.getInstance().getIdentifiers().size() == 1)
			return taskInformationPage.checkMandatoryFields();
			
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
		
		if(!ressourceFolder.exists()) {
			//make ressource directory for Code Generation Templates if it doesn't exist
			ressourceFolder.mkdirs();
			initlocalResourceDir(); //initialize needed subfolders 
		}
		
		final IntegratorModel integratorModel = IntegratorModel.getInstance();
		
		integratorModel.setTask();
		final FileUtilities fileUtilities = new FileUtilities(integratorModel.getTaskName());
		Task task = integratorModel.getTask();
		HashMap<String, File> crylTemplatesWithOption = integratorModel.getCryslTemplateFiles();
		if (getContainer().getCurrentPage().getName().equals(Constants.PAGE_TASK_INFORMATION)) {
			String fileWriteAttemptResult;
			if (integratorModel.isGuidedModeChosen() == false) {
				 fileWriteAttemptResult = fileUtilities.writeCryslTemplate(crylTemplatesWithOption, integratorModel.getLocationOfJSONFile(), integratorModel.getLocationOfIconFile());}
			else {
				fileWriteAttemptResult = fileUtilities.writeCryslTemplate( (File) crylTemplatesWithOption.values().toArray()[0], integratorModel.getLocationOfIconFile());
				fileUtilities.writeJSONFile(new ArrayList<Question>());
			}
				// Check if the contents of the provided files are valid.
				if (fileWriteAttemptResult.equals("")) {
					// Adding the trimmed task name to ensure it matches with the name of the image stored (refer FileUtilities)
					task.setImage(task.getName().replaceAll("[^A-Za-z0-9]", ""));
					task.setCodeGen(CodeGenerators.CrySL);
					fileUtilities.writeTaskToJSONFile(task);
					fileUtilities.updateThePluginXMLFileWithHelpData(integratorModel.getTaskName());
					return true;
				} else {
					MessageDialog.openError(getShell(), "Warning", fileWriteAttemptResult);
					return false;
				}
		} else {

			// collect input to task-related files from individual pages
			
			final String fileWriteAttemptResult = fileUtilities.writeCryslTemplate(crylTemplatesWithOption,  integratorModel.getLocationOfIconFile());
			final ArrayList<Question> questions = IntegratorModel.getInstance().getQuestions();
			
			// Check if the contents of the provided files are valid.
			if (fileWriteAttemptResult.equals("")) {
				// Adding the trimmed task name to ensure it matches with the name of the image stored (refer FileUtilities)
				task.setImage(task.getName().replaceAll("[^A-Za-z0-9]", ""));
				task.setCodeGen(CodeGenerators.CrySL);
				task.setModelFile("");
				task.setAdditionalResources("");
				fileUtilities.writeJSONFile(questions);
				fileUtilities.writeTaskToJSONFile(task);
				fileUtilities.updateThePluginXMLFileWithHelpData(integratorModel.getTaskName());
			} else {
				final MessageBox errorBox = new MessageBox(getShell(), SWT.ERROR | SWT.OK);
				errorBox.setText("Problems with the provided files.");
				errorBox.setMessage(fileWriteAttemptResult);
				errorBox.open();
			}
		}
		
		return true;
	}

	
	public void initlocalResourceDir() {
		File resourceCCTemp = new File(Constants.ECLIPSE_LOC_TEMP_DIR); 
		File resourceCCres = new File(Constants.ECLIPSE_LOC_RES_DIR);
		
		resourceCCTemp.mkdir(); //make local directory for Code Generation Templates
		resourceCCres.mkdir();  ////make local directory for Resources for Code Generation Templates
		
		File resourceCCaddres = new File(Constants.ECLIPSE_LOC_ADDRES_DIR);
		File resourceCCcla = new File(Constants.ECLIPSE_LOC_CLA_DIR);
		File resourceCCimg = new File(Constants.ECLIPSE_LOC_IMG_DIR);
		File resourceCCtaskdesc = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR);
		File resourceCCtasks = new File(Constants.ECLIPSE_LOC_TASKS_DIR);
		File resourceCCXSL = new File(Constants.ECLIPSE_LOC_XSL_DIR);
		File resourceCCtasksjson = new File(Constants.localjsonTaskFile);
		
		
		resourceCCaddres.mkdir();
		resourceCCcla.mkdir();
		resourceCCimg.mkdir();
		resourceCCtaskdesc.mkdir();
		resourceCCtasks.mkdir();
		resourceCCXSL.mkdir();
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
