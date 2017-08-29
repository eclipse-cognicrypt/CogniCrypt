package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.wizard.Wizard;

import crossing.e1.configurator.Constants;

public class TaskIntegrationWizard extends Wizard {

	public TaskIntegrationWizard() {
	}
	
	

	@Override
	public void addPages() {
		
		PageForTaskIntegratorWizard choicePageForModeOfWizard = new PageForTaskIntegratorWizard(
			Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD);
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
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return false;
	}

}
