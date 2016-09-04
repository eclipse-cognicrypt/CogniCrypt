package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.clafer.instance.InstanceClafer;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.utilities.XMLParser;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class XMLParserTestTest {
	ClaferModel claferModel;
	private static final String TASK_DESCRIPTION = "Find car";
	InstanceGenerator instGen;

	@Before
	public void setUp() throws Exception {
		String path = "src/main/resources/CryptoTasks.js";
		this.claferModel = new ClaferModel(path);
		this.instGen = new InstanceGenerator(path, "PasswordBasedEncryption", "description");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws DocumentException {
//		fail("Not yet implemented");
		XMLParser xmlparser = new XMLParser();
		HashMap<Question, Answer> constraints = new HashMap<Question, Answer>();
		InstanceClafer inst = this.instGen.generateInstances(constraints).get(0);
		
		xmlparser.displayInstanceValues(inst, constraints);
	}

}
