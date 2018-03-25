/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 */
 
package com.xyz.article.wizards;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.ui.IWorkbench;

/**
 * Class representing the first page of the wizard
 */

public class HolidayMainPage extends WizardPage implements Listener
{

	public static final String copyright = "(c) Copyright IBM Corporation 2002.";	
	
	IWorkbench workbench;
	IStructuredSelection selection;
	
	// widgets on this page 
	Combo travelDate;
	Combo travelMonth;
	Combo travelYear;
	Combo returnDate;
	Combo returnMonth;
	Combo returnYear;	
	Button priceButton;	
	Text fromText;
	Text toText;
	Button planeButton;
	Button carButton;
	
	// status variable for the possible errors on this page
	// timeStatus holds an error if the return date is before the departure date
	IStatus timeStatus;
	
	// holds an error is the destination and departure fields are the same
	IStatus destinationStatus;
		
	final static String[] dates ={ "1", "2", "3", "4", "5", "6", "7", "8", "9",
		"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
	final static String[] months= {"January", "February", "March", "April", "May",
		"June", "July", "August", "September", "October", "November", "December" };

	final static String[] years;
	final static int startingYear;
	
	static {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		startingYear = cal.get(Calendar.YEAR);
		years = new String[3];
		for (int i = -1; i < 2; i++) {
			years[i+1] = String.valueOf(startingYear + i);
		}
	}
	/**
	 * Constructor for HolidayMainPage.
	 */
	public HolidayMainPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page1");
		setTitle("Your Holiday");
		setDescription("Select the details of your holiday");
		this.workbench = workbench;
		this.selection = selection;
		destinationStatus = new Status(IStatus.OK, "not_used", 0, "", null);
		timeStatus = new Status(IStatus.OK, "not_used", 0, "", null);		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {

	    // create the composite to hold the widgets
		GridData gd;
		Composite composite =  new Composite(parent, SWT.NULL);

	    // create the desired layout for this wizard page
		GridLayout gl = new GridLayout();
		int ncol = 4;
		gl.numColumns = ncol;
		composite.setLayout(gl);

		// initial value for date of travel, today's date
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		
	    // create the widgets. If the appearance of the widget is different from the default, 
	    // create a GridData for it to set the alignment and define how much space it will occupy		
	    	    
	    // Date of travel
		new Label (composite, SWT.NONE).setText("Travel on:");						
		travelDate = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		travelDate.setLayoutData(gd);
		travelDate.setItems(dates);		
		travelDate.setText(travelDate.getItem(dayOfMonth -1)); // 0 based indexes

		travelMonth = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		travelMonth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		travelMonth.setItems(months);
		travelMonth.setText(travelMonth.getItem(month));

		travelYear = new Combo(composite,  SWT.BORDER | SWT.READ_ONLY);
		travelYear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		travelYear.setItems(years);
		travelYear.setText(travelYear.getItem(year - startingYear));


		// Date of return		
		new Label (composite, SWT.NONE).setText("Return on:");		
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;	
		returnDate = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		returnDate.setLayoutData(gd);
		returnDate.setItems(dates);

		returnMonth = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		returnMonth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		returnMonth.setItems(months);

		returnYear = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		returnYear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		returnYear.setItems(years);

		createLine(composite, ncol);

		// Departure				
		new Label (composite, SWT.NONE).setText("From:");				
		fromText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol - 1;
		fromText.setLayoutData(gd);
		
		// Destination
		new Label (composite, SWT.NONE).setText("To:");				
		toText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol - 1;
		toText.setLayoutData(gd);

		createLine(composite, ncol);

		// Choice of transport		
		planeButton = new Button(composite, SWT.RADIO);
		planeButton.setText("Take a plane");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		planeButton.setLayoutData(gd);
		planeButton.setSelection(true);
		
		carButton = new Button(composite, SWT.RADIO);
		carButton.setText("Rent a car");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		carButton.setLayoutData(gd);
		
		
	    // set the composite as the control for this page
		setControl(composite);		
		addListeners();
	}
	
	private void addListeners()
	{
		planeButton.addListener(SWT.Selection, this);
		carButton.addListener(SWT.Selection, this);
		fromText.addListener(SWT.KeyUp, this);
		toText.addListener(SWT.KeyUp, this);
		travelDate.addListener(SWT.Selection, this);
		travelMonth.addListener(SWT.Selection, this);
		travelYear.addListener(SWT.Selection, this);
		returnDate.addListener(SWT.Selection, this);
		returnMonth.addListener(SWT.Selection, this);
		returnYear.addListener(SWT.Selection, this);
	}

	
	/**
	 * @see Listener#handleEvent(Event)
	 */
	public void handleEvent(Event event) {
	    // Initialize a variable with the no error status
	    Status status = new Status(IStatus.OK, "not_used", 0, "", null);
	    // If the event is triggered by the destination or departure fields
	    // set the corresponding status variable to the right value
	    if ((event.widget == fromText) || (event.widget == toText)) {
	        if (fromText.getText().equals(toText.getText()) && !"".equals(fromText.getText()))
	            status = new Status(IStatus.ERROR, "not_used", 0, 
	                "Departure and destination cannot be the same", null);        
	        destinationStatus = status;
	    }

	    // If the event is triggered by any of the date fields  set
	    // corresponding status variable to the right value
	    if ((event.widget == returnDate) || (event.widget == returnMonth)
	    	|| (event.widget == returnYear) || (event.widget == travelDate)
	    	|| (event.widget == travelMonth) || (event.widget == travelYear)) {
			if	(isReturnDateSet() && !validDates()) 
	            status = new Status(IStatus.ERROR, "not_used", 0, 
	                "Return date cannot be before the travel date", null);	                
	        timeStatus = status;
			
	    }
	    // Show the most serious error
	    applyToStatusLine(findMostSevere());
		getWizard().getContainer().updateButtons();
	}

	/*
	 * Returns the next page.
	 * Saves the values from this page in the model associated 
	 * with the wizard. Initializes the widgets on the next page.
	 */
	
	public IWizardPage getNextPage()
	{    		
		saveDataToModel();		
		if (planeButton.getSelection()) {
			PlanePage page = ((HolidayWizard)getWizard()).planePage;
			page.onEnterPage();
			return page;
		}
	    // Returns the next page depending on the selected button
		if (carButton.getSelection()) { 
			CarPage page = ((HolidayWizard)getWizard()).carPage;
			return page;
		}
		return null;
	}

	/**
	 * @see IWizardPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage()
	{
		if (getErrorMessage() != null) return false;
		if (isTextNonEmpty(fromText)
			&& isTextNonEmpty(toText) &&
			(planeButton.getSelection() || carButton.getSelection()) 
			&& isReturnDateSet())
			return true;
		return false;
	}
	
	/*
	 * Saves the uses choices from this page to the model.
	 * Called on exit of the page
	 */
	private void saveDataToModel()
	{
	    // Gets the model
		HolidayWizard wizard = (HolidayWizard)getWizard();
		HolidayModel model = wizard.model;

		model.resetFlights = true;
		if ((model.departure != null) && (model.destination != null))
		    if (model.departure.equals(fromText.getText()) &&
		    	model.destination.equals(toText.getText()))
	    		model.resetFlights = false;
		
	    // Saves the user choices in the model
		model.departure = fromText.getText();
		model.destination = toText.getText();
		model.departureDate = travelDate.getText() + " "+ 
			travelMonth.getText()+ " "+ travelYear.getText();
		model.returnDate = returnDate.getText() + " "+ 
			returnMonth.getText()+ " "+ returnYear.getText();

		model.usePlane = planeButton.getSelection();
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 */
	private void applyToStatusLine(IStatus status) {
		String message= status.getMessage();
		if (message.length() == 0) message= null;
		switch (status.getSeverity()) {
			case IStatus.OK:
				setErrorMessage(null);
				setMessage(message);
				break;
			case IStatus.WARNING:
				setErrorMessage(null);
				setMessage(message, WizardPage.WARNING);
				break;				
			case IStatus.INFO:
				setErrorMessage(null);
				setMessage(message, WizardPage.INFORMATION);
				break;			
			default:
				setErrorMessage(message);
				setMessage(null);
				break;		
		}
	}	
	
	private IStatus findMostSevere()
	{
		if (timeStatus.matches(IStatus.ERROR))
			return timeStatus;
		if (destinationStatus.matches(IStatus.ERROR))
			return destinationStatus;
		if (timeStatus.getSeverity() >destinationStatus.getSeverity())
			return timeStatus;
		else return destinationStatus;	
	}

	
	private static boolean isTextNonEmpty(Text t)
	{
		String s = t.getText();
		if ((s!=null) && (s.trim().length() >0)) return true;
		return false;
	}	

	private void createLine(Composite parent, int ncol) 
	{
		Label line = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.BOLD);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = ncol;
		line.setLayoutData(gridData);
	}	

	/**
	 * @return true iff all fields of the return dates are set
	 */
	private boolean isReturnDateSet()
	{
		if ((returnDate.getSelectionIndex() >=0)
			&& (returnMonth.getSelectionIndex()>=0)
			&& (returnYear.getSelectionIndex()>=0)) return true;
		return false;
	}
	
	/*
	 * @return true if the travel and return dates are valid i.e. if the
	 * return date is the either the same or after the travel date 
	 */
	private boolean validDates()
	{
		if (isReturnDateSet()) {
			// compare the years first, if not equal, we have an answer		
			int i = Integer.parseInt(returnYear.getText());
			int j = Integer.parseInt(travelYear.getText());
			if (i>j) return true;
			if (i<j) return false;
			
			// if the years are equal, look at the month
			i = returnMonth.getSelectionIndex();
			j = travelMonth.getSelectionIndex();
			if (j== -1) {						
				// if the travel month still contains the initial value the selection index is still -1
				// we need to find the index of the current month
				String month1 = travelMonth.getText();
				for (int k = 0; k<12;k++)
					if (months[k].equals(month1)) { j= k; break;}
			}
			if (i>j) return true;
			if (i<j) return false;

			// if the months are also equal, comparing the ddays we have the answer			
			i = Integer.parseInt(returnDate.getText());
			j = Integer.parseInt(travelDate.getText());
			if (i<j) return false;
			return true;
		}
		return false;
	}

}

