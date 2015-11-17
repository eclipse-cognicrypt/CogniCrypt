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
 * @author Sarah Nadi
 *
 */

package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import crossing.e1.configurator.Labels;
import crossing.e1.configurator.questions.beginner.CryptoQuestion;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class RelevantQuestionsPage extends WizardPage {

	private ClaferModel model;
	private List<CryptoQuestion> relevantQuestions;
	private List<ButtonGroup> questionControls;

	public RelevantQuestionsPage(ClaferModel model,
			List<CryptoQuestion> relevantQuestions) {
		super("RELEVANT_QUESTIONS");
		setTitle("Relevant Questions");
		setDescription("Answering relevant questions");
		this.model = model;
		this.relevantQuestions = relevantQuestions;
		questionControls = new ArrayList<ButtonGroup>();
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);

		container.setBounds(10, 10, 450, 200);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		for (CryptoQuestion question : relevantQuestions) {

			ButtonGroup radioButtonGroup = new ButtonGroup();

			for (String choice : question.getChoices().keySet()){
				JButton button = new JButton(choice);
				radioButtonGroup.add(button);
			}
			
			questionControls.add(radioButtonGroup);

		}

		setControl(container);
	}
	
	/**
	 * @return Validation method which will be invoked upon clicking next on the
	 *         valueList page Next widgetPage is only accessible if there are
	 *         more than 0 instances for a given clafer and the chosen values
	 */
	public boolean validate(InstanceGenerator gen, ClaferModel claferModel) {
		
		Set<Constraint> constraints = new HashSet<Constraint>();
		
		for(int i = 0 ; i< relevantQuestions.size(); i++){
			String selectedValue = questionControls.get(i).getSelection().toString();
			Constraint constraint = relevantQuestions.get(i).getCorrespondingChoiceConstraint(selectedValue);
			constraints.add(constraint);
		}
		
		//gen.generateInstances(new ClaferModel(new ReadConfig().getClaferPath()), constraints);
		
//		for (AstConcreteClafer clafer : PropertiesMapperUtil.getPropertyLabels()
//				.keySet()) {
//			for (AstConcreteClafer claf : PropertiesMapperUtil
//					.getPropertyLabels().get(clafer)) {
//				
//				for(Constraint constraint : constraints){
//					if(constraint.getProperty().equals(claf.getName())){
//						
//					}
//				}
//			}
//		}
		
//		setMap();
//		gen.generateInstances(
//				new ClaferModel(new ReadConfig().getClaferPath()),
//				this.getMap());
		if (gen.getNoOfInstances() > 0) {
			return true;
		} else {
			setErrorMessage(Labels.INSTANCE_ERROR_MESSGAE);
			return true;
		}
		
		//return true;
	}

}
