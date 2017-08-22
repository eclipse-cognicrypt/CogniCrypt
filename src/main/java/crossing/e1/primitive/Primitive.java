package crossing.e1.primitive;
public class Primitive {

	private String name;
	private String modelFile;
	private boolean isSelected;

	

	public String getModelFile() {
		return this.modelFile;
	}

	public String getName() {
		return this.name;
	}

	public boolean isSelected() {
		return this.isSelected;
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

}
