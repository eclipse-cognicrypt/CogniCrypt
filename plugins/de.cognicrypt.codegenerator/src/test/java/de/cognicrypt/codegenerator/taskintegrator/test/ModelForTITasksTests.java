package de.cognicrypt.codegenerator.taskintegrator.test;

import java.io.File;

import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;

public class ModelForTITasksTests {

	@Test
	public void testModelAdvancedMode() {
		ModelAdvancedMode tiTask = new ModelAdvancedMode();
		tiTask.setCustomLibraryRequired(true);
		tiTask.setDescription("This is a description");
		tiTask.setGuidedModeChosen(false);
		tiTask.setLocationOfClaferFile(new File(""));
		tiTask.setLocationOfCustomLibrary(new File(""));
		tiTask.setLocationOfHelpXMLFile(new File(""));
		tiTask.setLocationOfJSONFile(new File(""));
		tiTask.setLocationOfXSLFile(new File(""));
		tiTask.setNameOfTheTask("Test");
		tiTask.setTask();
		tiTask.setTaskDescription("This is the task description.");
	}

}
