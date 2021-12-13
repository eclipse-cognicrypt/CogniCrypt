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
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.DeveloperProject;

public class CrySLCodeGenTest {

	@Test
	public void generateSymEnc() {
		String template = "secretkeyencryption";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_SYMENC");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(3, TestUtils.countMethods(encClassUnit));
			assertEquals(4, TestUtils.countStatements(encClassUnit, "generateSessionKey"));
			assertEquals(13, TestUtils.countStatements(encClassUnit, "encrypt"));
			assertEquals(11, TestUtils.countStatements(encClassUnit, "decrypt"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generatePBEnc() {
		String template = "encryption";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENC");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(3, TestUtils.countMethods(encClassUnit));
			assertEquals(12, TestUtils.countStatements(encClassUnit, "getKey"));
			assertEquals(13, TestUtils.countStatements(encClassUnit, "encrypt"));
			assertEquals(11, TestUtils.countStatements(encClassUnit, "decrypt"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generatePBEncFiles() {
		String template = "encryptionfiles";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENCFILES");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(3, TestUtils.countMethods(encClassUnit));
			assertEquals(12, TestUtils.countStatements(encClassUnit, "getKey"));
			assertEquals(15, TestUtils.countStatements(encClassUnit, "encrypt"));
			assertEquals(13, TestUtils.countStatements(encClassUnit, "decrypt"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generatePBEncStrings() {
		String template = "encryptionstrings";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENCSTRINGS");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(3, TestUtils.countMethods(encClassUnit));
			assertEquals(12, TestUtils.countStatements(encClassUnit, "getKey"));
			assertEquals(14, TestUtils.countStatements(encClassUnit, "encrypt"));
			assertEquals(12, TestUtils.countStatements(encClassUnit, "decrypt"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generateHybridEnc() {
		String template = "encryptionhybrid";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_HybridENC");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(5, TestUtils.countMethods(encClassUnit));
			assertEquals(4, TestUtils.countStatements(encClassUnit, "generateSessionKey"));
			assertEquals(5, TestUtils.countStatements(encClassUnit, "generateKeyPair"));
			assertEquals(7, TestUtils.countStatements(encClassUnit, "encryptSessionKey"));
			assertEquals(13, TestUtils.countStatements(encClassUnit, "encryptData"));
			assertEquals(11, TestUtils.countStatements(encClassUnit, "decryptData"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}
	}

	@Test
	public void generateHybridEncFiles() {
		String template = "encryptionhybridfiles";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_HybridENCFILES");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(5, TestUtils.countMethods(encClassUnit));
			assertEquals(4, TestUtils.countStatements(encClassUnit, "generateSessionKey"));
			assertEquals(5, TestUtils.countStatements(encClassUnit, "generateKeyPair"));
			assertEquals(7, TestUtils.countStatements(encClassUnit, "encryptSessionKey"));
			assertEquals(15, TestUtils.countStatements(encClassUnit, "encryptData"));
			assertEquals(13, TestUtils.countStatements(encClassUnit, "decryptData"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generateHybridEncStrings() {
		String template = "encryptionhybridstrings";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_HybridENCSTRINGS");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(5, TestUtils.countMethods(encClassUnit));
			assertEquals(4, TestUtils.countStatements(encClassUnit, "generateSessionKey"));
			assertEquals(5, TestUtils.countStatements(encClassUnit, "generateKeyPair"));
			assertEquals(7, TestUtils.countStatements(encClassUnit, "encryptSessionKey"));
			assertEquals(14, TestUtils.countStatements(encClassUnit, "encryptData"));
			assertEquals(12, TestUtils.countStatements(encClassUnit, "decryptData"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generateSecPwd() {
		String template = "securepassword";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_SecPwd");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "PasswordHasher.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(5, TestUtils.countMethods(encClassUnit));
			assertEquals(12, TestUtils.countStatements(encClassUnit, "createPWHash"));
			assertEquals(11, TestUtils.countStatements(encClassUnit, "verifyPWHash"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}
	}

	@Test
	public void generatedigSign() {
		String template = "digitalsignatures";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_DigSign");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureSigner.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(3, TestUtils.countMethods(encClassUnit));
			assertEquals(5, TestUtils.countStatements(encClassUnit, "getKey"));
			assertEquals(8, TestUtils.countStatements(encClassUnit, "sign"));
			// assertEquals(14, TestUtils.countStatements(encClassUnit, "vfy"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}
	}

	@Test
	public void generateStringHasher() {
		String template = "stringhashing";
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_StrHash");
			IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();
			CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

			boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "StringHasher.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
			assertEquals(2, TestUtils.countMethods(encClassUnit));
			assertEquals(5, TestUtils.countStatements(encClassUnit, "createHash"));
			assertEquals(5, TestUtils.countStatements(encClassUnit, "verifyHash"));
			TestUtils.deleteProject(testJavaProject.getProject());
		}
		catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}
	}

}
