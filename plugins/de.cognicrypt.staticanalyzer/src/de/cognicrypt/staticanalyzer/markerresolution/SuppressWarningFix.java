package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.XMLParser;

/**
 * @author André Sonntag
 */
public class SuppressWarningFix implements IMarkerResolution {

	private String label;

	public SuppressWarningFix(String label) {
		super();
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {

		File warningsFile = new File(
				marker.getResource().getProject().getLocation().toOSString() + "\\SuppressWarnings.xml");
		Document doc;
		try {
			if (warningsFile.exists()) {
				doc = XMLParser.getDocFromFile(warningsFile);
			} else {
				doc = XMLParser.createDoc("SuppressWarnings");
			}

			createSuppressWarningNode(doc, marker);
			XMLParser.writeXML(doc, warningsFile);
			marker.delete();

		} catch (IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method adds a new node to the warnings suppress xml file
	 * 
	 * @param f
	 *            warning File
	 * @param m
	 *            marker
	 * @throws CoreException
	 * @throws IOException
	 */
	public void createSuppressWarningNode(Document doc, IMarker m) throws CoreException, IOException {

		int id = (int) m.getAttribute(IMarker.SOURCE_ID);
		String ressource = m.getResource().getName();
//		int lineNumber = (int) m.getAttribute(IMarker.LINE_NUMBER);
		String message = (String) m.getAttribute(IMarker.MESSAGE);

		Element rootNode = doc.getDocumentElement();
		Element warningNode = XMLParser.createChildElement(rootNode, "SuppressWarning");
		XMLParser.createAttrForElement(warningNode, "ID", id + "");
		XMLParser.createChildElement(warningNode, "File", ressource);
		XMLParser.createChildElement(warningNode, "Message", message);
	}

}
