package de.cognicrypt.codegenerator.primitive.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.clafer.ClaferGenerator;

public class CopyClaferHeaderTest {

	@Test
	public void test() {

		// test with existing files
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeader, Constants.claferFooter);
		assert (finalClafer.exists());

		// test with non-existing files
		File finalClafer1 = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooterTest);
		assertTrue(finalClafer1 instanceof File);
		assertFalse(finalClafer1.exists());

	}

	//	@Test(expected = FileNotFoundException.class)
	//public void testFileNotFound() {
	//	File finalClafer1 = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooter);
	//}

}
