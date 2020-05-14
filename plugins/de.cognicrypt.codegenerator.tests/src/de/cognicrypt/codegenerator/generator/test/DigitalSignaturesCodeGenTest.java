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
import de.cognicrypt.codegenerator.generator.test.Constants;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Shahrzad Asghari
 */
public class DigitalSignaturesCodeGenTest {
	private Logger log = Logger.getLogger(DigitalSignaturesCodeGenTest.class.getName());
	private IJavaProject testJavaProject;
	private CodeGenerator generatorDigSignature;
	private Task digSignatureTask;
	private Configuration configDigSignature;
	private DeveloperProject developerProject;
	private IResource targetFile;

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		this.testJavaProject = TestUtils.createJavaProject(Constants.PROJECT_NAME);
		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, Constants.PACKAGE_NAME,
				Constants.CLASS_NAME);
		this.digSignatureTask = TestUtils.getTask("DigitalSignatures");
		this.generatorDigSignature = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorDigSignature.getDeveloperProject();
	}

	@Test
	public void testCodeGenerationDigSignatures() throws CoreException, IOException {
		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject,
				Constants.PACKAGE_NAME, Constants.JAVA_CLASS_NAME);
		TestUtils.openJavaFileInWorkspace(this.developerProject, Constants.PACKAGE_NAME, testClassUnit);

		this.configDigSignature = TestUtils.createCrySLConfiguration("digitalsignatures", testClassUnit.getResource(),
				generatorDigSignature, this.developerProject);
		final boolean encCheck = this.generatorDigSignature.generateCodeTemplates(this.configDigSignature,
				this.digSignatureTask.getAdditionalResources());
		assertTrue(encCheck);
	}
}
