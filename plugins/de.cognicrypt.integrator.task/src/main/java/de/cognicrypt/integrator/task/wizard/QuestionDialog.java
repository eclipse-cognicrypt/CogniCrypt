/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.ArrayList;
import java.util.Objects;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldSmallerUIElements;

public class QuestionDialog extends Dialog {

	public Text textQuestion;
	private String questionText;
	private String questionType;
	private Combo combo;
	private Text txtBoxHelptext;
	private Text textBoxTooltip;
	private Combo comboBoxAnswerType;
	private CompositeToHoldSmallerUIElements compositeToHoldAnswers;
	private final Question question;
	private Question questionDetails;
	int counter = 0;
	private String currentQuestionType = null;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 */
	public QuestionDialog(final Shell parentShell) {
		this(parentShell, null, null);
	}

	public QuestionDialog(final Shell parentShell, final Question question, final ArrayList<Question> listOfAllQuestions) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		this.question = question;
	}
	

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		getShell().setMinimumSize(900, 430);

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		final Label lblQuestion = new Label(composite, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblQuestion.setText("Question");

		this.textQuestion = new Text(composite, SWT.BORDER);
		this.textQuestion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Answer type");

		this.combo = new Combo(composite, SWT.READ_ONLY);
		this.combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.combo.select(-1);
		this.combo.setItems(Constants.dropDown, Constants.textBox, Constants.radioButton);

		final Label lblHelpText = new Label(composite, SWT.NONE);
		lblHelpText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblHelpText.setText("Describe the question");

		this.txtBoxHelptext = new Text(composite, SWT.BORDER);
		this.txtBoxHelptext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.txtBoxHelptext.setToolTipText("In order to help the user to understand the question give extra details about the question in the text box");

		final Label lblToolTip = new Label(composite, SWT.None);
		lblToolTip.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblToolTip.setText("Give tooltip");
		// visible only if the question type is text
		lblToolTip.setVisible(false);

		this.textBoxTooltip = new Text(composite, SWT.BORDER);
		this.textBoxTooltip.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.textBoxTooltip.setVisible(false);
		this.textBoxTooltip.setToolTipText("Give help text to be displayed when user hover over the text box");

		final Label lblAnswerType = new Label(composite, SWT.None);
		lblAnswerType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblAnswerType.setText("Expected answer is of type");
		// visible only if the question type is text
		lblAnswerType.setVisible(false);

		this.comboBoxAnswerType = new Combo(composite, SWT.NONE);
		this.comboBoxAnswerType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.comboBoxAnswerType.setItems(Constants.PORT_NUMBER, Constants.PASSWORD, Constants.IP_ADDRESS, "Other");
		this.comboBoxAnswerType.setVisible(false);

		final Button btnAddAnswer = new Button(composite, SWT.None);
		btnAddAnswer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnAddAnswer.setText("Add Answer");
		// Visibility depends on question type
		btnAddAnswer.setVisible(false);
		final boolean showRemoveButton = true;
		this.compositeToHoldAnswers = new CompositeToHoldSmallerUIElements(composite, SWT.NONE, null, showRemoveButton);
		final GridData gd_compositeToHoldAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_compositeToHoldAnswers.heightHint = 300;
		gd_compositeToHoldAnswers.widthHint = 890;
		this.compositeToHoldAnswers.setLayoutData(gd_compositeToHoldAnswers);
		this.compositeToHoldAnswers.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.compositeToHoldAnswers.setVisible(false);
		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Answer tempAnswer = new Answer();
				QuestionDialog.this.compositeToHoldAnswers.getListOfAllAnswer().add(tempAnswer);
				QuestionDialog.this.compositeToHoldAnswers.addAnswer(tempAnswer, showRemoveButton);
				QuestionDialog.this.compositeToHoldAnswers.setVisible(true);
			}

		});
		this.currentQuestionType = this.combo.getText();
		this.combo.addModifyListener(e -> {

			switch (QuestionDialog.this.combo.getText()) {
				case Constants.textBox:
					lblToolTip.setVisible(true);
					QuestionDialog.this.textBoxTooltip.setVisible(true);
					QuestionDialog.this.textBoxTooltip.setText("");
					QuestionDialog.this.comboBoxAnswerType.setVisible(true);
					lblAnswerType.setVisible(true);
					btnAddAnswer.setVisible(false);
					QuestionDialog.this.compositeToHoldAnswers.setVisible(false);
					QuestionDialog.this.compositeToHoldAnswers.getListOfAllAnswer().clear();
					QuestionDialog.this.compositeToHoldAnswers.updateAnswerContainer();
					final Answer emptyAnswer = new Answer();
					emptyAnswer.setDefaultAnswer(true);
					emptyAnswer.setValue("");
					QuestionDialog.this.compositeToHoldAnswers.getListOfAllAnswer().add(emptyAnswer);
					QuestionDialog.this.currentQuestionType = Constants.textBox;
					break;
				case Constants.dropDown:
					final boolean comboSelected = QuestionDialog.this.combo.getText().equalsIgnoreCase(Constants.dropDown) ? true : false;
					btnAddAnswer.setVisible(comboSelected);
					QuestionDialog.this.comboBoxAnswerType.setVisible(false);
					QuestionDialog.this.comboBoxAnswerType.select(-1);
					lblAnswerType.setVisible(false);
					lblToolTip.setVisible(false);
					QuestionDialog.this.textBoxTooltip.setText("");
					QuestionDialog.this.textBoxTooltip.setVisible(false);
					if (!QuestionDialog.this.currentQuestionType.equalsIgnoreCase(Constants.dropDown)) {
						QuestionDialog.this.compositeToHoldAnswers.getListOfAllAnswer().clear();
						QuestionDialog.this.compositeToHoldAnswers.updateAnswerContainer();
						QuestionDialog.this.compositeToHoldAnswers.setVisible(false);
						QuestionDialog.this.currentQuestionType = Constants.dropDown;
					}
					break;
				case Constants.radioButton:
					final boolean buttonSelected = QuestionDialog.this.combo.getText().equalsIgnoreCase(Constants.radioButton) ? true : false;
					btnAddAnswer.setVisible(buttonSelected);
					lblToolTip.setVisible(false);
					QuestionDialog.this.comboBoxAnswerType.setVisible(false);
					lblAnswerType.setVisible(false);
					QuestionDialog.this.textBoxTooltip.setText("");
					QuestionDialog.this.textBoxTooltip.setVisible(false);
					if (!QuestionDialog.this.currentQuestionType.equalsIgnoreCase(Constants.radioButton)) {
						QuestionDialog.this.compositeToHoldAnswers.getListOfAllAnswer().clear();
						QuestionDialog.this.compositeToHoldAnswers.updateAnswerContainer();
						QuestionDialog.this.compositeToHoldAnswers.setVisible(false);
						QuestionDialog.this.currentQuestionType = Constants.radioButton;
					}
					break;
				default:
					break;
			}
		});

		// executes when user wants to modify the question details
		if (this.question != null) {
			this.textQuestion.setText(this.question.getQuestionText());
			if (this.question.getElement().equals(Constants.GUIElements.combo)) {
				this.combo.setText(Constants.dropDown);
			} else if (this.question.getElement().equals(Constants.GUIElements.radio)) {
				this.combo.setText(Constants.radioButton);
			} else if (this.question.getElement().equals(Constants.GUIElements.text)) {
				this.combo.setText(Constants.textBox);
				this.textBoxTooltip.setText(this.question.getTooltip());
				this.comboBoxAnswerType.setText(this.question.getTextType());
				this.compositeToHoldAnswers.setVisible(false);
			}
			if (!this.question.getHelpText().isEmpty()) {
				this.txtBoxHelptext.setText(this.question.getHelpText());
			}
			if (!this.question.getTooltip().isEmpty()) {
				this.textBoxTooltip.setText(this.question.getTooltip());
			}

			if (!this.question.getElement().equals(Constants.GUIElements.text)) {
				for (final Answer answer : this.question.getAnswers()) {
					this.compositeToHoldAnswers.getListOfAllAnswer().add(answer);
					this.compositeToHoldAnswers.addAnswer(answer, showRemoveButton);
					this.compositeToHoldAnswers.setVisible(true);
				}
			}
		}

		return container;
	}

	@Override
	protected void okPressed() {
		setQuestionDetails();
		// closes the dialog only if user has added answers to the question
		if (this.compositeToHoldAnswers.getListOfAllAnswer().size() > 0) {
			super.okPressed();
		}
	}

	public String getQuestionText() {
		return this.questionText;
	}

	public void setQuestionText() {
		this.textQuestion.setText(this.question.getQuestionText());
	}

	public String getquestionType() {
		return this.questionType;
	}

	public void setQuestionType(final String type) {
		this.combo.setText(type);

	}

	// Saving question details
	public void setQuestionDetails() {
		final Question questionDetails = new Question();
		questionDetails.setQuestionText(this.textQuestion.getText());
		setQuestionElement(questionDetails, this.combo.getText());
		if (!this.txtBoxHelptext.getText().isEmpty()) {
			questionDetails.setHelpText(this.txtBoxHelptext.getText());
		}
		/**
		 * Executes only if the question type is not text this loop executes to delete empty text boxes in the question dialog
		 */
		if (!questionDetails.getElement().equals(Constants.GUIElements.text)) {
			for (int i = 0; i < this.compositeToHoldAnswers.getListOfAllAnswer().size(); i++) {
				if (Objects.equals(this.compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), null)
						|| Objects.equals(this.compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), "")) {
					this.compositeToHoldAnswers.deleteAnswer(this.compositeToHoldAnswers.getListOfAllAnswer().get(i));
					this.compositeToHoldAnswers.updateAnswerContainer();
					i--;
				}
			}
		}

		// opens a message box, alerting user to add answers to the question
		if (this.compositeToHoldAnswers.getListOfAllAnswer().size() == 0) {
			final MessageBox msgNoAnsAdded = new MessageBox(this.compositeToHoldAnswers.getShell());
			msgNoAnsAdded.setMessage("Please add answers to the question by clicking \"Add answer\" button");
			msgNoAnsAdded.open();

		}
		questionDetails.setAnswers(this.compositeToHoldAnswers.getListOfAllAnswer());

		if (this.combo.getText().equalsIgnoreCase(Constants.textBox)) {
			// sets the tooltip
			if (!this.textBoxTooltip.getText().equalsIgnoreCase("")) {
				questionDetails.setTooltip(this.textBoxTooltip.getText());
			}
			// sets the text answer Type
			if (this.comboBoxAnswerType.getText().isEmpty()) {
				questionDetails.setTextType("");
			} else {
				questionDetails.setTextType(this.comboBoxAnswerType.getText());
			}
			// adds code dependency to the answer if the question type is text
			final CodeDependency cd = new CodeDependency();
			final ArrayList<CodeDependency> codeDependenciesForTextType = new ArrayList<>();
			cd.setOption(getCapitaliseQuestionText(questionDetails.getQuestionText()));
			cd.setValue("");
			codeDependenciesForTextType.add(cd);
			questionDetails.getAnswers().get(0).setCodeDependencies(codeDependenciesForTextType);
		}
		checkQuestionHasDefaultAnswer(questionDetails);
		this.questionDetails = questionDetails;
	}

	/**
	 * sets the question element depending on the question type selected
	 *
	 * @param question
	 * @param element the value selected for the question type
	 */
	private void setQuestionElement(final Question question, final String element) {
		/**
		 * case 1: if the the question type is selected as drop down then sets the element to combo
		 */
		if (element.equals(Constants.dropDown)) {
			question.setElement(Constants.GUIElements.combo);
		}
		/**
		 * case 2: sets the question element to text if the question type is text box
		 */
		else if (element.equals(Constants.textBox)) {
			question.setElement(Constants.GUIElements.text);
		}
		/**
		 * case 3: sets the question element to text if the question type is radio button
		 */
		else if (element.equals(Constants.radioButton)) {
			question.setElement(Constants.GUIElements.radio);
		}
	}

	/**
	 * checks if for the question default answer is selected or not if no answer is selected as default answer then the function sets the first answer as the default answer of the
	 * particular question
	 */
	public void checkQuestionHasDefaultAnswer(final Question question) {
		boolean hasDefaultAnswer = false;
		for (final Answer answer : question.getAnswers()) {
			if (answer.isDefaultAnswer()) {
				hasDefaultAnswer = true;
			}
		}
		if (!hasDefaultAnswer) {
			if (question.getAnswers().size() > 0) {
				question.getAnswers().get(0).setDefaultAnswer(true);
			}
		}
	}

	/**
	 * Capitalize the first letter of each word of question text
	 *
	 * @param questionText the question text
	 * @return the capitalize text
	 */
	private String getCapitaliseQuestionText(final String questionText) {
		// TODO Auto-generated method stub
		final String trimmedQuestionText = questionText.trim().replaceAll(" +", " ");
		final String[] arr = trimmedQuestionText.split(" ");
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1));
		}

		return sb.toString();

	}

	/**
	 * @return the question
	 */
	public Question getQuestionDetails() {
		return this.questionDetails;
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
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
