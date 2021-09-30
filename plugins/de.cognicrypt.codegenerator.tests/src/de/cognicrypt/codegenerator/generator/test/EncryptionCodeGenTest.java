//<<<<<<< HEAD
//package de.cognicrypt.codegenerator.generator.test;
//
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//import java.util.logging.Logger;
//
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.jdt.core.ICompilationUnit;
//import org.eclipse.jdt.core.IJavaProject;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import de.cognicrypt.codegenerator.generator.CodeGenerator;
//import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
//import de.cognicrypt.codegenerator.tasks.Task;
//
//import de.cognicrypt.codegenerator.testutilities.TestUtils;
//import de.cognicrypt.codegenerator.generator.test.Constants;
//import de.cognicrypt.codegenerator.wizard.Configuration;
//import de.cognicrypt.utils.DeveloperProject;
//
///**
// * @author Shahrzad Asghari
// */
//public class EncryptionCodeGenTest {
//
//	private Logger log = Logger.getLogger(EncryptionCodeGenTest.class.getName());
//	private IJavaProject testJavaProject;
//	private CodeGenerator generatorEnc;
//	private Task encTask;
//	private Configuration configEnc;
//	private DeveloperProject developerProject;
//	private IResource targetFile;
//	private ICompilationUnit testClassUnit;
//
//	@After
//	public void tearDown() throws CoreException {
//		TestUtils.deleteProject(this.testJavaProject.getProject());
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		this.testJavaProject = TestUtils.createJavaProject(Constants.PROJECT_NAME);
//		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, Constants.PACKAGE_NAME,
//				Constants.CLASS_NAME);
//		this.encTask = TestUtils.getTask("Encryption");
//		this.generatorEnc = new CrySLBasedCodeGenerator(targetFile);
//		this.developerProject = this.generatorEnc.getDeveloperProject();
//		this.testClassUnit = TestUtils.getICompilationUnit(this.developerProject, Constants.PACKAGE_NAME,
//				Constants.JAVA_CLASS_NAME);
//		TestUtils.openJavaFileInWorkspace(this.developerProject, "testPackage", this.testClassUnit);
//
//	}
//
//	@Test
//	public void testCodeGenerationEncryption() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("encryption", testClassUnit.getResource(), generatorEnc,
//				this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//
//	@Test
//	public void testCodeGenerationEncryptionHybrid() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("encryptionhybrid", testClassUnit.getResource(),
//				generatorEnc, this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//
//	@Test
//	public void testCodeGenerationEncryptionFiles() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("encryptionfiles", testClassUnit.getResource(),
//				generatorEnc, this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//
//	@Test
//	public void testCodeGenerationEncryptionHybridFiles() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("encryptionhybridfiles", testClassUnit.getResource(),
//				generatorEnc, this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//
//	@Test
//	public void testCodeGenerationEncryptionHybridStrings() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("encryptionhybridstrings", testClassUnit.getResource(),
//				generatorEnc, this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//
//	@Test
//	public void testCodeGenerationEncryptionStrings() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("encryptionstrings", testClassUnit.getResource(),
//				generatorEnc, this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//
//	@Test
//	public void testCodeGenerationSecretKeyEncryption() throws CoreException, IOException {
//		this.configEnc = TestUtils.createCrySLConfiguration("secretkeyencryption", testClassUnit.getResource(),
//				generatorEnc, this.developerProject, "Encryption");
//		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
//				this.encTask.getAdditionalResources());
//		assertTrue(encCheck);
//	}
//=======
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
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Shahrzad Asghari
 */
public class EncryptionCodeGenTest {

	@Test
	public void testCodeGenerationEncryption() {
		String template = "encryption";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENC");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

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
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

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
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(12, TestUtils.countStatements(encClassUnit, "getKey"));
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
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

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
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureEncryptor.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(12, TestUtils.countStatements(encClassUnit, "getKey"));
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
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

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
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

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