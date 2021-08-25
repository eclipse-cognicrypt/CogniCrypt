/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.order.editor.wizard;

import java.util.List;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.order.editor.Constants;
import de.cognicrypt.order.editor.config.CryslFile;
import de.cognicrypt.order.editor.config.StaxParser;
import de.cognicrypt.order.editor.config.StaxWriter;

public class PageForOrderDiagramWizard extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public PageForOrderDiagramWizard(final String name, final String title, final String description) {
		super(name);
		setTitle(title);
		setDescription(description);
		setPageComplete(false);
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));
		
		// needs to be replaced by file selection drop down menu later
		final Button button = new Button(container, SWT.PUSH);
	    button.setText("Generate Statemachine Models");
	    
	    button.addSelectionListener(new SelectionListener() {
	    	

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
				StaxWriter configFile = new StaxWriter();
				
				configFile.setFile("config" + Constants.XML_EXTENSION);
		        try {
		            configFile.saveConfig();
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		        StaxParser read = new StaxParser();
		        List<CryslFile> readConfig = read.readConfig(configFile.getFile().getPath());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
	      });
	}
}
