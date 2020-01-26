package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Shahrzad Asghari
 */
public class DigitalSignaturesCodeGenTest {
	Logger log = Logger.getLogger(DigitalSignaturesCodeGenTest.class.getName());
	IJavaProject testJavaProject;
	CodeGenerator generatorDigSigniture;
	Task digSignitureTask;
	Configuration configDigSigniture;
	DeveloperProject developerProject;
	IResource targetFile;
	
	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		this.testJavaProject = TestUtils.createJavaProject("TestProject");
		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, "testPackage", "Test");
		this.digSignitureTask = TestUtils.getTask("DigitalSignatures");
		this.generatorDigSigniture = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorDigSigniture.getDeveloperProject();
	}
	@Test
	public void testCodeGenerationEncryption() throws CoreException, IOException {
		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject, "testPackage", "Test.java");
		TestUtils.openJavaFileInWorkspace(this.developerProject, "testPackage", testClassUnit);

		this.configDigSigniture = TestUtils.createCrySLConfiguration("digitalsignatures", testClassUnit.getResource(), generatorDigSigniture, this.developerProject);
		final boolean encCheck = this.generatorDigSigniture.generateCodeTemplates(this.configDigSigniture, this.digSignitureTask.getAdditionalResources());
		assertTrue(encCheck);
		
	}
}
