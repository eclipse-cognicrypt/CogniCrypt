package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ErrorMarkerGenerator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;
import de.cognicrypt.utils.Utils;

/**
 * This class prepares and triggers the analysis. After it has finished, it refreshes the project.
 *
 * @author Stefan Krueger
 *
 */
public class AnalysisKickOff {

	private static ErrorMarkerGenerator errGen;
	private static ResultsCCUIListener resultsReporter;
	private IJavaProject curProj;

	/**
	 * This method sets up the analysis by <br>
	 * 1) Creating a {@link ErrorMarkerGenerator} <br>
	 * 2) Creating a {@link ResultsCCUIListener} <br>
	 * 3) Finding the current project's class with a main method <br>
	 * 
	 * @param iJavaElement
	 *
	 * @return <code>true</code>/<code>false</code> if setup (not) successful
	 */
	public boolean setUp(final IJavaElement iJavaElement) {
		IProject ip = null;
		if (iJavaElement == null) {
			ip = Utils.getCurrentProject();
		} else {
			ip = iJavaElement.getJavaProject().getProject();
		}

		if (AnalysisKickOff.errGen == null) {
			AnalysisKickOff.errGen = new ErrorMarkerGenerator();
		} else {
			AnalysisKickOff.errGen.clearMarkers(ip);
		}
		if (AnalysisKickOff.resultsReporter == null || !AnalysisKickOff.resultsReporter.getReporterProject().equals(ip)) {
			AnalysisKickOff.resultsReporter = new ResultsCCUIListener(ip, AnalysisKickOff.errGen);
		}

		try {
			if (ip == null || !ip.hasNature(JavaCore.NATURE_ID)) {
				return false;
			}
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		this.curProj = JavaCore.create(ip);

		return true;
	}

	/**
	 * This method executes the actual analysis.
	 * 
	 * @return <code>true</code>/<code>false</code> Soot runs successfully
	 */
	public boolean run() {
		return this.curProj != null && SootRunner.runSoot(this.curProj, AnalysisKickOff.resultsReporter);
	}
}
