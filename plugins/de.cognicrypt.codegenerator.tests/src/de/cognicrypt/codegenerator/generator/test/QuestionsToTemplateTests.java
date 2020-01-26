package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;


import java.io.IOException;

import org.junit.Test;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.AltConfigWizard;
import de.cognicrypt.core.Constants;

import java.util.HashMap;

/**
 * @author Shahrzad Asghari
 * @author Enri Ozuni
 */
public class QuestionsToTemplateTests {
	
	@Test
	public void test1() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer1 = new Answer();
		question1answer1.setValue("Encrypted digital channel");
		question1answer1.setOption("");
		Answer question2answer1 = new Answer();
		question2answer1.setValue("Byte Array");
		question2answer1.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer1);
		constraints.put(question2, question2answer1);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test2() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer1 = new Answer();
		question1answer1.setValue("Encrypted digital channel");
		question1answer1.setOption("");
		Answer question2answer2 = new Answer();
		question2answer2.setValue("File");
		question2answer2.setOption("files");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer1);
		constraints.put(question2, question2answer2);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test3() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer1 = new Answer();
		question1answer1.setValue("Encrypted digital channel");
		question1answer1.setOption("");
		Answer question2answer3 = new Answer();
		question2answer3.setValue("String");
		question2answer3.setOption("strings");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer1);
		constraints.put(question2, question2answer3);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test4() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer1 = new Answer();
		question1answer1.setValue("Encrypted digital channel");
		question1answer1.setOption("");
		Answer question2answer4 = new Answer();
		question2answer4.setValue("Other/Do not know");
		question2answer4.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer1);
		constraints.put(question2, question2answer4);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test5() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer2 = new Answer();
		question1answer2.setValue("Encrypted Hard Drive");
		question1answer2.setOption("");
		Answer question2answer1 = new Answer();
		question2answer1.setValue("Byte Array");
		question2answer1.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer2);
		constraints.put(question2, question2answer1);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test6() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer2 = new Answer();
		question1answer2.setValue("Encrypted Hard Drive");
		question1answer2.setOption("");
		Answer question2answer2 = new Answer();
		question2answer2.setValue("File");
		question2answer2.setOption("files");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer2);
		constraints.put(question2, question2answer2);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test7() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer2 = new Answer();
		question1answer2.setValue("Encrypted Hard Drive");
		question1answer2.setOption("");
		Answer question2answer3 = new Answer();
		question2answer3.setValue("String");
		question2answer3.setOption("strings");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer2);
		constraints.put(question2, question2answer3);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test8() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer2 = new Answer();
		question1answer2.setValue("Encrypted Hard Drive");
		question1answer2.setOption("");
		Answer question2answer4 = new Answer();
		question2answer4.setValue("Other/Do not know");
		question2answer4.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer2);
		constraints.put(question2, question2answer4);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test9() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer3 = new Answer();
		question1answer3.setValue("Unencrypted digital channel (e.g. email)");
		question1answer3.setOption("hybrid");
		Answer question2answer1 = new Answer();
		question2answer1.setValue("Byte Array");
		question2answer1.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer3);
		constraints.put(question2, question2answer1);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybrid";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test10() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer3 = new Answer();
		question1answer3.setValue("Unencrypted digital channel (e.g. email)");
		question1answer3.setOption("hybrid");
		Answer question2answer2 = new Answer();
		question2answer2.setValue("File");
		question2answer2.setOption("files");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer3);
		constraints.put(question2, question2answer2);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybridfiles";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test11() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer3 = new Answer();
		question1answer3.setValue("Unencrypted digital channel (e.g. email)");
		question1answer3.setOption("hybrid");
		Answer question2answer3 = new Answer();
		question2answer3.setValue("String");
		question2answer3.setOption("strings");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer3);
		constraints.put(question2, question2answer3);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybridstrings";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test12() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer3 = new Answer();
		question1answer3.setValue("Unencrypted digital channel (e.g. email)");
		question1answer3.setOption("hybrid");
		Answer question2answer4 = new Answer();
		question2answer4.setValue("Other/Do not know");
		question2answer4.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer3);
		constraints.put(question2, question2answer4);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionhybrid";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test13() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer4 = new Answer();
		question1answer4.setValue("Unencrypted analog channel (e.g. phone, mail)");
		question1answer4.setOption("");
		Answer question2answer1 = new Answer();
		question2answer1.setValue("Byte Array");
		question2answer1.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer4);
		constraints.put(question2, question2answer1);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test14() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer4 = new Answer();
		question1answer4.setValue("Unencrypted analog channel (e.g. phone, mail)");
		question1answer4.setOption("");
		Answer question2answer2 = new Answer();
		question2answer2.setValue("File");
		question2answer2.setOption("files");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer4);
		constraints.put(question2, question2answer2);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test15() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer4 = new Answer();
		question1answer4.setValue("Unencrypted analog channel (e.g. phone, mail)");
		question1answer4.setOption("");
		Answer question2answer3 = new Answer();
		question2answer3.setValue("String");
		question2answer3.setOption("strings");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer4);
		constraints.put(question2, question2answer3);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test16() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer4 = new Answer();
		question1answer4.setValue("Unencrypted analog channel (e.g. phone, mail)");
		question1answer4.setOption("");
		Answer question2answer4 = new Answer();
		question2answer4.setValue("Other/Do not know");
		question2answer4.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer4);
		constraints.put(question2, question2answer4);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test17() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
		Answer question1answer5 = new Answer();
		question1answer5.setValue("No Sharing");
		question1answer5.setOption("");
		Answer question2answer1 = new Answer();
		question2answer1.setValue("Byte Array");
		question2answer1.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer5);
		constraints.put(question2, question2answer1);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test18() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer5 = new Answer();
		question1answer5.setValue("No Sharing");
		question1answer5.setOption("");
		Answer question2answer2 = new Answer();
		question2answer2.setValue("File");
		question2answer2.setOption("files");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer5);
		constraints.put(question2, question2answer2);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionfiles";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test19() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer5 = new Answer();
		question1answer5.setValue("No Sharing");
		question1answer5.setOption("");
		Answer question2answer3 = new Answer();
		question2answer3.setValue("String");
		question2answer3.setOption("strings");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer5);
		constraints.put(question2, question2answer3);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryptionstrings";
		assertEquals(wizard.constructTemplateName(), expected);
	}
	
	@Test
	public void test20() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task encryption = TestUtils.getTask("Encryption");
		wizard.setSelectedTask(encryption);
		Question question1 = new Question();
		question1.setId(0);
        question1.setQuestionText("Which method of communication would you prefer to use for key exchange?");
		Question question2 = new Question();
		question2.setId(1);
        question2.setQuestionText("What data type do you wish to encrypt?");
        Answer question1answer5 = new Answer();
		question1answer5.setValue("No Sharing");
		question1answer5.setOption("");
		Answer question2answer4 = new Answer();
		question2answer4.setValue("Other/Do not know");
		question2answer4.setOption("");
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		constraints.put(question1, question1answer5);
		constraints.put(question2, question2answer4);
		wizard.constructConstraints(constraints);
		String expected = "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption";
		assertEquals(wizard.constructTemplateName(), expected);
	}
}