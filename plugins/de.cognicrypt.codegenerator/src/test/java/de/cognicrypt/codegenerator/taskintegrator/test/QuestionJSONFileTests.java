package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.taskintegrator.controllers.SegregatesQuestionsIntoPages;
import de.cognicrypt.codegenerator.utilities.Utils;

public class QuestionJSONFileTests {

	static QuestionsJSONReader qjr;

	@BeforeClass
	public static void setUpBeforeClass() {
		QuestionJSONFileTests.qjr = new QuestionsJSONReader();
	}

	private String testFileFolder = "src/test/resources/taskintegrator/testQuestionsTI/";
	private String testQuestions3in1 = testFileFolder + "testQuestions3in1.json";
	private String testQuestions6in5 = testFileFolder + "testQuestions6in5.json";
	private String testQuestions6in2 = testFileFolder + "testQuestions6in2.json";
	private String testQuestions7in4 = testFileFolder + "testQuestions7in4.json";
	private String testQuestions10in10 = testFileFolder + "testQuestions10in10.json";
	private String testQuestions8in4 = testFileFolder + "testQuestions8in4.json";
	private String testQuestions6in3 = testFileFolder + "testQuestions6in3.json";

	private ArrayList<Question> originalQuestionList;
	private int expectedNumberOfPages;
	
	@Test
	/***
	 * This method should always performs a successful read and tests whether all questions are included in one page. This method should always assert success
	 */
	public void AllQuestionsInOnePage() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 1;
		originalQuestionList = readQuestionsFromFile(testQuestions3in1);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/***
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 5 pages. This method should always assert success
	 */
	public void OneOfThePagesContainsMultipleQuestion() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 5;
		originalQuestionList = readQuestionsFromFile(testQuestions6in5);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 2 pages. This method should always assert success
	 */
	public void EachPageContainsThreeQuestions() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 2;
		originalQuestionList = readQuestionsFromFile(testQuestions6in2);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 3 pages. This method should always assert success
	 */
	public void SixQuestionsIntoThreePages() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 3;
		originalQuestionList = readQuestionsFromFile(testQuestions6in3);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 7 questions are segregated into 4 pages. This method should always assert success
	 */
	public void OneQuestionHasBranch() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 4;
		originalQuestionList = readQuestionsFromFile(testQuestions7in4);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 8 questions are segregated into 4 pages. This method should always assert success
	 */
	public void EightQuestionsIntoFourPages() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 4;
		originalQuestionList = readQuestionsFromFile(testQuestions8in4);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 10 questions are segregated into 10 pages. This method should always assert success
	 */
	public void FourQuestionsHasBranch() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 10;
		originalQuestionList = readQuestionsFromFile(testQuestions10in10);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}


	/**
	 * 
	 * @param filePath
	 *        the path of the file
	 * @return list of all questions contained in the file
	 */
	public ArrayList<Question> readQuestionsFromFile(String filePath) {
		ArrayList<Question> originalQuestionList = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(filePath)));
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			originalQuestionList = gson.fromJson(reader, new TypeToken<ArrayList<Question>>() {}.getType());
		} catch (FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return originalQuestionList;
	}

}
