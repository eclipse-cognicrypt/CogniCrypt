package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;

public class ShutDownHandler implements IWorkbenchListener {

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		for (ResultsCCUIListener res : Activator.getResultsReporters()) {
			res.getMarkerGenerator().clearMarkers();
		}
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
