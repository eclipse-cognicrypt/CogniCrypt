/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenCrySLRule;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.advanced.AdvancedUserValueSelectionPage;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerModeQuestionnaire;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.GUIElements;
import de.cognicrypt.core.Constants.CodeGenerators;
import de.cognicrypt.core.Constants.GUIElements;
import de.cognicrypt.utils.Utils;

/**
 * This class implements the logic of the dialogue windows the user has to go through. Currently, methods getNextPage() and performFinish() have special handling of TLS task that
 * should be deleted once the task is integrated.
 *
 * @author Stefan Krueger
 * @author Sarah Nadi
 * @author Ram Kamath
 * @author Karim Al
 * @author André Sonntag
 *
 */
public class ConfiguratorWizard extends Wizard {

	private TaskSelectionPage taskListPage;
	private WizardPage preferenceSelectionPage;
	private DefaultAlgorithmPage defaultAlgorithmPage;
	private InstanceListPage instanceListPage;
	private ClaferModel claferModel;
	private HashMap<Question, Answer> constraints;
	private BeginnerModeQuestionnaire beginnerQuestions;
	private final HashMap<Integer, IWizardPage> createdPages;
	private int prevPageId;
	private List<Integer> protocolList;
	private final CodeGenerators generator;

	public ConfiguratorWizard(CodeGenerators codeGen) {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("Cryptography Task Configurator");
		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "icons/cognicrypt-medium.png");
		setDefaultPageImageDescriptor(image);

