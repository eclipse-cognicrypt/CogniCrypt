/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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

package crossing.e1.configurator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.codegeneration.XSLBasedGenerator;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.FileHelper;
import crossing.e1.configurator.utilities.Utils;
import crossing.e1.configurator.utilities.XMLParser;
import crossing.e1.configurator.wizard.advanced.AdvancedUserValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.BeginnerModeQuestionnaire;
import crossing.e1.configurator.wizard.beginner.BeginnerTaskQuestionPage;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

/**
 * This class implements the logic of the dialogue windows the user has to go through. Currently, methods getNextPage() and performFinish() have special handling of TLS task
 * that should be deleted once the task is integrated.
 * 
 * @author Stefan Krueger
 * @author Sarah Nadi
 * @author Ram Kamath
 * @author Karim Ali
 *
 */
public class ConfiguratorWizard extends Wizard {

	protected TaskSelectionPage taskListPage;
	protected TLSConfigurationServerClientPage tlsSCPage;
	protected TLSConfigurationHostPortPage tlsPage;
	protected TLSConfigurationKeyStorePage tlsKeyPage;
	protected WizardPage preferenceSelectionPage;
	protected InstanceListPage instanceListPage;
	private ClaferModel claferModel;
	private final XSLBasedGenerator codeGeneration = new XSLBasedGenerator();
	private HashMap<Question, Answer> constraints;
	private BeginnerModeQuestionnaire beginnerQuestions;

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
	}

	@Override
	public void addPages() {
		this.taskListPage = new TaskSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.taskListPage);
	}

	@Override
	public boolean canFinish() {
		return (this.tlsPage != null && this.tlsPage.isPageComplete()) || (this.instanceListPage != null && this.instanceListPage.isPageComplete());
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
	 * This method returns the next page. If current page is task list or any but the last question page, the first/next question page is returned.
	 * If the current page is the the last question page, the instance list page is returned.
	 * @param currentPage current page
	 * @return either next question page or instance list page
	 */
	@Override
	public IWizardPage getNextPage(final IWizardPage currentPage) {
		final Task selectedTask = this.taskListPage.getSelectedTask();
		if (currentPage == this.taskListPage && this.taskListPage.canProceed()) {
			// Special handling for the TLS task
			//			if (selectedTask.getDescription().equals("Communicate over a secure channel")) {
			//				this.tlsSCPage = new TLSConfigurationServerClientPage();
			//				addPage(this.tlsSCPage);
			//				this.preferenceSelectionPage = this.tlsSCPage;
			//			} else 
			{
				this.claferModel = new ClaferModel(Utils.getAbsolutePath(selectedTask.getModelFile()));

				if (this.taskListPage.isAdvancedMode()) {
					this.preferenceSelectionPage = new AdvancedUserValueSelectionPage(this.claferModel, (AstConcreteClafer) org.clafer.cli.Utils
						.getModelChildByName(this.claferModel.getModel(), "c0_" + selectedTask.getName()));
				} else {
					this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getXmlFile());
					this.preferenceSelectionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextQuestion(), this.beginnerQuestions.getTask());
				}
			}
			if (this.constraints != null) {
				this.constraints = null;
			}
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);
			}
			return this.preferenceSelectionPage;
		} else if (this.tlsKeyPage != null && this.tlsKeyPage.canFlipToNextPage()) {
			this.tlsPage = new TLSConfigurationHostPortPage();
			this.preferenceSelectionPage = this.tlsPage;
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);
			}

			return this.preferenceSelectionPage;
		} else if (this.tlsSCPage != null && this.tlsSCPage.canFlipToNextPage()) {

			this.tlsKeyPage = new TLSConfigurationKeyStorePage();
			this.preferenceSelectionPage = this.tlsKeyPage;
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);
			}
			return this.preferenceSelectionPage;
		}
		/**
		 * If current page is either question or properties page (in Advanced mode)
		 */
		else if (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage) {
			if (this.taskListPage.isAdvancedMode()) {
				//TODO: Implement for Advanced Mode
			} else {
				if (this.constraints == null) {
					this.constraints = new HashMap<Question, Answer>();
				}

				final Entry<Question, Answer> entry = ((BeginnerTaskQuestionPage) currentPage).getMap();
				this.constraints.put(entry.getKey(), entry.getValue());

				if (this.beginnerQuestions.hasMoreQuestions()) {
					final int nextID = entry.getValue().getNextID();
					if (nextID > -1) {
						this.preferenceSelectionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.getQuestionByID(nextID), this.beginnerQuestions.getTask());
						if (checkifInUpdateRound()) {
							this.beginnerQuestions.previousQuestion();
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

						//this.constraints.putAll(((BeginnerTaskQuestionPage) currentPage).getMap());
						return this.preferenceSelectionPage;
					}
				}
			}

			final InstanceGenerator instanceGenerator = new InstanceGenerator(new File(Utils.getAbsolutePath(selectedTask.getModelFile()))
				.getAbsolutePath(), "c0_" + this.taskListPage.getSelectedTask().getName(), this.taskListPage.getSelectedTask().getDescription());

			if (this.taskListPage.isAdvancedMode()) {
				instanceGenerator.generateInstancesAdvancedUserMode(((AdvancedUserValueSelectionPage) currentPage).getConstraints());
			} else {
				// running in beginner mode
				instanceGenerator.generateInstances(this.constraints);
			}

			if (instanceGenerator.getNoOfInstances() > 0) {
				this.instanceListPage = new InstanceListPage(instanceGenerator, selectedTask);
				addPage(this.instanceListPage);
				return this.instanceListPage;
			} else {
				if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
					final String message = this.taskListPage.isAdvancedMode() ? Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE : Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER;
					MessageDialog.openError(new Shell(), "Error", message);
				}
			}
		}

		return currentPage;
	}

	/**
	 * This method returns previous page. If currentPage is the first question, the task list page is returned. 
	 * If it is any other question page or the instance list page, the previous question page is returned.
	 *   
	 * @param currentPage current page, either instance list page or question page
	 * @return either previous question or task selection page
	 */
	@Override
	public IWizardPage getPreviousPage(final IWizardPage currentPage) {
		final boolean lastPage = currentPage instanceof InstanceListPage;
		if (!checkifInUpdateRound() && (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage || lastPage)) {
			if (!this.beginnerQuestions.isFirstQuestion()) {
				this.beginnerQuestions.previousQuestion();
			}
		}
		return super.getPreviousPage(currentPage);
	}

	/**
	 * This method is called once the user selects an instance. It writes the instance to an xml file and calls the code generation.
	 * 
	 * @return <code>true</code>/<code>false</code> if writing instance file and code generation are (un)successful
	 */
	@Override
	public boolean performFinish() {
		boolean ret = false;
		if (this.instanceListPage != null) {
			ret = this.instanceListPage.isPageComplete();
			try {
				XMLParser parser = new XMLParser();
				parser.displayInstanceValues(this.instanceListPage.getValue(), constraints);

				// Initialize Code Generation to retrieve developer project
				ret &= this.codeGeneration.initCodeGeneration();

				// Write Instance File into developer project
				final String xmlInstancePath = codeGeneration.getDeveloperProject().getProjectPath() + Constants.fileSeparator + Constants.pathToClaferInstanceFile;
				parser.writeClaferInstanceToFile(xmlInstancePath);

				// Generate code template
				ret &= this.codeGeneration.generateCodeTemplates(new File(xmlInstancePath), this.taskListPage.getSelectedTask().getAdditionalResources(), null);

				// Delete Instance File
				FileHelper.deleteFile(xmlInstancePath);
				this.codeGeneration.getDeveloperProject().refresh();
			} catch (final IOException | CoreException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		} else if (this.tlsPage != null) {
			// Special code for TLS task
			// Should be removed once its integration is finished.
			ret = this.tlsPage.isPageComplete();
			try {
				final File xslTLSfile = new File(Utils.getAbsolutePath(Constants.pathToTSLXSLFile));
				final File xmlInstanceFile = new File(Utils
					.getAbsolutePath(Constants.pathToClaferInstanceFolder + Constants.fileSeparator + Constants.pathToClaferInstanceTLSFile));
				final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				final Document doc = docBuilder.parse(xmlInstanceFile);

				doc.getElementsByTagName("host").item(0).setTextContent(this.tlsPage.getHost());
				doc.getElementsByTagName("port").item(0).setTextContent(this.tlsPage.getPort());
				doc.getElementsByTagName("path").item(0).setTextContent(this.tlsKeyPage.getPath());
				doc.getElementsByTagName("password").item(0).setTextContent(this.tlsKeyPage.getPassword());

				final TransformerFactory tF = TransformerFactory.newInstance();
				final Transformer tfr = tF.newTransformer();
				final DOMSource source = new DOMSource(doc);
				final StreamResult result = new StreamResult(xmlInstanceFile);
				tfr.transform(source, result);
				ret &= this.codeGeneration.generateCodeTemplates(xmlInstanceFile, this.taskListPage.getSelectedTask().getAdditionalResources(), xslTLSfile);
			} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		}
		return ret;
	}

}
