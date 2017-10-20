package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Utils;
import de.cognicrypt.staticanalyzer.results.ErrorMarkerGenerator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;

public class AnalysisKickOff {

	private String mainClass;
	private IJavaProject curProj;
	private static ErrorMarkerGenerator errGen;
	private static ResultsCCUIListener resultsReporter;

	public boolean setUp() {
		if (errGen == null) {
			errGen = new ErrorMarkerGenerator();
		} else {
			errGen.clearMarkers();
		}
		if (resultsReporter == null) {
			resultsReporter = new ResultsCCUIListener(errGen);
		}

		SearchRequestor requestor = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(SearchMatch match) throws CoreException {
				String name = match.getResource().getProjectRelativePath().toString();
				name = name.substring(name.indexOf('/') + 1);
				name = name.replace("." + match.getResource().getFileExtension(), "");
				name = name.replace("/", ".");
				if (!name.isEmpty()) {
					mainClass = name;
				}
			}
		};
		IProject ip = Utils.getCurrentProject();
		try {
			if (ip == null || !ip.hasNature(JavaCore.NATURE_ID)) {
				return false;
			}
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		curProj = JavaCore.create(ip);
		Utils.findMainMethodInCurrentProject(curProj, requestor);

		return true;
	}

	public boolean run() {
		if (curProj == null) {
			return false;
		}
		SootRunner.runSoot(curProj, mainClass, resultsReporter);

		return true;
	}

	public boolean cleanUp() {
		try {
			this.curProj.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		return true;
	}

}
