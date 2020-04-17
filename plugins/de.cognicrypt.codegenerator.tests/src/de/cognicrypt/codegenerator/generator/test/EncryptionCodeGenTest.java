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
public class EncryptionCodeGenTest {

	private Logger log = Logger.getLogger(EncryptionCodeGenTest.class.getName());
	private IJavaProject testJavaProject;
	private CodeGenerator generatorEnc;
	private Task encTask;
	private Configuration configEnc;
	private DeveloperProject developerProject;
	private IResource targetFile;
	private ICompilationUnit testClassUnit;

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		this.testJavaProject = TestUtils.createJavaProject(Constants.PROJECT_NAME);
		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, Constants.PACKAGE_NAME,
				Constants.CLASS_NAME);
		this.encTask = TestUtils.getTask("Encryption");
		this.generatorEnc = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorEnc.getDeveloperProject();
		this.testClassUnit = TestUtils.getICompilationUnit(this.developerProject, Constants.PACKAGE_NAME,
				Constants.JAVA_CLASS_NAME);
		TestUtils.openJavaFileInWorkspace(this.developerProject, "testPackage", this.testClassUnit);

	}

	@Test
	public void testCodeGenerationEncryption() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("encryption", testClassUnit.getResource(), generatorEnc,
				this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void testCodeGenerationEncryptionHybrid() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("encryptionhybrid", testClassUnit.getResource(),
				generatorEnc, this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void testCodeGenerationEncryptionFiles() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("encryptionfiles", testClassUnit.getResource(),
				generatorEnc, this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void testCodeGenerationEncryptionHybridFiles() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("encryptionhybridfiles", testClassUnit.getResource(),
				generatorEnc, this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void testCodeGenerationEncryptionHybridStrings() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("encryptionhybridstrings", testClassUnit.getResource(),
				generatorEnc, this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void testCodeGenerationEncryptionStrings() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("encryptionstrings", testClassUnit.getResource(),
				generatorEnc, this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void testCodeGenerationSecretKeyEncryption() throws CoreException, IOException {
		this.configEnc = TestUtils.createCrySLConfiguration("secretkeyencryption", testClassUnit.getResource(),
				generatorEnc, this.developerProject);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc,
				this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}
}