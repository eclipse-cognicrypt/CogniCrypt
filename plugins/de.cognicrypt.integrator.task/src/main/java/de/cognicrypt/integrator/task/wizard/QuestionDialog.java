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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.widgets.QuestionModificationComposite;

public class QuestionDialog extends Dialog {

	public Text textQuestion;
	private String questionText;
	private Text txtBoxHelpText;
	private QuestionModificationComposite compositeToHoldAnswers;
	private final Question question;
	private Question questionDetails;
	int counter = 0;

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
		lblQuestion.setText(Constants.QUESTION);

		this.textQuestion = new Text(composite, SWT.BORDER);
		this.textQuestion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblHelpText = new Label(composite, SWT.NONE);
		lblHelpText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblHelpText.setText(Constants.DESCRIBE_QUESTION);

		this.txtBoxHelpText = new Text(composite, SWT.BORDER);
		this.txtBoxHelpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnAddAnswer = new Button(composite, SWT.None);
		btnAddAnswer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnAddAnswer.setText(Constants.ADD_ANSWER);
		// Visibility depends on question type
		final GridData gdCompositeToHoldAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gdCompositeToHoldAnswers.heightHint = 300;
		gdCompositeToHoldAnswers.widthHint = 890;
		
		compositeToHoldAnswers = new QuestionModificationComposite(composite, SWT.None);
		this.compositeToHoldAnswers.setLayoutData(gdCompositeToHoldAnswers);
		this.compositeToHoldAnswers.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.compositeToHoldAnswers.setVisible(false);
		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Answer tempAnswer = new Answer();
				compositeToHoldAnswers.getAnswers().add(tempAnswer);
				compositeToHoldAnswers.addAnswer(tempAnswer, true);
				compositeToHoldAnswers.setVisible(true);
			}

		});

		// executes when user wants to modify the question details
		if (this.question != null) {
			this.textQuestion.setText(this.question.getQuestionText());

			if (!this.question.getHelpText().isEmpty()) {
				this.txtBoxHelpText.setText(this.question.getHelpText());
			}

			if (!this.question.getElement().equals(Constants.GUIElements.text)) {
				for (final Answer answer : this.question.getAnswers()) {
					this.compositeToHoldAnswers.getAnswers().add(answer);
					this.compositeToHoldAnswers.addAnswer(answer, true);
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
		if (this.compositeToHoldAnswers.getAnswers().size() > 0) {
			super.okPressed();
		}
	}

	public String getQuestionText() {
		return this.questionText;
	}

	public void setQuestionText() {
		this.textQuestion.setText(this.question.getQuestionText());
	}

	public void setQuestionDetails() {
		final Question questionDetails = new Question();
		questionDetails.setQuestionText(textQuestion.getText());
		setQuestionElement(questionDetails);
		if (!txtBoxHelpText.getText().isEmpty()) {
			questionDetails.setHelpText(txtBoxHelpText.getText());
		}
		
		
		/**
		 * Executes only if the question type is not text this loop executes to delete
		 * empty text boxes in the question dialog
		 */
		for (int i = 0; i < compositeToHoldAnswers.getAnswers().size(); i++) {
			
			Answer a = compositeToHoldAnswers.getAnswers().get(i);
			
			if (Objects.equals(a.getValue(), null) || Objects.equals(a.getValue(), "")
					|| Objects.equals(a.getOption(), null) || Objects.equals(a.getOption(), "")) {
				compositeToHoldAnswers.deleteAnswer(a);
				compositeToHoldAnswers.updateAnswerContainer();
				i--;
			}
		}

		// opens a message box, alerting user to add answers to the question
		if (compositeToHoldAnswers.getAnswers().size() == 0) {
			MessageDialog.openError(getShell(), "Warning", Constants.NO_ANSWERS_WARNING);
		}
		
		questionDetails.setAnswers(this.compositeToHoldAnswers.getAnswers());

		checkQuestionHasDefaultAnswer(questionDetails);
		this.questionDetails = questionDetails;
	}

	/**
	 * sets the question element depending on the question type selected
	 *
	 * @param question
	 * @param element the value selected for the question type
	 */
	private void setQuestionElement(final Question question) {
		question.setElement(Constants.GUIElements.combo);
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
