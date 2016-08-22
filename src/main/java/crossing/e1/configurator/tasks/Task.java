package crossing.e1.configurator.tasks;

public class Task {

	private String name;
	private String description;
	private String modelFile;
	private String xmlFile;
	private boolean isSelected;
	private String additionalResources;

	public String getAdditionalResources() {
		return this.additionalResources;
	}

	public String getDescription() {
		return this.description;
	}

	public String getModelFile() {
		return this.modelFile;
	}

	public String getName() {
		return this.name;
	}

	public String getXmlFile() {
		return this.xmlFile;
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

	public void setModelFile(final String modelFile) {
		this.modelFile = modelFile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setXmlFile(final String xmlFile) {
		this.xmlFile = xmlFile;
	}

}
