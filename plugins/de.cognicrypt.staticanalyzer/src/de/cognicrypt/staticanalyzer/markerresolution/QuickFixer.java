package de.cognicrypt.staticanalyzer.markerresolution;

import java.util.ArrayList;
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
	
	private ArrayList<IMarkerResolution> quickFixes;
	
	@Override
	public IMarkerResolution[] getResolutions(final IMarker mk) {
		quickFixes = new ArrayList<>();
		String message = "";
		String errorType = "";

		try {
			errorType = (String) mk.getAttribute("errorType");
			message = (String) mk.getAttribute(IMarker.MESSAGE);
			quickFixes.add(new SuppressWarningFix(Constants.SUPPRESSWARNING_FIX + message));

			if (errorType.equals(Constants.REQUIRED_PREDICATE_MARKER_TYPE)) {
				String predicate = (String) mk.getAttribute("predicate");

				if(predicate.equals("generatedKey") || predicate.equals("randomized")) {
					quickFixes.add(new EnsuresPredicateFix("This object comes from a stream/database/other external source and is actually secure."));
				}
			}
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		return quickFixes.toArray(new IMarkerResolution[quickFixes.size()]);
	}

}
