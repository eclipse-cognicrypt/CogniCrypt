package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

public class Answer {

	private String value;
	private Boolean defaultAnswer;
	private ArrayList<ClaferDependency> claferDependencies;
	private ArrayList<CodeDependency> codeDependencies;
	private int nextID;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<ClaferDependency> getClaferDependencies() {
		return claferDependencies;
	}

	public void setClaferDependencies(ArrayList<ClaferDependency> claferDependencies) {
		this.claferDependencies = claferDependencies;
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

	public ArrayList<CodeDependency> getCodeDependencies() {
		return codeDependencies;
	}

	public void setCodeDependencies(ArrayList<CodeDependency> codeDependencies) {
		this.codeDependencies = codeDependencies;
	}

	
	public int getNextID() {
		return nextID;
	}

	
	public void setNextID(int prevID) {
		this.nextID = prevID;
	}

}
