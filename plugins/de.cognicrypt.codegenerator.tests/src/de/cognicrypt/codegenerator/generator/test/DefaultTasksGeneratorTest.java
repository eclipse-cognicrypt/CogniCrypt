package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
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
 * @author Enri Ozuni
 */
public class DefaultTasksGeneratorTest {

	/**
	 * In the following tests we check for the right number of methods 
	 * in the appropriate classes. We choose this approach, because a
	 * comparing of the source code/bytes leads to problems when some 
	 * changes happen in the XSLTemplate.
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
		TestUtils.deleteProject(testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		DefaultTasksGeneratorTest.counter++;
		this.testJavaProject = TestUtils.createJavaProject("TestProject_"+counter);
		TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
		
		this.encTask = TestUtils.getTask("SymmetricEncryption");
		this.generatorEnc = new XSLBasedGenerator(testJavaProject.getProject(), encTask.getXslFile());
		
		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new XSLBasedGenerator(testJavaProject.getProject(), secPasswordTask.getXslFile());
		
		this.LTATask = TestUtils.getTask("LongTermArchiving");
		this.generatorLTA = new XSLBasedGenerator(testJavaProject.getProject(), LTATask.getXslFile());
		
		this.secMPCompTask = TestUtils.getTask("SECMUPACOMP");
		this.generatorSecMPComp = new XSLBasedGenerator(testJavaProject.getProject(), secMPCompTask.getXslFile());
		
		this.hybridEncTask = TestUtils.getTask("HybridEncryption");
		this.generatorHybridEnc = new XSLBasedGenerator(testJavaProject.getProject(), hybridEncTask.getXslFile());
		
		this.digitalSignTask = TestUtils.getTask("DigitalSignatures");
		this.generatorDigitalSIgn = new XSLBasedGenerator(testJavaProject.getProject(), digitalSignTask.getXslFile());
		
		this.developerProject = generatorEnc.getDeveloperProject();
	}

	/**
	 * Test if the code generation for all CogniCrypt tasks works, without any open
	 * class.
	 */
	@Test
	public void EncDefault() {
		this.configEnc = TestUtils.createXSLConfigurationForCodeGeneration(developerProject, encTask);
		boolean encCheck = generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
		assertTrue(encCheck);
	}
	
	@Test
	public void SecPasswordDefault() {
		this.configSecPassword = TestUtils.createXSLConfigurationForCodeGeneration(developerProject, secPasswordTask);
		boolean secPasswordCheck = generatorSecPassword.generateCodeTemplates(configSecPassword, secPasswordTask.getAdditionalResources());
		assertTrue(secPasswordCheck);
	}
	
	/**
	 * This test case is commented because it requires UI interaction
	 */
//	@Test
//	public void LTADefault() {
//		this.configLTA = TestUtils.createConfigurationForCodeGeneration(developerProject, LTATask);
//		boolean ltaCheck = generatorLTA.generateCodeTemplates(configLTA, LTATask.getAdditionalResources());
//		assertTrue(ltaCheck);
//	}
	
	@Test
	public void SECMUPACOMPDefault() {
		this.configSecMPComp = TestUtils.createXSLConfigurationForCodeGeneration(developerProject, secMPCompTask);
		boolean secMPCompCheck = generatorSecMPComp.generateCodeTemplates(configSecMPComp, secMPCompTask.getAdditionalResources());
		assertTrue(secMPCompCheck);
	}
	
	@Test
	public void HybridEncryptionDefault() {
		this.configHybridEnc = TestUtils.createXSLConfigurationForCodeGeneration(developerProject, hybridEncTask);
		boolean hybridEncryptionCheck = generatorHybridEnc.generateCodeTemplates(configHybridEnc, hybridEncTask.getAdditionalResources());
		assertTrue(hybridEncryptionCheck);
	}
	
	/**
	 * This test case is commented because it requires UI interaction
	 */
//	@Test
//	public void DigitalSignDefault() {
//		this.configDigitalSign = TestUtils.createConfigurationForCodeGeneration(developerProject, digitalSignTask);
//		boolean digitalSignCheck = generatorDigitalSIgn.generateCodeTemplates(configDigitalSign, digitalSignTask.getAdditionalResources());
//		assertTrue(digitalSignCheck);
//	}

}
