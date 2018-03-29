package clafer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedHashMap;

import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.clafer.ClaferGenerator;

public class ClaferGeneratorTest {

	@Test
	public void existingFiles() {

		// test with existing files
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeader, Constants.claferFooter);
		assert (finalClafer.exists());
	}

	@Test
	public void claferHeaderExists() {

		// test with claferHeader Exists
		File finalClafer2 = ClaferGenerator.copyClaferHeader(Constants.claferHeader, Constants.claferFooterTest);
		assertTrue(finalClafer2 instanceof File);
		assertTrue(finalClafer2.exists());
	}

	@Test
	public void nonExistingFiles() {

		// test with non-existing files
		File finalClafer1 = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooterTest2);
		assertTrue(finalClafer1 instanceof File);
		assertFalse(finalClafer1.exists());
	}
	@Test
	public void nonExistingFinalClafer() {
		LinkedHashMap<String, String> userInput = new LinkedHashMap<String, String>();
		userInput.put("name", "Cryptox");
		userInput.put("description", "This is description");
		userInput.put("Blocksize", "64");
		userInput.put("Keysize1", "654");
		userInput.put("mode", "OFB | CFB");
		userInput.put("Padding", "ZeroPadding|OneAndZeroes Padding");

		// test with non-existing files
		File finalClafer1 = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooterTest3);
		ClaferGenerator.printClafer(userInput, finalClafer1);
		assertTrue(finalClafer1 instanceof File);
		assertTrue(finalClafer1.exists());
	}

	@Test(expected = NullPointerException.class)
	public void ifMapEmpty() {

		// test with non-existing files
		File finalClafer1 = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooterTest3);
		ClaferGenerator.printClafer(null, finalClafer1);
		assertTrue(finalClafer1 instanceof File);
		assertFalse(finalClafer1.exists());
	}

}
