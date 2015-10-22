package crossing.e1.xml.export;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.ReadConfig;

import java.io.File;
import java.util.ArrayList;

public class ReadTaskConfig implements Lables{
	private File fXmlFile = new File(new ReadConfig().getPath("encryptXmlPath"));
	private DocumentBuilderFactory dbFactory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder dBuilder;
	private Document doc;
	ArrayList<Question> questions = null;

	public static void main(String argv[]) {
		ReadTaskConfig QA = new ReadTaskConfig();

		QA.getQA("c0_EncryptionUsingDigest");
		QA.displayVlaues();
	}

	void parseXML(String task) {
		try {

			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(Lables.TASK);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (((Element) nNode).getAttribute(Lables.TASK_NAME).toString()
						.equals(task))
					getQuestionsAndAnswers(nNode);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void getQuestionsAndAnswers(Node nNode) {
		/*
		 * questionAndAnswers will contain question and their associated
		 * properties as a key and value list will be properties , values( :
		 * separated)
		 */

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			NodeList questions = eElement.getElementsByTagName(Lables.QUESTION_LIST);
			for (int temp = 0; temp < questions.getLength(); temp++) {
				Node question = questions.item(temp);
				NodeList questionList = ((Element) question)
						.getElementsByTagName(Lables.QUESTION);
				parseQuestions(questionList);

			}

		}

	}

	void displayVlaues() {
		for (Question question : questions) {
			for (Answer ans : question.getAnswers()) {
				for (Dependency dep : ans.getDependencies()) {
					System.out.println(dep.getRefClafer() + "=>"
							+ dep.getOperator() + "=>" + dep.getValue());
				}
				System.out.println(ans.getRef() + "=>" + ans.getOperator()
						+ "=>" + ans.getValue());
			}
			System.out.println(question.getRefCalfer() + "=>"
					+ question.getDef() + "=>" + question.getDisplay());
		}
	}

	void parseQuestions(NodeList questionList) {
		questions = new ArrayList<Question>();
		for (int quest = 0; quest < questionList.getLength(); quest++) {
			Element que = (Element) questionList.item(quest);
			Question question = new Question(que);
			questions.add(question);
		}
	}

	public ArrayList<Question> getQA(String task) {
		System.out.println("Task name is " + task);
		parseXML(task);

		return questions;

	}

}
