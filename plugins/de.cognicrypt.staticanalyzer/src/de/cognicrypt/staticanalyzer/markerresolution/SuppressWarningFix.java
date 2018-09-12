package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.w3c.dom.Element;

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.XMLParser;

/**
 * This class writes the suppress warning information in a XML file and deletes
 * the marker on the UI
 *
 * @author André Sonntag
 */
public class SuppressWarningFix implements IMarkerResolution {

	private final String label;
	private XMLParser xmlParser;

	public SuppressWarningFix(final String label) {
		super();
		this.label = label;
	}

	/**
	 * This method adds a new entry to the warnings suppress xml file
	 *
	 * @param m
	 *            ErrorMarker
	 * @throws CoreException
	 * @throws IOException
	 */
	public void createSuppressWarningEntry(final IMarker m) throws CoreException, IOException {

		final int id = (int) m.getAttribute(IMarker.SOURCE_ID);
		final String ressource = m.getResource().getName();
		final int lineNumber = (int) m.getAttribute(IMarker.LINE_NUMBER);
		final String message = (String) m.getAttribute(IMarker.MESSAGE);

		final Element warningEntry = this.xmlParser.createChildElement(this.xmlParser.getRoot(),
				Constants.SUPPRESSWARNING_ELEMENT);
		this.xmlParser.createAttrForElement(warningEntry, Constants.ID_ATTR, id + "");
		this.xmlParser.createChildElement(warningEntry, Constants.FILE_ELEMENT, ressource);
		this.xmlParser.createChildElement(warningEntry, Constants.LINENUMBER_ELEMENT, lineNumber + "");
		this.xmlParser.createChildElement(warningEntry, Constants.MESSAGE_ELEMENT, message);
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void run(final IMarker marker) {

		final File warningsFile = new File(marker.getResource().getProject().getLocation().toOSString()
				+ Constants.outerFileSeparator + Constants.SUPPRESSWARNING_FILE);
		this.xmlParser = new XMLParser(warningsFile);
		try {
			if (warningsFile.exists()) {
				this.xmlParser.useDocFromFile();
			} else {
				this.xmlParser.createNewDoc();
				this.xmlParser.createRootElement(Constants.SUPPRESSWARNINGS_ELEMENT);
			}

			createSuppressWarningEntry(marker);
			this.xmlParser.writeXML();
			marker.delete();
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}

	}

}
