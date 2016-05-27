package crossing.e1.configurator.wizard.beginner;

import java.util.List;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.QuestionsJSONReader;

public class QuestionsBeginner {

	private List<Question> questionList;
	private String taskName;
	private int counter = 0;
	
	public QuestionsBeginner(final String taskName, final String filePath) {
		setTask(taskName);
		questionList = (new QuestionsJSONReader()).getQuestions(filePath);
	}

	public List<Question> getQutionare() throws NullPointerException {
		return questionList;
	}

	public String getTask() {
		return this.taskName;
	}

	public boolean hasQuestions() {
		//return this.counter < getQutionare().size();
		return questionList.size() > 0;
	}


	public Question nextQuestion() {
		return questionList.get(this.counter);
	}
	//
	// public String nextQuestionClafer() {
	// List<HashMap<String, String>> x = new ArrayList<HashMap<String, String>>(
	// getQutionare().keySet());
	// HashMap<String, String> questionClaferMap = x.get(x.size() - counter);
	// String claferName = questionClaferMap.get(x.get(x.size() - counter)
	// .keySet().toArray()[0].toString());
	// return claferName;
	// }

//	public List<Answer> nextValues() {
//		return getQutionare().get(this.counter++).getAnswers();
//	}
//
//	public void setQutionare(final ArrayList<Question> arrayList) {
//		this.questionList = arrayList;
//	}

	public void setTask(final String value) {
		this.taskName = value;
	}

	void setCounter(final int value) {
		this.counter = value;
	}

}
