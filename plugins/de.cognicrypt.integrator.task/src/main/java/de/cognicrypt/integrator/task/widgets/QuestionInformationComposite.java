/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;

/**
 * This class creates widgets to display the overview of the question details and creates button to add clafer/code dependency to the question
 */

public class QuestionInformationComposite extends Composite {

	public Text txtQuestion;
	public Text txtDescription;

	private Question question;
	

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public QuestionInformationComposite(final Composite parent, final int style, final Question questionParam) {
		super(parent, SWT.BORDER);

		setQuestion(questionParam);

		final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.minimumWidth = 300;
		setLayoutData(gridData);

		setLayout(new GridLayout(2, false));

		final QuestionModifyDeleteComposite grpModifyDeleteButtons = new QuestionModifyDeleteComposite(this, question);
		grpModifyDeleteButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpModifyDeleteButtons.setVisible(true);

		final QuestionOrderingComposite grpUpDownButtons = new QuestionOrderingComposite(this, question);
		grpUpDownButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpUpDownButtons.setVisible(true);

		final Group grpQuestionDetails = new Group(this, SWT.NONE);
		grpQuestionDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		grpQuestionDetails.setLayout(new GridLayout(2, false));

		final Label lblQuestion = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblQuestion.setText("Question:");

		txtQuestion = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestion.setEditable(false);
		final GridData gdTxtQuestion = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gdTxtQuestion.widthHint = 0;
		txtQuestion.setLayoutData(gdTxtQuestion);
		txtQuestion.setText(question.getQuestionText());
		
		final Label lblDescription = new Label(grpQuestionDetails, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblDescription.setText("Description:");
		
		txtDescription = new Text(grpQuestionDetails, SWT.BORDER);
		txtDescription.setEditable(false);
		final GridData gdTxtDescription= new GridData(SWT.FILL, SWT.CENTER, false, false);
		gdTxtDescription.widthHint = 0;
		txtDescription.setLayoutData(gdTxtDescription);
		txtDescription.setText(question.getHelpText());

		final Label lblAnswers = new Label(grpQuestionDetails, SWT.NONE);
		lblAnswers.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblAnswers.setText("Answers:");

		final QuestionModificationComposite compositeForAnswers = new QuestionModificationComposite(grpQuestionDetails, SWT.None);
		final GridData gdCompositeForAnswers = new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 2);
		gdCompositeForAnswers.heightHint = 100;
		gdCompositeForAnswers.widthHint = 500;
		compositeForAnswers.setLayoutData(gdCompositeForAnswers);
		for (final Answer answer : question.getAnswers()) {
			compositeForAnswers.addAnswer(answer, false);
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	private void setQuestion(final Question question) {
		this.question = question;
	}

	public void setTextQuestion(final String txtQuestion) {
		this.txtQuestion.setText(txtQuestion);
	}

	public String getTextQuestion() {
		return txtQuestion.getText();
	}
}
