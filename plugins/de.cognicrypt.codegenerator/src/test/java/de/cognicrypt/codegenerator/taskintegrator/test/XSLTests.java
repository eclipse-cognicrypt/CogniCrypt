/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.controllers.XSLStringGenerationAndManipulation;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLAttribute;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLTag;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;

public class XSLTests {

	HashMap<String, XSLTag> tags = new HashMap<>();

	@Before
	/**
	 * Generate all the tags.
	 */
	public void generateTagsFromConstants() {
		for (XSLTags xslTag : Constants.XSLTags.values()) {
			ArrayList<XSLAttribute> attributes = new ArrayList<>();
			for (String attributeName : xslTag.getXSLAttributes()) {
				attributes.add(new XSLAttribute(attributeName, "some value"));
			}
			tags.put(xslTag.getXSLTagFaceName(), new XSLTag(xslTag, attributes));
		}
	}

	@Test
	/**
	 * Check whether the enumeration in Constants generates valid files.
	 */
	public void testTagsFromConstants() {

		for (XSLTag tag : tags.values()) {

			System.out.println("Tag under consideration : " + tag.getXSLTagDetails().getXSLTagFaceName());
			boolean validTag = false;
			StringBuilder dataFromFile = new StringBuilder();

			dataFromFile.append(XSLStringGenerationAndManipulation.generateXSLStringFromPath(null, "", new Point(0, 0), tag.toString()));

			try {
				DocumentHelper.parseText(dataFromFile.toString());
				validTag = true;
			} catch (DocumentException e) {
				validTag = false;
			}

			assertTrue(validTag);

		}
	}
}
