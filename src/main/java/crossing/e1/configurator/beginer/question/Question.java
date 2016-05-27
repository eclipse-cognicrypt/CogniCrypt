package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

public class Question {
	
	String questionText;
	ArrayList<Answer> answers;
	
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(questionText + "\n");
		for(Answer answer : answers){
			builder.append("\t" + answer.toString() + "\n");
		}
		
		return builder.toString();
	}
	

}
