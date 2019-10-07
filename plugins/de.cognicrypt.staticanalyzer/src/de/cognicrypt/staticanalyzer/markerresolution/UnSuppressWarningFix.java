package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.File;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.XMLParser;

/**
 * This class removes the suppressed warning data from SuppresssedWarning.xml and changes info marker to error marker
 *
 * @author Seena Mathew
 */
public class UnSuppressWarningFix implements IMarkerResolution{

	private final String label;
	private XMLParser xmlParser;
	
	public UnSuppressWarningFix(final String label) {
		super();
		this.label = label;
		}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
	@Override
	public void run(final IMarker marker) {
		final File warningsFile = new File(marker.getResource().getProject().getLocation().toOSString() + Constants.outerFileSeparator + Constants.SUPPRESSWARNING_FILE);
		this.xmlParser = new XMLParser(warningsFile);
		try {
			if (warningsFile.exists()) {
				this.xmlParser.useDocFromFile();
			} else {
				this.xmlParser.createNewDoc();
				this.xmlParser.createRootElement(Constants.SUPPRESSWARNINGS_ELEMENT);
			}
			
			this.xmlParser.removeNodeByAttrValue(Constants.SUPPRESSWARNING_ELEMENT, Constants.ID_ATTR, marker.getAttribute(IMarker.SOURCE_ID) + "");
			this.xmlParser.writeXML();
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
	
	}
}