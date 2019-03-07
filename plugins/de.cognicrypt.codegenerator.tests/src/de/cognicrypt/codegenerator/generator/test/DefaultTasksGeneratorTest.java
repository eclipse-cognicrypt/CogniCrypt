package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.cognicrypt.codegenerator.DeveloperProject;
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
	CodeGenerator generatorSECCOM;
	CodeGenerator generatorSecMPComp;
	CodeGenerator generatorDigitalSIgn;
	Task encTask;
	Task secPasswordTask;
	Task SECCOMTask;
	Task secMPCompTask;
	Task digitalSignTask;
	Configuration configEnc;
	Configuration configSecPassword;
	Configuration configSecCom;
	Configuration configSecMPComp;
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

		this.encTask = TestUtils.getTask("Encryption");
		this.generatorEnc = new XSLBasedGenerator(this.testJavaProject.getProject(), this.encTask.getXslFile());

		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secPasswordTask.getXslFile());

		this.SECCOMTask = TestUtils.getTask("SecureCommunication");
		this.generatorSECCOM = new XSLBasedGenerator(this.testJavaProject.getProject(), this.SECCOMTask.getXslFile());

		this.secMPCompTask = TestUtils.getTask("SECMUPACOMP");
		this.generatorSecMPComp = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secMPCompTask.getXslFile());

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


	@Test
	public void SECMUPACOMPDefault() {
		this.configSecMPComp = TestUtils.createConfigurationForCodeGeneration(this.developerProject, this.secMPCompTask);
		final boolean secMPCompCheck = this.generatorSecMPComp.generateCodeTemplates(this.configSecMPComp, this.secMPCompTask.getAdditionalResources());
		assertTrue(secMPCompCheck);
	}

	@Test
	public void SECComDefault() {
		this.configSecCom = TestUtils.createConfigurationForCodeGeneration(this.developerProject, this.SECCOMTask);
		final boolean secComCheck = this.generatorSECCOM.generateCodeTemplates(this.configSecCom, this.SECCOMTask.getAdditionalResources());
		assertTrue(secComCheck);
	}
	

	/**
	 * This test case is commented because it requires UI interaction
	 */
	@Test
	public void DigitalSignDefault() {
		this.configDigitalSign = TestUtils.createConfigurationForCodeGeneration(developerProject, digitalSignTask);
		boolean digitalSignCheck = generatorDigitalSIgn.generateCodeTemplates(configDigitalSign, digitalSignTask.getAdditionalResources());
		assertTrue(digitalSignCheck);
	}
	
}
