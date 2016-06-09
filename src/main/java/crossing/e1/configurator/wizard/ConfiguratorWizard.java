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
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.rmi.CORBA.Util;
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

import crossing.e1.codegen.Utils;
import crossing.e1.codegen.generation.XSLBasedGenerator;
import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.Validator;
import crossing.e1.configurator.utilities.WriteToFileHelper;
import crossing.e1.configurator.utilities.XMLParser;
import crossing.e1.configurator.wizard.advanced.AdvancedUserValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.BeginnerModeQuestionnaire;
import crossing.e1.configurator.wizard.beginner.BeginnerTaskQuestionPage;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class ConfiguratorWizard extends Wizard {

	protected TaskSelectionPage taskListPage;
	protected TLSConfigurationServerClientPage tlsSCPage;
	protected TLSConfigurationHostPortPage tlsPage;
	protected TLSConfigurationKeyStorePage tlsKeyPage;
	protected WizardPage preferenceSelectionPage;
	protected InstanceListPage instanceListPage;
	private ClaferModel claferModel;
	private final XSLBasedGenerator codeGeneration = new XSLBasedGenerator();

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
		this.tlsSCPage = new TLSConfigurationServerClientPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.taskListPage);
		addPage(this.tlsSCPage);
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
				this.preferenceSelectionPage = this.tlsSCPage;
			} else {
				if (taskListPage.isAdvancedMode()) {
					preferenceSelectionPage = new AdvancedUserValueSelectionPage(null, this.claferModel);
				} else {

					try {
						claferModel =  new ClaferModel(Utils.resolveResourcePathToFile(selectedTask.getModelFile()).getAbsolutePath());
						claferModel.createClaferPropertiesMap((AstConcreteClafer) org.clafer.cli.Utils.getModelChildByName(claferModel.getModel(), "c0_" + selectedTask.getName())); 
					
					
						BeginnerModeQuestionnaire beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getXmlFile());
					if (beginnerQuestions.hasQuestions()) {
						preferenceSelectionPage = new BeginnerTaskQuestionPage(beginnerQuestions);
					}
						
					} catch (URISyntaxException | IOException e) {
						Activator.getDefault().logError(e);
					}
					
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
		else if (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage){
			InstanceGenerator instanceGenerator;
			try {
				instanceGenerator = new InstanceGenerator(Utils.resolveResourcePathToFile(selectedTask.getModelFile()).getAbsolutePath(), "c0_" +  this.taskListPage.getSelectedTask().getName(), this.taskListPage.getSelectedTask().getDescription());
				
				if (this.taskListPage.isAdvancedMode()){
					instanceGenerator.generateInstancesAdvancedUserMode(((AdvancedUserValueSelectionPage) currentPage).getConstraints());
				} else {
					// running in beginner mode
					((BeginnerTaskQuestionPage) currentPage).setMap(((BeginnerTaskQuestionPage) currentPage).getSelection(), claferModel);
					instanceGenerator.generateInstances(((BeginnerTaskQuestionPage) currentPage).getMap());
				}
				
				if (instanceGenerator.getNoOfInstances() > 0) {
					this.instanceListPage = new InstanceListPage(instanceGenerator, selectedTask);
					addPage(this.instanceListPage);
					return this.instanceListPage;
				}else{
					if("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())){
						String message = this.taskListPage.isAdvancedMode() ? Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE : Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER;
						MessageDialog.openError(new Shell(), "Error", message);
					}
				}
				
				
				
			} catch (URISyntaxException | IOException e) {
				Activator.getDefault().logError(e);
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
				// Print the result to the console
				final WriteToFileHelper write = new WriteToFileHelper();
				write.writeToFile(new XMLParser().displayInstanceValues(this.instanceListPage.getValue(), ""),
					Utils.resolveResourcePathToFile(Constants.pathToClaferInstanceFolder).getAbsolutePath() + Constants.fileSeparator + Constants.pathToClaferInstanceFile);
				// Generate code template
				ret &= this.codeGeneration.generateCodeTemplates(null, null);
			} catch (URISyntaxException | IOException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		} else if (this.tlsPage != null) {
			ret = this.tlsPage.isPageComplete();
			try {
				File xslTLSfile = Utils.resolveResourcePathToFile(Constants.pathToTSLXSLFile);
				File xmlInstanceFile = Utils.resolveResourcePathToFile(Constants.pathToClaferInstanceFolder + Constants.fileSeparator + Constants.pathToClaferInstanceTLSFile);
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
				ret &= this.codeGeneration.generateCodeTemplates(
					xmlInstanceFile,
					xslTLSfile);
			} catch (URISyntaxException | IOException | SAXException | ParserConfigurationException | TransformerException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		}
		return ret;
	}
	

}
