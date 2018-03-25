/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 */
 
package com.xyz.article.wizards;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard class
 */
public class HolidayWizard extends Wizard implements INewWizard
{
	public static final String copyright = "(c) Copyright IBM Corporation 2002.";	
	// wizard pages
	HolidayMainPage holidayPage;
	PlanePage planePage;
	CarPage   carPage;
	
	// the model
	HolidayModel model;
	
	// workbench selection when the wizard was started
	protected IStructuredSelection selection;
	
	// flag indicated whether the wizard can be completed or not 
	// if the user has selected plane as type of transport
	protected boolean planeCompleted = false;

	// flag indicated whether the wizard can be completed or not 
	// if the user has selected car as type of transport
	protected boolean carCompleted = false;
	
	// the workbench instance
	protected IWorkbench workbench;

	/**
	 * Constructor for HolidayMainWizard.
	 */
	public HolidayWizard() {
		super();
		model = new HolidayModel();
	}
	
	public void addPages()
	{
		holidayPage = new HolidayMainPage(workbench, selection);
		addPage(holidayPage);
		planePage = new PlanePage("");
		addPage(planePage);
		carPage = new CarPage("");
		addPage(carPage);
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		this.workbench = workbench;
		this.selection = selection;
		if (selection != null && !selection.isEmpty()) {
			Object obj = selection.getFirstElement();
			if (obj  instanceof IFolder) {
				IFolder folder = (IFolder) obj;				
				if (folder.getName().equals("Discounts"))
					model.discounted = true;				
			}
		}
	}

	public boolean canFinish()
	{
		// cannot completr the wizard from the first page
		if (this.getContainer().getCurrentPage() == holidayPage) 
			return false;
		// based on the type of transport return the right flag			
		if (model.usePlane) return planeCompleted;
		return carCompleted;
	}
	
	public boolean performFinish() 
	{
		String summary = model.toString();
		MessageDialog.openInformation(workbench.getActiveWorkbenchWindow().getShell(), 
			"Holiday info", summary);
		return true;
	}
}
