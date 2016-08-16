/**
 *
 */
package crossing.e1.featuremodel.clafer.test;

/**
 * @author Ram
 *
 */
public class InstanceGeneratorTest {
	//	private static final String CLAFER_PROPERTY_1 = "performance";
	//	private static final String CLAFER_PROPERTY_2 = "power";
	//	private static final String TASK_DESCRIPTION = "Find car";
	//
	//	private static final String TASK_NAME = "c0_CarFinder";
	//	private static final String XML_FILE_NAME = "testEncryptXmlPath";
	//	private static final String JS_PATH = "testClaferPath";
	//	ClaferModel claferModel;
	//	InstanceGenerator instanceGenerator;
	//	//QuestionsBeginner quest;
	//
	//	/**
	//	 * @throws java.lang.Exception
	//	 */
	//	@Before
	//	public void setUp() throws Exception {
	//		String path = "src/main/resources/testClafer.js";
	//		this.claferModel = new ClaferModel(path);
	//		//this.instanceGenerator = new InstanceGenerator(path);
	//		//this.quest = new QuestionsBeginner();
	//
	//	}
	//
	//	/**
	//	 * @throws java.lang.Exception
	//	 */
	//	@After
	//	public void tearDown() throws Exception {
	//		this.claferModel = null;
	//		this.instanceGenerator = null;
	//		//this.quest = null;
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#addConstraints(int, org.clafer.ast.AstConcreteClafer, int, org.clafer.ast.AstConcreteClafer, org.clafer.ast.AstConcreteClafer)}
	//	 * .
	//	 */
	//	@Test
	//	public final void testAddConstraints() {
	//		// ("Not yet implemented"); // TODO
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#generateInstanceMapping()} .
	//	 */
	//	@Test
	//	public final void testGenerateInstanceMapping() {
	//		this.instanceGenerator.setTaskName(TASK_DESCRIPTION);
	//		final HashMap<Question, Answer> map = new HashMap<Question, Answer>();
	//		assertNotNull("failed to reset instances Object", this.instanceGenerator.generateInstances(map));
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#generateInstancesAdvancedUserMode(java.util.Map)} .
	//	 */
	//	@Test
	//	public final void testGenerateInstancesAdvancedUserMode() {
	//		final HashMap<AstConcreteClafer, AstConcreteClafer> performance = this.claferModel.getChildrenListbyName(CLAFER_PROPERTY_1);
	//		final HashMap<AstConcreteClafer, AstConcreteClafer> power = this.claferModel.getChildrenListbyName(CLAFER_PROPERTY_2);
	//
	//		final HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map = new HashMap<>();
	//		ArrayList<AstConcreteClafer> key;
	//		ArrayList<Integer> values;
	//		final List<Integer> hashCodes = new ArrayList<>();
	//		/**
	//		 * Below are the hash codes of the expected instances
	//		 */
	//
	//		hashCodes.add(1706109440);
	//		hashCodes.add(1706109472);
	//		hashCodes.add(1706109950);
	//		hashCodes.add(1706109854);
	//
	//		this.instanceGenerator.setTaskName(TASK_DESCRIPTION);
	//		this.claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(TASK_DESCRIPTION));
	//
	//		for (final AstConcreteClafer p : power.keySet()) {
	//			key = new ArrayList<AstConcreteClafer>();
	//			values = new ArrayList<Integer>();
	//
	//			key.add(p);
	//			key.add(power.get(p));
	//			values.add(new Integer(2));
	//			values.add(new Integer(4));
	//
	//			map.put(key, values);
	//		}
	//		for (final AstConcreteClafer perform : performance.keySet()) {
	//			key = new ArrayList<AstConcreteClafer>();
	//			values = new ArrayList<Integer>();
	//
	//			key.add(perform);
	//			key.add(performance.get(perform));
	//			values.add(new Integer(2));
	//			values.add(new Integer(4));
	//			map.put(key, values);
	//		}
	//		if (map.isEmpty()) {
	//			System.out.println("MAP IS EMPTY");
	//		} else {
	////			this.instanceGenerator.generateInstancesAdvancedUserMode(map);
	//			final List<InstanceClafer> instances = new ArrayList<>(this.instanceGenerator.getInstances().values());
	//			assertNotNull("failed to return instances Object", instances);
	//			assertEquals(4, this.instanceGenerator.getNoOfInstances());
	//			for (final InstanceClafer inst : instances) {
	//				assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
	//			}
	//
	//		}
	//
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#generateInstances(java.util.HashMap)} .
	//	 */
	//	@Test
	//	public final void testGenerateInstancesBmodeNegative() {
	//		this.instanceGenerator.setTaskName(TASK_DESCRIPTION);
	//		final HashMap<Question, Answer> map = new HashMap<Question, Answer>();
	//		this.quest.init(TASK_NAME, XML_FILE_NAME);
	//		final ArrayList<Question> question = this.quest.getQutionare();
	//		final Answer answer1 = question.get(0).getAnswers().get(1);
	//		final Answer answer2 = question.get(1).getAnswers().get(1);
	//		map.put(question.get(0), answer1);
	//		map.put(question.get(1), answer2);
	//		this.claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(TASK_DESCRIPTION));
	//		this.instanceGenerator.generateInstances(map);
	//		assertEquals(0, this.instanceGenerator.getNoOfInstances());
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#generateInstances(java.util.HashMap)} .
	//	 */
	//	@Test
	//	public final void testGenerateInstancesBmodeNoConstraints() {
	//		this.instanceGenerator.setTaskName(TASK_DESCRIPTION);
	//		final HashMap<Question, Answer> map = new HashMap<Question, Answer>();
	//		final List<Integer> hashCodes = new ArrayList<>();
	//		/**
	//		 * Below are the hash codes of the expected instances
	//		 */
	//
	//		hashCodes.add(1706109440);
	//		hashCodes.add(1706109472);
	//		hashCodes.add(1706109950);
	//		hashCodes.add(1706109854);
	//
	//		this.instanceGenerator.generateInstances(map);
	//		final List<InstanceClafer> instances = new ArrayList<>(this.instanceGenerator.getInstances().values());
	//		assertNotNull("failed to return instances Object", instances);
	//		assertEquals(4, this.instanceGenerator.getNoOfInstances());
	//		for (final InstanceClafer inst : instances) {
	//			assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
	//		}
	//
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#generateInstances(java.util.HashMap)} .
	//	 */
	//	@Test
	//	public final void testGenerateInstancesBmodeWithConstraints() {
	//		this.instanceGenerator.setTaskName(TASK_DESCRIPTION);
	//		final HashMap<Question, Answer> map = new HashMap<Question, Answer>();
	//		this.quest.init(TASK_NAME, XML_FILE_NAME);
	//		final ArrayList<Question> question = this.quest.getQutionare();
	//		final Answer answer1 = question.get(0).getAnswers().get(0);// performance=3
	//		final Answer answer2 = question.get(1).getAnswers().get(0);// power=2
	//		map.put(question.get(0), answer1);
	//		map.put(question.get(1), answer2);
	//		this.claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(TASK_DESCRIPTION));
	//		final List<Integer> hashCodes = new ArrayList<>();
	//
	//		hashCodes.add(1706109472);
	//		this.instanceGenerator.generateInstances(map);
	//		final List<InstanceClafer> instances = new ArrayList<>(this.instanceGenerator.getInstances().values());
	//
	//		assertEquals(1, this.instanceGenerator.getNoOfInstances());
	//		for (final InstanceClafer inst : instances) {
	//			assertTrue(hashCodes.contains(new InstanceClaferHash(inst).hashCode()));
	//		}
	//		assertNotNull("failed to return instances Object", instances);
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#getInstanceName(org.clafer.instance.InstanceClafer)}
	//	 * .
	//	 */
	//	@Test
	//	public final void testGetInstanceName() {
	//		testGenerateInstancesBmodeWithConstraints();
	//		for (final String key : this.instanceGenerator.getInstances().keySet()) {
	//			final String instanceName = this.instanceGenerator.getInstanceName(this.instanceGenerator.getInstances().get(key));
	//			assertEquals("BMW F30+Model 3", instanceName);
	//		}
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#getInstances()}.
	//	 */
	//	@Test
	//	public final void testGetInstances() {
	//		testGenerateInstancesBmodeWithConstraints();
	//		assertNotNull("failed to return instances Object", this.instanceGenerator.getInstances());
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#getNoOfInstances()} .
	//	 */
	//	@Test
	//	public final void testGetNoOfInstances() {
	//		final int noOfInstances = 10;
	//		this.instanceGenerator.setNoOfInstances(10);
	//		assertEquals("failed to get number of instances", this.instanceGenerator.getNoOfInstances(), noOfInstances);
	//		assertNotEquals("failed to get number of instances", this.instanceGenerator.getNoOfInstances(), 0);
	//		this.instanceGenerator.setNoOfInstances(0);
	//
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#getTaskName()}.
	//	 */
	//	@Test
	//	public final void testGetTaskName() {
	//		assertNotNull(this.instanceGenerator.getTaskName());
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#InstanceGenerator()} .
	//	 */
	//	@Test
	//	public final void testInstanceGenerator() {
	//
	//		assertNotNull("failed to return instanceGenerator Object", this.instanceGenerator);
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#resetInstances()} .
	//	 */
	//	@Test
	//	public final void testResetInstances() {
	//		testGenerateInstancesBmodeWithConstraints();
	//		this.instanceGenerator.resetInstances();
	//		assertNull("failed to reset instances Object", this.instanceGenerator.getInstances());
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#setNoOfInstances(int)} .
	//	 */
	//	@Test
	//	public final void testSetNoOfInstances() {
	//		final int noOfInstances = 10;
	//		this.instanceGenerator.setNoOfInstances(10);
	//		assertEquals(noOfInstances, this.instanceGenerator.getNoOfInstances());
	//		assertNotEquals(0, this.instanceGenerator.getNoOfInstances());
	//		this.instanceGenerator.setNoOfInstances(0);
	//
	//	}
	//
	//	/**
	//	 * Test method for {@link crossing.e1.featuremodel.clafer.test.InstanceGenerator#setTaskName(java.lang.String)} .
	//	 */
	//	@Test
	//	public final void testSetTaskName() {
	//		final String taskName = this.instanceGenerator.getTaskName();
	//		this.instanceGenerator.setTaskName("TestTaskName");
	//		assertEquals("TestTaskName", this.instanceGenerator.getTaskName());
	//		this.instanceGenerator.setTaskName(taskName);
	//
	//	}

}
