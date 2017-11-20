package de.cognicrypt.staticanalyzer.results;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Constants;

/**
 * This class handles error markers for crypto misuses.
 * 
 * @author Stefan Krueger
 *
 */
public class ErrorMarkerGenerator {

	private List<IMarker> markers;

	public ErrorMarkerGenerator() {
		markers = new ArrayList<IMarker>();
	}

	/**
	 * Adds crypto-misuse error marker with message {@link message} into file {@link sourceFile} at Line {@link line}. 
	 * 
	 * @param sourceFile File the marker is generated into
	 * @param line Line the marker is generated at
	 * @param message Error Message
	 * @return <code>true</code>/<code>false</code> if error marker was (not) added successfully
	 */
	public boolean addMarker(IResource sourceFile, int line, String message) {
		if (!sourceFile.exists() || !sourceFile.isAccessible()) {
			Activator.getDefault().logError(Constants.NO_RES_FOUND);
			return false;
		}
		
		for (IMarker marker : markers) {
			try {
				if (marker.getAttribute(IMarker.MESSAGE).equals(message) && marker.getAttribute(IMarker.LINE_NUMBER).equals(line) && sourceFile.getName().equals(marker.getResource().getName())) {
					return true;
				}
			} catch (CoreException e) {
				//If this throws an exception, it's better to simply create the error marker, even if it already exists.
			}
		}
		
		IMarker marker;
		try {
			marker = sourceFile.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		markers.add(marker);
		return true;
	}

	/**
	 * Deletes markers from file and clears markers list.
	 * @return <code>true</code>/<code>false</code> if all error markers were (not) deleted successfully
	 */
	public Boolean clearMarkers() {
		boolean allMarkersDeleted = true;
		for (IMarker marker : markers) {
			allMarkersDeleted &= deleteMarker(marker);
		}
		markers.clear();
		return allMarkersDeleted;
	}

	private boolean deleteMarker(IMarker marker) {
		try {
			marker.delete();
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		return true;
	}

}
