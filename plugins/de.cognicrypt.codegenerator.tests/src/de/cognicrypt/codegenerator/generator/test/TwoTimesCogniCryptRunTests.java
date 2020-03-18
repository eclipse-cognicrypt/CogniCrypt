/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;

import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author André Sonntag
 */
public class TwoTimesCogniCryptRunTests {

	/**
	 * Scenario: User runs CogniCrypt two times without selecting a specific class
	 * or package.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runCCTwoTimesNoSpecificSelection() throws Exception {
		// task template
		String templateSecEnc = "secretkeyencryption";
		String templateSecPwd = "securepassword";

		// create Java project without any package or class
		IJavaProject generatedProject = TestUtils.createJavaProject("TestProject1");

		// setup for code generation
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(generatedProject.getResource());
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(templateSecEnc,
				generatedProject.getResource(), codeGenerator, developerProject);

		// first generation run
		boolean secEncCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(secEncCheck); // check if code generation is successful for the first run

		// setup for second generation
		chosenConfig = TestUtils.createCrySLConfiguration(templateSecPwd, generatedProject.getResource(), codeGenerator,
				developerProject);

		// second generation run
		boolean secPwdCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(secPwdCheck); // check if code generation is successful for the second run

		ICompilationUnit encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"SecureEncryptor.java");
		assertNotNull(encClass); // check if SecureEncryptor.java is created

		ICompilationUnit pwdHasherClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"PasswordHasher.java");
		assertNotNull(pwdHasherClass); // check if PasswordHasher.java is created

		ICompilationUnit outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"Output.java");
		assertNotNull(outputClass); // check if Output.java is created
		assertEquals(1, TestUtils.countMethods(outputClass));

		TestUtils.deleteProject(generatedProject.getProject());
	}

	/**
	 * Scenario: User runs CogniCrypt two times and selects the previous generated
	 * output class.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runCCTwoTimesOutputClassSelection() throws Exception {
		// task template
		String templateSecEnc = "secretkeyencryption";
		String templateSecPwd = "securepassword";

		// create Java project without any package or class
		IJavaProject generatedProject = TestUtils.createJavaProject("TestProject2");

		// setup for first generation
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(generatedProject.getResource());
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(templateSecEnc,
				generatedProject.getResource(), codeGenerator, developerProject);

		// first generation run
		boolean secEncCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(secEncCheck); // check if code generation is successful for the first run

		ICompilationUnit encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"SecureEncryptor.java");
		assertNotNull(encClass); // check if SecureEncryptor.java is created

		ICompilationUnit outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"Output.java");
		assertNotNull(outputClass);

		// setup for second generation
		codeGenerator = new CrySLBasedCodeGenerator(outputClass.getResource());
		developerProject = codeGenerator.getDeveloperProject();
		chosenConfig = TestUtils.createCrySLConfiguration(templateSecPwd, outputClass.getResource(), codeGenerator,
				developerProject);

		// second generation run
		boolean secPwdCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(secPwdCheck); // check if code generation is successful for the second run

		ICompilationUnit pwdHasherClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"PasswordHasher.java");
		assertNotNull(pwdHasherClass); // check if PasswordHasher.java is created

		outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "Output.java");
		assertNotNull(outputClass);

		int outputMethodCount = TestUtils.countMethods(outputClass);
		assertEquals(1, outputMethodCount);
	}

	/**
	 * Scenario: User runs CogniCrypt two times and selects a previous generated
	 * "logic" class.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runCCTwoTimesLogicClassSelection() throws Exception {
		// task template
		String templateSecEnc = "secretkeyencryption";
		String templateSecPwd = "securepassword";

		// create Java project without any package or class
		IJavaProject generatedProject = TestUtils.createJavaProject("TestProject3");

		// setup for first generation
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(generatedProject.getResource());
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(templateSecEnc,
				generatedProject.getResource(), codeGenerator, developerProject);

		// first generation run
		boolean secEncCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(secEncCheck); // check if code generation is successful for the first run

		ICompilationUnit encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"SecureEncryptor.java");
		assertNotNull(encClass); // check if SecureEncryptor.java is created

		ICompilationUnit outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"Output.java");
		assertNotNull(outputClass);

		// setup for second generation
		codeGenerator = new CrySLBasedCodeGenerator(encClass.getResource());
		developerProject = codeGenerator.getDeveloperProject();
		chosenConfig = TestUtils.createCrySLConfiguration(templateSecPwd, encClass.getResource(), codeGenerator,
				developerProject);

		// second generation run
		boolean secPwdCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(secPwdCheck); // check if code generation is successful for the second run

		ICompilationUnit pwdHasherClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"PasswordHasher.java");
		assertNotNull(pwdHasherClass); // check if PasswordHasher.java is created

		encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		assertNotNull(encClass);

		int secureEncryptorMethodCount = TestUtils.countMethods(encClass);

		assertEquals(4, secureEncryptorMethodCount);

	}
}
