package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import de.cognicrypt.core.Constants;
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
	 * @throws CoreException
	 */
	public boolean setUp(final IJavaElement iJavaElement) {
		IProject ip = null;
		if (iJavaElement == null) {
			ip = Utils.getCurrentProject();
		} else {
			ip = iJavaElement.getJavaProject().getProject();
		}

		if (AnalysisKickOff.resultsReporter != null && !AnalysisKickOff.resultsReporter.getReporterProject().equals(ip)) {
			AnalysisKickOff.resultsReporter = null;
			for (ResultsCCUIListener resRep : Activator.getResultsReporters()) {
				if (resRep.getReporterProject().equals(ip)) {
					AnalysisKickOff.resultsReporter = resRep;
					break;
				}
			}
		}

		if (AnalysisKickOff.resultsReporter == null) {
			AnalysisKickOff.resultsReporter = ResultsCCUIListener.createListener(ip);
		}
		resultsReporter.getMarkerGenerator().clearMarkers(ip);
		try {
			if (ip == null || (!ip.hasNature(JavaCore.NATURE_ID))) {
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
		Job analysis = new Job(Constants.ANALYSIS_LABEL) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				boolean runSoot = SootRunner.runSoot(curProj, AnalysisKickOff.resultsReporter);
				if (runSoot && !monitor.isCanceled()) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}

			@Override
			protected void canceling() {
				this.cancel();
			}
		};
		analysis.setPriority(Job.LONG);
		analysis.schedule();
		return this.curProj != null && analysis.shouldRun();
	}
}
