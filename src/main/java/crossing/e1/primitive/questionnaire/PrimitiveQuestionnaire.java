package crossing.e1.primitive.questionnaire;

import java.util.List;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.QuestionsJSONReader;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.primitive.types.Primitive;

public class PrimitiveQuestionnaire {
	private Primitive primitive;
	private List<Question> questionList;
	private int ID;
	
	public PrimitiveQuestionnaire(final Primitive primitive,final String filePath){
		this.primitive=primitive;
		this.questionList=new QuestionsJSONReader().getQuestions(filePath);
		this.ID = 0;
	}
	public int getCurrentID() {
		return this.ID;
	}

	public Question getQuestionByID(final int ID) {
		return this.questionList.get(ID);
	}

	public List<Question> getQuetionare() throws NullPointerException {
		return this.questionList;
	}

	public List<Question> getQuestionList() {
		return this.questionList;
	}
	
	public Primitive getPrimitive() {
		return this.primitive;
	}

	public boolean hasMoreQuestions() {
		return this.ID < getQuetionare().size();
	}

	public boolean isFirstQuestion() {
		return this.ID == 0;
	}
	public Question nextQuestion() {
		return this.questionList.get(this.ID++);
	}

	public Question previousQuestion() {
		return this.questionList.get(--this.ID);
	}

	public Question setQuestionByID(final int ID) {
		this.ID = ID;
		return this.questionList.get(this.ID);
	}

	public void setPrimitive(final Primitive primitive) {
		this.primitive = primitive;
	}


}
