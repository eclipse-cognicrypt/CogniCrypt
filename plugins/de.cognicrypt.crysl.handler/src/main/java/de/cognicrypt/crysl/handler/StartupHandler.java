/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IStartup;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

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
					IResource res = changedCrySLElements.get(0);
					IProject crySLProject = res.getProject();
					if (!crySLProject.hasNature(CrySLNature.NATURE_ID)) {
						addCrySLBuilderToProject(crySLProject);
					}
				}
				catch (CoreException e) {
					Activator.getDefault().logError(e, "Updating CrySL rules failed.");
				}

			}
			Activator.getDefault()
					.logInfo("CrySL rules persisted to " + Utils.getResourceFromWithin(Constants.RELATIVE_RULES_DIR, de.cognicrypt.core.Activator.PLUGIN_ID).getAbsolutePath());
		}
	}

	private static class ImportListener implements IResourceChangeListener {

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			
			IResourceDelta delta = event.getDelta();
			
			IResource deltaResource = delta.getAffectedChildren()[0].getResource();
			if (event.getType() == IResourceChangeEvent.POST_CHANGE && deltaResource instanceof IProject && (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED)) {
				try {
					IProject project = (IProject) deltaResource;
					if (!hasCrySLBuilder(project) && hasCrySLFiles(project)) {
						addCrySLBuilderToProject(project);
					}
				}
				catch (CoreException e) {}
			}
		}
		
		private static boolean hasCrySLBuilder(IProject project) throws CoreException {
			return Arrays.asList(project.getDescription().getBuildSpec()).stream().anyMatch(e -> "".equals(e.getBuilderName()));
		}
	}
	
	protected static void addCrySLBuilderToProject(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = CrySLNature.NATURE_ID;

			// validate the natures
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IStatus status = workspace.validateNatureSet(newNatures);

			// only apply new nature, if the status is ok
			if (status.getCode() == IStatus.OK) {
				description.setNatureIds(newNatures);
			}

			ICommand[] buildSpec = description.getBuildSpec();
			ICommand command = description.newCommand();
			command.setBuilderName(CrySLBuilder.BUILDER_ID);
			ICommand[] newbuilders = new ICommand[buildSpec.length + 1];
			System.arraycopy(buildSpec, 0, newbuilders, 0, buildSpec.length);
			newbuilders[buildSpec.length] = command;
			description.setBuildSpec(newbuilders);
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e);
		}
	}
	
	private static boolean hasCrySLFiles(IContainer cont) throws CoreException {
		boolean hasCrySLFiles = false;
		for (IResource member : cont.members()) {
			if (member instanceof IContainer) {
				hasCrySLFiles = hasCrySLFiles((IContainer) member);
			}
			
			if (member instanceof IFile && ".cryptsl".equals(((IFile) member).getFileExtension())) {
				return true;
			}
		}
		return hasCrySLFiles;
	}
	
}
