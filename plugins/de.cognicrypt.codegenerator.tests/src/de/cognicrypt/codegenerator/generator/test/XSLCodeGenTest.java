/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0
 * which is available at http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;
//<<<<<<< HEAD
//
//import java.io.IOException;
//import java.util.logging.Logger;
//import org.eclipse.core.runtime.CoreException;
//=======
//>>>>>>> refs/heads/develop
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;

import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Andre Sonntag
 * @author Enri Ozuni
 */
public class XSLCodeGenTest {

	@Test

	public void SECMUPACOMPDefault() {
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_SECMUPACOMP");
		Task secMPCompTask = TestUtils.getTask("SECMUPACOMP");
		CodeGenerator generatorSecMPComp = new XSLBasedGenerator(testJavaProject.getProject(), secMPCompTask.getCodeTemplate());
		DeveloperProject developerProject = generatorSecMPComp.getDeveloperProject();
		Configuration configSecMPComp = TestUtils.createXSLConfigurationForCodeGeneration(developerProject, secMPCompTask);
		boolean secMPCompCheck = generatorSecMPComp.generateCodeTemplates(configSecMPComp, secMPCompTask.getAdditionalResources());
		assertTrue(secMPCompCheck);
		TestUtils.deleteProject(testJavaProject.getProject());
	}

	@Test

	public void SECComDefault() {
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_SECCom");
		Task SECCOMTask = TestUtils.getTask("SecureCommunication");
		CodeGenerator generatorSECCOM = new XSLBasedGenerator(testJavaProject.getProject(), SECCOMTask.getCodeTemplate());
		DeveloperProject developerProject = generatorSECCOM.getDeveloperProject();
		
		Configuration configSecCom = TestUtils.createXSLConfigurationForCodeGeneration(developerProject, SECCOMTask);
		boolean secComCheck = generatorSECCOM.generateCodeTemplates(configSecCom, SECCOMTask.getAdditionalResources());
		assertTrue(secComCheck);
		TestUtils.deleteProject(testJavaProject.getProject());
	}

}
