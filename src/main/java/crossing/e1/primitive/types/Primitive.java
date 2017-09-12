package crossing.e1.primitive.types;
public class Primitive {

	private String name;
	private String xmlFile;
	private boolean isSelected;
	
	

	

	public String getName() {
		return this.name;
	}
	
	public String getXmlFile(){
		return this.xmlFile;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setXmlFile(String xml){
		this.xmlFile=xml;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}

}
