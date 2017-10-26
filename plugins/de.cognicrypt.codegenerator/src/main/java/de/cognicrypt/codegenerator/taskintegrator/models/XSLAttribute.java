package de.cognicrypt.codegenerator.taskintegrator.models;


public class XSLAttribute {
	
	private String XSLAttributeName;	
	private String XSLAttributeData;
	/**
	 * @param xSLAttributeName
	 * @param xSLAttributeData
	 */
	public XSLAttribute(String xSLAttributeName, String xSLAttributeData) {
		super();
		setXSLAttributeName(xSLAttributeName);
		setXSLAttributeData(xSLAttributeData);
	}
	/**
	 * @return the xSLAttributeName
	 */
	public String getXSLAttributeName() {
		return XSLAttributeName;
	}
	/**
	 * @param xSLAttributeName the xSLAttributeName to set
	 */
	public void setXSLAttributeName(String xSLAttributeName) {
		XSLAttributeName = xSLAttributeName;
	}
	/**
	 * @return the xSLAttributeData
	 */
	public String getXSLAttributeData() {
		return XSLAttributeData;
	}
	/**
	 * @param xSLAttributeData the xSLAttributeData to set
	 */
	public void setXSLAttributeData(String xSLAttributeData) {
		XSLAttributeData = xSLAttributeData;
	}
	


}
