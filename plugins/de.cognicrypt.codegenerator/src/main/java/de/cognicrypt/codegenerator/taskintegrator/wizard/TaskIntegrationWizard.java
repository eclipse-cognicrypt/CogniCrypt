package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.nio.file.Paths;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;



public class TaskIntegrationWizard extends Wizard {
	public TaskIntegrationWizard() {

	}



	@Override
	public void addPages() {

		// Just add the mode selection page, since the rest of the pages may not be necessary based on the choices.
		this.addPage(
			new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_MODE_OF_WIZARD,
			Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD,
			Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD
			));

		this.addPage(
			new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION,
			Constants.PAGE_TITLE_FOR_CLAFER_FILE_CREATION,
			Constants.PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION
			));

		this.addPage(
			new PageForTaskIntegratorWizard(
				Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS,
				Constants.PAGE_TITLE_FOR_HIGH_LEVEL_QUESTIONS,
				Constants.PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS
			));

		this.addPage(
			new PageForTaskIntegratorWizard(
				Constants.PAGE_NAME_FOR_XSL_FILE_CREATION,
				Constants.PAGE_TITLE_FOR_XSL_FILE_CREATION,
				Constants.PAGE_DESCRIPTION_FOR_XSL_FILE_CREATION
			));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		/*
		 * TODO finish button behavior important for the mode selection page. Postponing this code to
		 * a future time, but within the Sept milestone.
		 */
		ModelAdvancedMode objectForDataInNonGuidedMode=  ((PageForTaskIntegratorWizard) this.getPage(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)).getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
		FileUtilities fileUtilities = new FileUtilities(objectForDataInNonGuidedMode.getNameOfTheTask());
		if(this.getContainer().getCurrentPage().getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)){
			if(objectForDataInNonGuidedMode.isGuidedModeChosen() == false //&& this.objectForDataInNonGuidedMode.isGuidedModeForced() == false
				){
				fileUtilities.writeFiles(objectForDataInNonGuidedMode.getLocationOfClaferFile(), objectForDataInNonGuidedMode.getLocationOfJSONFile(), objectForDataInNonGuidedMode.getLocationOfXSLFile(), objectForDataInNonGuidedMode.getLocationOfCustomLibrary());			
				return true;
			}
		}


		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {

		/*
		 * TODO next button behavior important for the mode selection page. Postponing this code to
		 * a future time, but within the Sept milestone.
		switch(page.getName()){
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				if(((PageForTaskIntegratorWizard) this.getPage(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)).getCompositeChoiceForModeOfWizard() != null){

				}
				break;
			case Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION:
				break;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:
				break;
			case Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS:
				break;
		}
			*/
		// TODO for debuggnig only.
		if(page.getName().equals(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)){
			PageForTaskIntegratorWizard claferPage = (PageForTaskIntegratorWizard) page;
			CompositeToHoldGranularUIElements compoGran = (CompositeToHoldGranularUIElements) claferPage.getCompositeToHoldGranularUIElements();
			Control granComp = compoGran.getChildren()[0];
			Control granContro[] = ((Composite) granComp).getChildren();
			System.out.println("");
		}
		return super.getNextPage(page);
	}


}
