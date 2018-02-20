package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.text.AttributedString;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.wizard.AddDependenciesDialog;
import de.cognicrypt.codegenerator.taskintegrator.wizard.LinkAnswerDialog;
import de.cognicrypt.codegenerator.taskintegrator.wizard.QuestionDialog;

public class CompositeGranularUIForHighLevelQuestions extends Composite {
	private Text txtQuestionID;
	public Text txtQuestion;
	private Text txtAnswerType;
	
	private Question question;
	private AttributedString SUPER_SCRIPT;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeGranularUIForHighLevelQuestions(Composite parent, int style, Question questionParam, boolean linkAnswerPage) {
		super(parent, SWT.BORDER);
		
		setQuestion(questionParam);
		
		setLayout(null);
		
		GroupModifyDeleteButtons grpModifyDeleteButtons = new GroupModifyDeleteButtons(this, question);
		RowLayout rowLayout = (RowLayout) grpModifyDeleteButtons.getLayout();
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.fill = true;
		rowLayout.center = true;
		grpModifyDeleteButtons.setBounds(10, 5, 571, 53);
		
		//Only visible for "pageForHighLevelQuestions" page
		grpModifyDeleteButtons.setVisible(!linkAnswerPage);

		Button addDependencies = new Button(this, SWT.WRAP);
		addDependencies.setBounds(588, 50, 150, 103);
		addDependencies.setText("Click to\nLink Variability\nconstruct and \n Link code");
		addDependencies.setToolTipText("Click to Link variability construct and variables to use in code");
		//Only visible for "pageForHighLevelQuestions" page
		addDependencies.setVisible(!linkAnswerPage);

		addDependencies.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ClaferModel claferModel = ((CompositeToHoldGranularUIElements) addDependencies.getParent().getParent().getParent()).getClaferModel();
				AddDependenciesDialog addDependenciesDialog = new AddDependenciesDialog(parent.getShell(), question, claferModel);
				addDependenciesDialog.open();
			}
		});

		Button linkQstn = new Button(this, SWT.None);
		linkQstn.setBounds(588, 50, 100, 53);
		linkQstn.setText("Link Answer");

		//Visible only for the "pageForLinkAnswers" page 
		linkQstn.setVisible(linkAnswerPage);

		//opens the LinkAnswerDialog
		linkQstn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				//retrieves the list of all questions
				ArrayList<Question> listOfAllQuestions=((CompositeToHoldGranularUIElements)linkQstn.getParent().getParent().getParent()).getListOfAllQuestions();
				LinkAnswerDialog linkAnsDialog = new LinkAnswerDialog(parent.getShell(), question, listOfAllQuestions);
				linkAnsDialog.open();
			
			}
		});
		
		
		

		Group grpQuestionDetails = new Group(this, SWT.NONE);
		/***
		 * resizing the height of the grpQuestionDetails depending upon the page
		 */
		if(!linkAnswerPage){
		grpQuestionDetails.setBounds(10, 62, 571, 150);
		}
		else if(linkAnswerPage){
			grpQuestionDetails.setBounds(10, 5, 571, 150);

		}
		grpQuestionDetails.setLayout(null);
		grpQuestionDetails.setText("Question details");
		
		Label lblQuestionId = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestionId.setBounds(5, 35, 76, 17);
		lblQuestionId.setText("Question id:");
		
		txtQuestionID = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestionID.setEditable(false);
		txtQuestionID.setBounds(94, 30, 162, 29);
		
		// update this part.
		
		txtQuestionID.setText(Integer.toString(question.getId()));
		
		Label lblQuestion = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestion.setBounds(5, 70, 58, 17);
		lblQuestion.setText("Question:");
		
		txtQuestion = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestion.setEditable(false);
		txtQuestion.setBounds(94, 67, 403, 29);
		
		setTextQuestion(question.getQuestionText());

		Label lblType = new Label(grpQuestionDetails, SWT.NONE);
		lblType.setBounds(267, 35, 38, 20);
		lblType.setText("Type:");
		
		txtAnswerType = new Text(grpQuestionDetails, SWT.BORDER);
		txtAnswerType.setEditable(false);
		txtAnswerType.setBounds(315, 30, 182, 29);
		
		StringBuilder answerString = new StringBuilder();
		
		/*for(Answer answer : question.getAnswers()){
			answerString.append(answer.getValue());
			answerString.append(answer.isDefaultAnswer() ? "*" : "");
			SUPER_SCRIPT = new AttributedString("c");
			SUPER_SCRIPT.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
			answerString.append(answer.getCodeDependencies().size()>0 ? SUPER_SCRIPT.toString() : "");
			SUPER_SCRIPT = new AttributedString("x");
			SUPER_SCRIPT.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
			answerString.append(answer.getClaferDependencies().size()>0 ? SUPER_SCRIPT.toString() : "");
			answerString.append("|");
			
		}*/
		
		for(Answer answer : question.getAnswers()){
			//if(question.getQuestionType().equalsIgnoreCase(arg0)
			answerString.append("\"" );
			answerString.append(answer.getValue());
			answerString.append(answer.isDefaultAnswer() ? "*" : "");
			answerString.append("\"");
			/*SUPER_SCRIPT = new AttributedString("c");
			SUPER_SCRIPT.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
			answerString.append(answer.getCodeDependencies().size()>0 ? "@" : "");
			SUPER_SCRIPT = new AttributedString("x");
			SUPER_SCRIPT.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
			answerString.append(answer.getClaferDependencies().size()>0 ? "#" : "");
            */answerString.append(" | ");
		}

		txtAnswerType.setText(question.getQuestionType());

		Label lblAnswers = new Label(grpQuestionDetails, SWT.NONE);
		lblAnswers.setBounds(5, 108, 55, 17);
		lblAnswers.setText("Answers:");
		
		/**
		 * Following if conditions checks the question type and then
		 * creates the widgets to display the answer as the question type
		 */
		if (question.getQuestionType().equalsIgnoreCase("Drop down")) {
			
			Combo comboForAnswers = new Combo(grpQuestionDetails, SWT.READ_ONLY);
			comboForAnswers.setBounds(94, 102, 403, 29);

			for (Answer answer : question.getAnswers()) {
				comboForAnswers.add(answer.getValue());
			}
		} else if (question.getQuestionType().equalsIgnoreCase("Radio Button")) {
			int count = question.getAnswers().size();
			Button[] btn = new Button[count];
			int spaceToDisplayAns = 70;
			
			if(count==1){
				btn[0] = new Button(grpQuestionDetails, SWT.RADIO);
				btn[0].setBounds(spaceToDisplayAns, 102, 210, 29);
				btn[0].setText(question.getAnswers().get(0).getValue());
				
			}
			
			if (count == 2) {
				for (int i = 0; i < count; i++) {
					btn[i] = new Button(grpQuestionDetails, SWT.RADIO);
					btn[i].setBounds(spaceToDisplayAns, 102, 210, 29);
					btn[i].setText(question.getAnswers().get(i).getValue());
					if (spaceToDisplayAns < 500) {
						spaceToDisplayAns = spaceToDisplayAns + 220;
					}
				}

			} else if (count > 2) {
				for (int i = 0; i < count; i++) {
					btn[i] = new Button(grpQuestionDetails, SWT.RADIO);
					btn[i].setBounds(spaceToDisplayAns, 102, 80, 29);
					btn[i].setText(question.getAnswers().get(i).getValue());
					if (spaceToDisplayAns < 500) {
						spaceToDisplayAns = spaceToDisplayAns + 85;
					}
				}

			}

		}

		else if (question.getQuestionType().equalsIgnoreCase("text box")) {
			Text txtBox = new Text(grpQuestionDetails, SWT.BORDER);
			txtBox.setBounds(94, 102, 403, 29);
		}

		this.setSize(SWT.DEFAULT, 250);

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
	private void setQuestion(Question question) {
		this.question = question;
	}
	
	

	public void setTextQuestion(String txtQuestion){
		this.txtQuestion.setText(txtQuestion);
	}
	public String getTextQuestion(){
		return txtQuestion.getText();
	}
	
	public String getAnswerType(){
		return txtAnswerType.getText();
	}
}
