package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;

/**
 * @author Enri Ozuni
 */
public class DefaultTasksGeneratorTest {

	/**
	 * In the following tests we check for the right number of methods in the appropriate classes. We choose this approach, because a comparing of the source code/bytes leads to
	 * problems when some changes happen in the XSLTemplate.
	 */

	Logger log = Logger.getLogger(DefaultTasksGeneratorTest.class.getName());
	IJavaProject testJavaProject;
	CodeGenerator generatorEnc;
	CodeGenerator generatorSecPassword;
	CodeGenerator generatorLTA;
	CodeGenerator generatorSecMPComp;
	CodeGenerator generatorHybridEnc;
	CodeGenerator generatorDigitalSIgn;
	Task encTask;
	Task secPasswordTask;
	Task LTATask;
	Task secMPCompTask;
	Task hybridEncTask;
	Task digitalSignTask;
	Configuration configEnc;
	Configuration configSecPassword;
	Configuration configLTA;
	Configuration configSecMPComp;
	Configuration configHybridEnc;
	Configuration configDigitalSign;
	DeveloperProject developerProject;
	static int counter = 0;

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		DefaultTasksGeneratorTest.counter++;
		this.testJavaProject = TestUtils.createJavaProject("TestProject_" + counter);
		TestUtils.generateJavaClassInJavaProject(this.testJavaProject, "testPackage", "Test");

		this.encTask = TestUtils.getTask("SymmetricEncryption");
		this.generatorEnc = new XSLBasedGenerator(this.testJavaProject.getProject(), this.encTask.getXslFile());

		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secPasswordTask.getXslFile());

		this.LTATask = TestUtils.getTask("LongTermArchiving");
		this.generatorLTA = new XSLBasedGenerator(this.testJavaProject.getProject(), this.LTATask.getXslFile());

		this.secMPCompTask = TestUtils.getTask("SECMUPACOMP");
		this.generatorSecMPComp = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secMPCompTask.getXslFile());

		this.hybridEncTask = TestUtils.getTask("HybridEncryption");
		this.generatorHybridEnc = new XSLBasedGenerator(this.testJavaProject.getProject(), this.hybridEncTask.getXslFile());

		this.digitalSignTask = TestUtils.getTask("DigitalSignatures");
		this.generatorDigitalSIgn = new XSLBasedGenerator(this.testJavaProject.getProject(), this.digitalSignTask.getXslFile());

		this.developerProject = this.generatorEnc.getDeveloperProject();
	}

	/**
	 * Test if the code generation for all CogniCrypt tasks works, without any open class.
	 */
	@Test
	public void EncDefault() {
		this.configEnc = TestUtils.createConfigurationForCodeGeneration(this.developerProject, this.encTask);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc, this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void SecPasswordDefault() {
		this.configSecPassword = TestUtils.createConfigurationForCodeGeneration(this.developerProject, this.secPasswordTask);
		final boolean secPasswordCheck = this.generatorSecPassword.generateCodeTemplates(this.configSecPassword, this.secPasswordTask.getAdditionalResources());
		assertTrue(secPasswordCheck);
	}

	/**
	 * This test case is commented because it requires UI interaction
	 */
	// @Test
	// public void LTADefault() {
	// this.configLTA = TestUtils.createConfigurationForCodeGeneration(developerProject, LTATask);
	// boolean ltaCheck = generatorLTA.generateCodeTemplates(configLTA, LTATask.getAdditionalResources());
	// assertTrue(ltaCheck);
	// }

	@Test
	public void SECMUPACOMPDefault() {
		this.configSecMPComp = TestUtils.createConfigurationForCodeGeneration(this.developerProject, this.secMPCompTask);
		final boolean secMPCompCheck = this.generatorSecMPComp.generateCodeTemplates(this.configSecMPComp, this.secMPCompTask.getAdditionalResources());
		assertTrue(secMPCompCheck);
	}

	@Test
	public void HybridEncryptionDefault() {
		this.configHybridEnc = TestUtils.createConfigurationForCodeGeneration(this.developerProject, this.hybridEncTask);
		final boolean hybridEncryptionCheck = this.generatorHybridEnc.generateCodeTemplates(this.configHybridEnc, this.hybridEncTask.getAdditionalResources());
		assertTrue(hybridEncryptionCheck);
	}

	/**
	 * This test case is commented because it requires UI interaction
	 */
	// @Test
	// public void DigitalSignDefault() {
	// this.configDigitalSign = TestUtils.createConfigurationForCodeGeneration(developerProject, digitalSignTask);
	// boolean digitalSignCheck = generatorDigitalSIgn.generateCodeTemplates(configDigitalSign, digitalSignTask.getAdditionalResources());
	// assertTrue(digitalSignCheck);
	// }

}
