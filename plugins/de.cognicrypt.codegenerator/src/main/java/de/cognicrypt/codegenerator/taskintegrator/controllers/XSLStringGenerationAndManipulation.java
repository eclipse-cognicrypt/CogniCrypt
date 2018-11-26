/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.clafer.instance.InstanceClafer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.XMLClaferParser;
import de.cognicrypt.core.Constants;

public class XSLStringGenerationAndManipulation {

	/**
	 * 
	 * @param filePath
	 * @param existingText
	 * @param selected
	 * @return
	 */
	public static String generateXSLStringFromPath(String filePath, String existingText, Point selected, String stringToAdd) {
		StringBuilder dataFromFile = new StringBuilder();

		String xslStringBeforeAddingText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n<xsl:output method=\"text\"/>\n<xsl:template match=\"/\">\n\n\n\npackage <xsl:value-of select=\"//task/Package\"/>; \n<xsl:apply-templates select=\"//Import\"/>\n";
		String xslStringAfterAddingText = "\n</xsl:template>\n<xsl:template match=\"Import\">\nimport <xsl:value-of select=\".\"/>;\n</xsl:template>\n</xsl:stylesheet>";

		if (filePath != null) {
			Path path = Paths.get(filePath);
			if (path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA") || path.getFileName().toString().endsWith(".txt") || path
				.getFileName().toString().endsWith(".TXT")) {

				// Check if the XSL tags have already been created. If yes then add the text at the cursor location.
				if (existingText.contains("<?xml version=") || existingText.contains("<xsl:stylesheet xmlns:xsl=")) {
					dataFromFile.append(existingText.substring(0, selected.x));
					dataFromFile.append("\n");
					dataFromFile.append("<xsl:result-document href=\"\">\n");
					dataFromFile.append("package <xsl:value-of select=\"//task/Package\"/>;\n<xsl:apply-templates select=\"//Import\"/>\n");
					dataFromFile.append("\n");
					appendTextFromFileToStringBuilder(dataFromFile, filePath);
					dataFromFile.append("\n");
					dataFromFile.append("</xsl:result-document>");
					dataFromFile.append("\n");
					dataFromFile.append(existingText.substring(selected.y, existingText.length()));

				} else {
					dataFromFile.append(xslStringBeforeAddingText);
					appendTextFromFileToStringBuilder(dataFromFile, filePath);
					dataFromFile.append(xslStringAfterAddingText);
				}

			} else {
				appendTextFromFileToStringBuilder(dataFromFile, filePath);
			}
		} else {
			dataFromFile.append(xslStringBeforeAddingText);
			dataFromFile.append(stringToAdd);
			dataFromFile.append(xslStringAfterAddingText);
		}

		return dataFromFile.toString();
	}

	/**
	 * Read the data from the file and add it to the StringBuilder.
	 * 
	 * @param dataFromFile
	 *        the StringBuilder.
	 * @param filePath
	 *        the location of the file to be read.
	 */
	private static void appendTextFromFileToStringBuilder(StringBuilder dataFromFile, String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String line = br.readLine();

			while (line != null) {
				dataFromFile.append(line);
				dataFromFile.append(System.lineSeparator());
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			Activator.getDefault().logError(e);
		} catch (IOException e) {
			Activator.getDefault().logError(e);
		}

	}

	/**
	 * Set the colors to all the {@link XmlRegion}.
	 * 
	 * @param regions
	 *        the List of {@link XmlRegion}
	 * @return returns the {@link StyleRange} for the given code.
	 */
	public static List<StyleRange> computeStyleForXMLRegions(List<XmlRegion> regions) {
		List<StyleRange> styleRanges = new ArrayList<StyleRange>();
		for (XmlRegion xr : regions) {

			// The style itself depends on the region type
			// In this example, we use colors from the system
			StyleRange sr = new StyleRange();
			switch (xr.getXmlRegionType()) {
				case MARKUP:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
					break;

				case ATTRIBUTE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
					break;

				case ATTRIBUTE_VALUE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
					break;

				case MARKUP_VALUE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
					break;
				case COMMENT:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
					break;
				case INSTRUCTION:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
					break;
				case CDATA:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
					break;
				case WHITESPACE:
					break;
				default:
					break;
			}

			// Define the position and limit
			sr.start = xr.getStart();
			sr.length = xr.getEnd() - xr.getStart();
			styleRanges.add(sr);
		}

		return styleRanges;
	}

