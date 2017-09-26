package crossing.e1.primitive.questionnaire;

import java.util.ArrayList;

public class QuestionsList {
	
	private int id;
	private ArrayList<Questions> listQuestions;
	
	public ArrayList<Questions> getQuestions(){
		return this.listQuestions;
	}
	public int getID(){
		return this.id;
	}
	public void setQuestions(ArrayList<Questions> QuestionsList){
		this.listQuestions=QuestionsList;
	}
	public void setID(int id){
		this.id=id;
	}
}
