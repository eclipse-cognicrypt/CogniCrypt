package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;

import javax.swing.JButton;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.beginner.questions.CryptoQuestion;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.StringLabelMapper;

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
		
//		for (AstConcreteClafer clafer : StringLabelMapper.getPropertyLabels()
//				.keySet()) {
//			for (AstConcreteClafer claf : StringLabelMapper
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
			setErrorMessage(Lables.INSTANCE_ERROR_MESSGAE);
			return false;
		}
		
		//return true;
	}

}
