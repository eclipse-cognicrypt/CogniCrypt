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
 * @author Ram Kamath
 *
 */
package crossing.e1.configurator.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import crossing.e1.codegen.generation.XSLBasedGenerator;
import crossing.e1.configurator.Lables;
import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.utilities.Validator;
import crossing.e1.configurator.wizard.advanced.DisplayValuePage;
import crossing.e1.configurator.wizard.advanced.ValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.DummyPage;
import crossing.e1.configurator.wizard.beginner.QuestionsBeginner;
import crossing.e1.configurator.wizard.beginner.RelevantQuestionsPage;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.ParseClafer;
import crossing.e1.featuremodel.clafer.StringLabelMapper;

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
public class ConfiguratorWizard extends Wizard {

	protected TaskSelectionPage taskListPage;
	protected WizardPage valueListPage;
	protected InstanceListPage instanceListPage;
	protected DisplayValuePage finalValueListPage;
	protected QuestionsBeginner quest;
	private ClaferModel claferModel;

	InstanceGenerator instanceGenerator = new InstanceGenerator();	
	private final XSLBasedGenerator codeGeneration = new XSLBasedGenerator();
	ParseClafer parser = new ParseClafer();
	
	public ConfiguratorWizard() {
		super();
		setWindowTitle("Cyrptography Task Configurator");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		this.claferModel = new ClaferModel(new ReadConfig().getClaferPath());
		taskListPage = new TaskSelectionPage(claferModel);
		this.setForcePreviousAndNextButtons(true);
		addPage(taskListPage);
		quest = new QuestionsBeginner();

	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		boolean ret = finalValueListPage.isPageComplete();
		// Generate code template
		ret &= codeGeneration.generateCodeTemplates();
		return ret;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == taskListPage && taskListPage.canProceed()) {

			instanceGenerator.setTaskName(taskListPage.getValue());
			instanceGenerator.setNoOfInstances(0);

			if (taskListPage.isAdvancedMode())
				valueListPage = new ValueSelectionPage(null, claferModel);
			else {
				parser.setConstraintClafers(StringLabelMapper.getTaskLabels()
						.get(taskListPage.getValue()));
				quest.setTask(taskListPage.getValue());
				quest.init();
				if (quest.hasQuestions())
					valueListPage = new DummyPage(quest);
			}
			addPage(valueListPage);

			return valueListPage;
		} else if (currentPage.getTitle().equals("QuestionPage")) {
			if (taskListPage.isAdvancedMode()
					&& ((ValueSelectionPage) valueListPage).getPageStatus() == true) {
				if (new Validator().validate(instanceGenerator)) {
					instanceListPage = new InstanceListPage(instanceGenerator);
					addPage(instanceListPage);
					return instanceListPage;
				}
			} else if (!taskListPage.isAdvancedMode() && !quest.hasQuestions()) {
				// running in beginner mode
				
				quest.setMap(((DummyPage)currentPage).getSelection(), claferModel);
				instanceGenerator.generateInstances(new ClaferModel(
						new ReadConfig().getClaferPath()),
						quest.getMap());

				if (new Validator().validate(instanceGenerator)) {
					instanceListPage = new InstanceListPage(instanceGenerator);
					addPage(instanceListPage);
					return instanceListPage;
				} else {
					// currentPage.(Lables.INSTANCE_ERROR_MESSGAE);
				}
			}
			// else if(!taskListPage.isAdvancedMode() && quest.hasQuestions()){
			// DummyPage dummyPage = new
			// DummyPage(quest.nextQuestion(),quest.nextValues());
			// addPage(dummyPage);
			// System.out.println("Next page invoked");
			// return dummyPage;
			// }
		} else if (currentPage == instanceListPage) {
			finalValueListPage = new DisplayValuePage(
					instanceListPage.getValue());
			addPage(finalValueListPage);
			return finalValueListPage;
		}
		return currentPage;

	}
}
