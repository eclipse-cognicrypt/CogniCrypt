package crossing.e1.primitiveintegration.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.primitive.types.PrimitiveJSONReader;
import de.cognicrypt.codegenerator.utilities.Utils;

public class PrimitiveTypesTest {

	private static PrimitiveJSONReader reader = new PrimitiveJSONReader();
	private File primitiveTestFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "PrimitiveTest.json");
	private static Primitive primitive = new Primitive();

	@BeforeClass
	public static void setupPrimitveTest() {

		primitive.setName("Cipher Test");
		primitive.setXmlFile(Constants.testPrimitverFolder + "PrimitiveQuestionTest.json");
		primitive.setXslFile(Constants.testPrimitverFolder + "xslTest.xsl");

	}

	@Test
	public void test() {
		List<Primitive> primitives = PrimitiveJSONReader.getPrimitiveTypes(primitiveTestFile);
		setupPrimitveTest();
		primitive.setName("Cipher Test");
		primitive.setXmlFile(Constants.testPrimitverFolder + "TestPrimitiveQuestion.json");
		assertEquals(primitives.get(0).getName(), primitive.getName());
		assertEquals(primitives.get(0).getXmlFile(), primitive.getXmlFile());
		assertEquals(primitives.get(0).getXslFile(), primitive.getXslFile());

	}

}
