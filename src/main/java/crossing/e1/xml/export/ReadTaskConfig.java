package crossing.e1.xml.export;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import crossing.e1.configurator.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadTaskConfig {
	private File fXmlFile = new File(
			Utilities.getAbsolutePath("src/main/resources/Encrypt.xml"));
	private DocumentBuilderFactory dbFactory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder dBuilder;
	private Document doc;
	private HashMap<HashMap<String,String>, List<String>> questionAndAnswers;

	public static void main(String argv[]) {
		HashMap<HashMap<String,String>, List<String>> QA = new ReadTaskConfig()
				.getQA("Encrypt data using a secret key");
//		for (List l : QA.keySet()) {
//			System.out.println(l.size() + " " + QA.get(l).size());
//		}
	}

	void parseXML(String task) {
		try {

			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("Task");
			System.out.println("----------------------------");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName()
						+ " ID is " + ((Element) nNode).getAttribute("name"));
				if (((Element) nNode).getAttribute("name").toString()
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
		questionAndAnswers = new HashMap<HashMap<String,String>, List<String>>();
		HashMap<String,String> list1;
		ArrayList<String> list2;
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			NodeList answerList = null;
			Element answers = null;
			Element eElement = (Element) nNode;
			NodeList questions = eElement.getElementsByTagName("Questions");
			for (int temp = 0; temp < questions.getLength(); temp++) {
				Node question = questions.item(temp);
				NodeList questionList = ((Element) question)
						.getElementsByTagName("Question");
				System.out.println("No of questions "
						+ questionList.getLength());
				for (int quest = 0; quest < questionList.getLength(); quest++) {
					list1 = new HashMap<String,String>();
					list2 = new ArrayList<String>();
					System.out.println("QUESTION " + (quest + 1));
					Element que = (Element) questionList.item(quest);
					System.out.println("Question " + que.getAttribute("def"));
					list1.put(que.getAttribute("def"),que.getAttribute("refCalfer"));
					if (que.hasChildNodes()) {
						if (que.getElementsByTagName("Answers").getLength() > 0)
							answers = (Element) que.getElementsByTagName(
									"Answers").item(0);
						if (answers.hasChildNodes())
							answerList = answers.getElementsByTagName("Answer");
						for (int ans = 0; ans < answerList.getLength(); ans++) {
							Element answeritem = (Element) answerList.item(ans);
							list2.add(answeritem.getAttribute("value") + ":"
									+ answeritem.getAttribute("ref"));
							System.out.println(answeritem.getAttribute("value")
									+ " => " + answeritem.getAttribute("ref"));
						}

					}
					questionAndAnswers.put(list1, list2);
				}

			}

		}
	}

	public HashMap<HashMap<String,String>, List<String>> getQA(String task) {
		System.out.println("Task name is "+task);
		parseXML(task);

		return questionAndAnswers;

	}

}
