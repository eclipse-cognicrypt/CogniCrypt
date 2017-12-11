package de.cognicrypt.codegenerator.primitive.types;


public class MethodsQuestions {

	int id;
	String questionText;
	
	public int getId(){
		return this.id;
	}
	public String getQuestion(){
		return this.questionText;
	}
	void setId(int id){
		this.id=id;
	}
	void setQuestion(String question){
		this.questionText=question;
	}
}
