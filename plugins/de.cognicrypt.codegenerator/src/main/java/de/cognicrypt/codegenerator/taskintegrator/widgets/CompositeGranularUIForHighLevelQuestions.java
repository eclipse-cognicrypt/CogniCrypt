package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.text.AttributedString;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.wizard.QuestionDialog;

public class CompositeGranularUIForHighLevelQuestions extends Composite {
	private Text txtQuestionID;
	public Text txtQuestion;
	private Text txtAnswerType;
	private Text txtAnswers;
	
	private Question question;
	private AttributedString SUPER_SCRIPT;
	private ArrayList<ClaferFeature> claferFeatures;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeGranularUIForHighLevelQuestions(Composite parent, int style, Question questionParam) {
		super(parent, SWT.BORDER);
		
		setQuestion(questionParam);
		//setClaferFeatures(claferFeatures);
		
		setLayout(null);
		
		GroupModifyDeleteButtons grpModifyDeleteButtons = new GroupModifyDeleteButtons(this,question);
		RowLayout rowLayout = (RowLayout) grpModifyDeleteButtons.getLayout();
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.fill = true;
		rowLayout.center = true;
		grpModifyDeleteButtons.setBounds(10, 5, 571, 53);
		
		Button linkQstn=new Button(this,SWT.None);
		linkQstn.setBounds(588,20,100,53);
		linkQstn.setText("Link Question");
		linkQstn.setVisible(false);
		linkQstn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
			/*	QuestionDialog link=new QuestionDialog(parent.getShell(),questionParam,claferFeatures);
				link.open();
			*/}
		});
		
		Group grpQuestionDetails = new Group(this, SWT.NONE);
		grpQuestionDetails.setBounds(10, 62, 571, 180);
		grpQuestionDetails.setLayout(null);
		grpQuestionDetails.setText("Question details");
		
		Label lblQuestionId = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestionId.setBounds(5, 30, 76, 17);
		lblQuestionId.setText("Question id:");
		
		txtQuestionID = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestionID.setEditable(false);
		txtQuestionID.setBounds(94, 25, 162, 29);
		
		// update this part.
		
		txtQuestionID.setText(Integer.toString(question.getId()));
		//txtQuestionID.setText("0");
		
		Label lblQuestion = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestion.setBounds(5, 60, 58, 17);
		lblQuestion.setText("Question:");
		
		txtQuestion = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestion.setEditable(false);
		txtQuestion.setBounds(94, 57, 403, 29);
		
		setTextQuestion(question.getQuestionText());
		//setTextQuestion("Test");

		//txtQuestion.setText(question.getQuestionText());
		//txtQuestion.setText("question");
		
		Label lblType = new Label(grpQuestionDetails, SWT.NONE);
		lblType.setBounds(267, 30, 38, 20);
		lblType.setText("Type:");
		
		txtAnswerType = new Text(grpQuestionDetails, SWT.BORDER);
		txtAnswerType.setEditable(false);
		txtAnswerType.setBounds(315, 25, 182, 29);
		
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
		//txtAnswerType.setText(question.get);
		
		Label lblAnswers = new Label(grpQuestionDetails, SWT.NONE);
		lblAnswers.setBounds(5, 98, 55, 17);
		lblAnswers.setText("Answers:");
		
		txtAnswers = new Text(grpQuestionDetails, SWT.BORDER);
		txtAnswers.setEditable(false);
		txtAnswers.setBounds(94, 92, 403, 29);
		//txtAnswers.setText("answers");
		txtAnswers.setText(answerString.toString());
		
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
