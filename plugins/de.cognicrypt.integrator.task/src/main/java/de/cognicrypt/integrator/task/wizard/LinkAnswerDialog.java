/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.ArrayList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldSmallerUIElements;

public class LinkAnswerDialog extends Dialog {

	private Question question;
	private ArrayList<Question> listOfAllQuestions;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 * @param question represents the current question
	 * @param listOfAllQuestions
	 */
	public LinkAnswerDialog(final Shell parent, final Question question, final ArrayList<Question> listOfAllQuestions) {
		super(parent);
		setQuestion(question);
		setListOfAllQuestions(listOfAllQuestions);
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {

		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		getShell().setMinimumSize(700, 400);

		// executes when the question type is text
		if (this.question.getElement().equals(Constants.GUIElements.text)) {
			final Label lblLinkAnswersTabMessage = new Label(container, SWT.NONE);
			lblLinkAnswersTabMessage.setText("This type of question does not need to link answers");

		} else {

			final Label lblQuestion_1 = new Label(container, SWT.NONE);
			lblQuestion_1.setText("Question:");

			final Label qstnTxt = new Label(container, SWT.NONE);
			qstnTxt.setText(this.question.getQuestionText());

			// Group containing the headers
			final Group groupHeaderLinkAnswer = new Group(container, SWT.NONE);
			final GridData gd_groupHeaderLinkAnswer = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
			groupHeaderLinkAnswer.setLayoutData(gd_groupHeaderLinkAnswer);

			final Label lblAnswers = new Label(groupHeaderLinkAnswer, SWT.NONE);
			lblAnswers.setBounds(5, 5, 150, 25);
			lblAnswers.setText("Answers");

			final Label lblSelectQuestion = new Label(groupHeaderLinkAnswer, SWT.NONE);
			lblSelectQuestion.setBounds(180, 5, 530, 25);
			lblSelectQuestion.setText("Jump to question");

			// Scroll composite containing the needed widgets for linking the answers to other questions

			final CompositeToHoldSmallerUIElements scrollCompositeForAnswers = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, false);
			final GridData gd_LinkAns = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
			gd_LinkAns.heightHint = 200;
			gd_LinkAns.widthHint = 700;
			scrollCompositeForAnswers.setLayoutData(gd_LinkAns);
			scrollCompositeForAnswers.setLayout(new GridLayout(2, false));

			for (final Answer answer : this.question.getAnswers()) {
				scrollCompositeForAnswers.addElementsOfLinkAnswer(answer, this.question, this.listOfAllQuestions);
			}
		}

		return container;
	}

	/**
	 * @return the current question
	 */
	public Question getQuestion() {
		return this.question;
	}

	/**
	 * sets the current question
	 *
	 * @param question
	 */
	public void setQuestion(final Question question) {
		this.question = question;
	}

	/**
	 * @return listOfAllQuestions
	 */
	public ArrayList<Question> getListOfAllQuestions() {
		return this.listOfAllQuestions;
	}

	/**
	 * sets the listOfAllQuestions
	 *
	 * @param listOfAllQuestions
	 */
	public void setListOfAllQuestions(final ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}
}
