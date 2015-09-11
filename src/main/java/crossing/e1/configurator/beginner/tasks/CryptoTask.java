package crossing.e1.configurator.beginner.tasks;

import java.util.HashSet;

import crossing.e1.configurator.beginner.questions.CryptoQuestion;

public abstract class CryptoTask {
	
	protected String description;
	protected String displayText;
	protected HashSet<CryptoQuestion> relevantQuestions;
	protected String claferTaskName;
	
	public CryptoTask(String description, String displayText, String claferTaskName){
		this.description = description;
		this.displayText = displayText;
		this.claferTaskName = claferTaskName;
		relevantQuestions = new HashSet<CryptoQuestion>();
	}
	
	public CryptoTask(String description, String displayText, String claferTaskName, HashSet<CryptoQuestion> questions){
		this.description = description;
		this.displayText = displayText;
		this.claferTaskName = claferTaskName;
		relevantQuestions = questions;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public HashSet<CryptoQuestion> getRelevantQuestions() {
		return relevantQuestions;
	}

	public void setRelevantQuestions(HashSet<CryptoQuestion> relevantQuestions) {
		this.relevantQuestions = relevantQuestions;
	}

	public String getClaferTaskName() {
		return claferTaskName;
	}

	public void setClaferTaskName(String claferTaskName) {
		this.claferTaskName = claferTaskName;
	}
	
	

}
