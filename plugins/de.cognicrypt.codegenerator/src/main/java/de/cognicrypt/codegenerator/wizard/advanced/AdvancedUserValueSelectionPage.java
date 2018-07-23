/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard.advanced;

import java.util.ArrayList;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.PropertiesMapperUtil;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;

public class AdvancedUserValueSelectionPage extends WizardPage {

	private Composite container;
	private final List<PropertyWidget> userConstraints = new ArrayList<>();
	private final AstConcreteClafer taskClafer;
	int i;

	public AdvancedUserValueSelectionPage(final ClaferModel claferModel, Task task, final AstConcreteClafer taskClafer) {
		super(Constants.SELECT_PROPERTIES);
		setTitle(Constants.PROPERTIES + task.getDescription());
		setMessage("Please check the box to set the properties that configure the algorithm", INFORMATION);
		this.taskClafer = taskClafer;
	}

	private void createConstraints(final AstClafer parent, final AstAbstractClafer inputClafer, final Group titledPanel) {

		if (inputClafer.hasChildren()) {
			for (final AstConcreteClafer in : inputClafer.getChildren()) {
				createConstraints(parent, in, titledPanel);
			}
		}
		if (inputClafer.hasRef()) {
			createConstraints(parent, inputClafer.getRef().getTargetType(), titledPanel);
		}

		if (inputClafer.getSuperClafer() != null) {
			createConstraints(parent, inputClafer.getSuperClafer(), titledPanel);
		}
	}

	private void createConstraints(final AstClafer parent, final AstClafer inputClafer, final Group titledPanel) {

		if (inputClafer.hasChildren()) {
			if (inputClafer.getGroupCard() != null && inputClafer.getGroupCard().getLow() >= 1) {
				this.userConstraints
					.add(new PropertyWidget(titledPanel, parent, (AstConcreteClafer) inputClafer, ClaferModelUtils.removeScopePrefix(inputClafer.getName()), 1, 0, 1024, 0, 1, 1));
			} else {
				for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
					createConstraints(parent, childClafer, titledPanel);
				}
			}
		}
		if (inputClafer.hasRef()) {
			if (inputClafer.getRef().getTargetType().isPrimitive() && !(inputClafer.getRef().getTargetType().getName().contains("string"))) {
				if (ClaferModelUtils.isConcrete(inputClafer)) {
					Composite childComposite = new Composite(titledPanel, SWT.NONE);
					childComposite.setLayout(new GridLayout(4, false));
					GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
					childComposite.setLayoutData(gridData);
					this.userConstraints.add(new PropertyWidget(childComposite, parent, (AstConcreteClafer) inputClafer, ClaferModelUtils
						.removeScopePrefix(inputClafer.getName()), 1, 0, 1024, 0, 1, 1));
				}
			} else if (PropertiesMapperUtil.getenumMap().containsKey(inputClafer.getRef().getTargetType())) {
				createConstraints(inputClafer, inputClafer.getRef().getTargetType(), titledPanel);
			} else if (!inputClafer.getRef().getTargetType().isPrimitive()) {
				if (!ClaferModelUtils.removeScopePrefix(inputClafer.getRef().getTargetType().getName()).equals(titledPanel.getText().replaceAll("\\s", ""))) {
					if (inputClafer.getRef().getTargetType().hasChildren()) {
						final Group childPanel = createPanel(ClaferModelUtils.removeScopePrefix(inputClafer.getRef().getTargetType().getName()), titledPanel);
						createConstraints(inputClafer, inputClafer.getRef().getTargetType(), childPanel);
					}
				} else {
					//same panel as main algorithm type (e.g., kda in secure pwd storage)
					createConstraints(inputClafer, inputClafer.getRef().getTargetType(), titledPanel);
				}
			}
		}

		if (inputClafer.getSuperClafer() != null) {
			createConstraints(parent, inputClafer.getSuperClafer(), titledPanel);
		}
	}

	@Override
	public void createControl(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.container = new Composite(sc, SWT.NONE);
		this.container.setBounds(20, 10, 350, 200);
		final GridLayout layout = new GridLayout();
		this.container.setLayout(layout);
		layout.numColumns = 1;

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sc, "de.cognicrypt.codegenerator.help_id_2");

		// Add every constraints to its parent and group it as a separate titled panel
		for (final AstClafer taskAlgorithm : this.taskClafer.getChildren()) {
			if (!taskAlgorithm.getRef().getTargetType().hasRef()) {
				final Group titledPanel = createPanel(ClaferModelUtils.removeScopePrefix(taskAlgorithm.getRef().getTargetType().getName()), this.container);
				createConstraints(this.taskClafer, taskAlgorithm, titledPanel);
			}
			sc.setContent(container);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			setControl(sc);
		}
	}

	private Group createPanel(final String name, final Composite parent) {
		final Group titledPanel = new Group(parent, SWT.NONE);
		if (name.equals("Int")) {
			titledPanel.setText("Security Level");
		} else {
			titledPanel.setText(name.replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
		}
		final Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 10, SWT.BOLD));
		titledPanel.setFont(boldFont);
		titledPanel.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		titledPanel.setLayoutData(gridData);
		return titledPanel;
	}

	public List<PropertyWidget> getConstraints() {
		return this.userConstraints;
	}

	public boolean getPageStatus() {
		return PropertyWidget.status;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.container.setFocus();
		}
	}
}
