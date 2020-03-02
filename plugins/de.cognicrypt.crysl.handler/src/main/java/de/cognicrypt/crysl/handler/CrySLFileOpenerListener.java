/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.handler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import de.cognicrypt.core.Constants;

public class CrySLFileOpenerListener implements IPartListener2 {

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {

		if (Constants.cryslEditorID.equals(partRef.getId())) {
			IResource file = partRef.getPage().getActiveEditor().getEditorInput().getAdapter(IResource.class);
			if (Constants.cryslFileEnding.substring(1).equals(file.getFileExtension())) {
				IProject projectOfOpenedFile = file.getProject();
				if (!CrySLBuilderUtils.hasCrySLBuilder(projectOfOpenedFile)) {
					CrySLBuilderUtils.addCrySLBuilderToProject(projectOfOpenedFile);
				}
			}
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {}

}
