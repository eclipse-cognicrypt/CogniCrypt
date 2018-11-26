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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.primitive.providerUtils.UserJavaProject;

/**
 * 
 * @author Ahmed Ben Tahar
 */
public class JavaProjectBrowserPage extends WizardPage {

	Text text;
	File selectedJavaFile;
	String path;
	UserJavaProject project = new UserJavaProject();
	List<IMethod> methods = new ArrayList<IMethod>();

	public JavaProjectBrowserPage(String pageName) {
		super(pageName);
		setDescription("Please choose the Algorithm project from your computer.");
		setTitle("File browser");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setBounds(10, 10, 200, 300);

		container.setLayout(new GridLayout(3, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		Label question = new Label(container, SWT.NULL);
		question.setText("Select a Java Project: ");

		text = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		text.setEditable(true);
		//gd_text.widthHint = 107;
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.addModifyListener(e -> {
			this.selectedJavaFile = new File(getAbsolutePath());
		});

		Button btnBrowse = new Button(container, SWT.PUSH);
		GridData gd_btnBrowse = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse.widthHint = 85;
		btnBrowse.setLayoutData(gd_btnBrowse);
		btnBrowse.setBounds(295, 140, 75, 25);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(btnBrowse.getShell(), SWT.NULL);
				dialog.setFilterExtensions(new String[] { ".project" });
				dialog.setFilterPath("c:\\");
				path = dialog.open();
				if (path != null) {

					File file = new File(path);
					if (file.isFile())
						displayFiles(new String[] { file.toString() });
					else
						displayFiles(file.list());
				}
			}

		});
	}

	public void displayFiles(String[] files) {
		for (int i = 0; files != null && i < files.length; i++) {
			text.setText(files[i]);
			text.setEditable(true);

		}
	}

	// Get the absolute path of the selected file 
	public String getAbsolutePath() {
		return text.getText();
	}

	public File getSelectedFile() {
		return this.selectedJavaFile;
	}

}
