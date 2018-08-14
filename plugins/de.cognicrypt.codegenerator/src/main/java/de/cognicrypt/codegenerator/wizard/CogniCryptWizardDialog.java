package de.cognicrypt.codegenerator.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


public class CogniCryptWizardDialog extends WizardDialog {

	public CogniCryptWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		Button finishButton = getButton(IDialogConstants.FINISH_ID);
		finishButton.setText("Generate");
	}

	
	
}
