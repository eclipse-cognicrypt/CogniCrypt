package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.ClaferDependency;
import crossing.e1.configurator.beginer.question.CodeDependency;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import crossing.e1.taskintegrator.widgets.CompositeToHoldSmallerUIElements;
import crossing.e1.taskintegrator.widgets.GroupAnswer;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;

public class QuestionDialog extends Dialog {

	public Text textQuestion;
	private Label lblQuestionContent;
	private int counter = 0;
	private String questionText;
	private String questionType;
	private ArrayList<Answer> answerValues;
	private ArrayList<String> txtAnswer;
	private Combo combo;
	private CompositeToHoldSmallerUIElements compositeToHoldAnswers;
	private Question question;
	private Question questionDetails;
	private CompositeToHoldGranularUIElements compositeToHoldGranularUIElements;
	//int answerId=0;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public QuestionDialog(Shell parentShell, Question question) {
		super(parentShell);
		this.question = question;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(tabFolder.getSelectionIndex());
				if (tabFolder.getSelectionIndex() == 1) {
					lblQuestionContent.setText(textQuestion.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		TabItem tbtmQuestion = new TabItem(tabFolder, SWT.NONE);
		tbtmQuestion.setText("Question");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmQuestion.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label lblQuestion = new Label(composite, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblQuestion.setText("Question");

		textQuestion = new Text(composite, SWT.BORDER);
		textQuestion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type");

		combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setItems(new String[] { Constants.GUIElements.combo.toString(), Constants.GUIElements.text.toString(), Constants.GUIElements.itemselection
			.toString(), Constants.GUIElements.button.toString() });
		combo.select(-1);

		Button btnAddAnswer = new Button(composite, SWT.None);
		btnAddAnswer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnAddAnswer.setText("Add Answer");
		//Visibility depends on question type
		btnAddAnswer.setVisible(false);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		boolean showRemoveButton = true;
		compositeToHoldAnswers = new CompositeToHoldSmallerUIElements(composite, SWT.NONE, null, showRemoveButton);
		GridData gd_compositeToHoldAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_compositeToHoldAnswers.heightHint = 95;
		gd_compositeToHoldAnswers.widthHint = 650;
		compositeToHoldAnswers.setLayoutData(gd_compositeToHoldAnswers);
		compositeToHoldAnswers.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeToHoldAnswers.setVisible(false);

		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//compositeToHoldAnswers.setVisible(true);
				//answerId++;
				Answer tempAnswer = new Answer();
				compositeToHoldAnswers.getListOfAllAnswer().add(tempAnswer);
				compositeToHoldAnswers.addAnswer(tempAnswer, showRemoveButton);
				compositeToHoldAnswers.setVisible(true);
			}

		});

		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				switch (combo.getText()) {
					case "text":
						break;
					case "combo":
						boolean comboSelected = combo.getText().equalsIgnoreCase("combo") ? true : false;
						btnAddAnswer.setVisible(comboSelected);
						break;
					case "itemselection":
						boolean itemSelected = combo.getText().equalsIgnoreCase("itemselection") ? true : false;
						btnAddAnswer.setVisible(itemSelected);
						break;
					case "button":
						boolean buttonSelected = combo.getText().equalsIgnoreCase("button") ? true : false;
						btnAddAnswer.setVisible(buttonSelected);
						break;
					default:
						break;
				}
			}
		});

		if (question != null) {
			textQuestion.setText(question.getQuestionText());
			combo.setText(question.getQuestionType());
			for (Answer answer : question.getAnswers()) {
				compositeToHoldAnswers.getListOfAllAnswer().add(answer);
				compositeToHoldAnswers.addAnswer(answer, showRemoveButton);
				compositeToHoldAnswers.setVisible(true);
			}

		}

		TabItem tbtmLinkAnswers = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkAnswers.setText("Link answers");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmLinkAnswers.setControl(composite_1);
		composite_1.setLayout(new GridLayout(2, false));

		Label lblQuestion_1 = new Label(composite_1, SWT.NONE);
		lblQuestion_1.setText("Question");
		if (question != null) {
			Label qstnTxt = new Label(composite_1, SWT.NONE);
			qstnTxt.setText(question.getQuestionText());
			
			Label lblAnswers = new Label(container, SWT.NONE);
			lblAnswers.setText("Answers");
			
			Composite compositeForAnswers = new Composite(composite_1, SWT.NONE);
			compositeForAnswers.setLayout(new GridLayout(2, false));
			for (Answer answer : question.getAnswers()) {
				Label lblCurrentAnswer = new Label(compositeForAnswers, SWT.NONE);
				lblCurrentAnswer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				lblCurrentAnswer.setText(answer.getValue());

				Combo combo = new Combo(compositeForAnswers, SWT.DROP_DOWN);
				combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				combo.add("Default");
				for (int i = 1; i <= 5; i++) {
					if (question.getId() != i)
						combo.add("Quetsion id" + "i");
				}
			}
		}
		
		TabItem tbtmLinkClaferFeatures = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setText("Link Clafer features");

		TabItem tbtmLink = new TabItem(tabFolder, SWT.NONE);
		tbtmLink.setText("Link code");

		return container;

	}

	//to save the question text and type
	private void saveInput() {
		setQuestionDetails();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText() {
		textQuestion.setText(question.getQuestionText());
	}

	public String getquestionType() {
		return questionType;
	}

	public void setQuestionType(String type) {
		combo.setText(type);

	}

	//Saving question details
	public void setQuestionDetails() {
		Question questionDetails = new Question();
		questionDetails.setQuestionText(textQuestion.getText());
		questionDetails.setQuestionType(combo.getText());
		compositeToHoldAnswers.readAnswerValue();
		answerValues = new ArrayList<Answer>();
		for (int i = 0; i < compositeToHoldAnswers.getListOfAllAnswer().size(); i++) {
			answerValues.add(compositeToHoldAnswers.getListOfAllAnswer().get(i));
		}
		for (int i = 0; i < compositeToHoldAnswers.getListOfAllGroupAnswer().size(); i++) {
			answerValues.get(i).setValue(compositeToHoldAnswers.getListOfAllGroupAnswer().get(i).retrieveAnswer());
		}
		questionDetails.setAnswers(answerValues);
		this.questionDetails = questionDetails;
	}

	public Question getQuestionDetails() {
		return this.questionDetails;
	}

	public ArrayList<Answer> getAnswerValue() {
		//answerValues=(ArrayList<Answer>)compositeToHoldAnswers.getListOfAllAnswer();
		return answerValues;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(559, 351);
	}

}
