package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.wizard.PageForTaskIntegratorWizard;
import de.cognicrypt.codegenerator.taskintegrator.wizard.TaskIntegrationWizard;


public class TaskIntegrationWizardTest {

	@Test
	public void testGetTIPageByName() {
		TaskIntegrationWizard tiWizard = new TaskIntegrationWizard();
		
		PageForTaskIntegratorWizard pageMode = new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD);
		PageForTaskIntegratorWizard pageClafer = new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION, Constants.PAGE_TITLE_FOR_CLAFER_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION);
		PageForTaskIntegratorWizard pageHighLevelQuestions = new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_TITLE_FOR_HIGH_LEVEL_QUESTIONS, Constants.PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS);
		PageForTaskIntegratorWizard pageXSL = new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_XSL_FILE_CREATION, Constants.PAGE_TITLE_FOR_XSL_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_XSL_FILE_CREATION);

		assertEquals(pageClafer, tiWizard.getTIPageByName(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION));
		assertEquals(null, tiWizard.getTIPageByName(""));
	}

}
