/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ErrorMarkerGenerator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.utils.Utils;

/**
 * This class prepares and triggers the analysis. After it has finished, it refreshes the project.
 *
 * @author Stefan Krueger
 */
public class AnalysisKickOff {

	private static ResultsCCUIListener resultsReporter;
	private IJavaProject curProj;
	private boolean depOnly = false;

	public void analyzeDependenciesOnly(final Boolean depOnly) {
		this.depOnly = depOnly;
	}

	/**
	 * This method sets up the analysis by <br>
	 * 1) Creating a {@link ErrorMarkerGenerator} <br>
	 * 2) Creating a {@link ResultsCCUIListener} <br>
	 * 3) Finding the current project's class with a main method <br>
	 *
	 * @param iJavaElement
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
			for (final ResultsCCUIListener resRep : Activator.getResultsReporters()) {
				if (resRep.getReporterProject().equals(ip)) {
					AnalysisKickOff.resultsReporter = resRep;
					break;
				}
			}
		}

		if (AnalysisKickOff.resultsReporter == null) {
			AnalysisKickOff.resultsReporter = ResultsCCUIListener.createListener(ip);
		}

		resultsReporter.analyzeDependenciesOnly(depOnly);

		resultsReporter.getMarkerGenerator().clearMarkers(ip);
		try {
			if (ip == null || !ip.hasNature(JavaCore.NATURE_ID)) {
				Activator.getDefault().logInfo("The project " + ip.getName() + " does not have Java nature. No analysis necessary.");
				return false;
			}
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		IJavaProject javaProject = JavaCore.create(ip);
		if (javaProject == null) {
			Activator.getDefault().logInfo("JavaCore could not create IJavaProject for project " + ip.getName() + ".");
			return false;
		}
		this.curProj = javaProject;
		return true;
	}

	/**
	 * This method executes the actual analysis.
	 */
	public void run() {
		if (this.curProj == null)
			return;
		
		final Job analysis = new Job(Constants.ANALYSIS_LABEL) {

			@SuppressWarnings("deprecation")
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				int curSeed = 0;
				final SootThread sootThread = new SootThread(AnalysisKickOff.this.curProj, AnalysisKickOff.resultsReporter, depOnly);
				final MonitorReporter monitorThread = new MonitorReporter(AnalysisKickOff.resultsReporter, sootThread);
				monitorThread.start();
				sootThread.start();
				AnalysisKickOff.resultsReporter.setCgGenComplete(false);
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				SubMonitor cgGen = subMonitor.newChild(50);
				while (sootThread.isAlive()) {
					try {
						Thread.sleep(1);
					}	catch (final InterruptedException e) {}
					
					if(!monitorThread.isCgGen()) {
						cgGen.setWorkRemaining(1000).split(1);
						cgGen.setTaskName("Constructing call Graphs...");
						}
					else {
						if(monitorThread.getProcessedSeeds()- curSeed !=0) {
						curSeed = monitorThread.getProcessedSeeds();
						subMonitor.split(monitorThread.getWorkUnitsCompleted()/2);
						subMonitor.setTaskName("Completed "+monitorThread.getProcessedSeeds()+" of "+monitorThread.getTotalSeeds()+" seeds.");
						}
					}
					if (monitor.isCanceled()) {
						sootThread.stop();
						Activator.getDefault().logInfo("Static analysis job cancelled for " + curProj.getElementName() + ".");
						return Status.CANCEL_STATUS;
					}

				}
				monitor.done();
				AnalysisKickOff.resultsReporter.setPercentCompleted(0);
				AnalysisKickOff.resultsReporter.setProcessedSeeds(0);
				AnalysisKickOff.resultsReporter.setTotalSeeds(0);
				AnalysisKickOff.resultsReporter.setWorkUnitsCompleted(0);
				AnalysisKickOff.resultsReporter.setWork(0);
				if (sootThread.isSucc()) {
					Activator.getDefault().logInfo("Static analysis job successfully terminated for " + curProj.getElementName() + ".");
					return Status.OK_STATUS;
				} else {
					Activator.getDefault().logInfo("Static analysis failed for " + curProj.getElementName() + ".");
					return Status.CANCEL_STATUS;
				}

			}

			@Override
			protected void canceling() {
				cancel();
			}
		};
		analysis.setPriority(Job.LONG);
		analysis.schedule();
	}
}
