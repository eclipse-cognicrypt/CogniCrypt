/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import java.io.File;
import org.junit.Test;
import de.cognicrypt.integrator.task.models.ModelAdvancedMode;

public class ModelForTITasksTests {

	@Test
	public void testModelAdvancedMode() {
		final ModelAdvancedMode tiTask = new ModelAdvancedMode();
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
