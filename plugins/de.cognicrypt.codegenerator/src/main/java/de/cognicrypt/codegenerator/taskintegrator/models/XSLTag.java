package de.cognicrypt.codegenerator.taskintegrator.models;

import java.util.ArrayList;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.XSLTags;

public class XSLTag {
	private Constants.XSLTags XSLTagDetails; // Variable to hold all the XSL tag data.
	private ArrayList<XSLAttribute> XSLAttributes; // Variable to hold the selected tag attributes.
	/**
	 * @param xSLTagDetails
	 * @param xSLAttributes
	 */
	public XSLTag(XSLTags xSLTagDetails, ArrayList<XSLAttribute> xSLAttributes) {
		super();
		setXSLTagDetails(xSLTagDetails);
		setXSLAttributes(xSLAttributes);
	}
	/**
	 * @return XSLTagDetails the Tag details
	 */
	public Constants.XSLTags getXSLTagDetails() {
		return XSLTagDetails;
	}
	/**
	 * @param xSLTagDetails set the XSL tag details
	 */
	private void setXSLTagDetails(Constants.XSLTags xSLTagDetails) {
		XSLTagDetails = xSLTagDetails;
	}
	/**
	 * @return the xSLAttributes
	 */
	public ArrayList<XSLAttribute> getXSLAttributes() {
		return XSLAttributes;
	}
	/**
	 * @param xSLAttributes the xSLAttributes to set
	 */
	private void setXSLAttributes(ArrayList<XSLAttribute> xSLAttributes) {
		XSLAttributes = xSLAttributes;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder tagString = new StringBuilder();
		StringBuilder attributeString = new StringBuilder();
		
		// Generate the string for the attributes.
		for(XSLAttribute attribute : getXSLAttributes()){
			attributeString.append(" ");
			attributeString.append(attribute.getXSLAttributeName());
			attributeString.append("= \"");
			attributeString.append(attribute.getXSLAttributeData());
			attributeString.append("\"");
		}
		
		tagString.append(getXSLTagDetails().getXSLBeginTag());
		// The location of the attribute string insertion changes if there is no end tag. e.g. <xsl:value-of/>
		int VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES = 1;
		if(getXSLTagDetails().getXSLEndTag().equals("")){
			VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES = 2;
		}
		tagString.insert(getXSLTagDetails().getXSLBeginTag().length()-VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES, attributeString.toString());
		tagString.append(getXSLTagDetails().getXSLEndTag());
		
		return tagString.toString();
	}
	
	

}
