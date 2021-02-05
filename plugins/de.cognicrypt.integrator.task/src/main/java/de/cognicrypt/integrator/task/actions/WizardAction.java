/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.wizard.TaskIntegratorWizard;

public class WizardAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(final IAction arg0) {
		Constants.WizardActionFromContextMenuFlag = false;
		final WizardDialog dialog = new WizardDialog(new Shell(), new TaskIntegratorWizard());
		// dialog.setPageSize(Constants.DEFAULT_SIZE_FOR_TI_WIZARD);
		// dialog.setMinimumPageSize(Constants.DEFAULT_SIZE_FOR_COMPOSITES);
		dialog.open();
	}

	@Override
	public void selectionChanged(final IAction arg0, final ISelection arg1) {}

	@Override
	public void dispose() {}

	@Override
	public void init(final IWorkbenchWindow arg0) {}

}
