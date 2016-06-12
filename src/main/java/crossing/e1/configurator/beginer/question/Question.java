package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

public class Question {
	
	String questionText;
	ArrayList<Answer> answers;
	Answer defaultAnswer;
	
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
	
	public Answer getDefaultAnswer(){
		if (defaultAnswer == null){
			for(Answer answer: answers){
				if(answer.isDefaultAnswer()){
					defaultAnswer = answer;
					break;
				}
			}
		}
		
		return defaultAnswer;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(questionText + "\n");
		for(Answer answer : answers){
			
			builder.append("\t" + answer.getValue()  + " [dependencies=" + answer.getDependencies()+ "], defaultValue=" + answer.isDefaultAnswer() + "\n");

		}
		
		return builder.toString();
	}
	

}
