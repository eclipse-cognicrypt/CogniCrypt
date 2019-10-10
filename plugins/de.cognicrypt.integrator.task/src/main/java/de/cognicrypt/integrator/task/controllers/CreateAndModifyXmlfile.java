/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

public class CreateAndModifyXmlfile {

	private String taskName;
	private ArrayList<Page> pages;
	private String filePath;

	/**
	 * The class creates the xml file containing the help content of the task, adds the location of the xml file in the plugin.xml file and sets the page help id. The class needs to
	 * be initialized with the list of pages and task name
	 *
	 * @param pages list of pages
	 * @param taskName the name of the task
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */

	public CreateAndModifyXmlfile(final ArrayList<Page> pages, final String taskName, final boolean taskHasPageHelpContent)
		throws IOException, ParserConfigurationException, SAXException, TransformerException {
		setTaskName(taskName);
		setPages(pages);
		// creates the template xml file
		createXmlFile();
		setFilePath(Constants.XML_FILE_DIRECTORY_PATH + getTaskName() + Constants.XML_EXTENSION);

		if (taskHasPageHelpContent) {
			/**
			 * For each page this loop creates a list of questions which has help content, then adds the content to the xml file and then sets the page help id
			 */
			for (final Page page : pages) {
				final ArrayList<Question> questionWithHelpContent = new ArrayList<>();
				String pageHelpContent = "";
				for (final Question question : page.getContent()) {
					if (!question.getHelpText().isEmpty()) {
						questionWithHelpContent.add(question);
					}
				}
				if (questionWithHelpContent.size() > 0) {
					for (final Question qstn : questionWithHelpContent) {
						pageHelpContent = pageHelpContent + qstn.getHelpText() + "\n";
					}
					addHelpContentToXmlFile(pageHelpContent, page.getId());
					// sets the page help id field
					page.setHelpID(getTaskName() + "_Page" + page.getId());
				}
			}
		} else if (!taskHasPageHelpContent) {
			addHelpContentToXmlFile(Constants.helpContentNotAvailable, pages.get(0).getId());
			pages.get(0).setHelpID(getTaskName() + "_Page" + pages.get(0).getId());
		}

		/**
		 * Calls the following method to add the path to the new xml file in the plugin.xml
		 */
		updatePluginXmlFile(CodeGenUtils.getResourceFromWithin(Constants.pluginXmlFile));

	}

	/**
	 * This method creates the template xml file for the task and places the xml file in target Help folder
	 *
	 * @throws IOException
	 */
	private void createXmlFile() throws IOException {

		final File xmlFileTargetDirectory = new File(Utils.getResourceFromWithin(Constants.XML_FILE_DIRECTORY_PATH), getTaskName() + Constants.XML_EXTENSION);
		// creates the xml file
		xmlFileTargetDirectory.createNewFile();

		// writer to write contents at target location
		final FileWriter xmlWriter = new FileWriter(xmlFileTargetDirectory);
		final StringBuilder sb = new StringBuilder();
		sb.append(Constants.Xml_Declaration + System.lineSeparator());
		sb.append(Constants.NLS_Tag + System.lineSeparator());
		sb.append(Constants.contextsOpeningTag + System.lineSeparator() + Constants.contextsClosingTag);
		final String xmlContent = sb + "";
		try {
			xmlWriter.write(xmlContent);
		}
		finally {
			xmlWriter.flush();
			xmlWriter.close();
		}
	}

	/**
	 * This method adds the page help content in the template xml file created by createXmlFile() method of this class
	 *
	 * @param pageHelpContent help content of the page
	 * @param pageId the page id
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public void addHelpContentToXmlFile(final String pageHelpContent, final int pageId) throws ParserConfigurationException, SAXException, IOException, TransformerException {

		final File xmlFile = Utils.getResourceFromWithin(getFilePath());
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.parse(xmlFile);

		// gets the root element contexts
		final Node contexts = doc.getElementsByTagName(Constants.contextsElement).item(0);
		final Element context = doc.createElement(Constants.contextElement);
		contexts.appendChild(context);

		// value of id attribute
		final String idValue = getTaskName() + "_Page" + pageId;

		// creates the id attribute of context element
		final Attr idAttribute = doc.createAttribute(Constants.idAttribute);
		idAttribute.setValue(idValue);
		context.setAttributeNode(idAttribute);

		// creates the title attribute of context element
		final Attr titleAttribute = doc.createAttribute(Constants.titleAttribute);
		titleAttribute.setValue(Constants.titleAttributeValue);
		context.setAttributeNode(titleAttribute);

		// Creates the description element which will contain the help content of the page
		final Element description = doc.createElement(Constants.descriptionAttribute);
		description.appendChild(doc.createTextNode(pageHelpContent));
		context.appendChild(description);

		// writes the content in to xml file
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);

	}

	/**
	 * This method parses the plugin.xml file to add the path of the new xml file in it
	 *
	 * @param pluginXmlFile the plugin.xml file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	private void updatePluginXmlFile(final File pluginXmlFile) throws ParserConfigurationException, SAXException, IOException, TransformerException {

		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.parse(pluginXmlFile);

		// gets the plugin element of the file
		final Node plugin = doc.getElementsByTagName(Constants.pluginElement).item(0);
		final NodeList extensions = plugin.getChildNodes();

		/**
		 * Following loop adds the path to the xml file of the task at the target location
		 */

		for (int i = 0; i < extensions.getLength(); i++) {
			final Node extension = extensions.item(i);
			if (Constants.extensionElement.equals(extension.getNodeName())) {
				final NamedNodeMap attr = extension.getAttributes();
				if (attr.getLength() == 1) {
					final Node point = attr.getNamedItem(Constants.pointAttribute);
					if (point.getTextContent().equals(Constants.pointAttributeValue)) {
						final Element contexts = doc.createElement(Constants.contextsElement);
						extension.appendChild(contexts);

						final Attr file = doc.createAttribute(Constants.fileAttribute);
						String filePath = Utils.getResourceFromWithin(getFilePath()).toString();
						filePath = filePath.replace("\\", "/");
						file.setValue(filePath.substring(filePath.indexOf(Constants.startingFrom)));
						contexts.setAttributeNode(file);

					}
				}
			}

		}

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(pluginXmlFile);
		transformer.transform(source, result);

	}

	/**
	 * @return the task name
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/**
	 * Sets the task name
	 *
	 * @param taskName
	 */
	public void setTaskName(final String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the list of pages
	 */
	public ArrayList<Page> getPages() {
		return this.pages;
	}

	/**
	 * Sets the list of pages
	 *
	 * @param pages
	 */
	public void setPages(final ArrayList<Page> pages) {
		this.pages = pages;
	}

	/**
	 * @return the path of the file
	 */
	public String getFilePath() {
		return this.filePath;
	}

	/**
	 * sets the file path
	 *
	 * @param filePath the path of the file
	 */
	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

}
