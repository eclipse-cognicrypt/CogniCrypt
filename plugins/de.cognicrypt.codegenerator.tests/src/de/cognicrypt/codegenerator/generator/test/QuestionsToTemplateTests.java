package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.AltConfigWizard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The JUnit Plug-in tests check the correctness of CogniCrypt's code generation
 * feature in determining the template path when the user chooses an arbitrary
 * task and a set of given question-answers in the wizard dialog. This is
 * important since the code generation depends on the computed path to generate
 * the proper template for a given input from the user through CogniCrypt's
 * wizard.
 * 
 * @author Shahrzad Asghari
 * @author Enri Ozuni
 */
public class QuestionsToTemplateTests {

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted digital channel", "Byte Array");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted digital channel", "File");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted digital channel", "String");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted digital channel",
				"Other/Do not know");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted Hard Drive", "Byte Array");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted Hard Drive", "File");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted Hard Drive", "String");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Encrypted Hard Drive", "Other/Do not know");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Unencrypted digital channel (e.g. email)",
				"Byte Array");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybrid";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Unencrypted digital channel (e.g. email)",
				"File");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybridfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Unencrypted digital channel (e.g. email)",
				"String");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybridstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "Unencrypted digital channel (e.g. email)",
				"Other/Do not know");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybrid";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption",
				"Unencrypted analog channel (e.g. phone, mail)", "Byte Array");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption",
				"Unencrypted analog channel (e.g. phone, mail)", "File");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption",
				"Unencrypted analog channel (e.g. phone, mail)", "String");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption",
				"Unencrypted analog channel (e.g. phone, mail)", "Other/Do not know");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "No Sharing", "Byte Array");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "No Sharing", "File");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "No Sharing", "String");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		wizard = constructTemplateForEncryptionTask(wizard, "Encryption", "No Sharing", "Other/Do not know");
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testSecurePasswordTask() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task securepassword = TestUtils.getTask("SecurePassword");
		wizard.setSelectedTask(securepassword);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/securepassword";
		assertEquals(expected, wizard.constructTemplateName());
	}

	@Test
	public void testDigitalSignaturesTask() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task digitalsignatures = TestUtils.getTask("DigitalSignatures");
		wizard.setSelectedTask(digitalsignatures);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/digitalsignatures";
		assertEquals(expected, wizard.constructTemplateName());
	}

	private AltConfigWizard constructTemplateForEncryptionTask(AltConfigWizard wizard, String task, String firstAnswer,
			String secondAnswer) {
		Task encryptionTask = TestUtils.getTask(task);
		wizard.setSelectedTask(encryptionTask);
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(firstAnswer);
		answers.add(secondAnswer);
		HashMap<Question, Answer> constraints = TestUtils.setConstraintsForTask(encryptionTask, answers);
		wizard.addConstraints(constraints);
		return wizard;
	}
}