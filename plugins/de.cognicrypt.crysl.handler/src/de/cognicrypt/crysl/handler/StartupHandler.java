/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IStartup;
import de.cognicrypt.crysl.reader.CrySLModelReader;

/**
 * At startup, this handler registers a listener that will be informed after a build, whenever resources were changed.
 *
 * @author Eric Bodden
 * @author Stefan Krueger
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
			int i = event.getBuildKind();
			IResource r = event.getResource();
			int j = event.getType();
			final List<IResource> changedCrySLElements = new ArrayList<>();
			Activator.getDefault().logInfo("ResourcechangeListener has been triggered.");
			try {

				event.getDelta().accept(delta -> {
					switch (delta.getKind()) {
						case IResourceDelta.ADDED:
						case IResourceDelta.CHANGED:
							final IResource res = delta.getResource();
							if (res != null && res.getFileExtension() != null) {
								if (res.getFileExtension().endsWith("cryptsl")) {
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
					new CrySLModelReader(changedCrySLElements.get(0));
				}
				catch (ClassNotFoundException | CoreException | IOException e) {
					Activator.getDefault().logError(e, "Updating CrySL rules failed.");
				}

			}
		}
	}

	private static class ImportListener implements IResourceChangeListener {
		
		public void resourceChanged(IResourceChangeEvent event) {
//			int i = event.getBuildKind();
//			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
//				try {
//					List<IProject> projects = getProjects(event.getDelta());
//					for (IProject newProj : projects) {
//						System.out.println("Sensibly gotten here because there are " + projects.size() + " projects.");
//						IResource crySLFile = hasCrySLFiles(newProj);
//						System.out.println("Found " + newProj + ".");
//						if (crySLFile != null) {
//							new CrySLModelReader(crySLFile);
//						}
//					}
//				}
//				catch (ClassNotFoundException | CoreException | IOException e) {
//					Activator.getDefault().logError(e, "Importing CrySL rules failed.");
//				}
//			}
		}

//		private IResource hasCrySLFiles(IContainer cont) throws CoreException {
//			for (IResource member : cont.members()) {
//				if (member instanceof IContainer) {
//					return hasCrySLFiles((IContainer) member);
//				}
//				if (member instanceof IFile && ".cryptsl".equals(((IFile) member).getFileExtension())) {
//					return member;
//				}
//			}
//			return null;
//		}
//
//		private List<IProject> getProjects(IResourceDelta delta) throws CoreException {
//			final List<IProject> projects = new ArrayList<IProject>();
//			delta.accept(new IResourceDeltaVisitor() {
//				public boolean visit(IResourceDelta delta) throws CoreException {
//					if (delta.getKind() == IResourceDelta.ADDED && delta.getResource().getType() == IResource.PROJECT) {
//						IProject project = (IProject) delta.getResource();
//						if (project.isAccessible()) {
//							projects.add(project);
//						}
//					}
//					// only continue for the workspace root
//					return delta.getResource().getType() == IResource.ROOT;
//				}
//			});
//			return projects;
//		}
	}
}
