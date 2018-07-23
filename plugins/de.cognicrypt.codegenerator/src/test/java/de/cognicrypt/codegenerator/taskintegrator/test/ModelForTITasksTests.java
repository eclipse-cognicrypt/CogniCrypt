/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

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