		this.createdPages = new HashMap<>();
		generator = codeGen;
	}

	@Override
	public void addPages() {
		this.taskListPage = new TaskSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.taskListPage);
	}

	@Override
	public boolean canFinish() {
		final String pageName = getContainer().getCurrentPage().getName();
		if (pageName.equals(Constants.DEFAULT_ALGORITHM_PAGE)) {
			return (!this.defaultAlgorithmPage.isDefaultAlgorithm());
		}
		return (pageName.equals(Constants.ALGORITHM_SELECTION_PAGE));

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
	 * Creates a new {@link BeginnerTaskQuestionPage}.
	 * 
	 * @param curPage
	 *        Current page
	 * @param beginnerQuestionnaire
	 *        updated this variable from a list of questions to have access to the method to get specific Questions.
	 */
	private void createBeginnerPage(final Page curPage, final BeginnerModeQuestionnaire beginnerQuestionnaire) {
		List<String> selection = null;
		if (curPage.getContent().size() == 1) {
			final Question curQuestion = curPage.getContent().get(0);
			if (curQuestion.getElement().equals(GUIElements.itemselection)) {
				selection = new ArrayList<>();
				for (final AstConcreteClafer childClafer : this.claferModel.getModel().getRoot().getSuperClafer().getChildren()) {
					if (childClafer.getSuperClafer().getName().endsWith(curQuestion.getSelectionClafer())) {
						selection.add(ClaferModelUtils.removeScopePrefix(childClafer.getName()));
					}
				}
			}
		}
		// Pass the questionnaire instead of the all of the questions.
		this.preferenceSelectionPage = new BeginnerTaskQuestionPage(curPage, this.beginnerQuestions.getTask(), beginnerQuestionnaire, selection);
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

		int nextPageid = -1;
		// if page was already created, return the existing object
		if (currentPage instanceof BeginnerTaskQuestionPage) {
			this.createdPages.put(((BeginnerTaskQuestionPage) currentPage).getCurrentPageID(), currentPage);
			final BeginnerTaskQuestionPage beginnerTaskQuestionPage = (BeginnerTaskQuestionPage) currentPage;

			// remove set constraints if the user press the previous button
			if (prevPageId != beginnerTaskQuestionPage.getCurrentPageID()) {
				protocolList.add(beginnerTaskQuestionPage.getCurrentPageID());
				if (protocolList.size() > 2) {
					if (protocolList.get(protocolList.size() - 3) == beginnerTaskQuestionPage.getCurrentPageID()) {
						if (this.constraints != null) {
							BeginnerTaskQuestionPage previousPage = (BeginnerTaskQuestionPage) createdPages.get(prevPageId);
							Set<Question> previousPageQuestions = previousPage.getMap().keySet();
							for (Question q : previousPageQuestions) {
								this.constraints.remove(q);
							}
							protocolList.remove(protocolList.size() - 1);	//remove last page index
							protocolList.remove(protocolList.size() - 1);	//remove duplicate
						}
					}
				}
				prevPageId = beginnerTaskQuestionPage.getCurrentPageID();
			}

			if (this.beginnerQuestions.hasMorePages()) {

				nextPageid = beginnerTaskQuestionPage.getPageNextID();
			}
			if (this.createdPages.containsKey(nextPageid)) {
				return this.createdPages.get(nextPageid);
			}
		}
		if (currentPage instanceof TaskSelectionPage) {
			prevPageId = 0;
			this.protocolList = new ArrayList<>();
			protocolList.add(0);
			this.createdPages.clear();
		}

		// if page is shown for the first time, create the new object
		final Task selectedTask = this.taskListPage.getSelectedTask();
		if (currentPage == this.taskListPage && this.taskListPage.isPageComplete()) {
			this.claferModel = new ClaferModel(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile()));

			this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getQuestionsJSONFile());
			this.preferenceSelectionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextPage(), this.beginnerQuestions.getTask(), null);
			if (this.constraints != null) {
				this.constraints = null;
			}
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);
			}
			return this.preferenceSelectionPage;
		} else if (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage) {
			/**
			 * If current page is either question or properties page (in Advanced mode)
			 */
			if (this.constraints == null) {
				this.constraints = new HashMap<>();
			}

			final BeginnerTaskQuestionPage beginnerTaskQuestionPage = (BeginnerTaskQuestionPage) currentPage;
			final HashMap<Question, Answer> selectionMap = beginnerTaskQuestionPage.getMap();

			// Looping through all the entries that were added to the BeginnerTaskQuestionPage
			for (final Entry<Question, Answer> entry : selectionMap.entrySet()) {
				if (entry.getKey().getElement().equals(GUIElements.itemselection)) {
					handleItemSelection(entry);
				}
				this.constraints.put(entry.getKey(), entry.getValue());
			}

			if (this.beginnerQuestions.hasMorePages()) {

				final int nextID = beginnerTaskQuestionPage.getPageNextID();

				if (nextID > -1) {
					final Page curPage = this.beginnerQuestions.setPageByID(nextID);
					// Pass the variable for the questionnaire here instead of all the questions.
					createBeginnerPage(curPage, this.beginnerQuestions);
					if (checkifInUpdateRound()) {
						this.beginnerQuestions.previousPage();
					}

					final IWizardPage[] pages = getPages();
					for (int i = 1; i < pages.length; i++) {
						if (!(pages[i] instanceof BeginnerTaskQuestionPage)) {
							continue;
						}
						final BeginnerTaskQuestionPage oldPage = (BeginnerTaskQuestionPage) pages[i];
						if (oldPage.equals(this.preferenceSelectionPage)) {
							return oldPage;
						}
					}
					if (this.preferenceSelectionPage != null) {
						addPage(this.preferenceSelectionPage);
					}
					return this.preferenceSelectionPage;
				}
			}

			final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

			instanceGenerator.generateInstances(this.constraints);
			if (currentPage instanceof BeginnerTaskQuestionPage) {
				//default algorithm page will be added only for beginner mode
				if (instanceGenerator.getNoOfInstances() != 0) {
					this.defaultAlgorithmPage = new DefaultAlgorithmPage(instanceGenerator, this.constraints, this.taskListPage);
					addPage(this.defaultAlgorithmPage);
					return this.defaultAlgorithmPage;

				} else {
					if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
						final String message = Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER;
						MessageDialog.openError(new Shell(), "Error", message);
					}
				}
			} else if (currentPage instanceof AdvancedUserValueSelectionPage) {
				//instance list page will be added after advanced user value selection page in advanced mode.
				//(default algorithm page is not added in advanced mode)
				if (instanceGenerator.getNoOfInstances() > 0) {
					this.instanceListPage = new InstanceListPage(instanceGenerator, this.constraints, this.taskListPage, this.defaultAlgorithmPage);
					addPage(this.instanceListPage);
					return this.instanceListPage;

				} else {
					if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
						final String message = Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE;
						MessageDialog.openError(new Shell(), "Error", message);
					}
				}
			}

		}
		//adding instance details page after default algorithm page in beginner mode
		else if (currentPage instanceof DefaultAlgorithmPage) {
			final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

			instanceGenerator.generateInstances(this.constraints);
			//instance details page will be added after default algorithm page only if the number of instances is greater than 1
			if (this.defaultAlgorithmPage.isDefaultAlgorithm() && instanceGenerator.getNoOfInstances() > 1) {
				this.instanceListPage = new InstanceListPage(instanceGenerator, this.constraints, this.taskListPage, this.defaultAlgorithmPage);
				addPage(this.instanceListPage);
				return this.instanceListPage;
			}
		}

		return currentPage;
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
		final boolean lastPage = currentPage instanceof InstanceListPage || currentPage instanceof DefaultAlgorithmPage;
		if (!checkifInUpdateRound() && (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage || lastPage)) {
			if (!this.beginnerQuestions.isFirstPage()) {
				this.beginnerQuestions.previousPage();
			}

		}

		return super.getPreviousPage(currentPage);
	}

	private void handleItemSelection(final Entry<Question, Answer> entry) {
		final Answer ans = entry.getValue();
		ArrayList<ClaferDependency> claferDependencies = ans.getClaferDependencies();
		if (null == claferDependencies) {
			claferDependencies = new ArrayList<>();
		}

		String operand = "";
		for (final AstConcreteClafer childClafer : this.claferModel.getModel().getRoot().getSuperClafer().getChildren()) {
			if (childClafer.getSuperClafer().getName().endsWith("Task")) {
				for (final AstConcreteClafer grandChildClafer : childClafer.getChildren()) {
					if (grandChildClafer.getRef().getTargetType().getName().endsWith(entry.getKey().getSelectionClafer())) {
						operand = ClaferModelUtils.removeScopePrefix(grandChildClafer.getName());
						break;
					}
				}
			}
		}
		final ClaferDependency cd = new ClaferDependency();
		cd.setAlgorithm(this.taskListPage.getSelectedTask().getName());
		cd.setOperand(operand);
		cd.setOperator("++");
		cd.setValue(ans.getValue());
		claferDependencies.add(cd);
		ans.setClaferDependencies(claferDependencies);
	}

	/**
	 * This method is called once the user selects an instance. It writes the instance to an xml file and calls the code generation.
	 *
	 * @return <code>true</code>/<code>false</code> if writing instance file and code generation are (un)successful
	 */
	@Override
	public boolean performFinish() {
		boolean ret = false;
		final String currentPageName = getContainer().getCurrentPage().getName();
		final Task selectedTask = this.taskListPage.getSelectedTask();
		CodeGenerator codeGenerator;
		String additionalResources = selectedTask.getAdditionalResources();

		switch (generator) {
			case CrySL:
				List<List<CodeGenCrySLRule>> rules = new ArrayList<List<CodeGenCrySLRule>>();
				try {
					List<List<String>> stringRules = new ArrayList<List<String>>();
					stringRules.add(Arrays.asList(new String[] { "SecureRandom", "PBEKeySpec", "SecretKeyFactory", "SecretKey", "SecretKeySpec" }));
					stringRules.add(Arrays.asList(new String[] { "Cipher" }));

					for (List<String> rule : stringRules) {
						ArrayList<CodeGenCrySLRule> newRules = new ArrayList<CodeGenCrySLRule>();
						rules.add(newRules);
						for (String r : rule) {
							try {
								newRules.add(new CodeGenCrySLRule(Utils.getCryptSLRule(r)));
							} catch (FileNotFoundException ex) {
								Activator.getDefault().logError(ex, "CrySL rule " + r + " not found.");
							}
						}
					}

					codeGenerator = new CrySLBasedCodeGenerator(this.taskListPage.getSelectedProject());
					
					Map<CodeGenCrySLRule, ?> constraints = new HashMap<CodeGenCrySLRule, Object>();
					Configuration chosenConfig = new CrySLConfiguration(rules, constraints, codeGenerator.getDeveloperProject()
						.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);

					ret = codeGenerator.generateCodeTemplates(chosenConfig, additionalResources);
				} catch (Exception e) {
					Activator.getDefault().logError(e);
					return false;
				}
				break;
			case XSL:
				InstanceClafer instance = null;
				if (Constants.ALGORITHM_SELECTION_PAGE.equals(currentPageName)) {
					instance = this.instanceListPage.getValue();
					ret = this.instanceListPage.isPageComplete();
				} else if (Constants.DEFAULT_ALGORITHM_PAGE.equals(currentPageName)) {
					instance = this.defaultAlgorithmPage.getValue();
					ret = this.defaultAlgorithmPage.isPageComplete();

				}

				codeGenerator = new XSLBasedGenerator(this.taskListPage.getSelectedProject(), selectedTask.getXslFile());
				Configuration chosenConfig = new XSLConfiguration(instance, this.constraints, codeGenerator.getDeveloperProject()
					.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
				ret &= codeGenerator.generateCodeTemplates(chosenConfig, selectedTask.getAdditionalResources());
				break;
			default:
				break;

		}

		return ret;
	}

	public HashMap<Question, Answer> getConstraints() {
		return this.constraints;
	}
}
