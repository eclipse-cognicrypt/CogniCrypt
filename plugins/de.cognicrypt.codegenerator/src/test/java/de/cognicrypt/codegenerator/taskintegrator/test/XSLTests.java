package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.XSLTags;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLAttribute;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLTag;

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
			dataFromFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			dataFromFile.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n");
			dataFromFile.append("<xsl:output method=\"text\"/>\n");
			dataFromFile.append("<xsl:template match=\"/\">\n");
			dataFromFile.append("package <xsl:value-of select=\"//task/Package\"/>; \n");
			dataFromFile.append("<xsl:apply-templates select=\"//Import\"/>\n");

			dataFromFile.append(tag.toString());

			dataFromFile.append("\n");
			dataFromFile.append("</xsl:template>\n");
			dataFromFile.append("<xsl:template match=\"Import\">\n");
			dataFromFile.append("import <xsl:value-of select=\".\"/>;\n");
			dataFromFile.append("</xsl:template>\n");
			dataFromFile.append("</xsl:stylesheet>");

			try {
				DocumentHelper.parseText(dataFromFile.toString());
				validTag = true;
			} catch (DocumentException e) {
				validTag = false;
			}


			if (tag.getXSLTagDetails().getXSLTagFaceName().equals("Otherwise")) {
				assertFalse(validTag);
			} else {
				assertTrue(validTag);
			}
			
		}
	}
}
