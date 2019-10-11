/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.models;

import java.util.ArrayList;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;

public class XSLTag {

	private Constants.XSLTags XSLTagDetails; // Variable to hold all the XSL tag data.
	private ArrayList<XSLAttribute> XSLAttributes; // Variable to hold the selected tag attributes.

	/**
	 * @param xSLTagDetails This detail come from the enumeration {@code XSLTags} in {@link Constants} class. These include the XSLTagFaceName, XSLBeginTag, XSLEndTag and an
	 *        ArrayList of all possible attributes (XSLAttribute)
	 * @param xSLAttributes This is an ArrayList of type {@link XSLAttribute} that are actually chosen.
	 */
	public XSLTag(final XSLTags xSLTagDetails, final ArrayList<XSLAttribute> xSLAttributes) {
		super();
		setXSLTagDetails(xSLTagDetails);
		setXSLAttributes(xSLAttributes);
	}

	/**
	 * @return XSLTagDetails the constant from {@code XSLTags} in {@link Constants} passed to the constructor.
	 */
	public Constants.XSLTags getXSLTagDetails() {
		return this.XSLTagDetails;
	}

	/**
	 * @param xSLTagDetails set the constant from {@code XSLTags} in {@link Constants}.
	 */
	private void setXSLTagDetails(final Constants.XSLTags xSLTagDetails) {
		this.XSLTagDetails = xSLTagDetails;
	}

	/**
	 * @return the ArrayList of type {@link XSLAttribute}.
	 */
	public ArrayList<XSLAttribute> getXSLAttributes() {
		return this.XSLAttributes;
	}

	/**
	 * @param xSLAttributes set the ArrayList<{@link XSLAttribute}>.
	 */
	private void setXSLAttributes(final ArrayList<XSLAttribute> xSLAttributes) {
		this.XSLAttributes = xSLAttributes;
	}

	/**
	 * Overridden toString().
	 * <p>
	 * First generate a String from ArrayList of type {@link XSLAttribute} such that it is in the form {@code ' XSLAttributeName1 = "XSLAttributeData1"' ...}
	 * <p>
	 * A String for the complete XSLTag is generated, by appending the XSLBeginTag, the attribute string, and the XSLEndTag. XSLTags may have an empty XSLEndTag.
	 */
	@Override
	public String toString() {
		final StringBuilder tagString = new StringBuilder();
		final StringBuilder attributeString = new StringBuilder();

		// Generate the string for the attributes.
		for (final XSLAttribute attribute : getXSLAttributes()) {
			attributeString.append(" ");
			attributeString.append(attribute.getXSLAttributeName());
			attributeString.append("=\"");
			attributeString.append(attribute.getXSLAttributeData());
			attributeString.append("\"");
		}

		tagString.append(getXSLTagDetails().getXSLBeginTag());
		// The location of the attribute string insertion changes if there is no end tag. e.g. <xsl:value-of/>
		int VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES = 1;
		if (getXSLTagDetails().getXSLEndTag().equals("")) {
			VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES = 2;
		}
		tagString.insert(getXSLTagDetails().getXSLBeginTag().length() - VALUE_TO_GET_LOCATION_TO_INSERT_ATTRIBUTES, attributeString.toString());
		tagString.append(getXSLTagDetails().getXSLEndTag());

		return tagString.toString();
	}

}
