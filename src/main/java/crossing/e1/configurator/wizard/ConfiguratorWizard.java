
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
 * @author Ram Kamath, Sarah Nadi
 *
 */
package crossing.e1.configurator.wizard;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import crossing.e1.codegen.generation.XSLBasedGenerator;
import crossing.e1.configurator.Labels;
import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.utilities.Validator;
import crossing.e1.configurator.utilities.WriteToFileHelper;
import crossing.e1.configurator.wizard.advanced.ValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.DisplayQuestions;
import crossing.e1.configurator.wizard.beginner.QuestionsBeginner;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;
import crossing.e1.xml.export.PublishToXML;

public class ConfiguratorWizard extends Wizard {

	
	protected TaskSelectionPage taskListPage;
	protected WizardPage valueListPage;
	protected InstanceListPage instanceListPage;
	protected QuestionsBeginner quest;
	private ClaferModel claferModel;
	IPath path = null;
	private XSLBasedGenerator codeGeneration=new XSLBasedGenerator();
	private static final String XML_FILE_NAME="encryptXmlPath";
	private static final String PATH_FOR_CONFIG_XML = "/Configurator.xml";

	public ConfiguratorWizard() {
		super();

		try {
			// Set the Look and Feel of the application to the operating
			// system's look and feel.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}

		this.claferModel = new ClaferModel(new ReadConfig().getValueFromConfig("claferPath"));
		setWindowTitle("Cyrptography Task Configurator");
	}

	/**
	 * Select the project location of the project which has been selected in a
	 * workspace
	 */
	public boolean checkProjectSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {
				IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
				path = project.getLocation();
			} else {
				/**
				 * No project has been selected, exit with an error
				 */
				displayError("Please select projrct directory to launch the configurator");
				return false;

			}
		} else {
			displayError("Please select projrct directory to launch the configurator");
			return false;

		}
		return true;
	}

	private void displayError(String message) {
		MessageDialog.openError(new Shell(), "Error", message);
	}

	@Override
	public void addPages() {
		if (checkProjectSelection()) {
			taskListPage = new TaskSelectionPage(claferModel);
			this.setForcePreviousAndNextButtons(true);
			addPage(taskListPage);
		}
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		boolean ret = instanceListPage.isPageComplete();
		WriteToFileHelper write = new WriteToFileHelper();
		write.writeToFile(new PublishToXML().displayInstanceValues(instanceListPage.getValue(), ""),
				path.toString() + PATH_FOR_CONFIG_XML);
		// Generate code template
		 ret &= codeGeneration.generateCodeTemplates();
		return ret;

	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {

		if (currentPage == taskListPage && taskListPage.canProceed()) {
			if (taskListPage.isAdvancedMode())
				valueListPage = new ValueSelectionPage(null, claferModel);
			else {
				/**
				 * Before showing the question update properties of a chosen
				 * task
				 */
				claferModel.createClaferPropertiesMap(
						PropertiesMapperUtil.getTaskLabelsMap().get(taskListPage.getValue()));
				/**
				 * Create Questions object
				 */
				quest = new QuestionsBeginner();
				quest.init(PropertiesMapperUtil.getTaskLabelsMap().get(taskListPage.getValue()).getName(),XML_FILE_NAME);
				if (quest.hasQuestions())
					valueListPage = new DisplayQuestions(quest);
			}
			if (valueListPage != null)
				addPage(valueListPage);

			return valueListPage;
		}
		/**
		 * If current page is either question or properties page (in Advanced
		 * mode) check title. Maintain uniform title for second wizard page of
		 * the wizard
		 * 
		 */
		else if (currentPage.getTitle().equals(Labels.PROPERTIES)) {
			InstanceGenerator instanceGenerator = new InstanceGenerator("claferPath");
			instanceGenerator.setTaskName(taskListPage.getValue());
			instanceGenerator.setNoOfInstances(0);

			if (taskListPage.isAdvancedMode() && ((ValueSelectionPage) valueListPage).getPageStatus() == true) {
				instanceGenerator.generateInstancesAdvancedUserMode(((ValueSelectionPage) currentPage).getMap());
				if (new Validator().validate(instanceGenerator)) {
					instanceListPage = new InstanceListPage(instanceGenerator);
					addPage(instanceListPage);
					return instanceListPage;
				}
			} else if (!taskListPage.isAdvancedMode() && !quest.hasQuestions() && taskListPage.getStatus()) {
				// running in beginner mode
				((DisplayQuestions) currentPage).setMap(((DisplayQuestions) currentPage).getSelection(), claferModel);
				instanceGenerator.generateInstances(((DisplayQuestions) currentPage).getMap());

				if (new Validator().validate(instanceGenerator)) {
					instanceListPage = new InstanceListPage(instanceGenerator);
					addPage(instanceListPage);
					return instanceListPage;
				} 
			}
		}
		return currentPage;

	}
}
