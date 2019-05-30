/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.google.common.collect.Lists;

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ErrorMarkerGenerator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.utils.JavaVersion;
import de.cognicrypt.utils.Utils;

/**
 * This class prepares and triggers the analysis. After it has finished, it
 * refreshes the project.
 *
 * @author Stefan Krueger
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
		HashMap<String, String> hashDependency;
		try {
			String pathtoDepenencyHashmap = ip.getLocation().toOSString() + Constants.outerFileSeparator
					+ "dependencyHashmap.data";
			Path path = Paths.get(pathtoDepenencyHashmap);
			if (!Files.exists(path)) {
				hashDependency =  Utils.ExtractDepHashmap(ip);
				Utils.storeDepHashmaptoFile(hashDependency, ip);
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (AnalysisKickOff.resultsReporter != null
				&& !AnalysisKickOff.resultsReporter.getReporterProject().equals(ip)) {
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

		resultsReporter.getMarkerGenerator().clearMarkers(ip);
		try {
			if (ip == null || (!ip.hasNature(JavaCore.NATURE_ID))) {
				Activator.getDefault()
						.logInfo("The project " + ip.getName() + " does not have Java nature. No analysis necessary.");
				return false;
			}
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		IJavaProject javaProject = JavaCore.create(ip);
		if (javaProject == null) {
			Activator.getDefault().logInfo("JavaCore could not create IJavaProject for project " + ip.getName() + " .");
			return false;
		}
		this.curProj = javaProject;
		return true;
	}

	/**
	 * This method executes the actual analysis.
	 *
	 */
	public void run() {
		if (this.curProj == null)
			return;
		if (Utils.checkJavaVersion()) {
			Activator.getDefault().logInfo("Analysis cancelled as the IDEs' java version is " + System.getProperty("java.version", "<JavaVersionNotFound>") + ", which is greater than 1.8.");
			return;
		}
		final Job analysis = new Job(Constants.ANALYSIS_LABEL) {

			@SuppressWarnings("deprecation")
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final SootThread sootThread = new SootThread(AnalysisKickOff.this.curProj,
						AnalysisKickOff.resultsReporter);
				sootThread.start();
				while (sootThread.isAlive()) {
					try {
						Thread.sleep(500);
					}

					catch (final InterruptedException e) {
					}

					if (monitor.isCanceled()) {
						sootThread.stop();
						Activator.getDefault()
								.logInfo("Static analysis job cancelled for " + curProj.getElementName() + ".");
						return Status.CANCEL_STATUS;
					}

				}
				if (sootThread.isSucc()) {
					Activator.getDefault().logInfo(
							"Static analysis job successfully terminated for " + curProj.getElementName() + ".");
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
