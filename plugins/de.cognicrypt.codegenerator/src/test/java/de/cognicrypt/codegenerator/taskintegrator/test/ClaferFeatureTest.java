package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class ClaferFeatureTest {

	@Test
	public final void testPropertyAmount() {
		ArrayList<FeatureProperty> featureProperties = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			featureProperties.add(new FeatureProperty("featureProperty" + String.valueOf(i), "propertyType"));
		}
		ClaferFeature claferFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "testFeature", "");
		claferFeature.setFeatureProperties(featureProperties);

		assertEquals(claferFeature.getfeatureProperties(), featureProperties);
	}

	@Test
	public final void testFilesEqual() throws IOException {
		String testFileFolder = "src/test/resources/taskintegrator/";
		String testFile1 = testFileFolder + "testFile1.cfr";

		Path file1 = Paths.get(testFile1);
		Path file2 = Paths.get(testFile1);

		byte[] f1 = Files.readAllBytes(file1);
		byte[] f2 = Files.readAllBytes(file2);

		assertTrue(Arrays.equals(f1, f2));
	}

}
