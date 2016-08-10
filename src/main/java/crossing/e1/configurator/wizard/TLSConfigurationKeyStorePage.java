/**
 * Copyright 2015 Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Karim Ali, Stefan Kr�ger
 *
 */
package crossing.e1.configurator.wizard;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.utilities.Labels;

public class TLSConfigurationKeyStorePage extends WizardPage {

	private static Composite container;
	private Label pathLabel;
	private Text pathText;
	private Label passwordLabel;
	private Text passwordText;
	private Button selectFilePushButton;

	TLSConfigurationKeyStorePage tls;
	boolean complete = false;

	public TLSConfigurationKeyStorePage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TLS_PAGE);
		setDescription("Please enter the path to your keystore.");
		tls = this;
	}

	@Override
	public boolean canFlipToNextPage() {
		return complete;
	}

	@Override
	public boolean isPageComplete() {
		return false;
	}

	@Override
	public void createControl(final Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 250, 200);
		final GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 10;
		container.setLayout(layout);

		this.pathLabel = new Label(container, SWT.NONE);
		this.pathLabel.setText("Path to Key Store");
		this.pathText = new Text(container, SWT.SINGLE);
		this.pathText.setText("C:\\");
		this.pathText.setLayoutData(new GridData(200, 15));

		this.selectFilePushButton = new Button(container, SWT.PUSH);
		this.selectFilePushButton.setText("Select File");
		selectFilePushButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(new JPanel()) == JFileChooser.APPROVE_OPTION) {
					String s = fileChooser.getSelectedFile().getAbsolutePath();
					pathText.setText(s);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		pathText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				String pathTextcontent = pathText.getText();
				if (!pathTextcontent.isEmpty()) {
					File keyStoreFile = new File(pathTextcontent.replace("\\", "\\\\"));
					if (keyStoreFile.exists()) {
						complete = true;
						tls.setPageComplete(complete);
						return;
					}
				}
				complete = false;
				tls.setPageComplete(complete);
			}
		});

		this.passwordLabel = new Label(container, SWT.NONE);
		this.passwordLabel.setText("Password of Key Store");
		this.passwordText = new Text(container, SWT.PASSWORD);
		this.passwordText.setLayoutData(new GridData(200, 15));

		setControl(container);
	}

	public String getPath() {
		return pathText.getText().replace("\\", "\\\\");
	}

	public String getPassword() {
		return passwordText.getText();
	}
}
