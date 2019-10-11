/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class ClaferModelTest {

	public static final String testFileFolder = "src/test/resources/taskintegrator/";
	public static final ArrayList<String> testFiles = new ArrayList<>();

	@Test
	public final void testGetMissingFeatures() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature aesFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
		final ClaferFeature securityFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Security", "Enum -> integer");
		final ClaferFeature performanceFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Performance", "Enum->integer");

		claferModel.add(aesFeature);
		claferModel.add(securityFeature);
		claferModel.add(performanceFeature);

		final ClaferModel missingFeaturesAES = claferModel.getMissingFeatures(aesFeature);
		assertEquals(1, missingFeaturesAES.getClaferModel().size());
		assertTrue(missingFeaturesAES.getClaferModel().get(0).getFeatureName().equals("Algorithm"));

		final ClaferModel missingFeaturesSecurity = claferModel.getMissingFeatures(securityFeature);
		assertEquals(1, missingFeaturesSecurity.getClaferModel().size());
		assertEquals(ClaferFeature.class, missingFeaturesSecurity.getFeature("Enum").getClass());

		final ClaferModel missingFeaturesPerformance = claferModel.getMissingFeatures(performanceFeature);
		assertEquals(1, missingFeaturesPerformance.getClaferModel().size());
		assertEquals(ClaferFeature.class, missingFeaturesPerformance.getFeature("Enum").getClass());
	}

	@Test
	public final void testDoNotImplementPrimitiveTypes() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		final ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new ClaferProperty("number", "integer"));
		featureProperties.add(new ClaferProperty("characters", "string"));
		cfrFeature.setFeatureProperties(featureProperties);
		claferModel.add(cfrFeature);

		claferModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, "myInt", "integer"));
		claferModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, "myDouble", "double"));

		for (final ClaferFeature refFeature : claferModel) {
			final ClaferModel missingFeatures = claferModel.getMissingFeatures(refFeature);
			assertTrue(missingFeatures.getClaferModel().size() == 0);
		}
	}

	@Test
	public final void testNoDuplicateFeaturesImplemented() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
		claferModel.add(cfrFeature);

		cfrFeature.getFeatureProperties().add(new ClaferProperty("p1", "int"));
		cfrFeature.getFeatureProperties().add(new ClaferProperty("p2", "int"));
		cfrFeature.getFeatureProperties().add(new ClaferProperty("p3", "int"));

		claferModel.implementMissingFeatures(cfrFeature);

		for (final ClaferFeature refFeature : claferModel) {
			for (final ClaferFeature curFeature : claferModel) {
				if (refFeature != curFeature && refFeature.getFeatureName().equals(curFeature.getFeatureName())) {
					fail("Conflicting features named \"" + refFeature.getFeatureName() + "\" found");
				}
			}
		}
	}

	@Test
	public final void testNoEmptyFeatureInheritance() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "");
		claferModel.implementMissingFeatures(cfrFeature);

		for (final ClaferFeature currentFeature : claferModel) {
			assertTrue(!currentFeature.getFeatureName().isEmpty());
		}
	}

	@Test
	public final void testHasFeature() {
		final ClaferModel claferModel = new ClaferModel();
		claferModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", ""));

		assertTrue(claferModel.hasFeature("AES"));
	}

	@Test
	public final void testNoEmptyPropertyType() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "A", "B");
		final ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new ClaferProperty("1", ""));
		featureProperties.add(new ClaferProperty("2", ""));
		claferModel.add(cfrFeature);
		cfrFeature.setFeatureProperties(featureProperties);
		claferModel.implementMissingFeatures(cfrFeature);

		for (final ClaferFeature currentFeature : claferModel) {
			assertTrue(!currentFeature.getFeatureName().isEmpty());
		}
	}

	@Test
	public final void testCompileClaferFeature() throws IOException {
		final String temporaryCfrFile = testFileFolder + "testFile2_tmp.cfr";

		/**
		 * Create Clafer feature abstract Algorithm securityLevel -> Security
		 */
		final ClaferFeature algoFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		final ArrayList<ClaferProperty> propertyList = new ArrayList<>();
		propertyList.add(new ClaferProperty("securityLevel", "Security"));
		algoFeature.setFeatureProperties(propertyList);

		// add feature to an empty list
		final ClaferModel claferModel = new ClaferModel();
		claferModel.add(algoFeature);

		// automatically create missing features (a concrete Clafer Security is supposed to be created)
		claferModel.implementMissingFeatures(algoFeature);
		claferModel.toFile(temporaryCfrFile);
		assertTrue(ClaferModel.compile(temporaryCfrFile));

		// remember for clean-up
		testFiles.add("testFile2_tmp.cfr");
		testFiles.add("testFile2_tmp.js");
	}

	@Test
	public final void testRemoveUnusedFeatures() {
		final ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.CONCRETE, "A", "B");
		final ArrayList<ClaferProperty> propertiesA = new ArrayList<>();
		propertiesA.add(new ClaferProperty("1", "x"));
		propertiesA.add(new ClaferProperty("2", "y"));
		featureA.setFeatureProperties(propertiesA);

		final ClaferModel claferModel = new ClaferModel();
		claferModel.add(featureA);

		claferModel.implementMissingFeatures(featureA);

		// change the type of property 1 to x
		// y will be unused
		propertiesA.get(1).setPropertyType("x");

		// look for y in the model of unused features
		final ClaferModel unusedFeatures = claferModel.getUnusedFeatures();
		ClaferFeature featureY = null;
		for (final ClaferFeature cfrFeature : unusedFeatures) {
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
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.CONCRETE, "A", "B");

		final ArrayList<ClaferProperty> propertiesA = new ArrayList<>();

		final ClaferProperty propertyA1 = new ClaferProperty("1", "x");
		final ClaferProperty propertyA2 = new ClaferProperty("2", "y");
		propertiesA.add(propertyA1);
		propertiesA.add(propertyA2);
		featureA.setFeatureProperties(propertiesA);
		final ClaferFeature featureB = new ClaferFeature(Constants.FeatureType.ABSTRACT, "B", "");

		final ClaferFeature featureC = new ClaferFeature(Constants.FeatureType.CONCRETE, "C", "");

		// create the same properties as or A on purpose
		// they are stored in different objects and should not be found
		final ArrayList<ClaferProperty> propertiesC = new ArrayList<>();
		propertiesC.add(new ClaferProperty("1", "x"));
		propertiesC.add(new ClaferProperty("2", "y"));
		featureC.setFeatureProperties(propertiesA);

		claferModel.add(featureA);
		claferModel.add(featureB);
		claferModel.add(featureC);

		System.out.println();

		assertEquals(featureA, claferModel.getParentFeatureOfProperty(propertyA1));
		assertEquals(null, claferModel.getParentFeatureOfProperty(new ClaferProperty("1", "x")));
	}

	@Test
	public final void testModelSerialization() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature cfrFeatureA = new ClaferFeature(Constants.FeatureType.ABSTRACT, "A", "");
		final ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new ClaferProperty("size", "int"));
		cfrFeatureA.setFeatureProperties(featureProperties);
		claferModel.add(cfrFeatureA);

		if (!claferModel.toBinary(testFileFolder + "serializationTest.tmp")) {
			fail("Serialization failed");
		}

		final ClaferModel modelFromBinaries = ClaferModel.createFromBinaries(testFileFolder + "serializationTest.tmp");
		assertEquals(1, modelFromBinaries.getClaferModel().size());
		assertEquals("A", modelFromBinaries.getClaferModel().get(0).getFeatureName());

		// remember for clean-up
		testFiles.add("serializationTest.tmp");
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// generate the paths and delete the files
		for (final String filename : testFiles) {
			final Path path = Paths.get(testFileFolder + filename);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}

	}

}
