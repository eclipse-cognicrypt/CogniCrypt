package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.utils.DeveloperProject;

/**
 * @author Andre Sonntag, Enri Ozuni
 */
public class DefaultTasksGeneratorTest {

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

	@Before
	public void setUp() throws Exception {

		DefaultTasksGeneratorTest.counter++;
		this.testJavaProject = TestUtils.createJavaProject("TestProject_" + counter);

		this.encTask = TestUtils.getTask("Encryption");
		this.generatorEnc = new XSLBasedGenerator(this.testJavaProject.getProject(), this.encTask.getCodeTemplate());

		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secPasswordTask.getCodeTemplate());

		this.SECCOMTask = TestUtils.getTask("SecureCommunication");
		this.generatorSECCOM = new XSLBasedGenerator(this.testJavaProject.getProject(), this.SECCOMTask.getCodeTemplate());

		this.secMPCompTask = TestUtils.getTask("SECMUPACOMP");
		this.generatorSecMPComp = new XSLBasedGenerator(this.testJavaProject.getProject(), this.secMPCompTask.getCodeTemplate());

		this.digitalSignTask = TestUtils.getTask("DigitalSignatures");
		this.generatorDigitalSIgn = new XSLBasedGenerator(this.testJavaProject.getProject(), this.digitalSignTask.getCodeTemplate());

		this.developerProject = this.generatorEnc.getDeveloperProject();
	}

	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Test
	public void EncDefault() {
		this.configEnc = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.encTask);
		final boolean encCheck = this.generatorEnc.generateCodeTemplates(this.configEnc, this.encTask.getAdditionalResources());
		assertTrue(encCheck);
	}

	@Test
	public void SecPasswordDefault() {
		this.configSecPassword = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.secPasswordTask);
		final boolean secPasswordCheck = this.generatorSecPassword.generateCodeTemplates(this.configSecPassword, this.secPasswordTask.getAdditionalResources());
		assertTrue(secPasswordCheck);
	}

	@Test
	public void SECMUPACOMPDefault() {
		this.configSecMPComp = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.secMPCompTask);
		final boolean secMPCompCheck = this.generatorSecMPComp.generateCodeTemplates(this.configSecMPComp, this.secMPCompTask.getAdditionalResources());
		assertTrue(secMPCompCheck);
	}

	@Test
	public void SECComDefault() {
		this.configSecCom = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.SECCOMTask);
		final boolean secComCheck = this.generatorSECCOM.generateCodeTemplates(this.configSecCom, this.SECCOMTask.getAdditionalResources());
		assertTrue(secComCheck);
	}

	@Test
	public void DigitalSignDefault() {
		this.configDigitalSign = TestUtils.createXSLConfigurationForCodeGeneration(this.developerProject, this.digitalSignTask);
		boolean digitalSignCheck = generatorDigitalSIgn.generateCodeTemplates(configDigitalSign, digitalSignTask.getAdditionalResources());
		assertTrue(digitalSignCheck);
	}

}
