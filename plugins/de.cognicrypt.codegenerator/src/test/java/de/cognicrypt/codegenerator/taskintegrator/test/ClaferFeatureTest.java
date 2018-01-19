package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
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

	@Test
	public final void testImplementMissingFeatures() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
				
		ClaferModel addedFeatures = claferModel.implementMissingFeatures(cfrFeature); 
		assertTrue(addedFeatures.getClaferModel().size() == 1);
		assertTrue(addedFeatures.getClaferModel().get(0).getFeatureName().equals("Algorithm"));
	}

	@Test
	public final void testNoDuplicateFeaturesImplemented() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
		claferModel.add(cfrFeature);

		cfrFeature.getFeatureProperties().add(new FeatureProperty("p1", "int"));
		cfrFeature.getFeatureProperties().add(new FeatureProperty("p2", "int"));
		cfrFeature.getFeatureProperties().add(new FeatureProperty("p3", "int"));

		claferModel.implementMissingFeatures(cfrFeature);

		for (ClaferFeature refFeature : claferModel) {
			for (ClaferFeature curFeature : claferModel) {
				if (refFeature != curFeature && refFeature.getFeatureName().equals(curFeature.getFeatureName())) {
					fail("Conflicting features named \"" + refFeature.getFeatureName() + "\" found");
				}
			}
		}
	}

	@Test
	public final void testNoEmptyFeatureInheritance() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "");
		claferModel.implementMissingFeatures(cfrFeature);

		for (ClaferFeature currentFeature : claferModel) {
			assertTrue(!currentFeature.getFeatureName().isEmpty());
		}
	}
	
	@Test
	public final void testHasFeature() {
		ClaferModel claferModel = new ClaferModel();
		claferModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", ""));

		assertTrue(claferModel.hasFeature("AES"));
	}

	@Test
	public final void testNoEmptyPropertyType() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "A", "B");
		ArrayList<FeatureProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new FeatureProperty("1", ""));
		featureProperties.add(new FeatureProperty("2", ""));
		claferModel.add(cfrFeature);
		cfrFeature.setFeatureProperties(featureProperties);
		claferModel.implementMissingFeatures(cfrFeature);

		for (ClaferFeature currentFeature : claferModel) {
			assertTrue(!currentFeature.getFeatureName().isEmpty());
		}
	}

	@Test
	public final void testCompileClaferFeature() throws IOException {
		String temporaryCfrFile = testFileFolder + "testFile2_tmp.cfr";

		/**
		 * Create Clafer feature
		 * abstract Algorithm
		 *   securityLevel -> Security
		 */
		ClaferFeature algoFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		ArrayList<FeatureProperty> propertyList = new ArrayList<>();
		propertyList.add(new FeatureProperty("securityLevel", "Security"));
		algoFeature.setFeatureProperties(propertyList);

		// add feature to an empty list
		ClaferModel claferModel = new ClaferModel();
		claferModel.add(algoFeature);

		// automatically create missing features (a concrete Clafer Security is supposed to be created)
		claferModel.implementMissingFeatures(algoFeature);
		claferModel.toFile(temporaryCfrFile);
		assertTrue(ClaferModel.compile(temporaryCfrFile));
	}
	
	@Test
	public final void testRemoveUnusedFeatures() {
		ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.CONCRETE, "A", "B");
		ArrayList<FeatureProperty> propertiesA = new ArrayList<>();
		propertiesA.add(new FeatureProperty("1", "x"));
		propertiesA.add(new FeatureProperty("2", "y"));
		featureA.setFeatureProperties(propertiesA);

		ClaferModel claferModel = new ClaferModel();
		claferModel.add(featureA);
		
		claferModel.implementMissingFeatures(featureA);
		
		// change the type of property 1 to x
		// y will be unused
		propertiesA.get(1).setPropertyType("x");
		
		// look for y in the model of unused features
		ClaferModel unusedFeatures = claferModel.getUnusedFeatures();
		ClaferFeature featureY = null;
		for (ClaferFeature cfrFeature : unusedFeatures) {
			if (cfrFeature.getFeatureName().equals("y")) {
				featureY = cfrFeature;
			}
		}
		
		// assert one feature is unused and assert it to be y
		assertTrue(unusedFeatures.getClaferModel().size() == 1);
		assertTrue(unusedFeatures.getClaferModel().get(0) == featureY);
	}

	@Test
	public final void testGetParentFeatureOfProperty() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.CONCRETE, "A", "B");

		ArrayList<FeatureProperty> propertiesA = new ArrayList<>();
		

		FeatureProperty propertyA1 = new FeatureProperty("1", "x");
		FeatureProperty propertyA2 = new FeatureProperty("2", "y");
		propertiesA.add(propertyA1);
		propertiesA.add(propertyA2);
		featureA.setFeatureProperties(propertiesA);
		ClaferFeature featureB = new ClaferFeature(Constants.FeatureType.ABSTRACT, "B", "");

		ClaferFeature featureC = new ClaferFeature(Constants.FeatureType.CONCRETE, "C", "");

		// create the same properties as or A on purpose
		// they are stored in different objects and should not be found
		ArrayList<FeatureProperty> propertiesC = new ArrayList<>();
		propertiesC.add(new FeatureProperty("1", "x"));
		propertiesC.add(new FeatureProperty("2", "y"));
		featureC.setFeatureProperties(propertiesA);

		claferModel.add(featureA);
		claferModel.add(featureB);
		claferModel.add(featureC);

		System.out.println();
		
		assertEquals(featureA, claferModel.getParentFeatureOfProperty(propertyA1));
		assertEquals(null, claferModel.getParentFeatureOfProperty(new FeatureProperty("1", "x")));
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// gather all files to be deleted
		ArrayList<String> temporaryFiles = new ArrayList<>();
		temporaryFiles.add(testFileFolder + "testFile1_tmp.cfr");
		temporaryFiles.add(testFileFolder + "testFile2_tmp.cfr");
		temporaryFiles.add(testFileFolder + "testFile2_tmp.js");
		
		// generate the paths and delete the files
		for (String filename : temporaryFiles) {
			Path path = Paths.get(filename);
			Files.delete(path);
		}
		
	}

}
