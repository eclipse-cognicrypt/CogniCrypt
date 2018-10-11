package de.cognicrypt.staticanalyzer.markerresolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;

/**
 * This method provides solutions for the marker resolution
 *
 * @author André Sonntag
 */
public class QuickFixer implements IMarkerResolutionGenerator {
	@Override
	public IMarkerResolution[] getResolutions(final IMarker mk) {

		String message = "";
		try {
			message = (String) mk.getAttribute(IMarker.MESSAGE);
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		return new IMarkerResolution[] { new SuppressWarningFix(Constants.SUPPRESSWARNING_FIX + message) };
	}

}
