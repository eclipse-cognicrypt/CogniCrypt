/**
 * 
 */
package crossing.e1.featuremodel.clafer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crossing.e1.configurator.ReadConfig;

/**
 * @author Ram
 *
 */
public class InstanceGeneratorTest {

	private String path = new ReadConfig().getPath("claferPath");
	private String claferName = "performance";
	private String taskDescription = "Encrypt data using a secret key";
	ClaferModel claferModel;
	InstanceGenerator instanceGenerator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		claferModel = new ClaferModel(path);
		instanceGenerator = new InstanceGenerator();
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
		assertNotNull("failed to return instanceGenerator Object",
				instanceGenerator);
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstances(java.util.HashMap)}
	 * .
	 */
	@Test
	public final void testGenerateInstances() {
		fail("Not yet implemented"); // TODO
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
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#advancedModeHandler(org.clafer.ast.AstConcreteClafer, java.util.Map)}
	 * .
	 */
	@Test
	public final void testAdvancedModeHandler() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#basicModeHandler(org.clafer.ast.AstConcreteClafer, java.util.HashMap)}
	 * .
	 */
	@Test
	public final void testBasicModeHandler() {
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
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getScope()}.
	 */
	@Test
	public final void testGetScope() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getInstances()}.
	 */
	@Test
	public final void testGetInstances() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#resetInstances()}
	 * .
	 */
	@Test
	public final void testResetInstances() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#generateInstanceMapping()}
	 * .
	 */
	@Test
	public final void testGenerateInstanceMapping() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.InstanceGenerator#getInstanceName(org.clafer.instance.InstanceClafer)}
	 * .
	 */
	@Test
	public final void testGetInstanceName() {
		fail("Not yet implemented"); // TODO
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
		assertEquals("failed to get number of instances",
				instanceGenerator.getNoOfInstances(), noOfInstances);
		assertNotEquals("failed to get number of instances",
				instanceGenerator.getNoOfInstances(), 0);
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
		assertEquals("failed to set number of instances",
				instanceGenerator.getNoOfInstances(), noOfInstances);
		assertNotEquals("failed to set number of instances",
				instanceGenerator.getNoOfInstances(), 0);
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
