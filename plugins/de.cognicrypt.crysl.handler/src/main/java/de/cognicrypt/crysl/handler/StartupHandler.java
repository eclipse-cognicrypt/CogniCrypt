/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.handler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IStartup;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.Activator;
import de.cognicrypt.crysl.builder.CrySLBuilderUtils;
import de.cognicrypt.crysl.builder.CrySLNature;

/**
 * At startup, this handler registers a listener that will be informed after a build, whenever resources were changed.
 *
 * @author Stefan Krueger
 * @author Eric Bodden
 */
public class StartupHandler implements IStartup {

	private static final AfterBuildListener BUILD_LISTENER = new AfterBuildListener();
	private static final ImportListener IMPORT_LISTENER = new ImportListener();

	@Override
	public void earlyStartup() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(StartupHandler.BUILD_LISTENER, IResourceChangeEvent.POST_BUILD);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(StartupHandler.IMPORT_LISTENER, IResourceChangeEvent.POST_CHANGE);
	}

	private static class AfterBuildListener implements IResourceChangeListener {

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			Activator.getDefault().logInfo("ResourcechangeListener has been triggered.");
			final List<IResource> changedCrySLElements = new ArrayList<>();
			try {

				event.getDelta().accept(delta -> {
					switch (delta.getKind()) {
						case IResourceDelta.ADDED:
						case IResourceDelta.CHANGED:
							final IResource res = delta.getResource();
							if (res != null && res.getFileExtension() != null) {
								if (Constants.cryslFileEnding.substring(1).equals(res.getFileExtension())) {
									changedCrySLElements.add(res);
								}

							}
					}
					return true;
				});
			}
			catch (final CoreException e) {}

			if (!changedCrySLElements.isEmpty()) {
				try {
					IResource res = changedCrySLElements.get(0);
					IProject crySLProject = res.getProject();
					if (!crySLProject.hasNature(CrySLNature.NATURE_ID)) {
						CrySLBuilderUtils.addCrySLBuilderToProject(crySLProject);
					}
				}
				catch (CoreException e) {
					Activator.getDefault().logError(e, "Updating CrySL rules failed.");
				}

			}
		}
	}

	private static class ImportListener implements IResourceChangeListener {

		@Override
		public void resourceChanged(IResourceChangeEvent event) {

			IResourceDelta delta = event.getDelta();

			IResourceDelta[] affectedChildren = delta.getAffectedChildren();
			if (affectedChildren.length == 0) {
				return;
			}
			IResource deltaResource = affectedChildren[0].getResource();
			if (event.getType() == IResourceChangeEvent.POST_CHANGE && deltaResource instanceof IProject
					&& (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED)) {
				try {
					IProject project = (IProject) deltaResource;
					if (!CrySLBuilderUtils.hasCrySLBuilder(project) && CrySLBuilderUtils.hasCrySLFiles(project)) {
						CrySLBuilderUtils.addCrySLBuilderToProject(project);
					}
				}
				catch (CoreException e) {}
			}
		}
	}
}
