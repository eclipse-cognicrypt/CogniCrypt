package de.cognicrypt.codegenerator.tasks;

public class Task {

	private String name;
	private String description;
	private String taskDescription;
	private String modelFile;
	private String questionsJSONFile;
	private boolean isSelected;
	private String additionalResources;
	private String xslFile;

	public String getAdditionalResources() {
		return this.additionalResources;
	}

	public String getDescription() {
		return this.description;
	}

	public String getTaskDescription() {
		return this.taskDescription;
	}

	public String getModelFile() {
		return this.modelFile;
	}

	public String getName() {
		return this.name;
	}

	public String getQuestionsJSONFile() {
		return this.questionsJSONFile;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setAdditionalResources(final String additionalResources) {
		this.additionalResources = additionalResources;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setTaskDescription(final String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public void setModelFile(final String modelFile) {
		this.modelFile = modelFile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setQuestionsJSONFile(final String questionsJSONFile) {
		this.questionsJSONFile = questionsJSONFile;
	}

	/**
	 * Getter method of style sheet.
	 * 
	 * @return the xslFile
	 */
	public String getXslFile() {
		return this.xslFile;
	}

	/**
	 * Setter method for style sheet.
	 * 
	 * @param xslFile
	 *        the xslFile to set
	 */
	public void setXslFile(final String xslFile) {
		this.xslFile = xslFile;
	}

}
