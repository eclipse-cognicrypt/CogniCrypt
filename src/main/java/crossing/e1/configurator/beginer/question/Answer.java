package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

public class Answer {
	
	String value;
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
	
	@Override
	public String toString() {
		return "TaskAnswer [value=" + value + ", dependencies=" + dependencies
				+ "]";
	}
	
	

	
}
