package crossing.e1.configurator.wizard.beginner;

import java.util.HashSet;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.beginner.questions.CryptoQuestion;
import crossing.e1.featuremodel.clafer.ClaferModel;

public class RelevantQuestionsPage extends WizardPage {

	private ClaferModel model;
	private HashSet<CryptoQuestion> relevantQuestions;

	public RelevantQuestionsPage(ClaferModel model,
			HashSet<CryptoQuestion> relevantQuestions) {
		super("RELEVANT_QUESTIONS");
		setTitle("Relevant Questions");
		setDescription("Answering relevant questions");
		this.model = model;
		this.relevantQuestions = relevantQuestions;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);

		container.setBounds(10, 10, 450, 200);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		for (CryptoQuestion question : relevantQuestions) {

			Group group1 = new Group(container, SWT.SHADOW_IN);
			group1.setText(question.getQuestionText());
			group1.setSize(100,10);
			group1.setLayout(new RowLayout(SWT.HORIZONTAL));

			for (String choice : question.getChoices().keySet())
				new Button(group1, SWT.RADIO).setText(choice);

		}

		setControl(container);
	}

}
