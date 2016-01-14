package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.List;

import crossing.e1.xml.export.Answer;
import crossing.e1.xml.export.Question;
import crossing.e1.xml.export.ReadTaskConfig;

public class QuestionsBeginner {

	private ArrayList<Question> qutionare;
	private String taskName;
	int counter = 0;

	void setCounter(int value) {
		counter = value;
	}

	public void init(String taskName,String filePath) {
		setTask(taskName);
		setQutionare(new ReadTaskConfig().getQA(taskName,filePath));
		
	}

	public ArrayList<Question> getQutionare() {
		return qutionare;
	}

	public void setQutionare(ArrayList<Question> arrayList) {
		this.qutionare = arrayList;
	}

	public void setTask(String value) {
		taskName = value;

	}

	public String getTask() {
		return taskName;

	}

	public boolean hasQuestions() {
		if (counter < getQutionare().size())
			return true;
		else
			return false;
	}

	public List<Answer> nextValues() {
		List<Answer> answers = getQutionare().get(counter).getAnswers();
		counter = counter + 1;
		return answers;
	}

	public Question nextQuestion() {
		List<Question> key = getQutionare();
		return key.get(counter);
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

}
