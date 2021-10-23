/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class CogniCryptWizardDialog extends WizardDialog {

	public CogniCryptWizardDialog(final Shell parentShell, final IWizard newWizard) {
		super(parentShell, newWizard);
		this.setPageSize(new Point(400, 600));
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		final Button finishButton = getButton(IDialogConstants.FINISH_ID);
		finishButton.setText("Generate");
	}
}