	public static void getListOfValidSuggestionsForXSLTags(String jsFilePath, String taskName, String taskDescription, List<Question> questions, HashMap<String, String> tagValueTagData) {
		if (jsFilePath != null) {
			InstanceGenerator instanceGenerator = new InstanceGenerator(jsFilePath, "c0_" + taskName, taskDescription);

			// This will contain the xml strings that are generated for every -> operator encountered.
			List<Document> xmlStrings = new ArrayList<Document>();

			XMLClaferParser xmlParser = new XMLClaferParser();
			// this will remain empty for the first instance, that contains no -> operators.
			HashMap<Question, Answer> constraints = new HashMap<>();
			List<InstanceClafer> instances = instanceGenerator.generateInstances(constraints);
			if (instances.size() > 0) {
				InstanceClafer initialInstance = instances.get(0);
				xmlStrings.add(xmlParser.displayInstanceValues(initialInstance, constraints));

				// Questions needed to get the answer that has a constraint with the -> operator.
				//QuestionsJSONReader reader = new QuestionsJSONReader();
				// TODO update this to read the data generated in the questions page.

				//List<Page> pages = reader.getPages("/src/main/resources/TaskDesc/SymmetricEncryption.json");

				//for (Page page : pages) {
				for (Question question : questions) {
					for (Answer answer : question.getAnswers()) {
						if (answer.getClaferDependencies() != null) {
							for (ClaferDependency claferDependency : answer.getClaferDependencies()) {
								if ("->".equals(claferDependency.getOperator())) {
									xmlStrings.add(getXMLForNewAlgorithmInsertion(question, answer, xmlParser, instanceGenerator, claferDependency));

								}
							} // clafer dependency loop
						} // clafer dependency check
						if (answer.getCodeDependencies() != null) {
							for (CodeDependency codeDependency : answer.getCodeDependencies()) {
								//xmlStrings.get(0).elementByID(Constants.Code).addElement(codeDependency.getOption()).addText(codeDependency.getValue() + "");
								Element root = xmlStrings.get(0).getRootElement();

								for (Iterator<Element> element = root.elementIterator(Constants.Code); element.hasNext();) {
									Element codeElement = element.next();
									// TODO fix question page to not create null code dependencies
									if (codeDependency != null && codeDependency.getOption() != null && codeDependency.getValue() != null) {
										codeElement.addElement(codeDependency.getOption()).addText(codeDependency.getValue() + "");
									}
								}
							} // code dependency loop
						} // code dependency check
					} // answer loop
				} // question loop
					//} // page loop

				// Process each xml document that is generated.
				for (Document xmlDocument : xmlStrings) {
					processXMLDocument(xmlDocument, tagValueTagData);
				}
			}

		}
	}

	/**
	 * This method is created to be able to exit the nested loops as soon as the correct instance is found.
	 * 
	 * @param question
	 *        The question object from the outer loop.
	 * @param answer
	 *        The answer object from the outer loop.
	 * @param xmlParser
	 *        This object is needed to generate the xml string.
	 * @param instanceGenerator
	 *        This object is needed to generate the instances
	 * @param claferDependency
	 *        The claferDependency from the outer loop
	 * @return
	 */
	private static Document getXMLForNewAlgorithmInsertion(Question question, Answer answer, XMLClaferParser xmlParser, InstanceGenerator instanceGenerator, ClaferDependency claferDependency) {
		HashMap<Question, Answer> constraints = new HashMap<>();
		constraints.put(question, answer);
		String constraintOnType = claferDependency.getAlgorithm();
		for (InstanceClafer instance : instanceGenerator.generateInstances(constraints)) {
			for (InstanceClafer childInstance : instance.getChildren()) {
				// check if the name of the constraint on the clafer instance is the same as the one on the clafer dependency from the outer loop.
				if (childInstance.getType().getName().equals(constraintOnType)) {
					return xmlParser.displayInstanceValues(instance, constraints);
				}
			} // child instance loop
		} // instance loop
		return null;
	}

