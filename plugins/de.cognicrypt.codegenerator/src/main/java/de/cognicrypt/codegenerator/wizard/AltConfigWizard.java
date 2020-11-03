/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerModeQuestionnaire;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;

public class AltConfigWizard extends Wizard {

	private Task selectedTask;
	private HashMap<Question, Answer> constraints;
	private BeginnerModeQuestionnaire beginnerQuestions;

	public AltConfigWizard() {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("CogniCrypt");

		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "platform:/plugin/de.cognicrypt.core/icons/cognicrypt-medium.png ");
		setDefaultPageImageDescriptor(image);
		this.constraints = new LinkedHashMap<>();
	}

	@Override
	public void addPages() {
		setForcePreviousAndNextButtons(true);
		addPage(new TaskSelectionPage());
	}

	@Override
	public boolean canFinish() {
		final IWizardPage page = getContainer().getCurrentPage();
		return page instanceof LocatorPage && page.isPageComplete();

	}

	private boolean checkifInUpdateRound() {
		boolean updateRound = false;
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (final StackTraceElement el : stack) {
			if (el.getMethodName().contains("updateButtons")) {
				updateRound = true;
				break;
			}
		}
		return updateRound;
	}

	/**
	 * This method returns the next page. If current page is task list or any but the last question page, the first/next question page is returned. If the current page is the last
	 * question page, the instance list page is returned.
	 *
	 * @param currentPage
	 *        current page
	 * @return either next question page or instance list page
	 */
	@Override
	public IWizardPage getNextPage(final IWizardPage currentPage) {
		if (checkifInUpdateRound()) {
			return currentPage;
		}
		if (currentPage instanceof TaskSelectionPage) {
			selectedTask = ((TaskSelectionPage) currentPage).getSelectedTask();
			this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getQuestionsJSONFile());
			// It is possible that now questions are within a BeginnerModeQuestionnaire

			if (this.beginnerQuestions.hasPages()) {
				final BeginnerTaskQuestionPage questionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextPage(), this.beginnerQuestions.getTask(), null);
				addPage(questionPage);
				return questionPage;
			} else {
				return addLocatorPage();
			}
		}

		//Only case that is left: BeginnerTaskQuestionPage
		final BeginnerTaskQuestionPage curQuestionPage = (BeginnerTaskQuestionPage) currentPage;
		final HashMap<Question, Answer> curQuestionAnswerMap = curQuestionPage.getMap();

		for (final Entry<Question, Answer> entry : curQuestionAnswerMap.entrySet()) {
			this.constraints.put(entry.getKey(), entry.getValue());
		}

		final int nextPageid = curQuestionPage.getPageNextID();
		if (this.beginnerQuestions.hasMorePages() && nextPageid > -1) {
			final BeginnerTaskQuestionPage questionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.getPageByID(nextPageid), this.beginnerQuestions.getTask(), null);
			addPage(questionPage);
			return questionPage;
		} else {
			CodeGenerators generator = selectedTask.getCodeGen();
			if (generator == CodeGenerators.CrySL) {
				String selectedTemplate = constructTemplateName();
				selectedTask.setCodeTemplate(selectedTemplate);
				return addLocatorPage();
			} else if (generator == CodeGenerators.XSL) {
				final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
					.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

				instanceGenerator.generateInstances(this.constraints);

				if (instanceGenerator.getNoOfInstances() > 0) {
					return addLocatorPage();
				} else {
					MessageDialog.openError(new Shell(), "Error", Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER);
				}
			}
		}
		return currentPage;
	}

	public String constructTemplateName() {
		String selectedTemplate = selectedTask.getCodeTemplate();
		for (Answer resp : this.constraints.values()) {
			if (resp.getOption() != null) {
				selectedTemplate += resp.getOption();
			}
		}
		return selectedTemplate;
	}

	public void addConstraints(HashMap<Question, Answer> constraint) {
		this.constraints.putAll(constraint);
	}

	public void setSelectedTask(Task selectedTask) {
		this.selectedTask = selectedTask;
	}

	private IWizardPage addLocatorPage() {
		final LocatorPage locatorPage = new LocatorPage("Locator");
		addPage(locatorPage);
		return locatorPage;
	}

	/**
	 * This method returns previous page. If currentPage is the first question, the task list page is returned. If it is any other question page or the instance list page, the
	 * previous question page is returned.
	 *
	 * @param currentPage
	 *        current page, either instance list page or question page
	 * @return either previous question or task selection page
	 */
	@Override
	public IWizardPage getPreviousPage(final IWizardPage currentPage) {
		if (!checkifInUpdateRound()) {
			final IWizardPage[] pages = getPages();
			for (int i = 0; i < pages.length; i++) {
				if (currentPage.equals(pages[i])) {
					if (currentPage instanceof BeginnerTaskQuestionPage) {
						((BeginnerTaskQuestionPage) currentPage).setPageInactive();
					}
					final BeginnerTaskQuestionPage prevPage = (BeginnerTaskQuestionPage) pages[i - 1];
					for (final Entry<Question, Answer> quesAns : prevPage.getSelection().entrySet()) {
						this.constraints.remove(quesAns.getKey());
					}
					return prevPage;
				}
			}
		}
		if (currentPage instanceof LocatorPage && selectedTask.getCodeGen() == CodeGenerators.CrySL) {
			resetAnswers();
		}

		return super.getPreviousPage(currentPage);
	}

	public void resetAnswers() {
		int substringLength = 0;
		for (Answer response : this.constraints.values()) {
			if (response.getOption() != null) {
				substringLength += response.getOption().length();
			}
		}
		String oldCodeTemplate = selectedTask.getCodeTemplate();
		selectedTask.setCodeTemplate(oldCodeTemplate.substring(0, oldCodeTemplate.length() - substringLength));
	}

	/**
	 * This method is called once the user selects an instance. It writes the instance to an xml file and calls the code generation.
	 *
	 * @return <code>true</code>/<code>false</code> if writing instance file and code generation are (un)successful
	 */
	@Override
	public boolean performFinish() {
		boolean ret = false;
		final CodeGenerators genKind = selectedTask.getCodeGen();
		CodeGenerator codeGenerator = null;
		String additionalResources = selectedTask.getAdditionalResources();
		final LocatorPage currentPage = (LocatorPage) getContainer().getCurrentPage();
		IResource targetFile = (IResource) currentPage.getSelectedResource().getFirstElement();

		String taskName = selectedTask.getName();
		JOptionPane optionPane = new JOptionPane("CogniCrypt is now generating code that implements " + selectedTask.getDescription() + "\ninto file " + ((targetFile != null)
			? targetFile.getName()
			: "Output.java") + ". This should take no longer than a few seconds.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		JDialog waitingDialog = optionPane.createDialog("Generating Code");
		waitingDialog.setModal(false);
		waitingDialog.setVisible(true);
		Configuration chosenConfig = null;
		try {
			String codeTemplate = selectedTask.getCodeTemplate();
			switch (genKind) {
				case CrySL:
					CrySLBasedCodeGenerator.clearParameterCache();
					File templateFile = CodeGenUtils.getResourceFromWithin(codeTemplate).listFiles()[0];
					codeGenerator = new CrySLBasedCodeGenerator(targetFile);
					String projectRelDir = Constants.outerFileSeparator + codeGenerator.getDeveloperProject()
						.getSourcePath() + Constants.outerFileSeparator + Constants.PackageName + Constants.outerFileSeparator;
					String pathToTemplateFile = projectRelDir + templateFile.getName();
					String resFileOSPath = "";

					IPath projectPath = targetFile.getProject().getRawLocation();
					if (projectPath == null) {
						projectPath = targetFile.getProject().getLocation();
					}
					resFileOSPath = projectPath.toOSString() + pathToTemplateFile;

					Files.createDirectories(Paths.get(projectPath.toOSString() + projectRelDir));
					Files.copy(templateFile.toPath(), Paths.get(resFileOSPath), StandardCopyOption.REPLACE_EXISTING);
					codeGenerator.getDeveloperProject().refresh();

					resetAnswers();
					chosenConfig = new CrySLConfiguration(resFileOSPath, ((CrySLBasedCodeGenerator) codeGenerator).setUpTemplateClass(pathToTemplateFile));
					break;
				case XSL:
					this.constraints = (this.constraints != null) ? this.constraints : new HashMap<>();
					final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
						.getAbsolutePath(), "c0_" + taskName, selectedTask.getDescription());
					instanceGenerator.generateInstances(this.constraints);

					// Initialize Code Generation
					codeGenerator = new XSLBasedGenerator(targetFile, codeTemplate);
					chosenConfig = new XSLConfiguration(instanceGenerator.getInstances().values().iterator()
						.next(), this.constraints, codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
					break;
				default:
					return false;
			}
			ret = codeGenerator.generateCodeTemplates(chosenConfig, additionalResources);

			try {
				codeGenerator.getDeveloperProject().refresh();
			} catch (CoreException e1) {
				Activator.getDefault().logError(e1, Constants.CodeGenerationErrorMessage);
			}

		} catch (Exception ex) {
			Activator.getDefault().logError(ex, Constants.CodeGenerationErrorMessage);
		} finally {
			waitingDialog.setVisible(false);
		}

		waitingDialog.setVisible(false);
		return ret;
	}

	public HashMap<Question, Answer> getConstraints() {
		return this.constraints;
	}

}
