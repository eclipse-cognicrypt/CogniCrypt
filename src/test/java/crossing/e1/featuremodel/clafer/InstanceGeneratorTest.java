/**
 * 
 */
package crossing.e1.featuremodel.clafer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private String path = new ReadConfig().getPath("claferPath");
	private String claferName = "performance";
	private String taskDescription = "Find car";
	ClaferModel claferModel;
	InstanceGenerator instanceGenerator;
	QuestionsBeginner quest;
	private String taskName = "c0_CarFinder";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		claferModel = new ClaferModel(new ReadConfig().getPath("claferPath"));
		instanceGenerator = new InstanceGenerator();
		quest = new QuestionsBeginner();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
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
		instanceGenerator.setTaskName(taskDescription);
		HashMap<String, Answer> map = new HashMap<String, Answer>();
		List<Integer> hashCodes = new ArrayList<>();
		/*
		 * Below are the hash codes of the expected instances
		 */
		hashCodes.add(-3546765);
		hashCodes.add(-2136166356);
		hashCodes.add(-606937044);
		hashCodes.add(-771579251);
		hashCodes.add(-921709371);

		List<InstanceClafer> instances = instanceGenerator.generateInstances(map);
		assertEquals(instanceGenerator.getNoOfInstances(), 4);
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
	public final void testGenerateInstancesBmodeWithConstraints() {
		instanceGenerator.setTaskName(taskDescription);
		HashMap<String, Answer> map = new HashMap<String, Answer>();
		quest.init(taskName);
		ArrayList<Question> q = quest.getQutionare();
		Answer answer1 = q.get(0).getAnswers().get(0);
		Answer answer2 = q.get(0).getAnswers().get(0);
		map.put(claferName, answer1);
		map.put("", answer2);

		List<Integer> hashCodes = new ArrayList<>();
		hashCodes.add(-3546765);
		hashCodes.add(-2136166356);
		hashCodes.add(-606937044);
		hashCodes.add(-771579251);
		hashCodes.add(-921709371);

		List<InstanceClafer> instances = instanceGenerator.generateInstances(map);

		assertEquals(instanceGenerator.getNoOfInstances(), 4);
		for (InstanceClafer inst : instances) {

			System.out.println(new InstanceClaferHash(inst).hashCode());
			assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
		}
		assertNotNull("failed to return instances Object", instances);
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstancesAdvancedUserMode(java.util.Map)}
	 * .
	 */
	@Test
	public final void testGenerateInstancesAdvancedUserMode() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#addConstraints(int, org.clafer.ast.AstConcreteClafer, int, org.clafer.ast.AstConcreteClafer, org.clafer.ast.AstConcreteClafer)}
	 * .
	 */
	@Test
	public final void testAddConstraints() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getInstances()}.
	 */
	@Test
	public final void testGetInstances() {
		// testGenerateInstances();
		assertNotNull("failed to return instances Object", instanceGenerator.getInstances());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#resetInstances()}
	 * .
	 */
	@Test
	public final void testResetInstances() {
		// testGenerateInstances();
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
		// testGenerateInstances();
		instanceGenerator.generateInstanceMapping();
		assertNotNull("failed to reset instances Object", instanceGenerator.getInstances());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getInstanceName(org.clafer.instance.InstanceClafer)}
	 * .
	 */
	@Test
	public final void testGetInstanceName() {
		// testGenerateInstances();
		for (String key : instanceGenerator.getInstances().keySet())
			System.out.println("instance " + instanceGenerator.getInstances().get(key));
		;
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
		assertEquals("failed to set number of instances", instanceGenerator.getNoOfInstances(), noOfInstances);
		assertNotEquals("failed to set number of instances", instanceGenerator.getNoOfInstances(), 0);
		instanceGenerator.setNoOfInstances(0);

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getTaskName()}.
	 */
	@Test
	public final void testGetTaskName() {
		assertNotNull("Empty Task Name", instanceGenerator.getTaskName());
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
		assertEquals("", "TestTaskName", instanceGenerator.getTaskName());
		instanceGenerator.setTaskName(taskName);

	}

}
