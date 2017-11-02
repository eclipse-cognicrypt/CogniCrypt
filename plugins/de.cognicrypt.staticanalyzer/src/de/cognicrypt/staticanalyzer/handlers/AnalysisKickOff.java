package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.resources.IProject;
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

/**
 * This class prepares and triggers the analysis. After it has finished, it refreshes the project.
 * 
 * @author Stefan Krueger
 *
 */
public class AnalysisKickOff {

	private String mainClass;
	private IJavaProject curProj;
	private static ErrorMarkerGenerator errGen;
	private static ResultsCCUIListener resultsReporter;

	/**
	 * This method sets up the analysis by <br>
	 * 1) Creating a {@link ErrorMarkerGenerator} <br>
	 * 2) Creating a {@link ResultsCCUIListener} <br>
	 * 3) Finding the current project's class with a main method <br>
	 * 
	 * @return <code>true</code>/<code>false</code> if setup (not) successful
	 */
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

	/**
	 * This method executes the actual analysis.
	 * @return <code>true</code>/<code>false</code> Soot runs successfully
	 */
	public boolean run() {
		if (curProj == null) {
			return false;
		}
		return SootRunner.runSoot(curProj, mainClass, resultsReporter);
	}
}
