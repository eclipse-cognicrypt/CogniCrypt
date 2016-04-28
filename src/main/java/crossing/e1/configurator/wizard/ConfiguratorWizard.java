/**
 * Copyright 2015 Technische UniversitÃ¤t Darmstadt
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
 * @author Ram Kamath, Sarah Nadi, Karim Ali, Stefan Krüger
 *
 */
package crossing.e1.configurator.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import crossing.e1.codegen.Utils;
import crossing.e1.codegen.generation.XSLBasedGenerator;
import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.Validator;
import crossing.e1.configurator.utilities.WriteToFileHelper;
import crossing.e1.configurator.utilities.XMLParser;
import crossing.e1.configurator.wizard.advanced.ValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.DisplayQuestions;
import crossing.e1.configurator.wizard.beginner.QuestionsBeginner;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class ConfiguratorWizard extends Wizard {

	protected TaskSelectionPage taskListPage;
	protected TLSConfigurationServerClientPage tlsSCPage;
	protected TLSConfigurationHostPortPage tlsPage;
	protected WizardPage valueListPage;
	protected InstanceListPage instanceListPage;
	protected QuestionsBeginner quest;
	private ClaferModel claferModel;
	private final XSLBasedGenerator codeGeneration = new XSLBasedGenerator();

	public ConfiguratorWizard() {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			this.claferModel = new ClaferModel(Utils.resolveResourcePathToFile(Constants.claferPath).getAbsolutePath());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | URISyntaxException | IOException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("Cryptography Task Configurator");
	}

	@Override
	public void addPages() {
		this.taskListPage = new TaskSelectionPage(this.claferModel);
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
		if (currentPage == this.taskListPage && this.taskListPage.canProceed()) {
			// Special handling for the TLS task
			if (this.taskListPage.getValue().equals("Communicate over a secure channel")) {
				this.valueListPage = this.tlsSCPage;
			} else {
				if (this.taskListPage.isAdvancedMode()) {
					this.valueListPage = new ValueSelectionPage(null, this.claferModel);
				} else {
					/**
					 * Before showing the question update properties of a chosen task
					 */
					this.claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(this.taskListPage.getValue()));
					/**
					 * Create Questions object
					 */
					this.quest = new QuestionsBeginner();
					try {
						this.quest.init(PropertiesMapperUtil.getTaskLabelsMap().get(this.taskListPage.getValue()).getName(),
							Utils.resolveResourcePathToFile(Constants.XML_FILE_NAME).getAbsolutePath());
					} catch (URISyntaxException | IOException e) {
						Activator.getDefault().logError(e);
					}
					if (this.quest.hasQuestions()) {
						this.valueListPage = new DisplayQuestions(this.quest);
					}
				}
			}
			if (this.valueListPage != null) {
				addPage(this.valueListPage);
			}
			return this.valueListPage;
		} else if (this.tlsSCPage != null && this.tlsSCPage.canFlipToNextPage()) {
			this.tlsPage = new TLSConfigurationHostPortPage();
			this.valueListPage = this.tlsPage;
			if (this.valueListPage != null) {
				addPage(this.valueListPage);
			}
			return this.valueListPage;
		}
		/**
		 * If current page is either question or properties page (in Advanced mode) check title. Maintain uniform title for second wizard page of the wizard
		 */
		else if (currentPage.getTitle().equals(Labels.PROPERTIES)) {
			InstanceGenerator instanceGenerator;
			try {
				instanceGenerator = new InstanceGenerator(Utils.resolveResourcePathToFile(Constants.claferPath).getAbsolutePath());
				instanceGenerator.setTaskName(this.taskListPage.getValue());
				instanceGenerator.setNoOfInstances(0);
				if (this.taskListPage.isAdvancedMode() && ((ValueSelectionPage) this.valueListPage).getPageStatus() == true) {
					instanceGenerator.generateInstancesAdvancedUserMode(((ValueSelectionPage) currentPage).getConstraints());
					if (new Validator().validate(instanceGenerator)) {
						this.instanceListPage = new InstanceListPage(instanceGenerator);
						addPage(this.instanceListPage);
						return this.instanceListPage;
					}
				} else if (!this.taskListPage.isAdvancedMode() && !this.quest.hasQuestions() && this.taskListPage.getStatus()) {
					// running in beginner mode
					((DisplayQuestions) currentPage).setMap(((DisplayQuestions) currentPage).getSelection(), this.claferModel);
					instanceGenerator.generateInstances(((DisplayQuestions) currentPage).getMap());
					if (new Validator().validate(instanceGenerator)) {
						this.instanceListPage = new InstanceListPage(instanceGenerator);
						addPage(this.instanceListPage);
						return this.instanceListPage;
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
