package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;

import org.clafer.instance.InstanceClafer;
import org.junit.Test;

import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;


public class ClaferComparatorTest {

	@Test
	public void testComparator() {
		String modelFilename = "src/test/resources/security.js";
		File modelFile = CodeGenUtils.getResourceFromWithin(modelFilename);
		String taskName = "SecurityTestTask";

		InstanceGenerator instanceGenerator = new InstanceGenerator(modelFile.getAbsolutePath(), "c0_" + taskName, "");
		instanceGenerator.generateInstances(new HashMap<>());

		// the first instance has a security clafer as a first child
		// it should be the strongest security possible, namely 4
		assertEquals(Integer.valueOf(4), (Integer) ((InstanceClafer) instanceGenerator.getGeneratedInstances().get(0).getChildren()[0].getRef()).getRef());
	}

}
