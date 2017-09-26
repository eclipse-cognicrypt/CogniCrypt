package crossing.e1.primitive.questionnaire;

import java.util.List;

import crossing.e1.primitive.types.Primitive;

public class PrimitiveQuestionnaire {

	private Primitive primitive;
	private List<QuestionsList> questionList;
	private int ID;

	public PrimitiveQuestionnaire(final Primitive primitive, final String filePath) {
		this.primitive = primitive;
		this.questionList = new PrimitiveQuestionsJSONReader().getQuestions(filePath);
		this.ID = 0;
	}

	public int getCurrentID() {
		return this.ID;
	}

	public QuestionsList getQuestionByID(final int ID) {
		return this.questionList.get(ID);
	}

	public List<QuestionsList> getQuetionare() throws NullPointerException {
		return this.questionList;
	}

	public List<QuestionsList> getQuestionList() {
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

	public QuestionsList nextQuestion() {
		return this.questionList.get(this.ID++);
	}

	public QuestionsList previousQuestion() {
		return this.questionList.get(--this.ID);
	}

	public QuestionsList setQuestionByID(final int ID) {
		this.ID = ID;
		return this.questionList.get(this.ID);
	}

	public void setPrimitive(final Primitive primitive) {
		this.primitive = primitive;
	}

}
