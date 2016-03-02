package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.List;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.ReadTaskConfig;

public class QuestionsBeginner {

	private ArrayList<Question> qutionare;
	private String taskName;
	private int counter = 0;

	public ArrayList<Question> getQutionare() {
		return this.qutionare;
	}

	public String getTask() {
		return this.taskName;
	}

	public boolean hasQuestions() {
		return this.counter < getQutionare().size();
	}

	public void init(final String taskName, final String filePath) {
		setTask(taskName);
		setQutionare(new ReadTaskConfig().getQA(taskName, filePath));
	}

	public Question nextQuestion() {
		final List<Question> key = getQutionare();
		return key.get(this.counter);
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

	public List<Answer> nextValues() {
		return getQutionare().get(this.counter++).getAnswers();
	}

	public void setQutionare(final ArrayList<Question> arrayList) {
		this.qutionare = arrayList;
	}

	public void setTask(final String value) {
		this.taskName = value;
	}

	void setCounter(final int value) {
		this.counter = value;
	}

}
