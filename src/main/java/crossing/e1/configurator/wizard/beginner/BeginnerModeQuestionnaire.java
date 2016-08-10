package crossing.e1.configurator.wizard.beginner;

import java.util.List;

import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.QuestionsJSONReader;
import crossing.e1.configurator.tasks.Task;

public class BeginnerModeQuestionnaire {

	private List<Question> questionList;
	private Task task;
	private int counter = 0;

	public BeginnerModeQuestionnaire(final Task task, final String filePath) {
		this.task = task;
		questionList = (new QuestionsJSONReader()).getQuestions(filePath);
	}

	public List<Question> getQutionare() throws NullPointerException {
		return questionList;
	}

	public Task getTask() {
		return this.task;
	}

	public boolean hasQuestions() {
		//return this.counter < getQutionare().size();
		return questionList.size() > 0;
	}

	public Question nextQuestion() {
		return questionList.get(this.counter);
	}

	public void setTask(final Task task) {
		this.task = task;
	}

	void setCounter(final int value) {
		this.counter = value;
	}

}
