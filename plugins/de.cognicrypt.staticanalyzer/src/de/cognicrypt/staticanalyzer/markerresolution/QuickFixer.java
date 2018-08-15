package de.cognicrypt.staticanalyzer.markerresolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * @author André Sonntag
 */
public class QuickFixer implements IMarkerResolutionGenerator {
	@Override
	public IMarkerResolution[] getResolutions(IMarker mk) {
		
		String message = "";
		try {
			message = (String) mk.getAttribute(IMarker.MESSAGE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return new IMarkerResolution[] { new SuppressWarning("Suppress Warning: "+message) };
	}

}
