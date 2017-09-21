package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;

public class DialogsTestPage extends WizardPage {

	public DialogsTestPage() {
		super("Dialogs test page");
		setDescription("This page enables testing of dialogs by opening them on button click.");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);

		final GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		Question question = new Question();
		Answer answer1 = new Answer();
		Answer answer2 = new Answer();
		Answer answer3 = new Answer();
		Answer answer4 = new Answer();
		answer1.setValue("Very secure");
		answer2.setValue("Secure");
		answer3.setValue("Medium");
		answer4.setValue("Weak");

		ArrayList<Answer> answerList = new ArrayList<>();
		answerList.add(answer1);
		answerList.add(answer2);
		answerList.add(answer3);
		answerList.add(answer4);

		question.setId(4);
		question.setQuestionText("What level of security do you need for your application?");
		question.setElement(GUIElements.combo);
		question.setAnswers(answerList);

		ClaferConstraintDialog cfrConstrDialog = new ClaferConstraintDialog(parent.getShell());
		ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(parent.getShell());
		QuestionDialog questionDialog = new QuestionDialog(parent.getShell());
		QuestionLinkingDialog questionLinkingDialog = new QuestionLinkingDialog(parent.getShell(), question);

		Button btn = new Button(container, SWT.PUSH);
		btn.setText("Clafer Constraint Dialog");
		btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				cfrConstrDialog.open();
			}
		});

		Button btn2 = new Button(container, SWT.PUSH);
		btn2.setText("Clafer Feature Dialog");
		btn2.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				cfrFeatureDialog.open();
			}
		});

		Button btn3 = new Button(container, SWT.PUSH);
		btn3.setText("Question Dialog");
		btn3.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				questionDialog.open();
			}
		});

		Button btn4 = new Button(container, SWT.PUSH);
		btn4.setText("Question Linking Dialog");
		btn4.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				questionLinkingDialog.open();
			}
		});

		setControl(container);
	}

}
