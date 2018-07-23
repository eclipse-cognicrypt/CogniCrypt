package de.cognicrypt.codegenerator.taskintegrator.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.cognicrypt.codegenerator.taskintegrator.wizard.TaskIntegrationWizard;
import de.cognicrypt.core.Constants;

public class WizardAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction arg0) {
		Constants.WizardActionFromContextMenuFlag = false;
		final WizardDialog dialog = new WizardDialog(new Shell(), new TaskIntegrationWizard());
		//dialog.setPageSize(Constants.DEFAULT_SIZE_FOR_TI_WIZARD);	
		//dialog.setMinimumPageSize(Constants.DEFAULT_SIZE_FOR_COMPOSITES);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {}

	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow arg0) {}

}
