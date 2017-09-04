package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.wizard.Wizard;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.ModelAdvancedMode;


public class TaskIntegrationWizard extends Wizard {
	private ModelAdvancedMode objectForDataInNonGuidedMode;
	PageForTaskIntegratorWizard choicePageForModeOfWizard;

	public TaskIntegrationWizard() {
	}
	
	

	@Override
	public void addPages() {
		
		choicePageForModeOfWizard = new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD
			);
		this.addPage(choicePageForModeOfWizard);
		
		PageForTaskIntegratorWizard pageForClaferFileCreation = new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION, Constants.PAGE_TITLE_FOR_CLAFER_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION);
		this.addPage(pageForClaferFileCreation);
		
		PageForTaskIntegratorWizard pageForXSLFileCreation = new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_XSL_FILE_CREATION, Constants.PAGE_TITLE_FOR_XSL_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_XSL_FILE_CREATION);
		this.addPage(pageForXSLFileCreation);
		
		PageForTaskIntegratorWizard pageForHighLevelQuestions = new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_TITLE_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS);
		this.addPage(pageForHighLevelQuestions);
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	/*@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		
		
		return super.getNextPage(page);
	}*/



	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		//choicePageForModeOfWizard.setDataForAdvancedMode(choicePageForModeOfWizard.getControl());
		System.out.println(objectForDataInNonGuidedMode.getNameOfTheTask());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfCustomLibrary());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfClaferFile());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfJSONFile());
		System.out.println(objectForDataInNonGuidedMode.getLocationOfXSLFile());
		return false;
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
