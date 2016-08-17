package crossing.e1.configurator.tasks;

public class Task {

	private String name;
	private String description;
	private String modelFile;
	private String xmlFile;
	private boolean isSelected;
	private String additionalResources;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	
	public String getAdditionalResources() {
		return additionalResources;
	}

	
	public void setAdditionalResources(String additionalResources) {
		this.additionalResources = additionalResources;
	}

}
