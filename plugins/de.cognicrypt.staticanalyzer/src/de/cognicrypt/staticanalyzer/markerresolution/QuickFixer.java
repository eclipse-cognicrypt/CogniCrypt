/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.markerresolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;

/**
 * This method provides solutions for the marker resolution
 *
 * @author Andre Sonntag
 */
public class QuickFixer implements IMarkerResolutionGenerator {

	private List<String> secureExtenernalSources = Arrays.asList(new String[] {"randomized", "generatedKey", "generatedKeyPair", "generatedPubKey", "generatedPrivKey"});
	private List<IMarkerResolution> quickFixes;
	private boolean isSuppressed = false;

	@Override
	public IMarkerResolution[] getResolutions(final IMarker mk) {
		quickFixes = new ArrayList<>();
		String message = "";
		String errorType = "";
		int severity;
		try {
			severity = (int) mk.getAttribute(IMarker.SEVERITY);  //java.lang.NullPointerException
			errorType = (String) mk.getAttribute("errorType");
			message = (String) mk.getAttribute(IMarker.MESSAGE);
			if (severity == 2) {
				isSuppressed = false;
				quickFixes.add(new SuppressWarningFix(Constants.SUPPRESSWARNING_FIX + message));
			} else if (severity == 0) {				
				isSuppressed = true;
				quickFixes.add(new UnSuppressWarningFix(Constants.UNSUPPRESSWARNING_FIX + message));
			}
			
			// we need to check this, because the ensuring of a predicate with more as one parameter does not work currently.
			if(!isSuppressed) {
				String predicate = (String) mk.getAttribute("predicate");
				if (errorType.equals(Constants.REQUIRED_PREDICATE_MARKER_TYPE) && predicate != null) {
					if(secureExtenernalSources.contains(predicate)) {					
						quickFixes.add(new EnsuresPredicateFix(Constants.ENSUREPREDICATE_FIX));
					}
				}
			}
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		quickFixes.add(new IssueReportFix(Constants.FALSEPOSTIVEREPORTER_FIX));
		return quickFixes.toArray(new IMarkerResolution[quickFixes.size()]);
	}

}
