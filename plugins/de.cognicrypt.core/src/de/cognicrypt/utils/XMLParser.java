package de.cognicrypt.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class XMLParser {

	public static Document getDocFromFile(File f) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	public static Document createDoc(String rootElement) throws ParserConfigurationException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement(rootElement);
		doc.appendChild(root);
		return doc;
	}

	public static Element createChildElement(Element c0, String childElement, String value) {
		Document doc = c0.getOwnerDocument();
		Element child = doc.createElement(childElement);
		child.appendChild(doc.createTextNode(value));
		return createChildElement(c0, child);
	}

	public static Element createChildElement(Element c0, String childElement) {
		Document doc = c0.getOwnerDocument();
		Element child = doc.createElement(childElement);
		return createChildElement(c0, child);
	}

	public static Element createChildElement(Element c0, Element c1) {
		c0.appendChild(c1);
		return c1;
	}
	
	public static void createAttrForElement(Element c0, String attrElement, String value) {
		Document doc = c0.getOwnerDocument();
		Attr attr = doc.createAttribute("id");
		attr.setValue(value);
		c0.setAttributeNode(attr);
	}
	
	public static void writeXML(Document doc, String path) throws TransformerException {
		
		File f = new File(path);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.err.println("Problems with File");
				//Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
			}
		}
		writeXML(doc, f);
	}

	public static void writeXML(Document doc, File f) throws TransformerException {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(f);
		transformer.transform(source, result);
		System.out.println("File saved!");
	}
	
	
	public static void removeNodeByAttrValue(Document doc,String nodename, String attrElement, String value) {		
		NodeList nl = doc.getElementsByTagName(nodename);
		for(int i = 0; i < nl.getLength(); i++) {
			NamedNodeMap map = nl.item(i).getAttributes();
			for(int j = 0; j < map.getLength(); j++) {
				if(map.item(j).getTextContent().equals(value)) {
					nl.item(i).getParentNode().removeChild(nl.item(i));					
				}
			}
		}		
	}
		
	public static List<String> getElementValuesByTagName(Document doc, String tagname){
		NodeList nodes = doc.getElementsByTagName(tagname);
		List<String> valueList = new ArrayList<>();
		for(int i = 0; i < nodes.getLength(); i++) {
			valueList.add(nodes.item(i).getTextContent());
		}
		return valueList;
	}
	
	
	public static ArrayList<String> getAttrValuesByAttrName(Document doc, String nodename, String attrname){
		ArrayList<String> valueList = new ArrayList<>();
		NodeList nl = doc.getElementsByTagName(nodename);
		for(int i = 0; i < nl.getLength(); i++) {
			NamedNodeMap map = nl.item(i).getAttributes();
			for(int j = 0; j < map.getLength(); j++) {
				valueList.add(map.item(j).getTextContent());
			}
		}
		return valueList;	
	}
	
	
	
	public static String getElementValueById(Document doc, String elementId){
		Element element = doc.getElementById(elementId);
		return element.getTextContent();
	}
	
	
	
}
