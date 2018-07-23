/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.primitive.providerUtils.UserJavaProject;
import de.cognicrypt.core.Constants;

/**
 * This class is responsible for displaying the methods related to the custom algorithm For instance, in case of primitive of type symmetric block cipher, the required methods are
 * encryption an decryption.
 * 
 * @author Ahmed Ben Tahar
 */

public class MethodSelectorPage extends WizardPage {

	private Label encryptionLabel;
	private String projectPath;
	private UserJavaProject project = new UserJavaProject();

	public MethodSelectorPage(String selectedProjectPath) {
		super("Methods Selector");
		setTitle("Methods Selector");
		setDescription("Getting methods-related algorithm");
		this.projectPath = selectedProjectPath;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		encryptionLabel = new Label(container, SWT.NULL);
		encryptionLabel.setText("Select the encryption method:   ");

		Combo encryptionCombo = new Combo(container, SWT.NONE);
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 140;
		encryptionCombo.setLayoutData(gd_combo);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		//Field assist
		final ControlDecoration deco = new ControlDecoration(encryptionCombo, SWT.CENTER | SWT.RIGHT);
		Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		deco.setDescriptionText("Sample text");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);
		deco.show();

		Label decryptionLabel = new Label(container, SWT.NONE);
		decryptionLabel.setText("Select the decryption method:   ");

		Combo decryptionCombo = new Combo(container, SWT.NONE);
		decryptionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label keyGenerationLabel = new Label(container, SWT.NONE);
		keyGenerationLabel.setText("Select the keyGeneration method: ");
		Combo keyGenerationCombo = new Combo(container, SWT.NONE);
		keyGenerationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		try {
			// import project 
			this.project.ImportProject(this.projectPath);
			this.project.setProject(this.project.cloneProject(this.project.getProject().getName()));
			this.project.addPackage(Constants.PRIMITIVE_PACKAGE);
			//Display methods from the imported project in the combo box 
			for (IMethod method : project.listOfAllMethods()) {
				encryptionCombo.add(method.getElementName());
				decryptionCombo.add(method.getElementName());
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public UserJavaProject getUserProject() {
		return this.project;
	}

	public void setUserProject(UserJavaProject project) {
		this.project = project;
	}
}
