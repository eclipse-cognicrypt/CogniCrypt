/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositePattern;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositePatternEnum;

public class ClaferFeaturePatternDialog extends Dialog {

	private Composite compositePatternDetailsContainer;
	private CompositePattern compositePatternDetails;
	private Combo comboPattern;

	private ClaferModel resultModel;

	public ClaferFeaturePatternDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		// restrict resizing the dialog below a minimum
		getShell().setMinimumSize(520, 600);

		Label lblPattern = new Label(container, SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblPattern.setText("Pattern");

		comboPattern = new Combo(container, SWT.NONE);
		comboPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboPattern.add("Enumeration");
		comboPattern.add("Ordered Enumeration");
		comboPattern.select(0);

		comboPattern.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updatePatternDetailsComposite();
				validate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		compositePatternDetailsContainer = new Composite(container, SWT.NONE);
		compositePatternDetailsContainer.setLayout(new GridLayout(1, false));
		compositePatternDetailsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		updatePatternDetailsComposite();
		validate();

		return container;
	}

	private void validate() {
		boolean contentValid = compositePatternDetails.validate();

		if (contentValid) {
			if (getButton(IDialogConstants.OK_ID) != null) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		} else {
			if (getButton(IDialogConstants.OK_ID) != null) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		}

	}

	private void updatePatternDetailsComposite() {
		String selectedPattern = comboPattern.getText();

		// TODO consider outsourcing patterns into a simple Map<String, PatternComposite>,
		// where PatternComposite is an interface providing the appropriate getResult method
		if (compositePatternDetails != null) {
			compositePatternDetails.dispose();
		}
		if (selectedPattern.equals("Enumeration")) {
			compositePatternDetails = new CompositePatternEnum(compositePatternDetailsContainer, false);
			compositePatternDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		} else if (selectedPattern.equals("Ordered Enumeration")) {
			compositePatternDetails = new CompositePatternEnum(compositePatternDetailsContainer, true);
			compositePatternDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		Listener validationListener = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				validate();
			}
		};

		compositePatternDetails.addListener(SWT.Selection, validationListener);

		compositePatternDetailsContainer.layout();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		validate();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 700);
	}

	@Override
	protected void okPressed() {
		saveResultModel();
		super.okPressed();
	}

	private void saveResultModel() {
		if (compositePatternDetails != null && compositePatternDetails instanceof CompositePattern) {
			resultModel = compositePatternDetails.getResultModel();
		} else {
			Activator.getDefault().logError("Unknown return from the Clafer pattern dialog");
		}
	}

	public ClaferModel getResultModel() {
		return resultModel;
	}

}
