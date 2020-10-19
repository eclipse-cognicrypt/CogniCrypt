/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;
import de.cognicrypt.integrator.task.controllers.XSLStringGenerationAndManipulation;
import de.cognicrypt.integrator.task.models.XSLAttribute;
import de.cognicrypt.integrator.task.models.XSLTag;

public class XSLTests {

	HashMap<String, XSLTag> tags = new HashMap<>();

	@Before
	/**
	 * Generate all the tags.
	 */
	public void generateTagsFromConstants() {
		for (final XSLTags xslTag : Constants.XSLTags.values()) {
			final ArrayList<XSLAttribute> attributes = new ArrayList<>();
			for (final String attributeName : xslTag.getXSLAttributes()) {
				attributes.add(new XSLAttribute(attributeName, "some value"));
			}
			this.tags.put(xslTag.getXSLTagFaceName(), new XSLTag(xslTag, attributes));
		}
	}

	@Test
	/**
	 * Check whether the enumeration in Constants generates valid files.
	 */
	public void testTagsFromConstants() {

		for (final XSLTag tag : this.tags.values()) {

			System.out.println("Tag under consideration : " + tag.getXSLTagDetails().getXSLTagFaceName());
			boolean validTag = false;
			final StringBuilder dataFromFile = new StringBuilder();

			dataFromFile.append(XSLStringGenerationAndManipulation.generateXSLStringFromPath(null, "", new Point(0, 0), tag.toString()));

			try {
				DocumentHelper.parseText(dataFromFile.toString());
				validTag = true;
			}
			catch (final DocumentException e) {
				validTag = false;
			}

			assertTrue(validTag);

		}
	}
}
