package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class ClaferFeatureTest {

	public static final String testFileFolder = "src/test/resources/taskintegrator/";

	@Test
	public final void testPropertyAmount() {
		ArrayList<FeatureProperty> featureProperties = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			featureProperties.add(new FeatureProperty("featureProperty" + String.valueOf(i), "propertyType"));
		}
		ClaferFeature claferFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "testFeature", "");
		claferFeature.setFeatureProperties(featureProperties);

		assertEquals(claferFeature.getFeatureProperties(), featureProperties);
	}

	@Test
	public final void testClaferFeatureToString() throws IOException {
		// file to test against
		String expectedFilename = testFileFolder + "testFile1.cfr";

		// programmatically created Clafer feature
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		cfrFeature.getFeatureProperties().add(new FeatureProperty("securityLevel", "Security"));

		// generate file from ClaferFeature instance
		String actualFilename = testFileFolder + "testFile1_tmp.cfr";
		Path actualPath = Paths.get(actualFilename);

		Charset charset = Charset.forName("UTF-8");
		String s = cfrFeature.toString();
		try (BufferedWriter writer = Files.newBufferedWriter(actualPath, charset)) {
			writer.write(s, 0, s.length());
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		// compare the files
		Object[] expectedLines = Files.readAllLines(Paths.get(expectedFilename)).toArray();
		Object[] actualLines = Files.readAllLines(Paths.get(actualFilename)).toArray();
		assertArrayEquals(expectedLines, actualLines);
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// gather all files to be deleted
		ArrayList<String> temporaryFiles = new ArrayList<>();
		temporaryFiles.add(testFileFolder + "testFile1_tmp.cfr");
		
		// generate the paths and delete the files
		for (String filename : temporaryFiles) {
			Path path = Paths.get(filename);
			Files.delete(path);
		}
		
	}

}
