/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.results;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;

/**
 * This class handles error markers for crypto misuses.
 *
 * @author Stefan Krueger
 *
 */
public class ErrorMarkerGenerator {

	private final List<IMarker> markers;

	public ErrorMarkerGenerator() {
		this.markers = new ArrayList<>();
	}

	/**
	 * Adds crypto-misuse error marker with message {@link message} into file
	 * {@link sourceFile} at Line {@link line}.
	 *
	 * @param sourceFile File the marker is generated into
	 * @param line       Line the marker is generated at
	 * @param message    Error Message
	 * @return <code>true</code>/<code>false</code> if error marker was (not) added
	 *         successfully
	 */
	public boolean addMarker(final IResource sourceFile, final int line, final String message) {
		return addMarker(sourceFile, line, message, false);
	}

	/**
	 * Adds crypto-misuse error marker with message {@link message} into file
	 * {@link sourceFile} at Line {@link line}.
	 *
	 * @param sourceFile File the marker is generated into
	 * @param line       Line the marker is generated at
	 * @param message    Error Message
	 * @param isWarning  Determines whether marker type is warning or error
	 * @return <code>true</code>/<code>false</code> if error marker was (not) added
	 *         successfully
	 */
	public boolean addMarker(final IResource sourceFile, final int line, final String message, boolean isWarning) {
		if (!sourceFile.exists() || !sourceFile.isAccessible()) {
			Activator.getDefault().logError(Constants.NO_RES_FOUND);
			return false;
		}

		IMarker marker;
		try {
			marker = sourceFile.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			marker.setAttribute(IMarker.SEVERITY, (!isWarning) ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		this.markers.add(marker);
		return true;
	}

	/**
	 * Deletes markers from file and clears markers list.
	 * 
	 * @return <code>true</code>/<code>false</code> if all error markers were (not)
	 *         deleted successfully
	 */
	public boolean clearMarkers() {
		return clearMarkers(null);
	}

	public boolean clearMarkers(final IProject curProj) {
		boolean allMarkersDeleted = true;
		try {
			for (final IMarker marker : this.markers) {
				if (curProj == null || (curProj != null && curProj.equals(marker.getResource().getProject()))) {
					marker.delete();
				}
			}
			if (curProj != null) {
				curProj.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
		}
		this.markers.clear();
		return allMarkersDeleted;
	}

}
