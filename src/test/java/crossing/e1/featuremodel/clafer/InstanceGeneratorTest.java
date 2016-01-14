/**
 * 
 */
package crossing.e1.featuremodel.clafer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.wizard.beginner.QuestionsBeginner;
import crossing.e1.xml.export.Answer;
import crossing.e1.xml.export.Question;

/**
 * @author Ram
 *
 */
public class InstanceGeneratorTest {
	ClaferModel claferModel;
	InstanceGenerator instanceGenerator;
	QuestionsBeginner quest;
	
	
	private static final String CLAFER_PROPERTY_1 = "performance";
	private static final String CLAFER_PROPERTY_2 = "power";
	private static final String TASK_DESCRIPTION = "Find car";
	private static final String TASK_NAME = "c0_CarFinder";
	private static final String XML_FILE_NAME="testEncryptXmlPath";
	private static final String JS_PATH="testClaferPath";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		claferModel = new ClaferModel(new ReadConfig().getValueFromConfig(JS_PATH));
		instanceGenerator = new InstanceGenerator(JS_PATH);
		quest = new QuestionsBeginner();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		claferModel = null;
		instanceGenerator = null;
		quest = null;
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#InstanceGenerator()}
	 * .
	 */
	@Test
	public final void testInstanceGenerator() {

		assertNotNull("failed to return instanceGenerator Object", instanceGenerator);
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstances(java.util.HashMap)}
	 * .
	 */
	@Test
	public final void testGenerateInstancesBmodeNoConstraints() {
		instanceGenerator.setTaskName(TASK_DESCRIPTION);
		HashMap<Question, Answer> map = new HashMap<Question, Answer>();
		List<Integer> hashCodes = new ArrayList<>();
		/**
		 * Below are the hash codes of the expected instances
		 */

		hashCodes.add(1706109440);
		hashCodes.add(1706109472);
		hashCodes.add(1706109950);
		hashCodes.add(1706109854);

		instanceGenerator.generateInstances(map);
		List<InstanceClafer> instances = new ArrayList<>(instanceGenerator.getInstances().values());
		assertNotNull("failed to return instances Object", instances);
		assertEquals(4, instanceGenerator.getNoOfInstances());
		for (InstanceClafer inst : instances) {
			assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
		}

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstances(java.util.HashMap)}
	 * .
	 */
	@Test
	public final void testGenerateInstancesBmodeWithConstraints() {
		instanceGenerator.setTaskName(TASK_DESCRIPTION);
		HashMap<Question, Answer> map = new HashMap<Question, Answer>();
		quest.init(TASK_NAME,XML_FILE_NAME);
		ArrayList<Question> question = quest.getQutionare();
		Answer answer1 = question.get(0).getAnswers().get(0);// performance=3
		Answer answer2 = question.get(1).getAnswers().get(0);// power=2
		map.put(question.get(0), answer1);
		map.put(question.get(1), answer2);
		claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(TASK_DESCRIPTION));
		List<Integer> hashCodes = new ArrayList<>();

		hashCodes.add(1706109472);
		instanceGenerator.generateInstances(map);
		List<InstanceClafer> instances = new ArrayList<>(instanceGenerator.getInstances().values());

