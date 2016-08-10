package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

public class Answer {

	String value;
	Boolean defaultAnswer;
	ArrayList<Dependency> dependencies;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(ArrayList<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public Boolean isDefaultAnswer() {
		return defaultAnswer == null ? false : defaultAnswer;
	}

	public void setDefaultAnswer(Boolean defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
	}

	@Override
	public String toString() {
		//the combo viewer calls the toString() method so just display the value
		return value;
	}

}
