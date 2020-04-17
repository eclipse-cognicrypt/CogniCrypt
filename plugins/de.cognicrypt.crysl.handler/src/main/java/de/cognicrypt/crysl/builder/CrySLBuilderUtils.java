/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.builder;

import java.util.Arrays;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.Activator;

public class CrySLBuilderUtils {

	public static void addCrySLBuilderToProject(IProject project) {
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

	public static boolean hasCrySLFiles(IContainer cont) throws CoreException {
		boolean hasCrySLFiles = false;
		for (IResource member : cont.members()) {
			if (member instanceof IContainer) {
				hasCrySLFiles = hasCrySLFiles((IContainer) member);
			}

			if (member instanceof IFile && Constants.cryslFileEnding.equals(((IFile) member).getFileExtension())) {
				return true;
			}
		}
		return hasCrySLFiles;
	}

	public static boolean hasCrySLBuilder(IProject project) throws CoreException {
		return Arrays.asList(project.getDescription().getBuildSpec()).stream().anyMatch(e -> CrySLBuilder.BUILDER_ID.equals(e.getBuilderName()));
	}

}
