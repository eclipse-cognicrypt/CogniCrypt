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
public class DigitalSignaturesCodeGenTest {
	
	@Test
	public void testCodeGenerationDigSignatures() {
		String template = "digitalsignatures";
//		String taskName = "DigitalSignatures";
		IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_DigSign");
		IResource targetFile = TestUtils.generateJavaClassInJavaProject(testJavaProject, CodeGenTestConstants.PACKAGE_NAME, CodeGenTestConstants.CLASS_NAME);
		CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(targetFile);
		DeveloperProject developerProject = codeGenerator.getDeveloperProject();
		CrySLConfiguration chosenConfig = TestUtils.createCrySLConfiguration(template, targetFile, codeGenerator, developerProject);

		boolean encCheck = codeGenerator.generateCodeTemplates(chosenConfig, "");
		assertTrue(encCheck);

		ICompilationUnit testClassUnit = JavaCore.createCompilationUnitFrom((IFile) targetFile);
		TestUtils.openJavaFileInWorkspace(developerProject, CodeGenTestConstants.PACKAGE_NAME, testClassUnit);
		assertEquals(1, TestUtils.countMethods(testClassUnit));

		ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageNameAsName, "SecureSigner.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encClassUnit);
		assertEquals(3, TestUtils.countMethods(encClassUnit));
		assertEquals(5, TestUtils.countStatements(encClassUnit, "getKey"));
		assertEquals(8, TestUtils.countStatements(encClassUnit, "sign"));
		// assertEquals(14, TestUtils.countStatements(encClassUnit, "vfy"));
		TestUtils.deleteProject(testJavaProject.getProject());
	}
	
}
