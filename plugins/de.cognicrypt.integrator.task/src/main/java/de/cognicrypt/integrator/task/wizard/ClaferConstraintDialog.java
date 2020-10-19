/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.integrator.task.controllers.ClaferModelContentProvider;
import de.cognicrypt.integrator.task.controllers.ClaferModelLabelProvider;
import de.cognicrypt.integrator.task.models.ClaferConstraint;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class ClaferConstraintDialog extends Dialog {

	private Text text;
	private final ClaferConstraint cfrConstraint;

	private ClaferFeature currentFeature;
	private ClaferModel claferModel;

	private ClaferConstraint modifiedConstraint;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public ClaferConstraintDialog(final Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.RESIZE | SWT.TITLE);

		this.cfrConstraint = new ClaferConstraint();
	}

	public ClaferConstraintDialog(final Shell parentShell, final ClaferFeature currentFeature, final ClaferModel claferModel) {
		this(parentShell);
		this.currentFeature = currentFeature;
		this.claferModel = claferModel;
	}

	public ClaferConstraintDialog(final Shell parentShell, final ClaferFeature currentFeature, final ClaferModel claferModel, final ClaferConstraint modifiedConstraint) {
		this(parentShell, currentFeature, claferModel);
		this.modifiedConstraint = modifiedConstraint;
	}

	public ClaferFeature getClafer() {
		return this.currentFeature;
	}

	private void appendConstraint(final String addition) {
		if (!this.text.getText().isEmpty() && !this.text.getText().endsWith(" ")) {
			this.text.insert(" ");
		}

		this.text.insert(addition);
		this.text.setFocus();
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gl_container = new GridLayout(1, false);
		container.setLayout(gl_container);

		getShell().setMinimumSize(600, 400);

		final TreeViewer treeViewer = new TreeViewer(container);
		// TODO be more readable about these predicates
		treeViewer.setContentProvider(new ClaferModelContentProvider(feat -> !feat.getFeatureName().isEmpty(), prop -> !prop.getPropertyName().isEmpty()));
		treeViewer.setLabelProvider(new ClaferModelLabelProvider());

		// create a temporary clafer model that contains the current model as well as the feature currently being created
		final ClaferModel tempModel = this.claferModel.clone();
		tempModel.add(this.currentFeature);

		treeViewer.setInput(tempModel);
		treeViewer.expandAll();
		treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		treeViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (treeViewer.getSelection() instanceof TreeSelection) {
					final TreeSelection ts = (TreeSelection) treeViewer.getSelection();

					// add feature name to text field if feature name clicked
					if (ts.getFirstElement() instanceof ClaferFeature) {
						final ClaferFeature featureClicked = (ClaferFeature) ts.getFirstElement();
						appendConstraint(featureClicked.getFeatureName());
					}
					// add to text field if property name clicked
					else if (ts.getFirstElement() instanceof ClaferProperty) {
						final ClaferProperty propertyClicked = (ClaferProperty) ts.getFirstElement();
						((ClaferModel) treeViewer.getInput()).getParentFeatureOfProperty(propertyClicked);

						final StringBuilder addition = new StringBuilder();
						addition.append(propertyClicked.getPropertyName());

						appendConstraint(addition.toString());
					}
				}
				super.mouseDoubleClick(e);
			}
		});

		final Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		final RowLayout rl_group = new RowLayout(SWT.HORIZONTAL);
		group.setLayout(rl_group);

		final ArrayList<Entry<String, String>> buttonContents = new ArrayList<>();
		buttonContents.add(new SimpleEntry<String, String>("NOT", "!="));
		buttonContents.add(new SimpleEntry<String, String>("EQUALS", "="));
		buttonContents.add(new SimpleEntry<String, String>("AND", "AND"));
		buttonContents.add(new SimpleEntry<String, String>("OR", "OR"));
		buttonContents.add(new SimpleEntry<String, String>("IMPLIES", "=>"));
		buttonContents.add(new SimpleEntry<String, String>("(", "("));
		buttonContents.add(new SimpleEntry<String, String>(")", ")"));
		buttonContents.add(new SimpleEntry<String, String>(">", ">"));
		buttonContents.add(new SimpleEntry<String, String>("<", "<"));

		for (final Entry<String, String> btn : buttonContents) {
			final Button newButton = new Button(group, SWT.PUSH);
			newButton.setText(btn.getKey());
			newButton.addListener(SWT.Selection, arg0 -> appendConstraint(btn.getValue()));
		}

		this.text = new Text(container, SWT.BORDER | SWT.WRAP | SWT.SINGLE);
		this.text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		if (this.modifiedConstraint != null) {
			this.text.setText(this.modifiedConstraint.getConstraint());
		}

		return container;
	}

	@Override
	protected boolean isResizable() {
		return true;
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
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 600);
	}

	@Override
	protected void okPressed() {
		this.cfrConstraint.setConstraint(this.text.getText());
		super.okPressed();
	}

	public ClaferConstraint getResult() {
		return this.cfrConstraint;
	}

}
