package de.cognicrypt.codegenerator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.wizard.AltConfigWizard;
import de.cognicrypt.codegenerator.wizard.CogniCryptWizardDialog;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;

public class RunCodeGeneratorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Constants.WizardActionFromContextMenuFlag = true;
		final CogniCryptWizardDialog dialog = new CogniCryptWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new AltConfigWizard(CodeGenerators.CrySL));
		dialog.setHelpAvailable(false);
		
		return dialog.open();
	}

}
