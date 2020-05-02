/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0
 * which is available at http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Andre Sonntag, Enri Ozuni
 */
public class XSLCodeGenTest {

	Logger log = Logger.getLogger(XSLCodeGenTest.class.getName());
	IJavaProject testJavaProject;
	CodeGenerator generatorSECCOM;
	CodeGenerator generatorSecMPComp;
	Task SECCOMTask;
	Task secMPCompTask;
	Configuration configSecCom;
	Configuration configSecMPComp;
	DeveloperProject developerProject;
	static int counter = 0;

	@Before
	public void setUp() {
		XSLCodeGenTest.counter++;
		try {
			this.testJavaProject = TestUtils.createJavaProject("TestProject_" + counter);
		} catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project.");
		}

		this.SECCOMTask = TestUtils.getTask("SecureCommunication");
		this.generatorSECCOM = new XSLBasedGenerator(this.testJavaProject.getProject(), this.SECCOMTask.getCodeTemplate());

		this.secMPCompTask = TestUtils.getTask("SECMUPACOMP");
		this.generatorSecMPComp = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secMPCompTask.getCodeTemplate());

		this.developerProject = this.generatorSECCOM.getDeveloperProject();
	}

	@After
	public void tearDown() {
		try {
			TestUtils.deleteProject(this.testJavaProject.getProject());
		} catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to delete test project.");
		}
	}

	@Test
	public void SECMUPACOMPDefault() {
		this.configSecMPComp = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.secMPCompTask);
		final boolean secMPCompCheck = this.generatorSecMPComp.generateCodeTemplates(this.configSecMPComp, this.secMPCompTask.getAdditionalResources());
		assertTrue(secMPCompCheck);
	}

	@Test
	public void SECComDefault() {
		this.configSecCom = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.SECCOMTask);
		final boolean secComCheck = this.generatorSECCOM.generateCodeTemplates(this.configSecCom, this.SECCOMTask.getAdditionalResources());
		assertTrue(secComCheck);
	}

}
