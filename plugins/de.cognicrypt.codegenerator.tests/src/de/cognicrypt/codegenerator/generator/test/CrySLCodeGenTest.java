package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.generator.CodeGenCrySLRule;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.Utils;

public class CrySLCodeGenTest {

	@Test
	public void generatePBEEnc() {

		List<CodeGenCrySLRule> rules = new ArrayList<CodeGenCrySLRule>();
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_ENC");
			TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(testJavaProject.getProject());
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();

			List<List<String>> stringRules = new ArrayList<List<String>>();
			stringRules.add(Arrays.asList(
					new String[] { "SecureRandom", "PBEKeySpec", "SecretKeyFactory", "SecretKey", "SecretKeySpec" }));
			stringRules.add(Arrays.asList(new String[] { "Cipher" }));

			for (List<String> rule : stringRules) {
				ArrayList<CodeGenCrySLRule> newRules = new ArrayList<CodeGenCrySLRule>();
				rules.addAll(newRules);
				for (String r : rule) {
					newRules.add(new CodeGenCrySLRule(Utils.getCryptSLRule(r), null, null));
				}
			}

			CrySLConfiguration codeGenConfig = TestUtils.createCrySLConfigurationForCodeGeneration(developerProject,
					rules);
			boolean encCheck = codeGenerator.generateCodeTemplates(codeGenConfig, null);
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(developerProject, "Crypto", "Output.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, "Crypto",
					"CogniCryptCipher.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", encClassUnit);
			assertEquals(1, TestUtils.countMethods(encClassUnit));

			ICompilationUnit keyClassUnit = TestUtils.getICompilationUnit(developerProject, "testPackage",
					"CogniCryptSecretKeySpec.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", keyClassUnit);
			assertEquals(1, TestUtils.countMethods(keyClassUnit));

		} catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		} catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		} catch (ClassNotFoundException e) {
			Activator.getDefault().logError(e, "At least one CrySL rule could not be loaded.");
		} catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	@Test
	public void generateSymEnc() {

		List<CodeGenCrySLRule> rules = new ArrayList<CodeGenCrySLRule>();
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_PBEENC");
			TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(testJavaProject.getProject());
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();

			List<List<String>> stringRules = new ArrayList<List<String>>();
			stringRules.add(Arrays.asList(new String[] { "KeyGenerator" }));
			stringRules.add(Arrays.asList(new String[] { "Cipher" }));

			for (List<String> rule : stringRules) {
				ArrayList<CodeGenCrySLRule> newRules = new ArrayList<CodeGenCrySLRule>();
				rules.addAll(newRules);
				for (String r : rule) {
					newRules.add(new CodeGenCrySLRule(Utils.getCryptSLRule(r), null, null));
				}
			}

			CrySLConfiguration codeGenConfig = TestUtils.createCrySLConfigurationForCodeGeneration(developerProject,
					rules);
			boolean encCheck = codeGenerator.generateCodeTemplates(codeGenConfig, null);
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(developerProject, "Crypto", "Output.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, "Crypto",
					"CogniCryptCipher.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", encClassUnit);
			assertEquals(1, TestUtils.countMethods(encClassUnit));

			ICompilationUnit keyClassUnit = TestUtils.getICompilationUnit(developerProject, "testPackage",
					"CogniCryptKeyGenerator.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", keyClassUnit);
			assertEquals(1, TestUtils.countMethods(keyClassUnit));
		} catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		} catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		} catch (ClassNotFoundException e) {
			Activator.getDefault().logError(e, "At least one CrySL rule could not be loaded.");
		} catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

	public void generatePWD() {

		List<CodeGenCrySLRule> rules = new ArrayList<CodeGenCrySLRule>();
		try {
			IJavaProject testJavaProject = TestUtils.createJavaProject("TestProject_ENC");
			TestUtils.generateJavaClassInJavaProject(testJavaProject, "testPackage", "Test");
			CodeGenerator codeGenerator = new CrySLBasedCodeGenerator(testJavaProject.getProject());
			DeveloperProject developerProject = codeGenerator.getDeveloperProject();

			List<List<String>> stringRules = new ArrayList<List<String>>();
			stringRules.add(Arrays.asList(new String[] { "SecureRandom", "PBEKeySpec", "SecretKeyFactory" }));
			for (List<String> rule : stringRules) {
				ArrayList<CodeGenCrySLRule> newRules = new ArrayList<CodeGenCrySLRule>();
				rules.addAll(newRules);
				for (String r : rule) {
					try {
						newRules.add(new CodeGenCrySLRule(Utils.getCryptSLRule(r), null, null));
					} catch (FileNotFoundException ex) {
						Activator.getDefault().logError(ex, "CrySL rule" + r + " not found.");
					}
				}
			}

			CrySLConfiguration codeGenConfig = TestUtils.createCrySLConfigurationForCodeGeneration(developerProject,
					rules);
			boolean encCheck = codeGenerator.generateCodeTemplates(codeGenConfig, null);
			assertTrue(encCheck);

			ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(developerProject, "Crypto", "Output.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", testClassUnit);
			assertEquals(1, TestUtils.countMethods(testClassUnit));

			ICompilationUnit encClassUnit = TestUtils.getICompilationUnit(developerProject, "Crypto",
					"CogniCryptCipher.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", encClassUnit);
			assertEquals(1, TestUtils.countMethods(encClassUnit));

			ICompilationUnit keyClassUnit = TestUtils.getICompilationUnit(developerProject, "testPackage",
					"CogniCryptSecretKeySpec.java");
			TestUtils.openJavaFileInWorkspace(developerProject, "Crypto", keyClassUnit);
			assertEquals(1, TestUtils.countMethods(keyClassUnit));

		} catch (JavaModelException e) {
			Activator.getDefault().logError(e, "Could not create Java class in test project.");
		} catch (CoreException e) {
			Activator.getDefault().logError(e, "Failed to create test project or to retrieve compilation unit.");
		} catch (ClassNotFoundException e) {
			Activator.getDefault().logError(e, "At least one CrySL rule could not be loaded.");
		} catch (IOException e) {
			Activator.getDefault().logError(e, "Reading of at least one CrySL rule failed.");
		}

	}

}
