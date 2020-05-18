/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.Severities;
import de.cognicrypt.staticanalyzer.Activator;

/**
 * This class handles error markers for crypto misuses.
 *
 * @author Stefan Krueger
 * @author Andre Sonntag
 */
public class ErrorMarkerGenerator {

	private final List<IMarker> markers;

	public ErrorMarkerGenerator() {
		this.markers = new ArrayList<>();
	}

	/**
	 * Adds crypto-misuse error marker with message {@link message} into file {@link sourceFile} at Line {@link line}.
	 *
	 * @param markerType name of the error
	 * @param id unique id of the error
	 * @param sourceFile File the marker is generated into
	 * @param line Line the marker is generated at
	 * @param message Error Message
	 * @param sev Severities type
	 * @return <code>true</code>/<code>false</code> if error marker was (not) added successfully
	 */
	public boolean addMarker(final String markerType, final int id, final IResource sourceFile, final int line, final String message, final String crySLRuleName, final String jimpleCode, final Severities sev,
			final HashMap<String, String> additionalErrorInfos, boolean isSuppressed) {

		if (!sourceFile.exists() || !sourceFile.isAccessible()) {
			Activator.getDefault().logError(Constants.NO_RES_FOUND);
			return false;
		}

		IMarker marker;
		try {
			marker = sourceFile.createMarker(markerType);
			marker.setAttribute("errorType", markerType);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute("crySLRuleName", crySLRuleName);
			marker.setAttribute("errorJimpleCode", jimpleCode);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			if (isSuppressed) {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			} else {
				marker.setAttribute(IMarker.SEVERITY,
						(sev == Severities.Error) ? IMarker.SEVERITY_ERROR : ((sev == Severities.Warning) ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_INFO));
			}
			marker.setAttribute(IMarker.SOURCE_ID, id);

			if (markerType.equals(Constants.REQUIRED_PREDICATE_MARKER_TYPE)) {
				marker.setAttribute("predicate", additionalErrorInfos.get("predicate"));
				marker.setAttribute("predicateParamCount", additionalErrorInfos.get("predicateParamCount"));
				marker.setAttribute("errorParam", additionalErrorInfos.get("errorParam"));
				marker.setAttribute("errorParamIndex", additionalErrorInfos.get("errorParamIndex"));
			}

		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		return this.markers.add(marker);
	}

	/**
	 * Deletes markers from file and clears markers list.
	 *
	 * @return <code>true</code>/<code>false</code> if all error markers were (not) deleted successfully
	 */
	public boolean clearMarkers() {
		return clearMarkers(null);
	}

	public boolean clearMarkers(final IProject curProj) {
		final boolean allMarkersDeleted = true;
		try {
			for (final IMarker marker : this.markers) {
				if (curProj == null || (curProj != null && curProj.equals(marker.getResource().getProject()))) {
					marker.delete();
				}
			}
			if (curProj != null) {
				curProj.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		this.markers.clear();
		return allMarkersDeleted;
	}

}
