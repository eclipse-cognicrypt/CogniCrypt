package de.cognicrypt.staticanalyzer.markerresolution;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import crypto.analysis.errors.RequiredPredicateError;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.annotations.impl.LoadAnnotation;

/**
 * This method provides solutions for the marker resolution
 *
 * @author André Sonntag
 */
public class QuickFixer implements IMarkerResolutionGenerator {

	ArrayList<IMarkerResolution> quickFixes;
	boolean toggle = true;

	@Override
	public IMarkerResolution[] getResolutions(final IMarker mk) {

		quickFixes = new ArrayList<>();
		String message = "";

		try {
			message = (String) mk.getAttribute(IMarker.MESSAGE);
			quickFixes.add(new SuppressWarningFix(Constants.SUPPRESSWARNING_FIX + message));

			if (toggle) {
				if (mk.getAttribute("error") instanceof RequiredPredicateError) {
					RequiredPredicateError rpError = (RequiredPredicateError) mk.getAttribute("error");
					if (rpError.getContradictedPredicate().getPredName().equals("generatedKey")
							|| rpError.getContradictedPredicate().getPredName().equals("randomized")) {
						quickFixes.add(new LoadAnnotationFix(
								"This object comes from a stream/database/other external source and is actually secure.",
								rpError));
					}
				}
			}

		} catch (CoreException e) {
			Activator.getDefault().logError(e);
		}

		return quickFixes.toArray(new IMarkerResolution[quickFixes.size()]);
	}

}
