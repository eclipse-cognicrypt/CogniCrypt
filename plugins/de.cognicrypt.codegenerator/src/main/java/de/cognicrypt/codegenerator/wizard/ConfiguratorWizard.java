/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.GUIElements;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.FileHelper;
import de.cognicrypt.codegenerator.utilities.Labels;
import de.cognicrypt.codegenerator.utilities.Utils;
import de.cognicrypt.codegenerator.utilities.XMLParser;
import de.cognicrypt.codegenerator.wizard.advanced.AdvancedUserValueSelectionPage;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerModeQuestionnaire;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;

/**
 * This class implements the logic of the dialogue windows the user has to go through. Currently, methods getNextPage() and performFinish() have special handling of TLS task that
 * should be deleted once the task is integrated.
 *
 * @author Stefan Krueger
 * @author Sarah Nadi
 * @author Ram Kamath
 * @author Karim Ali
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
	private HashMap<Integer, IWizardPage> createdPages = new HashMap<Integer, IWizardPage>();

	public ConfiguratorWizard() {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("Cryptography Task Configurator");
		ImageDescriptor image =
            AbstractUIPlugin.
                imageDescriptorFromPlugin("de.cognicrypt.codegenerator",
                   "icons/cognicrypt-medium.png");
        setDefaultPageImageDescriptor(image);        
		}

	@Override
	public void addPages() {
		this.taskListPage = new TaskSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.taskListPage);
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage().getName().equals(Labels.DEFAULT_ALGORITHM_PAGE ))
			return(this.defaultAlgorithmPage.isDefaultAlgorithm());
		else if(getContainer().getCurrentPage().getName().equals(Labels.ALGORITHM_SELECTION_PAGE ))
				return true;		
		else 
			return false;
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
	 * 
	 * @param curPage
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
	 * This method returns the next page. If current page is task list or any but the last question page, the first/next question page is returned. If the current page is the the
	 * last question page, the instance list page is returned.
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
			createdPages.put(((BeginnerTaskQuestionPage) currentPage).getCurrentPageID(), currentPage);
			this.beginnerQuestions.getCurrentPageID();
			BeginnerTaskQuestionPage beginnerTaskQuestionPage = (BeginnerTaskQuestionPage) currentPage;

			if (this.beginnerQuestions.hasMorePages()) {
				nextPageid = beginnerTaskQuestionPage.getPageNextID();
			}
			if (createdPages.containsKey(nextPageid)) {
				return createdPages.get(nextPageid);
			}
			
		}
		if (currentPage instanceof TaskSelectionPage) {
			createdPages.clear();
		}

		// if page is shown for the first time, create the new object
		final Task selectedTask = this.taskListPage.getSelectedTask();
		if (currentPage == this.taskListPage && this.taskListPage.isPageComplete()) {
			this.claferModel = new ClaferModel(Utils.getResourceFromWithin(selectedTask.getModelFile()));

			if (this.taskListPage.isGuidedMode()) {
				this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getXmlFile()); 
				this.preferenceSelectionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextPage(), this.beginnerQuestions.getTask(),null);
			} else {
				// Updated the calls to accommodate for the pages instead of questions.
				//this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getXmlFile());
				//this.preferenceSelectionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextQuestion(), this.beginnerQuestions.getTask());

				// The 3rd parameter in this constructor call is benign, it only exists to call the constructor designed for pages
				this.preferenceSelectionPage = new AdvancedUserValueSelectionPage(this.claferModel, (AstConcreteClafer) org.clafer.cli.Utils
									.getModelChildByName(this.claferModel.getModel(), "c0_" + selectedTask.getName()));
				   }
			if (this.constraints != null) {
				this.constraints = null;
			}
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);
			}
			return this.preferenceSelectionPage;
		}
		/**
		 * If current page is either question or properties page (in Advanced mode)
		 */
		else if (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage) {
			if (this.taskListPage.isGuidedMode()) {
				if (this.constraints == null) {
					this.constraints = new HashMap<>();
				}

				final BeginnerTaskQuestionPage beginnerTaskQuestionPage = (BeginnerTaskQuestionPage) currentPage;
				final HashMap<Question, Answer> selectionMap = beginnerTaskQuestionPage.getMap();

				// Looping through all the entries that were added to the BeginnerTaskQuestionPage
				for (Entry<Question, Answer> entry : selectionMap.entrySet()) {
					if (entry.getKey().getElement().equals(GUIElements.itemselection)) {
						handleItemSelection(entry);
					}

					this.constraints.put(entry.getKey(), entry.getValue());
				}

				if (this.beginnerQuestions.hasMorePages()) {
					int nextID = beginnerTaskQuestionPage.getPageNextID();

					if (nextID > -1) {
						final Page curPage = this.beginnerQuestions.setPageByID(nextID);
						// Pass the variable for the questionnaire here instead of all the questions. 
						createBeginnerPage(curPage, beginnerQuestions);
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
			}

			final InstanceGenerator instanceGenerator = new InstanceGenerator(Utils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + this.taskListPage.getSelectedTask().getName(), this.taskListPage.getSelectedTask().getDescription());

			if (this.taskListPage.isGuidedMode()) {
				// running in beginner mode
				instanceGenerator.generateInstances(this.constraints);
			}
            if(currentPage instanceof BeginnerTaskQuestionPage) {
            	//default algorithm page will be added only for beginner mode
			if (instanceGenerator.getNoOfInstances() !=0) {
				this.defaultAlgorithmPage = new DefaultAlgorithmPage(instanceGenerator, taskListPage, this);
				addPage(this.defaultAlgorithmPage);
				return this.defaultAlgorithmPage;
				
				
			} else {
				if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
					final String message = Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER ;
					MessageDialog.openError(new Shell(), "Error", message);
				}
			}
            }
            else if(currentPage instanceof AdvancedUserValueSelectionPage) {
            	//instance list page will be added after advanced user value selection page in advanced mode.
            	//(default algorithm page is not added in advanced mode)
    			if (instanceGenerator.getNoOfInstances() >0) {
    				this.instanceListPage = new InstanceListPage(instanceGenerator,taskListPage, this);
    				addPage(this.instanceListPage);
    				return this.instanceListPage;
    				
    			} else {
    				if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
    					//final String message = this.taskListPage.isGuidedMode() ? Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER : Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE;
    					final String message = Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE;
    					MessageDialog.openError(new Shell(), "Error", message);
    				}
    			}
                }
			
		}
		//adding instance details page after default algorithm page in beginner mode
		else if (currentPage instanceof DefaultAlgorithmPage){
			final InstanceGenerator instanceGenerator = new InstanceGenerator(Utils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + this.taskListPage.getSelectedTask().getName(), this.taskListPage.getSelectedTask().getDescription());

			if (this.taskListPage.isGuidedMode()) {
				// running in beginner mode
				instanceGenerator.generateInstances(this.constraints);
			}
			//instance details page will be added after default algorithm page only if the number of instances is greater than 1
			if(!this.defaultAlgorithmPage.isDefaultAlgorithm() && instanceGenerator.getNoOfInstances() >1){
				this.instanceListPage = new InstanceListPage(instanceGenerator, taskListPage, this);
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
		final boolean lastPage = currentPage instanceof InstanceListPage || currentPage instanceof DefaultAlgorithmPage ;
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
		if (getContainer().getCurrentPage().getName().equals(Labels.ALGORITHM_SELECTION_PAGE ) ) {
			ret = this.instanceListPage.isPageComplete();
			try {
				final XMLParser parser = new XMLParser();
				parser.displayInstanceValues(this.instanceListPage.getValue(), this.constraints);
				
				// Initialize Code Generation
				XSLBasedGenerator codeGenerator = new XSLBasedGenerator(this.taskListPage.getSelectedProject(), this.instanceListPage.getProviderFromInstance());

				// Write Instance File into developer project
				final String xmlInstancePath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
				parser.writeClaferInstanceToFile(xmlInstancePath);

				// Generate code template
				ret &= codeGenerator.generateCodeTemplates(new File(xmlInstancePath), this.taskListPage.getSelectedTask().getAdditionalResources(), codeGenerator.getProvider());

				// Delete Instance File
				FileHelper.deleteFile(xmlInstancePath);
				codeGenerator.getDeveloperProject().refresh();
			} catch (final IOException | CoreException | BadLocationException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		}
		if ( getContainer().getCurrentPage().getName().equals(Labels.DEFAULT_ALGORITHM_PAGE ) ) {
			ret = this.defaultAlgorithmPage.isPageComplete();
			try {
				final XMLParser parser = new XMLParser();
				parser.displayInstanceValues(this.defaultAlgorithmPage.getValue(), this.constraints);
				
				// Initialize Code Generation
				XSLBasedGenerator codeGenerator = new XSLBasedGenerator(this.taskListPage.getSelectedProject());

				// Write Instance File into developer project
				final String xmlInstancePath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
				parser.writeClaferInstanceToFile(xmlInstancePath);

				// Generate code template
				ret &= codeGenerator.generateCodeTemplates(new File(xmlInstancePath), this.taskListPage.getSelectedTask().getAdditionalResources(), codeGenerator.getProvider());

				// Delete Instance File
				FileHelper.deleteFile(xmlInstancePath);
				codeGenerator.getDeveloperProject().refresh();
			} catch (final IOException | CoreException | BadLocationException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		}
		return ret;
	}
	public  HashMap<Question, Answer> getConstraints(){
		return constraints;
	}
}
