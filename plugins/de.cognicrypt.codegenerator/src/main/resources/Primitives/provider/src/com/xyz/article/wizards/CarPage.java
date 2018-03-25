/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 */
package com.xyz.article.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page shown when the user has chosen car as means of 
 * transport
 */
public class CarPage extends WizardPage implements Listener
{
	public static final String copyright = "(c) Copyright IBM Corporation 2002.";

	String[] rentalCompanyNames = {"Cheap Cars", "Family Cars", "Luxury Cars"};
	float[] prices = {90, 120, 150};
	
	Combo companyCombo;
	Text priceText;	
	Button insuranceButton;
		
	/**
	 * Constructor for HolidayDocumentPage.
	 * @param arg0
	 */
	public CarPage(String arg0) {
		super(arg0);
		setTitle("Travel by car");
		setDescription("Specify car choices");
		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{
	    // create the composite to hold the widgets
	    Composite composite = new Composite(parent, SWT.NONE);
	    
	    // create the desired layout for this wizard page
	    GridLayout gl = new GridLayout();
	    int ncol = 2;
	    gl.numColumns = ncol;
	    composite.setLayout(gl);
	    
	    // create the widgets. If the appearance of the widget is different from the default, 
	    // create a GridData for it to set the alignment and define how much space it will occupy
	    
	    // rental company
	    new Label (composite, SWT.NONE).setText("Rental company");
	    companyCombo = new Combo(composite, SWT.BORDER|SWT.READ_ONLY);
	    companyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    companyCombo.setItems(rentalCompanyNames);
	    companyCombo.addListener(SWT.Selection, this);
	    
	    
	    new Label (composite, SWT.NONE).setText("Price:");
	    priceText = new Text(composite, SWT.BORDER|SWT.MULTI);
	    priceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    priceText.setEditable(false);
	    
	    // insurance button
	    insuranceButton = new Button(composite, SWT.CHECK);
	    insuranceButton.setText("Buy insurance");
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    gd.horizontalSpan = ncol;
		insuranceButton.setLayoutData(gd);
		insuranceButton.setSelection(true);
	    // set the composite as the control for this page
	    setControl(composite);
	}

    /*
     * Process the events: when the user has entered all information
     * the wizard can be finished
     */
	public void handleEvent(Event e)
	{
		if (e.widget == companyCombo) {
			if (companyCombo.getSelectionIndex() >=0)
				priceText.setText("£"+prices[companyCombo.getSelectionIndex()]);
		}
		setPageComplete(isPageComplete());
		getWizard().getContainer().updateButtons();		
	}

	
	public boolean canFlipToNextPage()
	{
		// no next page for this path through the wizard
		return false;
	}

	/*
	 * Sets the completed field on the wizard class when all the information 
	 * is entered and the wizard can be completed
	 */	 
	public boolean isPageComplete()
	{
		HolidayWizard wizard = (HolidayWizard)getWizard();
		if (companyCombo.getText().length() == 0) {
			wizard.carCompleted = false;
			return false;
		}
		saveDataToModel();
		return true;
	}
	private void saveDataToModel()
	{
		HolidayWizard wizard = (HolidayWizard)getWizard();
		wizard.model.rentalCompany = companyCombo.getText();
		wizard.model.carPrice = priceText.getText();
		wizard.model.buyInsurance = insuranceButton.getSelection();
		wizard.carCompleted = true;
	}
}
