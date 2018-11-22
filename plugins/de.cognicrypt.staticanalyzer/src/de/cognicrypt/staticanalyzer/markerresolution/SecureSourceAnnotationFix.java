package de.cognicrypt.staticanalyzer.markerresolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IMarkerResolution;

import crypto.analysis.errors.RequiredPredicateError;
import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.annotations.SecureSourceAnnotationManager;
import de.cognicrypt.staticanalyzer.statment.CCStatement;

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
			// Temp solution
			final SuppressWarningFix tempFix = new SuppressWarningFix("");
			tempFix.run(marker);
			
			this.manager = new SecureSourceAnnotationManager(new DeveloperProject(marker.getResource().getProject()));
			this.manager.addAdditionalFiles("resources/Annotations");
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			final CCStatement cc = new CCStatement(this.error.getErrorLocation());
			this.manager.annotateProblemSource(marker, cc.getOuterMethod(),
					cc.getParameterVarNameByIndex(this.error.getExtractedValues().getCallSite().getIndex()),
					this.error.getExtractedValues().getCallSite().getIndex());

			marker.delete();
			marker.getResource().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		} catch (final BadLocationException e) {
			Activator.getDefault().logError(e);
		}
	}
}
