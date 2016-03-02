package crossing.e1.configurator.beginer.question;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.ReadConfig;

public class ReadTaskConfig implements Labels {
	private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder dBuilder;
	private Document doc;
	ArrayList<Question> questions = null;

	/**
	 * Method to parse file and populate questions
	 * 
	 * @param task
	 * @param xmlFileName
	 */
	void parseXML(String task, String xmlFileName) {
		try {

			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new File(new ReadConfig().getPathFromConfig(xmlFileName)));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(Labels.TASK);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (((Element) nNode).getAttribute(Labels.TASK_NAME).toString().equals(task))
					getQuestionsAndAnswers(nNode);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * questionAndAnswers will contain question and their associated properties
	 * as a key and value list will be properties , values( : separated)
	 * 
	 * @param nNode
	 */
	void getQuestionsAndAnswers(Node nNode) {

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			NodeList questions = eElement.getElementsByTagName(Labels.QUESTION_LIST);
			for (int temp = 0; temp < questions.getLength(); temp++) {
				Node question = questions.item(temp);
				NodeList questionList = ((Element) question).getElementsByTagName(Labels.QUESTION);
				parseQuestions(questionList);

			}

		}

	}

	/**
	 * Prepares the list of questions
	 * 
	 * @param questionList
	 */
	void parseQuestions(NodeList questionList) {
		questions = new ArrayList<Question>();
		for (int quest = 0; quest < questionList.getLength(); quest++) {
			Element que = (Element) questionList.item(quest);
			Question question = new Question(que);
			questions.add(question);
		}
	}

	/**
	 * Accepts taskName and xml filename as an input , returns list of Question
	 * objects for a given task
	 * 
	 * @param task
	 * @param fileName
	 * @return
	 */
	public ArrayList<Question> getQA(String task, String fileName) {
		parseXML(task, fileName);
		return questions;

	}

}
