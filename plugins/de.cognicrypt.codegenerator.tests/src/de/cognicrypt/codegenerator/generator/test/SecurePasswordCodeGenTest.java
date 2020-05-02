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
public class SecurePasswordCodeGenTest {

	private Logger log = Logger.getLogger(SecurePasswordCodeGenTest.class.getName());
	private IJavaProject testJavaProject;
	private CodeGenerator generatorSecPassword;
	private Task secPasswordTask;
	private Configuration configSecPassword;
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
		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorSecPassword.getDeveloperProject();
	}

	@Test
	public void testCodeGenerationSecurePassword() throws CoreException, IOException {
		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject,
				Constants.PACKAGE_NAME, Constants.JAVA_CLASS_NAME);
		TestUtils.openJavaFileInWorkspace(this.developerProject, Constants.PACKAGE_NAME, testClassUnit);

		this.configSecPassword = TestUtils.createCrySLConfiguration("securepassword", testClassUnit.getResource(),
				generatorSecPassword, this.developerProject);
		final boolean encCheck = this.generatorSecPassword.generateCodeTemplates(this.configSecPassword,
				this.secPasswordTask.getAdditionalResources());
		assertTrue(encCheck);
	}
}