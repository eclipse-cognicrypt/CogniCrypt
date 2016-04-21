package crossing.e1.configurator.beginer.question;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.utilities.Labels;

public class ReadTaskConfig implements Labels {
	private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder dBuilder;
	private Document doc;
	private ArrayList<Question> questions = null;

	/**
	 * Accepts taskName and xml filename as an input , returns list of Question objects for a given task
	 *
	 * @param task
	 * @param fileName
	 * @return
	 */
	public ArrayList<Question> getQA(final String task, final String fileName) {
		parseXML(task, fileName);
		return this.questions;
	}

	/**
	 *
	 * questionAndAnswers will contain question and their associated properties as a key and value list will be
	 * properties , values( : separated)
	 *
	 * @param nNode
	 */
	private void getQuestionsAndAnswers(final Node nNode) {
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			final Element eElement = (Element) nNode;
			final NodeList questions = eElement.getElementsByTagName(Labels.QUESTION_LIST);
			for (int temp = 0; temp < questions.getLength(); temp++) {
				final Node question = questions.item(temp);
				final NodeList questionList = ((Element) question).getElementsByTagName(Labels.QUESTION);
				parseQuestions(questionList);
			}
		}
	}

	/**
	 * Prepares the list of questions
	 *
	 * @param questionList
	 */
	private void parseQuestions(final NodeList questionList) {
		this.questions = new ArrayList<Question>();
		for (int quest = 0; quest < questionList.getLength(); quest++) {
			final Element que = (Element) questionList.item(quest);
			final Question question = new Question(que);
			this.questions.add(question);
		}
	}

	/**
	 * Method to parse file and populate questions
	 *
	 * @param task
	 * @param xmlFileName
	 */
	private void parseXML(final String task, final String xmlFileName) {
		try {
			this.dBuilder = this.dbFactory.newDocumentBuilder();
			this.doc = this.dBuilder.parse(new File(xmlFileName));
			this.doc.getDocumentElement().normalize();
			final NodeList nList = this.doc.getElementsByTagName(Labels.TASK);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				final Node nNode = nList.item(temp);
				if (((Element) nNode).getAttribute(Labels.TASK_NAME).toString().equals(task)) {
					getQuestionsAndAnswers(nNode);
				}

			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

}
