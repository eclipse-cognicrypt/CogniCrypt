package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.nio.file.Paths;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;



public class TaskIntegrationWizard extends Wizard {
	private ModelAdvancedMode objectForDataInNonGuidedMode;

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
		if(this.getContainer().getCurrentPage().getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)){
			setDataForAdvancedMode(
				((PageForTaskIntegratorWizard) this.getPage(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)).getCompositeChoiceForModeOfWizard()
				);
			if(
				this.objectForDataInNonGuidedMode.isGuidedModeChosen() == false &&
				this.objectForDataInNonGuidedMode.isGuidedModeForced() == false){
				// and if the validations on all the text boxes is true, then close.
				return true;
			}
		}


		/*
		System.out.println(objectForDataInNonGuidedMode.getNameOfTheTask());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfCustomLibrary());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfClaferFile());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfJSONFile());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfXSLFile());

		System.out.println(objectForDataInNonGuidedMode.isCustomLibraryRequired());
		System.out.println(objectForDataInNonGuidedMode.isGuidedModeChosen());
		System.out.println(objectForDataInNonGuidedMode.isGuidedModeForced());

		return false;*/
		return true;
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



	private void setDataForAdvancedMode(Composite compositeChoiceForModeOfWizard){
		// Check if this call is from the initial call to the getNextPage.
		if(compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK)!=null){
			//TaskIntegrationWizard wizard = (TaskIntegrationWizard) this.getWizard();
			setObjectForDataInNonGuidedMode(
				new ModelAdvancedMode(
					compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_NAME_OF_THE_TASK).toString(),
					Paths.get(compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK).toString()),
					Paths.get(compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE).toString()),
					Paths.get(compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE).toString()),
					Paths.get(compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE).toString()),
					(boolean) compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED),
					(boolean) compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN),
					(boolean) compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED)
						)
					);
		}

	}

	/**
	 * @return the objectForDataInNonGuidedMode
	 */
	public ModelAdvancedMode getObjectForDataInNonGuidedMode() {
		return objectForDataInNonGuidedMode;
	}



	/**
	 * @param objectForDataInNonGuidedMode the objectForDataInNonGuidedMode to set
	 */
	public void setObjectForDataInNonGuidedMode(ModelAdvancedMode objectForDataInNonGuidedMode) {
		this.objectForDataInNonGuidedMode = objectForDataInNonGuidedMode;
	}

}
