package crossing.e1.configurator.wizard.beginner;

import java.util.List;

import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.QuestionsJSONReader;
import crossing.e1.configurator.tasks.Task;

public class BeginnerModeQuestionnaire {

	private final List<Question> questionList;
	private Task task;
	private int ID;

	public BeginnerModeQuestionnaire(final Task task, final String filePath) {
		this.task = task;
		this.questionList = (new QuestionsJSONReader()).getQuestions(filePath);
		this.ID = 0;
	}

	public int getCurrentID() {
		return this.ID;
	}

	public Question getQuestionByID(final int ID) {
		return this.questionList.get(ID);
	}

	public List<Question> getQutionare() throws NullPointerException {
		return this.questionList;
	}

	public Task getTask() {
		return this.task;
	}

	public boolean hasMoreQuestions() {
		return this.ID < getQutionare().size();
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

	public void setTask(final Task task) {
		this.task = task;
	}

}
