package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;

import java.awt.font.TextAttribute;
import java.text.AttributedString;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;

import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;


public class CompositeGranularUIForHighLevelQuestions extends Composite {
	private Text txtQuestionID;
	private Text txtQuestion;
	private Text txtAnswerType;
	private Text txtAnswers;
	
	private Question question;
	private AttributedString SUPER_SCRIPT;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeGranularUIForHighLevelQuestions(Composite parent, int style, Question questionParam) {
		super(parent, SWT.BORDER);
		
		setQuestion(questionParam);
		
		setLayout(null);
		
		GroupModifyDeleteButtons grpModifyDeleteButtons = new GroupModifyDeleteButtons(this);
		RowLayout rowLayout = (RowLayout) grpModifyDeleteButtons.getLayout();
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.fill = true;
		rowLayout.center = true;
		grpModifyDeleteButtons.setBounds(10, 10, 471, 41);
		
		Group grpQuestionDetails = new Group(this, SWT.NONE);
		grpQuestionDetails.setBounds(10, 57, 471, 126);
		grpQuestionDetails.setLayout(null);
		grpQuestionDetails.setText("Question details");
		
		Label lblQuestionId = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestionId.setBounds(5, 11, 66, 17);
		lblQuestionId.setText("Question id:");
		
		txtQuestionID = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestionID.setEditable(false);
		txtQuestionID.setBounds(76, 5, 162, 29);
		
		// update this part.
		txtQuestionID.setText(Integer.toString(question.getId()));
		//txtQuestionID.setText("0");
		
		Label lblQuestion = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestion.setBounds(18, 45, 53, 17);
		lblQuestion.setText("Question:");
		
		txtQuestion = new Text(grpQuestionDetails, SWT.BORDER);
		txtQuestion.setEditable(false);
		txtQuestion.setBounds(76, 39, 384, 29);
		
		txtQuestion.setText(question.getQuestionText());
		//txtQuestion.setText("question");
		
		Label lblType = new Label(grpQuestionDetails, SWT.NONE);
		lblType.setBounds(244, 11, 30, 17);
		lblType.setText("Type:");
		
		txtAnswerType = new Text(grpQuestionDetails, SWT.BORDER);
		txtAnswerType.setEditable(false);
		txtAnswerType.setBounds(278, 5, 182, 29);
		
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
			answerString.append(answer.getValue());
			answerString.append(answer.isDefaultAnswer() ? "*" : "");
			SUPER_SCRIPT = new AttributedString("c");
			SUPER_SCRIPT.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
			answerString.append(answer.getCodeDependencies().size()>0 ? "@" : "");
			SUPER_SCRIPT = new AttributedString("x");
			SUPER_SCRIPT.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
			answerString.append(answer.getClaferDependencies().size()>0 ? "#" : "");
			answerString.append("|");
		}
			
		
		
	
		
		txtAnswerType.setText("type");
		//txtAnswerType.setText(question.get);
		
		Label lblAnswers = new Label(grpQuestionDetails, SWT.NONE);
		lblAnswers.setBounds(20, 79, 51, 17);
		lblAnswers.setText("Answers:");
		
		txtAnswers = new Text(grpQuestionDetails, SWT.BORDER);
		txtAnswers.setEditable(false);
		txtAnswers.setBounds(76, 73, 384, 29);
		//txtAnswers.setText("answers");
		txtAnswers.setText(answerString.toString());
		
		this.setSize(SWT.DEFAULT, 190);

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
}
