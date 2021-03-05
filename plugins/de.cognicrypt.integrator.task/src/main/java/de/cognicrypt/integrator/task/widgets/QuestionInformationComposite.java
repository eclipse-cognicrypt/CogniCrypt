/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.QuestionsPage;

/**
 * This class creates widgets to display the overview of the question details and creates button to add clafer/code dependency to the question
 */

public class QuestionInformationComposite extends Composite {

	private Text questionText;

	private final int questionIndex;
	private final QuestionModificationComposite answersComposite;
	
	public final ControlDecoration questionDec;
	public final ControlDecoration answersDec;

	private QuestionsPage questionsPage;
	
	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public QuestionInformationComposite(final Composite parent, final int style, final int questionIndex, QuestionsDisplayComposite questionsDisplayComposite, QuestionsPage questionsPage) {
		super(parent, SWT.BORDER);

		this.questionsPage = questionsPage;
		
		this.questionIndex = questionIndex;
		Question q = IntegratorModel.getInstance().getQuestions().get(questionIndex);

		final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		setLayoutData(gridData);
		setLayout(new GridLayout(1, true));

		final Composite questionsAndDelete = new Composite(this, SWT.NONE);
		questionsAndDelete.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		questionsAndDelete.setLayout(new GridLayout(2, true));	

		final Label lblQuestion = new Label(questionsAndDelete, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true));
		lblQuestion.setText("Question");
		
		final Button btnDeleteQuestion = new Button(questionsAndDelete, SWT.NONE);
		btnDeleteQuestion.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		btnDeleteQuestion.setImage(sharedImages.getImage(ISharedImages.IMG_TOOL_DELETE));
		btnDeleteQuestion.setToolTipText("Click to delete the question");
		btnDeleteQuestion.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("Are you sure that you want to remove this question?");
				confirmationMessageBox.setText("Remove Question");
				final int response = confirmationMessageBox.open();
				if (response == SWT.YES) {
					questionsDisplayComposite.deleteQuestion(questionIndex);
				}
			}
		});
		
		
		questionText = new Text(this, SWT.BORDER);
		final GridData gdTxtQuestion = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gdTxtQuestion.widthHint = 0;
		questionText.setText(q.getQuestionText());
		questionText.setLayoutData(gdTxtQuestion);

		// Initialize the decorator for the label for the text box with initial error state
		questionDec = new ControlDecoration(lblQuestion, SWT.TOP | SWT.RIGHT);
		questionDec.setShowOnlyOnFocus(false);
		checkQuestionDec();
		
		questionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				IntegratorModel.getInstance().getQuestion(questionIndex).setQuestionText(questionText.getText().trim());
				checkQuestionDec();
			}
		});
		
		final Text txtDescription = new Text(this, SWT.BORDER);
		final GridData gdTxtDescription= new GridData(SWT.FILL, SWT.CENTER, true, true);
		gdTxtDescription.widthHint = 0;
		txtDescription.setMessage("Describe the question");
		txtDescription.setText(q.getHelpText());
		txtDescription.setLayoutData(gdTxtDescription);
		
		txtDescription.addModifyListener(
				e -> IntegratorModel.getInstance().getQuestion(questionIndex).setHelpText(txtDescription.getText().trim()));
		
		final Label spacer2 = new Label(this, SWT.HORIZONTAL);
	    spacer2.setLayoutData(new GridData(GridData.FILL, 15));
		
		
	    final Composite answersAndAdd = new Composite(this, SWT.NONE);
	    answersAndAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
	    answersAndAdd.setLayout(new GridLayout(2, true));	
	    
		final Label lblAnswers = new Label(answersAndAdd, SWT.NONE);
		lblAnswers.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true));
		lblAnswers.setText("Answers");
		
		final Button btnAddAnswer = new Button(answersAndAdd, SWT.NONE);
		btnAddAnswer.setToolTipText("Click to add an answer");
		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				addAnswer();
			}
		});
		btnAddAnswer.setText("Add Answer");
		
		btnAddAnswer.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));

		answersComposite = new QuestionModificationComposite(this, SWT.NONE, questionIndex, questionsPage);

		
		for(int i=0; i < q.getAnswers().size(); i++) {
			answersComposite.addAnswerUIElements(i);
		}
		
		answersDec = new ControlDecoration(lblAnswers, SWT.TOP | SWT.RIGHT);
		answersDec.setShowOnlyOnFocus(false);
		checkAnswersDec();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	public void setTextQuestion(final String txtQuestion) {
		this.questionText.setText(txtQuestion);
	}

	public String getTextQuestion() {
		return questionText.getText();
	}
	
	public void addAnswer() {
		int answerIndex = IntegratorModel.getInstance().getQuestion(questionIndex).getAnswers().size();
		
		Answer a = new Answer();
		a.setValue("");
		a.setOption(IntegratorModel.getInstance().getIdentifiers().get(answerIndex % IntegratorModel.getInstance().getIdentifiers().size()));
		
		if(answerIndex == 0)
			a.setDefaultAnswer(true);
		
		IntegratorModel.getInstance().getQuestions().get(questionIndex).getAnswers().add(a);
		answersComposite.addAnswerUIElements(answerIndex);
		
		checkAnswersDec();
	}
	
	
	public void removeAnswer(int answerIndex) {
		
		boolean wasDefaultAnswer = IntegratorModel.getInstance().getAnswer(questionIndex, answerIndex).isDefaultAnswer();
		
		IntegratorModel.getInstance().getQuestion(questionIndex).getAnswers().remove(answerIndex);
		
		if(wasDefaultAnswer && !IntegratorModel.getInstance().getQuestion(questionIndex).getAnswers().isEmpty())
			IntegratorModel.getInstance().getAnswer(questionIndex, 0).setDefaultAnswer(true);
			
		checkAnswersDec();
	}
	
	private void checkQuestionDec() {
		if(IntegratorModel.getInstance().getQuestion(questionIndex).getQuestionText().isEmpty()) { 
			questionDec.setImage(Constants.DEC_ERROR);
			questionDec.setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_QUESTION_NAME);
		}else {
			questionDec.setImage(Constants.DEC_REQUIRED);
			questionDec.setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
		}
		
		questionsPage.checkPageComplete();
	}
	
	public void checkAnswersDec() {
		
		ArrayList<Answer> answers = IntegratorModel.getInstance().getQuestions().get(questionIndex).getAnswers(); 

		if(answers.isEmpty()) { 
			answersDec.setImage(Constants.DEC_ERROR);
			answersDec.setDescriptionText(Constants.ERROR + Constants.ERROR_BLANK_ANSWERS_LIST);
		}else {
			boolean answerIsEmpty = false;

			for(Answer a : answers) {
				answerIsEmpty |= a.getValue().isEmpty();
			}
			
			if(answerIsEmpty) {
				answersDec.setImage(Constants.DEC_ERROR);
				answersDec.setDescriptionText(Constants.ERROR + Constants.ERROR_EMPTY_ANSWER_TEXT);
			}else {
				answersDec.setImage(Constants.DEC_REQUIRED);
				answersDec.setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
			}
		}
		
		questionsPage.checkPageComplete();
	}
}
