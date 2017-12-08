package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
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

	private static ErrorMarkerGenerator errGen;
	private static ResultsCCUIListener resultsReporter;
	private String mainClass;
	private IJavaProject curProj;

	/**
	 * This method executes the actual analysis.
	 * 
	 * @return <code>true</code>/<code>false</code> Soot runs successfully
	 */
	public boolean run() {
		if (this.curProj == null) {
			return false;
		}
		return SootRunner.runSoot(this.curProj, this.mainClass, AnalysisKickOff.resultsReporter);
	}

	/**
	 * This method sets up the analysis by <br>
	 * 1) Creating a {@link ErrorMarkerGenerator} <br>
	 * 2) Creating a {@link ResultsCCUIListener} <br>
	 * 3) Finding the current project's class with a main method <br>
	 *
	 * @return <code>true</code>/<code>false</code> if setup (not) successful
	 */
	public boolean setUp() {
		final IProject ip = Utils.getCurrentProject();

		if (AnalysisKickOff.errGen == null) {
			AnalysisKickOff.errGen = new ErrorMarkerGenerator();
		} else {
			AnalysisKickOff.errGen.clearMarkers(ip);
		}
		if (AnalysisKickOff.resultsReporter == null) {
			AnalysisKickOff.resultsReporter = new ResultsCCUIListener(AnalysisKickOff.errGen);
		}

		final SearchRequestor requestor = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(final SearchMatch match) throws CoreException {
				final IResource resource = match.getResource();
				final IJavaElement classEl = JavaCore.create(resource);
				final int isClassFile = classEl.getElementType();
				if (isClassFile == IJavaElement.CLASS_FILE || isClassFile == IJavaElement.COMPILATION_UNIT) {
					String name = classEl.getParent().getElementName() + "." + classEl.getElementName();

					name = name.replace("." + resource.getFileExtension(), "");
					if (name.startsWith(".")) {
						name = name.substring(1);
					}
					if (!name.isEmpty()) {
						AnalysisKickOff.this.mainClass = name;
					}
				}
			}
		};
		try {
			if (ip == null || !ip.hasNature(JavaCore.NATURE_ID)) {
				return false;
			}
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		this.curProj = JavaCore.create(ip);
		Utils.findMainMethodInCurrentProject(this.curProj, requestor);

		return true;
	}
}
