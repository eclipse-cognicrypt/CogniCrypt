/**
 * Copyright 2015 Technische UniversitÃ¤t Darmstadt
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
 * @author Karim Ali, Stefan Krüger
 *
 */
package crossing.e1.configurator.wizard;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Labels;

public class TLSConfigurationPage extends WizardPage {

	private static final String SUCCESS = "SUCCESS!";
	private Composite container;
	private Button testConnectionPushButton;
	private Label hostLabel;
	private Text hostText;
	private Label portLabel;
	private Text portText;
	private Label testConnectionLabel;
	
	TLSConfigurationPage tls;
	boolean complete = false;
	
	public TLSConfigurationPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TLS_PAGE);
		setDescription(Labels.DESCRIPTION_TLS_PAGE);
		tls = this;
	}

	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public boolean isPageComplete() {
		return this.testConnectionLabel.getText().equals(SUCCESS);
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
		
		this.portLabel = new Label(this.container, SWT.NONE);
		this.portLabel.setText(Labels.PORT);
		this.portText = new Text(this.container, SWT.SINGLE);
		this.portText.setText("80");
		this.portText.setLayoutData(new GridData(100, 15));

		
		this.testConnectionLabel = new Label(this.container, SWT.DOWN);
		this.testConnectionLabel.setText("                         ");
		
		this.testConnectionPushButton = new Button(this.container, SWT.PUSH);
		this.testConnectionPushButton.setText(Constants.TEST_CONNECTION);
		testConnectionPushButton.addSelectionListener(new SelectionListener() {
						
			@Override
			public void widgetSelected(SelectionEvent e) {
				String host = hostText.getText();
				String port = portText.getText();
				try {
					TLSConnection tlstest = new TLSConnection(Integer.parseInt(port), host);
					testConnectionLabel.setText(SUCCESS);
					testConnectionLabel.redraw();
					complete = true;
					tls.setPageComplete(complete);
				} catch (IOException | NumberFormatException | URISyntaxException e1) {				
					testConnectionLabel.setText("FAIL!");
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		setControl(this.container);
	}
	
	
}
