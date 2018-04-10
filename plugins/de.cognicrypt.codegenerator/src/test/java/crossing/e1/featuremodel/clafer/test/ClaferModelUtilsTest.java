package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.core.Constants;

public class ClaferModelUtilsTest {

	ClaferModel claferModel;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.claferModel = new ClaferModel(Constants.claferPath);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {}

	/**
	 * Test method for {@link crossing.e1.featuremodel.clafer.test.ClaferModelUtils#findClaferByName(org.clafer.ast.AstClafer, java.lang.String)} .
	 */
	@Test
	public final void testFindClaferByName() {
		// String childClaferName = "c0_outputSize";
		// AstConcreteClafer clafer = claferModel.getClafersByName(claferName)
		// .get(0);
		// AstClafer childclafer = ClaferModelUtils.findClaferByName(clafer,
		// childClaferName);
		// assertNotNull("failed to find the child clafer with given name",
		// childclafer);
		// childclafer = ClaferModelUtils.findClaferByName(clafer,
		// "TestCaseClaferName");
		// assertNull("False positive,child clafer found with an invalid name",
		// childclafer);

	}

	/**
	 * Test method for {@link crossing.e1.featuremodel.clafer.test.ClaferModelUtils#isConcrete(org.clafer.ast.AstClafer)} .
	 */
	@Test
	public final void testIsAbstract() {
		// AstClafer clafer = claferModel.getClafersByName(abstarctClaferName)
		// .get(0).getSuperClafer();
		// assertTrue("Failed to detect abstract clafer",
		// ClaferModelUtils.isAbstract(clafer));
		// clafer = claferModel.getClafersByName(claferName).get(0);
		// assertFalse("Failed to detect abstract clafer",
		// ClaferModelUtils.isAbstract(clafer));

	}

	/**
	 * Test method for {@link crossing.e1.featuremodel.clafer.test.ClaferModelUtils#removeScopePrefix(java.lang.String)} .
	 */
	@Test
	public final void testTrimScope() {
		final String beforeTrimValue = "c0_testString";
		final String comparableValue = "TestString";
		assertEquals("Failed to trim string value", ClaferModelUtils.removeScopePrefix(beforeTrimValue), comparableValue);
		assertNotEquals("Failed to trim string value", beforeTrimValue, comparableValue);

	}

}
