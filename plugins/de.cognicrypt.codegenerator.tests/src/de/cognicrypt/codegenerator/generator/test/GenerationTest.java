package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.progress.WorkbenchJob;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;


public class GenerationTest {

	/**
	 * In the following tests we check for the right number of methods 
	 * in the appropriate classes. We choose this approach, because a
	 * comparing of the source code/bytes lead to problems when some 
	 * changes happen in the XSLTemplate.
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

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteJavaProject(testJavaProject);
	}

	@Before
	public void setUp() throws Exception {

		this.testJavaProject = TestUtils.createJavaProject("TestProject");
		TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
		this.encTask = TestUtils.getTask("SymmetricEncryption");
		this.generatorEnc = new XSLBasedGenerator(testJavaProject.getProject(), encTask.getXslFile());
		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new XSLBasedGenerator(testJavaProject.getProject(), secPasswordTask.getXslFile());
		this.developerProject = generatorEnc.getDeveloperProject();
	}

	/**
	 * Test if the codegeneration for SymmetricEncrytion works, without any open
	 * class.
	 */
	@Test
	public void testCodeGeneration() {
		this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
		boolean encCheck = generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	/**
	 * Test if the codegeneration for SymmetricEncrytion works with an open Test
	 * class.
	 */
	@Test
	public void testCodeGenerationInTestClass() throws CoreException, IOException {

		ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(developerProject, "testPackage", "Test.java");
		TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
		
		this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
		generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
	    assertEquals(1, countMethods(testClassUnit));
	}

	/**
	 * Test if the Output class has the right methods, after the codegeneration runs
	 * two times (different tasks), without any open class.
	 */
	@Test
	public void testCodeGenerationTwoTimesNoClassOpen() throws CoreException, IOException {

		this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
		generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
		
		this.configSecPassword = TestUtils.createConfigurationForCodeGeneration(developerProject, secPasswordTask);
		generatorSecPassword.generateCodeTemplates(configSecPassword, secPasswordTask.getAdditionalResources());
		
		ICompilationUnit outputUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageName,"Output.java");
	    assertEquals(2, countMethods(outputUnit));
	}

	/**
	 * Test if the codegeneration puts the templageUsage-method in the open Enc
	 * class.
	 */
	@Test
	public void testCodeGenerationInEncClass() throws CoreException, IOException {

		this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
		generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
		ICompilationUnit encUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageName, "Enc.java");
		TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encUnit);
		
		this.configSecPassword = TestUtils.createConfigurationForCodeGeneration(developerProject, secPasswordTask);
		generatorSecPassword.generateCodeTemplates(configSecPassword, secPasswordTask.getAdditionalResources());
		
		ICompilationUnit outputUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageName,"Output.java");
	    assertEquals(2, countMethods(outputUnit));
	}
	
	/**
	 * This method counts methods in ICompilationUnits
	 * 
	 * @param unit
	 * @return
	 * @throws JavaModelException
	 */
	private int countMethods(ICompilationUnit unit) throws JavaModelException {
		return unit.getAllTypes()[0].getMethods().length;
	}

}
