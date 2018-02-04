package de.cognicrypt.codegenerator.primitive.utilities;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXML {

	DocumentBuilderFactory docFactory;
	DocumentBuilder docBuilder;
	Document doc;
	Element rootElement;

	public WriteXML() {
		
	}
	public void createDocument() throws ParserConfigurationException{
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docBuilder = docFactory.newDocumentBuilder();
		this.doc = docBuilder.newDocument();
	}

	public void setRoot(String root) {

		this.rootElement = this.doc.createElement(root);
		doc.appendChild(rootElement);
	}

	public void addElement(String name, String value) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value));
		this.rootElement.appendChild(element);
	}

	// write the content into xml file
	public void isCreated(File xmlFile) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(this.doc);
		StreamResult result = new StreamResult(xmlFile);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
		System.out.println("File saved!");
	}

}
