package de.cognicrypt.staticanalyzer.markerresolution;



import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;

import crypto.analysis.errors.RequiredPredicateError;
import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.annotations.LoadAnnotationManager;
import de.cognicrypt.staticanalyzer.statment.CCStatement;

/**
 * This class adds the Load annotation to the right object deletes the marker on
 * the UI
 *
 * @author André Sonntag
 */
public class LoadAnnotationFix implements IMarkerResolution {

	private final String label;
	private final RequiredPredicateError error;
	private LoadAnnotationManager manager = null;

	public LoadAnnotationFix(final String label, final RequiredPredicateError error) {
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
			
			manager = new LoadAnnotationManager(new DeveloperProject(marker.getResource().getProject()));
			manager.addAdditionalFiles("resources/Annotations");
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			CCStatement cc = new CCStatement(error.getErrorLocation());
			manager.annotateProblemSource(marker, cc.getVarNameByIndex(error.getExtractedValues().getCallSite().getIndex()));	
			marker.delete();
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
	}
}
