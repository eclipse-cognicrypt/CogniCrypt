/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.widgets.CompositePattern;
import de.cognicrypt.integrator.task.widgets.CompositePatternEnum;

public class ClaferFeaturePatternDialog extends Dialog {

	private Composite compositePatternDetailsContainer;
	private CompositePattern compositePatternDetails;
	private Combo comboPattern;

	private ClaferModel resultModel;

	public ClaferFeaturePatternDialog(final Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		// restrict resizing the dialog below a minimum
		getShell().setMinimumSize(520, 600);

		final Label lblPattern = new Label(container, SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblPattern.setText("Pattern");

		this.comboPattern = new Combo(container, SWT.NONE);
		this.comboPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.comboPattern.add("Enumeration");
		this.comboPattern.add("Ordered Enumeration");
		this.comboPattern.select(0);

		this.comboPattern.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				updatePatternDetailsComposite();
				validate();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.compositePatternDetailsContainer = new Composite(container, SWT.NONE);
		this.compositePatternDetailsContainer.setLayout(new GridLayout(1, false));
		this.compositePatternDetailsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		updatePatternDetailsComposite();
		validate();

		return container;
	}

	private void validate() {
		final boolean contentValid = this.compositePatternDetails.validate();

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
		final String selectedPattern = this.comboPattern.getText();

		// TODO consider outsourcing patterns into a simple Map<String, PatternComposite>,
		// where PatternComposite is an interface providing the appropriate getResult method
		if (this.compositePatternDetails != null) {
			this.compositePatternDetails.dispose();
		}
		if (selectedPattern.equals("Enumeration")) {
			this.compositePatternDetails = new CompositePatternEnum(this.compositePatternDetailsContainer, false);
			this.compositePatternDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		} else if (selectedPattern.equals("Ordered Enumeration")) {
			this.compositePatternDetails = new CompositePatternEnum(this.compositePatternDetailsContainer, true);
			this.compositePatternDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		final Listener validationListener = arg0 -> validate();

		this.compositePatternDetails.addListener(SWT.Selection, validationListener);

		this.compositePatternDetailsContainer.layout();
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
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
		if (this.compositePatternDetails != null && this.compositePatternDetails instanceof CompositePattern) {
			this.resultModel = this.compositePatternDetails.getResultModel();
		} else {
			Activator.getDefault().logError("Unknown return from the Clafer pattern dialog");
		}
	}

	public ClaferModel getResultModel() {
		return this.resultModel;
	}

}
