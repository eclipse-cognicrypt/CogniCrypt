package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;

public class GroupForLinkAnswer extends Group {
	private Question currentQuestion;
	private ArrayList<Question> listOfAllQuestions;

	public GroupForLinkAnswer(Composite parent, int style, Answer answer, Question currentQuestion, ArrayList<Question> listOfAllQuestions) {
		super(parent, style);
		setCurrentQuestion(currentQuestion);
		setListOfAllQuestion(listOfAllQuestions);

		/**
		 * Non-editable text field containing the answer
		 */
		Text answerTxt = new Text(this, SWT.BORDER);
		answerTxt.setBounds(5, 5, 155, 25);
		answerTxt.setEditable(false);
		answerTxt.setText(answer.getValue());

		/**
		 * Combo containing the list of all questions that can be linked to
		 * when selecting a particular answer
		 */
		Combo comboForLinkAnswers = new Combo(this, SWT.READ_ONLY);
		comboForLinkAnswers.setBounds(165, 5, 460, 25);
		comboForLinkAnswers.add("Default");
		
		for (int i = 0; i < listOfAllQuestions.size(); i++) {
			
			/**
			 * Executes when the total no. of question created for a task is equal to one
			 */
			if (listOfAllQuestions.size() == 1) {
				comboForLinkAnswers.removeAll();
				comboForLinkAnswers.add("Please add more questions to link the answers");
			}
			
			/**
			 * adding all the list of questions to the combo box
			 * excluding the current question
			 */
			if (currentQuestion.getId() != listOfAllQuestions.get(i).getId()) {
				comboForLinkAnswers.add(listOfAllQuestions.get(i).getQuestionText());
			}

		}

		/**
		 * Executes when the link answer dialog is opened for the 
		 * second time to edit the stored data
		 */
		if (answer.getNextID() != -2) {

			if (answer.getNextID() == -1) {
				comboForLinkAnswers.setText("Default");
			} else {
				for (Question question : listOfAllQuestions) {
					if (question.getId() == answer.getNextID()) {
						comboForLinkAnswers.setText(question.getQuestionText());
					}
				}
			}
		}
		
		/**
		 * sets the answer nextID to the ID of selected question form the combo box
		 */
		comboForLinkAnswers.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comboForLinkAnswers.getText().equalsIgnoreCase("Default")) {
					answer.setNextID(-1);
				} else {
					for (Question question : listOfAllQuestions) {
						if (question.getQuestionText().equalsIgnoreCase(comboForLinkAnswers.getText())) {
							answer.setNextID(question.getId());
						}
					}
				}
			}

		});
	
	}
	
	/**
	 * sets the currentQuestions
	 * @param currentQuestion 
	 */
	public void setCurrentQuestion(Question currentQuestion){
		this.currentQuestion=currentQuestion;
	}
	
	/**
	 * sets the list of all questions
	 * @param listOfAllQuestion
	 */
	public void setListOfAllQuestion(ArrayList<Question> listOfAllQuestions){
		this.listOfAllQuestions=listOfAllQuestions;
	}
	@Override
	protected void checkSubclass() {
		// To disable the check that prevents subclassing of SWT components
	}
	

}
