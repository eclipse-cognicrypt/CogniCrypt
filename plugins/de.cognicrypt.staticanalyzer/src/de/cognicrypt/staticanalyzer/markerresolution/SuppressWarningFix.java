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
	private XMLParser xmlParser;

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
		xmlParser = new XMLParser(warningsFile);
		try {
			if (warningsFile.exists()) {
				xmlParser.useDocFromFile();
			} else {
				xmlParser.createNewDoc();
				xmlParser.createRootElement("SuppressWarnings");
			}

			createSuppressWarningEntry(marker);
			xmlParser.writeXML();
			marker.delete();

		} catch (IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
		}

	}

	/**
	 * This method adds a new entry to the warnings suppress xml file
	 * 
	 * @param m ErrorMarker
	 * @throws CoreException
	 * @throws IOException
	 */
	public void createSuppressWarningEntry(IMarker m) throws CoreException, IOException {

		int id = (int) m.getAttribute(IMarker.SOURCE_ID);
		String ressource = m.getResource().getName();
		int lineNumber = (int) m.getAttribute(IMarker.LINE_NUMBER);
		String message = (String) m.getAttribute(IMarker.MESSAGE);

		Element root = xmlParser.getRoot();
		Element warningEntry = xmlParser.createChildElement(root, "SuppressWarning");
		xmlParser.createAttrForElement(warningEntry, "ID", id + "");
		xmlParser.createChildElement(warningEntry, "File", ressource);
		xmlParser.createChildElement(warningEntry, "LineNumber", lineNumber + "");
		xmlParser.createChildElement(warningEntry, "Message", message);
	}

}