		assertEquals(1, instanceGenerator.getNoOfInstances());
		for (InstanceClafer inst : instances) {
			assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
		}
		assertNotNull("failed to return instances Object", instances);
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstances(java.util.HashMap)}
	 * .
	 */
	@Test
	public final void testGenerateInstancesBmodeNegative() {
		instanceGenerator.setTaskName(TASK_DESCRIPTION);
		HashMap<Question, Answer> map = new HashMap<Question, Answer>();
		quest.init(TASK_NAME,XML_FILE_NAME);
		ArrayList<Question> question = quest.getQutionare();
		Answer answer1 = question.get(0).getAnswers().get(1);
		Answer answer2 = question.get(1).getAnswers().get(1);
		map.put(question.get(0), answer1);
		map.put(question.get(1), answer2);
		claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(TASK_DESCRIPTION));
		instanceGenerator.generateInstances(map);
		assertEquals(0, instanceGenerator.getNoOfInstances());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstancesAdvancedUserMode(java.util.Map)}
	 * .
	 */
	@Test
	public final void testGenerateInstancesAdvancedUserMode() {
		HashMap<AstConcreteClafer, AstConcreteClafer> performance = claferModel.getChildrenListbyName(CLAFER_PROPERTY_1);
		HashMap<AstConcreteClafer, AstConcreteClafer> power = claferModel.getChildrenListbyName(CLAFER_PROPERTY_2);
		
		HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map = new HashMap<>();
		ArrayList<AstConcreteClafer> key;
		ArrayList<Integer> values;
		List<Integer> hashCodes = new ArrayList<>();
		/**
		 * Below are the hash codes of the expected instances
		 */

		hashCodes.add(1706109440);
		hashCodes.add(1706109472);
		hashCodes.add(1706109950);
		hashCodes.add(1706109854);

		instanceGenerator.setTaskName(TASK_DESCRIPTION);
		claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(TASK_DESCRIPTION));

		
		for (AstConcreteClafer p : power.keySet()) {
			key = new ArrayList<AstConcreteClafer>();
			values = new ArrayList<Integer>();

			key.add((AstConcreteClafer) p);
			key.add(power.get(p));
			values.add(new Integer(2));
			values.add(new Integer(4));

			map.put(key, values);
		}
		for (AstConcreteClafer perform : performance.keySet()) {
			key = new ArrayList<AstConcreteClafer>();
			values = new ArrayList<Integer>();

			key.add((AstConcreteClafer) perform);
			key.add(performance.get(perform));
			values.add(new Integer(2));
			values.add(new Integer(4));
			map.put(key, values);
		}
		if (map.isEmpty()) {
			System.out.println("MAP IS EMPTY");
		} else {
			instanceGenerator.generateInstancesAdvancedUserMode(map);
			List<InstanceClafer> instances = new ArrayList<>(instanceGenerator.getInstances().values());
			assertNotNull("failed to return instances Object", instances);
			assertEquals(4, instanceGenerator.getNoOfInstances());
			for (InstanceClafer inst : instances) {
				assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
			}

		}

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#addConstraints(int, org.clafer.ast.AstConcreteClafer, int, org.clafer.ast.AstConcreteClafer, org.clafer.ast.AstConcreteClafer)}
	 * .
	 */
	@Test
	public final void testAddConstraints() {
		// ("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getInstances()}.
	 */
	@Test
	public final void testGetInstances() {
		testGenerateInstancesBmodeWithConstraints();
		assertNotNull("failed to return instances Object", instanceGenerator.getInstances());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#resetInstances()}
	 * .
	 */
	@Test
	public final void testResetInstances() {
		testGenerateInstancesBmodeWithConstraints();
		instanceGenerator.resetInstances();
		assertNull("failed to reset instances Object", instanceGenerator.getInstances());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstanceMapping()}
	 * .
	 */
	@Test
	public final void testGenerateInstanceMapping() {
		instanceGenerator.setTaskName(TASK_DESCRIPTION);
		HashMap<Question, Answer> map = new HashMap<Question, Answer>();
		assertNotNull("failed to reset instances Object", instanceGenerator.generateInstances(map));
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getInstanceName(org.clafer.instance.InstanceClafer)}
	 * .
	 */
	@Test
	public final void testGetInstanceName() {
		testGenerateInstancesBmodeWithConstraints();
		for (String key : instanceGenerator.getInstances().keySet()) {
			String instanceName = instanceGenerator.getInstanceName(instanceGenerator.getInstances().get(key));
			assertEquals("BMW F30+Model 3", instanceName);
		}
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getNoOfInstances()}
	 * .
	 */
	@Test
	public final void testGetNoOfInstances() {
		int noOfInstances = 10;
		instanceGenerator.setNoOfInstances(10);
		assertEquals("failed to get number of instances", instanceGenerator.getNoOfInstances(), noOfInstances);
		assertNotEquals("failed to get number of instances", instanceGenerator.getNoOfInstances(), 0);
		instanceGenerator.setNoOfInstances(0);

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#setNoOfInstances(int)}
	 * .
	 */
	@Test
	public final void testSetNoOfInstances() {
		int noOfInstances = 10;
		instanceGenerator.setNoOfInstances(10);
		assertEquals(noOfInstances, instanceGenerator.getNoOfInstances());
		assertNotEquals(0, instanceGenerator.getNoOfInstances());
		instanceGenerator.setNoOfInstances(0);

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getTaskName()}.
	 */
	@Test
	public final void testGetTaskName() {
		assertNotNull(instanceGenerator.getTaskName());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#setTaskName(java.lang.String)}
	 * .
	 */
	@Test
	public final void testSetTaskName() {
		String taskName = instanceGenerator.getTaskName();
		instanceGenerator.setTaskName("TestTaskName");
		assertEquals("TestTaskName", instanceGenerator.getTaskName());
		instanceGenerator.setTaskName(taskName);

	}

}
