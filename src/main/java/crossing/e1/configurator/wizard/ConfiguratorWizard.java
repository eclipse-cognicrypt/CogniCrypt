/**
 * Copyright 2015 Technische Universität Darmstadt
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
/**
 * @author Ram Kamath, Sarah Nadi, Karim Ali, Stefan Kr�ger
 *
 */
package crossing.e1.configurator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
		return (tlsPage != null && tlsPage.isPageComplete()) || (instanceListPage != null && instanceListPage.isPageComplete());
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		Task selectedTask = this.taskListPage.getSelectedTask();
		if (currentPage == this.taskListPage && this.taskListPage.canProceed()) {
			// Special handling for the TLS task
			if (selectedTask.getDescription().equals("Communicate over a secure channel")) {
				this.tlsSCPage = new TLSConfigurationServerClientPage();
				addPage(this.tlsSCPage);
				this.preferenceSelectionPage = this.tlsSCPage;
			} else {
				claferModel = new ClaferModel(Utils.getAbsolutePath(selectedTask.getModelFile()));

				if (taskListPage.isAdvancedMode()) {
					preferenceSelectionPage = new AdvancedUserValueSelectionPage(this.claferModel, (AstConcreteClafer) org.clafer.cli.Utils
						.getModelChildByName(claferModel.getModel(), "c0_" + selectedTask.getName()));
				} else {
					beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getXmlFile());
					preferenceSelectionPage = new BeginnerTaskQuestionPage(beginnerQuestions.nextQuestion(), beginnerQuestions.getTask());
				}
			}
			if (preferenceSelectionPage != null) {
				addPage(preferenceSelectionPage);
			}
			return preferenceSelectionPage;
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
				if (constraints == null) {
					constraints = ((BeginnerTaskQuestionPage) currentPage).getMap();
				}

				if (beginnerQuestions.hasMoreQuestions()) {

					preferenceSelectionPage = new BeginnerTaskQuestionPage(beginnerQuestions.nextQuestion(), beginnerQuestions.getTask());
					if (checkifInUpdateRound()) {
						beginnerQuestions.previousQuestion();
					}
					IWizardPage[] pages = getPages();
					for (int i = 1; i < pages.length; i++) {
						if (!(pages[i] instanceof BeginnerTaskQuestionPage))
							continue;
						BeginnerTaskQuestionPage oldPage = (BeginnerTaskQuestionPage) pages[i];
						if (oldPage.equals(preferenceSelectionPage)) {
							return oldPage;
						}
					}
					if (preferenceSelectionPage != null) {
						addPage(preferenceSelectionPage);
					}

					constraints.putAll(((BeginnerTaskQuestionPage) currentPage).getMap());
					return preferenceSelectionPage;
				}
			}

			InstanceGenerator instanceGenerator = new InstanceGenerator(new File(Utils.getAbsolutePath(selectedTask.getModelFile()))
				.getAbsolutePath(), "c0_" + this.taskListPage.getSelectedTask().getName(), this.taskListPage.getSelectedTask().getDescription());

			if (this.taskListPage.isAdvancedMode()) {
				instanceGenerator.generateInstancesAdvancedUserMode(((AdvancedUserValueSelectionPage) currentPage).getConstraints());
			} else {
				// running in beginner mode
				instanceGenerator.generateInstances(constraints);
			}

			if (instanceGenerator.getNoOfInstances() > 0) {
				this.instanceListPage = new InstanceListPage(instanceGenerator, selectedTask);
				addPage(this.instanceListPage);
				return this.instanceListPage;
			} else {
				if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
					String message = this.taskListPage.isAdvancedMode() ? Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE : Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER;
					MessageDialog.openError(new Shell(), "Error", message);
				}
			}
		}

		return currentPage;
	}

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
				String xmlInstancePath = codeGeneration.getDeveloperProject().getProjectPath() + Constants.fileSeparator + Constants.pathToClaferInstanceFile;
				parser.writeClaferInstanceToFile(xmlInstancePath);
				
				// Generate code template
				ret &= this.codeGeneration.generateCodeTemplates(new File(xmlInstancePath), taskListPage.getSelectedTask().getAdditionalResources(), null);

				// Delete Instance File
				FileHelper.deleteFile(xmlInstancePath);
				codeGeneration.getDeveloperProject().refresh();
			} catch (Exception e) {//(URISyntaxException | IOException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		} else if (this.tlsPage != null) {
			ret = this.tlsPage.isPageComplete();
			try {
				File xslTLSfile = new File(Utils.getAbsolutePath(Constants.pathToTSLXSLFile));
				File xmlInstanceFile = new File(Utils.getAbsolutePath(Constants.pathToClaferInstanceFolder + Constants.fileSeparator + Constants.pathToClaferInstanceTLSFile));
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(xmlInstanceFile);

				doc.getElementsByTagName("host").item(0).setTextContent(tlsPage.getHost());
				doc.getElementsByTagName("port").item(0).setTextContent(tlsPage.getPort());
				doc.getElementsByTagName("path").item(0).setTextContent(tlsKeyPage.getPath());
				doc.getElementsByTagName("password").item(0).setTextContent(tlsKeyPage.getPassword());

				TransformerFactory tF = TransformerFactory.newInstance();
				Transformer tfr = tF.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(xmlInstanceFile);
				tfr.transform(source, result);
				ret &= this.codeGeneration.generateCodeTemplates(xmlInstanceFile, taskListPage.getSelectedTask().getAdditionalResources(), xslTLSfile);
			} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		}
		return ret;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage currentPage) {
		boolean lastPage = currentPage instanceof InstanceListPage;
		if (!checkifInUpdateRound() && (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage || lastPage)) {
			if (!beginnerQuestions.isFirstQuestion()) {
				beginnerQuestions.previousQuestion();
			}
		}
		return super.getPreviousPage(currentPage);
	}

	private boolean checkifInUpdateRound() {
		boolean updateRound = false;
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement el : stack) {
			if (el.getMethodName().contains("updateButtons")) {
				updateRound = true;
				break;
			}
		}
		return updateRound;
	}

}