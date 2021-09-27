package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.junit.Test;

import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.utils.DeveloperProject;

public class CodeGenLocationSelectionTest {
//	/**
//	 * Scenario: user doesn't select a specific class or package.
//	 * Expected behavior: CC generates its own package with the necessary classes
//	 * @throws Exception
//	 */
	@Test
	public void noSpecificSelection() throws Exception {
		// task
		String template = "secretkeyencryption";
		// create Java project without any package or class
		IJavaProject generatedProject = TestUtils.createJavaProject("TestProject_SYMENC");
		// setup for code generation
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(generatedProject.getResource());
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, generatedProject.getResource(),
				codeGenerator, developerProject);
		// run code generation
		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");

		assertTrue(encCheck); // check if code generation is successful
		assertTrue(TestUtils.packageExists(generatedProject, Constants.PackageNameAsName)); // check if package is
																							// created
		ICompilationUnit encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"SecureEncryptor.java");
		assertNotNull(encClass); // check if SecureEncryptor.java is created
		ICompilationUnit outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"Output.java");
		assertNotNull(outputClass); // check if Output.java is created
		TestUtils.deleteProject(generatedProject.getProject());
	}

	/**
	 * Scenario: user selects just a package. Expected behavior: CC doesn't
	 * generates its own package just the necessary classes in the user selected
	 * package
	 * 
	 * @throws Exception
	 */
	@Test
	public void packageSelection() throws Exception {
		// task
		String template = "secretkeyencryption";

		// package name
		String packageName = "de.test.test";

		// create Java project with package
		IJavaProject generatedProject = TestUtils.createJavaProject("TestProject_SYMENC");
		IPackageFragment generatedPackage = TestUtils.generatePackageInJavaProject(generatedProject, packageName);

		// setup for code generation
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(generatedPackage.getResource());
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, generatedPackage.getResource(),
				codeGenerator, developerProject);

		// run code generation
		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");

		assertTrue(encCheck); // check if code generation is successful
		assertTrue(TestUtils.packageExists(generatedProject, Constants.PackageNameAsName)); // check if package is
																							// created

		ICompilationUnit encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"SecureEncryptor.java");
		assertNotNull(encClass); // check if SecureEncryptor.java is created

		ICompilationUnit outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"Output.java");
		assertNotNull(outputClass); // check if Output.java is created
		TestUtils.deleteProject(generatedProject.getProject());
	}

	/**
	 * Case three: user selects one of his own classes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void ownClassSelection() throws Exception {
		// task
		String template = "secretkeyencryption";

		// create java project with a test class
		IJavaProject generatedProject = TestUtils.createJavaProject(Constants.PROJECT_NAME);
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(generatedProject, Constants.PACKAGE_NAME,
				Constants.CLASS_NAME);
		// setup for code generation
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator,
				developerProject);
		// run code generation
		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");

		assertTrue(encCheck); // check if code generation is successful
		assertTrue(TestUtils.packageExists(generatedProject, Constants.PackageNameAsName)); // check if package is
																							// created

		ICompilationUnit encClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"SecureEncryptor.java");
		assertNotNull(encClass); // check if SecureEncryptor.java is created
		ICompilationUnit outputClass = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName,
				"Output.java");
		assertNull(outputClass); // check if Output.java is not created
		TestUtils.deleteProject(generatedProject.getProject());
	}
}
