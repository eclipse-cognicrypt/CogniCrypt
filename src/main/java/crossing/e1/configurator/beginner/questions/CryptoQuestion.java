package crossing.e1.configurator.beginner.questions;

import java.util.HashMap;

import org.clafer.ast.AstBoolExpr;

import crossing.e1.configurator.wizard.beginner.Constraint;

public abstract class CryptoQuestion {
	
	protected String questionText;
	protected String correspondingClaferProperty;
	protected HashMap<String, Constraint> choices;

	public CryptoQuestion(String questionText, HashMap<String, Constraint> choices){
		this.questionText = questionText;
		this.choices = choices;
	}
	
	public CryptoQuestion(String questionText){
		this.questionText = questionText;
		this.choices = new HashMap<String, Constraint>();		
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public HashMap<String, Constraint> getChoices() {
		return choices;
	}

	public void setChoices(HashMap<String, Constraint> choices) {
		this.choices = choices;
	}
	
	public Constraint getCorrespondingChoiceConstraint(String key){
		return choices.get(key);
	}
	
	
}
