package crossing.e1.configurator.beginner.questions;

import java.util.HashMap;

import org.clafer.ast.AstBoolExpr;

public abstract class CryptoQuestion {
	
	protected String questionText;
	protected HashMap<String, AstBoolExpr> choices;

	public CryptoQuestion(String questionText, HashMap<String, AstBoolExpr> choices){
		this.questionText = questionText;
		this.choices = choices;
	}
	
	public CryptoQuestion(String questionText){
		this.questionText = questionText;
		this.choices = new HashMap<String, AstBoolExpr>();		
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public HashMap<String, AstBoolExpr> getChoices() {
		return choices;
	}

	public void setChoices(HashMap<String, AstBoolExpr> choices) {
		this.choices = choices;
	}
	
	
}
