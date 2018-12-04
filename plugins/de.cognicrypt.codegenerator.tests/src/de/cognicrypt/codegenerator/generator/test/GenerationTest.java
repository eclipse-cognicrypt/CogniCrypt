package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.core.Constants;

/**
 * @author André Sonntag
 */
public class GenerationTest {

	/**
	 * In the following tests we check for the right number of methods in the
	 * appropriate classes. We choose this approach, because a comparing of the
	 * source code/bytes leads to problems when some changes happen in the
	 * XSLTemplate.
	 */

	Logger log = Logger.getLogger(GenerationTest.class.getName());
	IJavaProject testJavaProject;
	CodeGenerator generatorEnc;
	CodeGenerator generatorSecPassword;
	Task encTask;
	Task secPasswordTask;
	Configuration configEnc;
	Configuration configSecPassword;
	DeveloperProject developerProject;
	static int counter = 0;

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		GenerationTest.counter++;
		this.testJavaProject = TestUtils.createJavaProject("TestProject_" + counter);
		TestUtils.generateJavaClassInJavaProject(this.testJavaProject, "testPackage", "Test");
		this.encTask = TestUtils.getTask("SymmetricEncryption");
		this.generatorEnc = new XSLBasedGenerator(this.testJavaProject.getProject(), this.encTask.getXslFile());
		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new XSLBasedGenerator(this.testJavaProject.getProject(),
				this.secPasswordTask.getXslFile());
		this.developerProject = this.generatorEnc.getDeveloperProject();
	}

	/**
	 * Test if the codegeneration for SymmetricEncrytion works, without any open
	 * class.
	 */
	@Test
	public void testCodeGeneration() {
		this.configEnc = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.encTask);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	/**
	 * Test if the codegeneration for SymmetricEncrytion works with an open Test
	 * class.
	 */
	@Test
	public void testCodeGenerationInTestClass() throws CoreException, IOException {

		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject, "testPackage",
				"Test.java");
		TestUtils.openJavaFileInWorkspace(this.developerProject, "testPackage", testClassUnit);

		this.configEnc = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.encTask);
		this.generatorEnc.generateCodeTemplates(this.configEnc, this.encTask.getAdditionalResources());
		assertEquals(1, countMethods(testClassUnit));
	}

	/**
	 * Test if the Output class has the right methods, after the codegeneration runs
	 * two times (different tasks), without any open class.
	 */
	@Test
	public void testCodeGenerationTwoTimesNoClassOpen() throws CoreException, IOException {

		this.configEnc = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.encTask);
		this.generatorEnc.generateCodeTemplates(this.configEnc, this.encTask.getAdditionalResources());

		this.configSecPassword = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject,
				this.secPasswordTask);
		this.generatorSecPassword.generateCodeTemplates(this.configSecPassword,
				this.secPasswordTask.getAdditionalResources());

		final ICompilationUnit outputUnit = TestUtils.getICompilationUnit(this.developerProject, Constants.PackageName,
				"Output.java");
		assertEquals(2, countMethods(outputUnit));
	}

	/**
	 * Test if the codegeneration puts the templageUsage-method in the open Enc
	 * class.
	 */
//	@Test
	public void testCodeGenerationInEncClass() throws CoreException, IOException {
		this.configEnc = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.encTask);
		this.generatorEnc.generateCodeTemplates(this.configEnc, this.encTask.getAdditionalResources());
		final ICompilationUnit encUnit = TestUtils.getICompilationUnit(this.developerProject, Constants.PackageName,
				"Enc.java");
		TestUtils.openJavaFileInWorkspace(this.developerProject, Constants.PackageName, encUnit);

		this.configSecPassword = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject,
				this.secPasswordTask);
		this.generatorSecPassword.generateCodeTemplates(this.configSecPassword,
				this.secPasswordTask.getAdditionalResources());

		final ICompilationUnit outputUnit = TestUtils.getICompilationUnit(this.developerProject, Constants.PackageName,
				"Output.java");
		assertEquals(2, countMethods(outputUnit));
	}

	/**
	 * This method counts methods in ICompilationUnits
	 *
	 * @param unit
	 * @return
	 * @throws JavaModelException
	 */
	private int countMethods(final ICompilationUnit unit) throws JavaModelException {
		return unit.getAllTypes()[0].getMethods().length;
	}
}
