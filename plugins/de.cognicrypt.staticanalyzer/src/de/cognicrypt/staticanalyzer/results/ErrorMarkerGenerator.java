package de.cognicrypt.staticanalyzer.results;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.cognicrypt.staticanalyzer.Activator;

public class ErrorMarkerGenerator {

	private List<IMarker> markers;

	public ErrorMarkerGenerator() {
		markers = new ArrayList<IMarker>();
	}

	public boolean addMarker(IResource res, int line, String message) {
		if (!res.exists() || !res.isAccessible()) {
			Activator.getDefault().logError("No resource to generate error marker for found.");
			return false;
		}
		IMarker marker;
		try {
			marker = res.createMarker(IMarker.PROBLEM);
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
