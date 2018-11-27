/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.cognicrypt.codegenerator.wizard.AltConfigWizard;
import de.cognicrypt.codegenerator.wizard.CogniCryptWizardDialog;
import de.cognicrypt.core.Constants;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown in the UI. When the user tries to use the action, this
 * delegate will be created and execution will be delegated to it.
 *
 * @see IWorkbenchWindowActionDelegate
 */
public class WizardActionFromContextMenu implements IWorkbenchWindowActionDelegate {

	private Shell shell;

	/**
	 * The constructor.
	 */
	public WizardActionFromContextMenu() {}

	/**
	 * We can use this method to dispose of any system resources we previously allocated.
	 *
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	@Override
	public void dispose() {}

	/**
	 * We will cache window object in order to be able to provide parent shell for the message dialog.
	 *
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	@Override
	public void init(final IWorkbenchWindow window) {
		this.shell=window.getShell();
	}

	/**
	 * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench UI.
	 *
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@Override
	public void run(final IAction action) {
		Constants.WizardActionFromContextMenuFlag = true;
		final CogniCryptWizardDialog dialog = new CogniCryptWizardDialog(shell, new AltConfigWizard());
		
		dialog.open();
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but this can only happen after the delegate has been created.
	 *
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {}
}
