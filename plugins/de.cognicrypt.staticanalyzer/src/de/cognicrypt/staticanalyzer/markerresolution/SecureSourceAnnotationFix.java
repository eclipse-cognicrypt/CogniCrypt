package de.cognicrypt.staticanalyzer.markerresolution;



import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IMarkerResolution;
import org.w3c.dom.Element;

import crypto.analysis.errors.RequiredPredicateError;
import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.annotations.SecureSourceAnnotationManager;
import de.cognicrypt.staticanalyzer.statment.CCStatement;
import de.cognicrypt.utils.XMLParser;

/**
 * This class adds the Load annotation to the right object deletes the marker on
 * the UI
 *
 * @author André Sonntag
 */
public class SecureSourceAnnotationFix implements IMarkerResolution {

	private final String label;
	private final RequiredPredicateError error;
	private SecureSourceAnnotationManager manager = null;
	
	public SecureSourceAnnotationFix(final String label, final RequiredPredicateError error) {
		super();
		this.label = label;
		this.error = error;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void run(final IMarker marker) {		
		try {
			//Temp solution
			SuppressWarningFix tempFix = new SuppressWarningFix("");
			//tempFix.run(marker);
			
			manager = new SecureSourceAnnotationManager(new DeveloperProject(marker.getResource().getProject()));
			manager.addAdditionalFiles("resources/Annotations");
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			CCStatement cc = new CCStatement(error.getErrorLocation());
			manager.annotateProblemSource(marker, cc.getOuterMethod(), cc.getParameterVarNameByIndex(error.getExtractedValues().getCallSite().getIndex()), error.getExtractedValues().getCallSite().getIndex());	
			marker.delete();
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		} catch (BadLocationException e) {
			Activator.getDefault().logError(e);
		}
	}
}
