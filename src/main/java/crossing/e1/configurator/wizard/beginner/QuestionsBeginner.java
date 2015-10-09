package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import crossing.e1.xml.export.ReadTaskConfig;

public class QuestionsBeginner {

	private HashMap<HashMap<String, String>, List<String>> qutionare;
	private String taskName;
	int counter = 0;

	void setCounter(int value) {
		counter = value;
	}

	public void init(String taskName) {
		setTask(taskName);
		setQutionare(new ReadTaskConfig().getQA(taskName));
		setCounter(qutionare.size());

	}

	public HashMap<HashMap<String, String>, List<String>> getQutionare() {
		return qutionare;
	}

	public void setQutionare(
			HashMap<HashMap<String, String>, List<String>> qutionare) {
		this.qutionare = qutionare;
	}

	public void setTask(String value) {
		taskName = value;

	}

	public String getTask() {
		return taskName;

	}

	public boolean hasQuestions() {
		if (counter > 0)
			return true;
		else
			return false;
	}

	public List<String> nextValues() {
		List<HashMap<String, String>> key = new ArrayList<HashMap<String, String>>(
				getQutionare().keySet());
		List<String> answers = getQutionare()
				.get(key.get(key.size() - counter));
		counter = counter - 1;
		return answers;
	}

	public String nextQuestion() {
		List<HashMap<String, String>> x = new ArrayList<HashMap<String, String>>(
				getQutionare().keySet());
		return x.get(x.size() - counter).keySet().toArray()[0].toString();
	}

	public String nextQuestionClafer() {
		List<HashMap<String, String>> x = new ArrayList<HashMap<String, String>>(
				getQutionare().keySet());
		HashMap<String, String> questionClaferMap = x.get(x.size() - counter);
		String claferName = questionClaferMap.get(x.get(x.size() - counter)
				.keySet().toArray()[0].toString());
		return claferName;
	}

}
