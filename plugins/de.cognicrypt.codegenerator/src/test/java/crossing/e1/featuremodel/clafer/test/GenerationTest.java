package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.FileHelper;
import de.cognicrypt.codegenerator.utilities.Utils;
import de.cognicrypt.codegenerator.wizard.Configuration;

public class GenerationTest {

	Logger log = Logger.getLogger(GenerationTest.class.getName());
	IJavaProject testJavaProject;
	CodeGenerator generatorEnc;
	CodeGenerator generatorSecPassword;
	Task encTask;
	Task secPasswordTask;
	Configuration configEnc;
	Configuration configSecPassword;
	DeveloperProject developerProject;

	String testEncClass = "package testPackage;\r\n\r\nimport java.security.GeneralSecurityException;\r\n\r\nimport javax.crypto.SecretKey;\r\n\r\nimport Crypto.Enc;\r\nimport Crypto.KeyDeriv;\r\n\r\npublic class Test {\r\n\tpublic byte[] templateUsage(byte[] data, char[] pwd) throws GeneralSecurityException {\r\n\t\tKeyDeriv kd = new KeyDeriv();\r\n\t\tSecretKey key = kd.getKey(pwd);\r\n\t\tEnc enc = new Enc();\r\n\r\n\t\tbyte[] ciphertext = enc.encrypt(data, key);\r\n\t\treturn ciphertext;\r\n\r\n\t}\r\n}\r\n";
	String outputClassTwoTemplates = "package Crypto;\r\n\r\nimport java.security.GeneralSecurityException;\r\n\r\nimport javax.crypto.SecretKey;\r\n\r\npublic class Output {\r\n\tpublic byte[] templateUsage(byte[] data, char[] pwd) throws GeneralSecurityException {\r\n\t\tKeyDeriv kd = new KeyDeriv();\r\n\t\tSecretKey key = kd.getKey(pwd);\r\n\t\tEnc enc = new Enc();\r\n\r\n\t\tbyte[] ciphertext = enc.encrypt(data, key);\r\n\t\treturn ciphertext;\r\n\r\n\t}\r\n\r\n\tpublic void templateUsage(char[] pwd) throws GeneralSecurityException {\r\n\t\tPWHasher pwHasher = new PWHasher();\r\n\t\tString pwdHash = pwHasher.createPWHash(pwd);\r\n\t\tBoolean t = pwHasher.verifyPWHash(pwd, pwdHash);\r\n\t}\r\n}";	
	String encClass = "\npackage Crypto;\n\nimport java.security.GeneralSecurityException;\nimport java.security.SecureRandom;\n\nimport javax.crypto.Cipher;\nimport javax.crypto.SecretKey;\nimport javax.crypto.spec.IvParameterSpec;\n\n/** @author CogniCrypt */\npublic class Enc {\n\n\tpublic byte[] encrypt(byte[] data, SecretKey key) throws GeneralSecurityException {\n\n\t\tbyte[] ivb = new byte[16];\n\t\tSecureRandom.getInstanceStrong().nextBytes(ivb);\n\t\tIvParameterSpec iv = new IvParameterSpec(ivb);\n\n\t\tCipher c = Cipher.getInstance(\"AES/CFB/PKCS5Padding\");\n\t\tc.init(Cipher.ENCRYPT_MODE, key, iv);\n\n\t\tbyte[] res = c.doFinal(data);\n\n\t\tbyte[] ret = new byte[res.length + ivb.length];\n\t\tSystem.arraycopy(ivb, 0, ret, 0, ivb.length);\n\t\tSystem.arraycopy(res, 0, ret, ivb.length, res.length);\n\n\t\treturn ret;\n\n\t}\n\n\tpublic byte[] decrypt(byte[] ciphertext, SecretKey key) throws GeneralSecurityException {\n\n\t\tbyte[] ivb = new byte[16];\n\t\tSystem.arraycopy(ciphertext, 0, ivb, 0, ivb.length);\n\t\tIvParameterSpec iv = new IvParameterSpec(ivb);\n\t\tbyte[] data = new byte[ciphertext.length - ivb.length];\n\t\tSystem.arraycopy(ciphertext, ivb.length, data, 0, data.length);\n\n\t\tCipher c = Cipher.getInstance(\"AES/CFB/PKCS5Padding\");\n\t\tc.init(Cipher.DECRYPT_MODE, key, iv);\n\n\t\tbyte[] res = c.doFinal(data);\n\n\t\treturn res;\n\n\t}\n}\n";
		
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
	 * Test if the codegeneration for SymmetricEncrytion works, without any open class.
	 */
	@Test
	public void testCodeGeneration() {
			this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
			boolean encCheck = generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
			assertTrue(encCheck);
	}

	/**
	 * Test if the codegeneration for SymmetricEncrytion works with an open Test class.
	 */
	@Test
	public void testCodeGenerationInTestClass() throws CoreException, IOException {
			ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(developerProject, "testPackage", "Test.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "testPackage", testClassUnit);
			this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
			generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
			assertArrayEquals(testEncClass.getBytes(), TestUtils.fileToByteArray(developerProject, "testPackage", testClassUnit));
	}
	

	
	
	/**
	 * Test if the Output class has the right methods, after the codegeneration runs two times (different tasks), without any open class.
	 */
	@Test
	public void TestCodeGenerationTwoTimesNoClassOpen() throws CoreException, IOException {

			this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
			generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
			this.configSecPassword = TestUtils.createConfigurationForCodeGeneration(developerProject,secPasswordTask);
			generatorSecPassword.generateCodeTemplates(configSecPassword, secPasswordTask.getAdditionalResources());
			ICompilationUnit outputUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageName, "Output.java");
			assertArrayEquals(outputClassTwoTemplates.getBytes(), TestUtils.fileToByteArray(developerProject, Constants.PackageName, outputUnit));
	}
	
	/**
	 * Test if the codegeneration puts the templageUsage-method in the open Enc class.
	 */
	@Test
	public void TestCodeGenerationInEncClass() throws CoreException, IOException {
			
			this.configEnc = TestUtils.createConfigurationForCodeGeneration(developerProject, encTask);
			generatorEnc.generateCodeTemplates(configEnc, encTask.getAdditionalResources());
			ICompilationUnit encUnit = TestUtils.getICompilationUnit(developerProject, Constants.PackageName, "Enc.java");
			TestUtils.openJavaFileInWorkspace(developerProject, Constants.PackageName, encUnit);
			
			this.configSecPassword = TestUtils.createConfigurationForCodeGeneration(developerProject,secPasswordTask);
			generatorSecPassword.generateCodeTemplates(configSecPassword, secPasswordTask.getAdditionalResources());
			assertArrayEquals(encClass.getBytes(), TestUtils.fileToByteArray(developerProject, Constants.PackageName, encUnit));
	}
		
	
}