	/**
	 * Process the XML document here to generate values to be displayed to the user for selection.
	 * 
	 * @param xmlDocument
	 *        The serialized object representing the generated XML string.
	 * @param tagValueTagData
	 */
	private static void processXMLDocument(Document xmlDocument, HashMap<String, String> tagValueTagData) {
		Element root = xmlDocument.getRootElement();
		// send a slash as a parameter to keep the recursive method as generic as possible.
		processElement(root, "", Constants.SLASH, true, tagValueTagData);
	}

	/**
	 * This method will process each element individually, and is called recursively to process nested tags.
	 * 
	 * @param xmlElement
	 *        The element under consideration.
	 * @param existingNameToBeDisplayed
	 *        The string that will be displayed to the user for selection.
	 * @param existingDataForXSLDocument
	 *        The actual string that will be added to the code base on the selection that is done by the user.
	 * @param isRoot
	 *        true if the element is the root element.
	 * @param tagValueTagData
	 */
	private static void processElement(Element xmlElement, String existingNameToBeDisplayed, String existingDataForXSLDocument, boolean isRoot, HashMap<String, String> tagValueTagData) {
		StringBuilder tagNameToBeDisplayed = new StringBuilder();
		StringBuilder tagDataForXSLDocument = new StringBuilder();

		tagNameToBeDisplayed.append(existingNameToBeDisplayed);
		tagDataForXSLDocument.append(existingDataForXSLDocument);

		if (!isRoot) {
			tagNameToBeDisplayed.append(Constants.DOT);
		}
		tagNameToBeDisplayed.append(xmlElement.getName());
		tagDataForXSLDocument.append(Constants.SLASH);
		tagDataForXSLDocument.append(xmlElement.getName());

		int builderDisplayDataSizeTillRoot = tagNameToBeDisplayed.length();
		int builderTagDataSizeTillRoot = tagDataForXSLDocument.length();

		if (xmlElement.attributeCount() == 0 && !xmlElement.elementIterator().hasNext()) {
			// adding the tag, if there are no attributes.
			tagValueTagData.put(tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString());
		} else {
			for (Iterator<Attribute> attribute = xmlElement.attributeIterator(); attribute.hasNext();) {
				Attribute attributeData = attribute.next();
				// TODO the name of the task can be fixed here based on what is chosen before.	

				if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
					tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
				}

				if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
					tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
				}

				tagNameToBeDisplayed.append(Constants.DOT);
				tagNameToBeDisplayed.append("@" + attributeData.getName());

				tagDataForXSLDocument.append(Constants.ATTRIBUTE_BEGIN);
				tagDataForXSLDocument.append(attributeData.getName());
				tagDataForXSLDocument.append(Constants.ATTRIBUTE_END);

				tagValueTagData.put(tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString());

				// Adding the loop for the remaining elements within the attribute loop to have unique tags based on the attributes. 
				for (Iterator<Element> element = xmlElement.elementIterator(); element.hasNext();) {
					Element currentElement = element.next();
					// do not consider the imports tag. The data is not relevant.
					if (!currentElement.getName().equals("Imports")) {
						if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
							tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
						}

						if (isRoot) {
							if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
								tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
							}
						}
						// recursive call
						processElement(currentElement, tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString(), false, tagValueTagData);
					}
				}
			}
		}

		// A similar loop outside the attribute loop to check the tags that are not nested.
		for (Iterator<Element> element = xmlElement.elementIterator(); element.hasNext();) {
			Element currentElement = element.next();
			// do not consider the imports tag. The data is not relevant.
			if (!currentElement.getName().equals("Imports")) {
				if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
					tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
				}

				if (isRoot) {
					if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
						tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
					}
				}
				// recursive call
				processElement(currentElement, tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString(), false, tagValueTagData);
			}
		}
	}
}
