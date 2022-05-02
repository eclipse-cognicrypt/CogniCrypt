package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Test;

import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Shahrzad Asghari
 */
public class EncryptionCodeGenTest {
	
	//task
	Task EncTask = TestUtils.getTask("Encryption");
	
	@Test
	public void testCodeGenerationEncryption() {
		String template = "encryption";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENC");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(12, TestUtils.countStatements(encClassUnit, "getKey"));
		assertEquals(13, TestUtils.countStatements(encClassUnit, "encrypt"));
		assertEquals(11, TestUtils.countStatements(encClassUnit, "decrypt"));
		TestUtils.deleteProject(testJavaProject.getProject());
	}

	@Test
	public void testCodeGenerationEncryptionHybrid() {
		String template = "encryptionhybrid";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_HybridENC");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
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

	@Test
	public void testCodeGenerationEncryptionFiles() {		
		String template = "encryptionfiles";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENCFILES");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(12, TestUtils.countStatements(encClassUnit, "generateKey"));
		assertEquals(15, TestUtils.countStatements(encClassUnit, "encrypt"));
		assertEquals(13, TestUtils.countStatements(encClassUnit, "decrypt"));
		TestUtils.deleteProject(testJavaProject.getProject());
	}

	@Test
	public void testCodeGenerationEncryptionHybridFiles() {	
		String template = "encryptionhybridfiles";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_HybridENCFILES");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
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

	@Test
	public void testCodeGenerationEncryptionStrings() {		
		String template = "encryptionstrings";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENCSTRINGS");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(12, TestUtils.countStatements(encClassUnit, "generateKey"));
		assertEquals(14, TestUtils.countStatements(encClassUnit, "encrypt"));
		assertEquals(12, TestUtils.countStatements(encClassUnit, "decrypt"));
		TestUtils.deleteProject(testJavaProject.getProject());
	}
	
	@Test
	public void testCodeGenerationEncryptionHybridStrings() {		
		String template = "encryptionhybridstrings";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_HybridENCSTRINGS");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
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

	@Test
	public void testCodeGenerationSecretKeyEncryption() {
		String template = "secretkeyencryption";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_SYMENC");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject, EncTask);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(4, TestUtils.countStatements(encClassUnit, "generateSessionKey"));
		assertEquals(13, TestUtils.countStatements(encClassUnit, "encrypt"));
		assertEquals(11, TestUtils.countStatements(encClassUnit, "decrypt"));
		TestUtils.deleteProject(testJavaProject.getProject());
	}
	
}