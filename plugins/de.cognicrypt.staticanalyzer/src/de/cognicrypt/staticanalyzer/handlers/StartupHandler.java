/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.handlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;

/**
 * At startup, this handler registers a listener that will be informed after a build, whenever resources were changed.
 *
 * @author Eric Bodden
 * @author Stefan Krueger
 */
public class StartupHandler implements IStartup {
	static Queue<AnalysisKickOff> analysis_Queue = new LinkedList<AnalysisKickOff>();
	static boolean analysis_running = false;

	private static class AfterBuildListener implements IResourceChangeListener {

		/**
		 * This method sets up the analysis by <br>
		 * 1) Listening to any resource change in the workspace <br>
		 * 2) Setting Up the analysis by calling the "setup" method of {@link AnalysisKickOff} <br>
		 * 3) Running the analysis on the setup {@link AnalysisKickOff} object <br>
		 * It maintains a Queue and a monitor flag that allows running only one analysis at a time.
		 *
		 * @param event : an object of the {@link IResourceChangeEvent} class, contains info about the changed resources from the workspace
		 * @return <code>true</code>/<code>false</code> if change (not) in java element
		 * @throws Exception if javaElement containing changed resource accessed wrongly
		 * @throws CoreException if javaElement containing changed resource accessed wrongly
		 */
		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			if (store.getBoolean(Constants.AUTOMATED_ANALYSIS) == false) {
				return;
			} else {
				final List<IJavaElement> changedJavaElements = new ArrayList<>();
				Activator.getDefault().logInfo("ResourcechangeListener has been triggered.");
				try {

					event.getDelta().accept(delta -> {
						switch (delta.getKind()) {
							case IResourceDelta.ADDED:
							case IResourceDelta.CHANGED:
								final IResource res = delta.getResource();
								if (res != null && res.getFileExtension() != null) {
									try {
										final IJavaElement javaElement = JavaCore.create(res);
										if (javaElement != null) {
											if (javaElement instanceof ICompilationUnit) {
												if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
													changedJavaElements.add(javaElement);
												}
												return false;
											}
										}
									}
									catch (final Exception ex) {
										return false;
									}
								}
						}
						return true;
					});
					if (changedJavaElements.isEmpty()) {
						for (final IResourceDelta ev : event.getDelta().getAffectedChildren()) {
							ev.accept(delta -> {
								switch (delta.getKind()) {
									case IResourceDelta.ADDED:
									case IResourceDelta.CHANGED:
										final IResource res = delta.getResource();
										final IJavaElement javaElement = JavaCore.create(res);
										if (javaElement != null) {
											if (javaElement instanceof IJavaProject) {
												if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
													changedJavaElements.add(javaElement);
												}
												return false;
											}
										}
								}
								return true;
							});
						}
					}
				}
				catch (final CoreException e) {}

				if (changedJavaElements.isEmpty()) {
					Activator.getDefault().logInfo("No changed resource found. Abort.");
					return;
				}
				if (!changedJavaElements.isEmpty()) {
					final AnalysisKickOff ako = new AnalysisKickOff();
					final boolean stat = ako.setUp(changedJavaElements.get(0));
					if (stat) {
						analysis_Queue.add(ako);
					}
					while (analysis_Queue.size() > 0) {
						if (!analysis_running) {
							final AnalysisKickOff ak = analysis_Queue.remove();
							analysis_running = true;
							ak.run();
							analysis_running = false;
						}
					}
				}
			}
		}
	}

	private static final AfterBuildListener BUILD_LISTENER = new AfterBuildListener();

	@Override
	public void earlyStartup() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(StartupHandler.BUILD_LISTENER, IResourceChangeEvent.POST_BUILD);
	}

}
