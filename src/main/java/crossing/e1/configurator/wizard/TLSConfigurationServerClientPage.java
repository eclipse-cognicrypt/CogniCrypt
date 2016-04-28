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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import crossing.e1.configurator.utilities.Labels;

public class TLSConfigurationServerClientPage extends WizardPage {

	private Composite container;
	private Button serverRadioButton;
	private Button clientRadioButton;
	
	TLSConfigurationServerClientPage tls;
	boolean complete = false;
	
	public TLSConfigurationServerClientPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TLS_PAGE);
		setDescription("Please select whether you want to create the server or client side.");
		tls = this;
	}

	
	@Override
	public boolean canFlipToNextPage() {
		return clientRadioButton.getSelection();
	}

	@Override
	public boolean isPageComplete() {
		return false;
	}


	@Override
	public void createControl(final Composite parent) {
		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 400, 200);
		final GridLayout layout = new GridLayout(4, true);
		layout.horizontalSpacing = 20;
		this.container.setLayout(layout);

		this.clientRadioButton = new Button(this.container, SWT.RADIO);
		this.clientRadioButton.setText("Client");
		clientRadioButton.addSelectionListener(new SelectionListener() {
						
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					complete = true;
					tls.setPageComplete(complete);
				} catch (NumberFormatException e1) {				
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		this.serverRadioButton = new Button(this.container, SWT.RADIO);
		this.serverRadioButton.setText("Server");
		serverRadioButton.addSelectionListener(new SelectionListener() {
						
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					complete = true;
					tls.setPageComplete(complete);
				} catch (NumberFormatException e1) {				
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
