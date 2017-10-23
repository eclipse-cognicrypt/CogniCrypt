package crossing.e1.taskintegrator.models;

import java.util.ArrayList;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.XSLTags;

public class XSLTag {
	private Constants.XSLTags XSLTagDetails;
	private ArrayList<XSLAttribute> XSLAttributes;
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
	 * @return the xSLTagDetails
	 */
	public Constants.XSLTags getXSLTagDetails() {
		return XSLTagDetails;
	}
	/**
	 * @param xSLTagDetails the xSLTagDetails to set
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
		
		for(XSLAttribute attribute : getXSLAttributes()){
			attributeString.append(" " + attribute.getXSLAttributeName() + "= \"" + attribute.getXSLAttributeData() + "\"");
		}
		
		tagString.append(getXSLTagDetails().getXSLBeginTag());
		int VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES = 1;
		if(getXSLTagDetails().getXSLEndTag().equals("")){
			VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES = 2;
		}
		tagString.insert(getXSLTagDetails().getXSLBeginTag().length()-VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES, attributeString.toString());
		tagString.append(getXSLTagDetails().getXSLEndTag());
		
		return tagString.toString();
	}
	
	

}
