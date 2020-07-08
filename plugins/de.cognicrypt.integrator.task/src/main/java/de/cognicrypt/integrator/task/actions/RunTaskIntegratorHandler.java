/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.wizard.TaskIntegrationWizard;
import de.cognicrypt.integrator.task.wizard.TaskIntegratorWizardDialog;

public class RunTaskIntegratorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Constants.WizardActionFromContextMenuFlag = true;
		final TaskIntegratorWizardDialog dialog = new TaskIntegratorWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new TaskIntegrationWizard());
		dialog.setHelpAvailable(false);
		return dialog.open();
	}

}
