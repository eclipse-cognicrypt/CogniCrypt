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
 * @author Karim Ali
 *
 */
package crossing.e1.configurator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModel;

public class TLSConfigurationPage extends WizardPage {

	private Composite container;
	private Button testConnectionPushButton;
	private Label hostLabel;
	private Text hostText;
	private Label portLabel;
	private Text portText;
	private final ClaferModel model;

	public TLSConfigurationPage(final ClaferModel claferModel) {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TLS_PAGE);
		setDescription(Labels.DESCRIPTION_TLS_PAGE);
		this.model = claferModel;
	}

	public boolean canProceed() {
		return false;
	}

	@Override
	public void createControl(final Composite parent) {
		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 400, 200);
		final GridLayout layout = new GridLayout(4, true);
		layout.horizontalSpacing = 20;
		this.container.setLayout(layout);

		this.hostLabel = new Label(this.container, SWT.NONE);
		this.hostLabel.setText(Labels.HOST);
		this.hostText = new Text(this.container, SWT.SINGLE);
		this.hostText.setText("localhost");
		this.hostText.setLayoutData(new GridData(200, 15));
//		this.hostText.getBounds().width = 2 * this.hostText.getBounds().width;
		
		this.portLabel = new Label(this.container, SWT.NONE);
		this.portLabel.setText(Labels.PORT);
		this.portText = new Text(this.container, SWT.SINGLE);
		this.portText.setText("80");
		this.portText.setLayoutData(new GridData(100, 15));
//		this.portText.getBounds().width = 2 * this.portText.getBounds().width;

		this.testConnectionPushButton = new Button(this.container, SWT.PUSH);
		this.testConnectionPushButton.setText(Constants.TEST_CONNECTION);
		
		setControl(this.container);
	}
}
