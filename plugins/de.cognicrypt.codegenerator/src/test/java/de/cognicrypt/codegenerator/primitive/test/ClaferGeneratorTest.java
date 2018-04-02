package de.cognicrypt.codegenerator.primitive.test;

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
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTestR, Constants.claferFooterTestR);
		assert (finalClafer.exists());
	}

	@Test
	public void onlyClaferHeaderExists() {

		// test with claferHeader Exists
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTestR, Constants.claferFooterTest);
		assertTrue(finalClafer instanceof File);
		assertTrue(finalClafer.exists());
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

		// test with non-existing FinalClafer file
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTestR, Constants.claferFooterTest3);
		ClaferGenerator.printClafer(userInput, finalClafer);
		assertTrue(finalClafer instanceof File);
		assertTrue(finalClafer.exists());
	}

	@Test(expected = NullPointerException.class)
	public void ifMapEmpty() {

		// test with emptyMap
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooterTest3);
		ClaferGenerator.printClafer(null, finalClafer);
		assertTrue(finalClafer instanceof File);
		assertFalse(finalClafer.exists());
	}

}
