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
public class UserAuthManagerCodeGenTest {
	private Logger log = Logger.getLogger(UserAuthManagerCodeGenTest.class.getName());
	private IJavaProject testJavaProject;
	private CodeGenerator generatorAuthManager;
	private Task authManagerTask;
	private Configuration configAuthManager;
	private DeveloperProject developerProject;
	private IResource targetFile;
	private ICompilationUnit testClassUnit;
	private String taskName = "UserAuthorityManager";

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		this.testJavaProject = TestUtils.createJavaProject(CodeGenTestConstants.PROJECT_NAME);
		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, CodeGenTestConstants.PACKAGE_NAME,
				CodeGenTestConstants.CLASS_NAME);
		this.authManagerTask = TestUtils.getTask("UserAuthorityManager");
		this.generatorAuthManager = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorAuthManager.getDeveloperProject();
		this.testClassUnit = TestUtils.getICompilationUnit(this.developerProject, CodeGenTestConstants.PACKAGE_NAME,
				CodeGenTestConstants.JAVA_CLASS_NAME);
		TestUtils.openJavaFileInWorkspace(this.developerProject, "testPackage", this.testClassUnit);

	}

	/**
	 * Scenario: User chooses User Authentication manager task and on the first pages
	 * chooses the second answer to generate user authentication service.
	 *
	 * @throws CoreException.
	 * @throws IOException this exception happens if an I/O error appears while creating and copying a file in createCrySLConfiguration.
	 * @throws CoreException this exceptions happens when the project does not exist or is not open.
	 */
	@Test
	public void testCodeGenerationUserAuthentication() throws IOException, CoreException {
		this.configAuthManager = TestUtils.createCrySLConfiguration("userauthoritymanagerauth", testClassUnit.getResource(),
				generatorAuthManager, this.developerProject, taskName);
		final boolean encCheck = this.generatorAuthManager.generateCodeTemplates(this.configAuthManager,
				this.authManagerTask.getAdditionalResources());

		assertTrue(encCheck);
	}

	/**
	 * Scenario: User chooses User Authentication manager task and on the first pages
	 * chooses the first answer to generate a password generator service.
	 *
	 * @throws CoreException this exceptions happens when the project does not exist or is not open.
	 * @throws IOException this exception happens if an I/O error appears while creating and copying a file in createCrySLConfiguration.
	 */
	@Test
	public void testCodeGenerationPassGenerator() throws CoreException, IOException {
		this.configAuthManager = TestUtils.createCrySLConfiguration("userauthoritymanagerpassgen", testClassUnit.getResource(),
				generatorAuthManager, this.developerProject, taskName);
		final boolean encCheck = this.generatorAuthManager.generateCodeTemplates(this.configAuthManager,
				this.authManagerTask.getAdditionalResources());

		assertTrue(encCheck);
	}
}