/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.test;

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

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.core.Constants;

public class ClaferModelTest {

	public static final String testFileFolder = "src/test/resources/taskintegrator/";
	public static final ArrayList<String> testFiles = new ArrayList<>();

	@Test
	public final void testGetMissingFeatures() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature aesFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
		ClaferFeature securityFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Security", "Enum -> integer");
		ClaferFeature performanceFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Performance", "Enum->integer");

		claferModel.add(aesFeature);
		claferModel.add(securityFeature);
		claferModel.add(performanceFeature);

		ClaferModel missingFeaturesAES = claferModel.getMissingFeatures(aesFeature);
		assertEquals(1, missingFeaturesAES.getClaferModel().size());
		assertTrue(missingFeaturesAES.getClaferModel().get(0).getFeatureName().equals("Algorithm"));

		ClaferModel missingFeaturesSecurity = claferModel.getMissingFeatures(securityFeature);
		assertEquals(1, missingFeaturesSecurity.getClaferModel().size());
		assertEquals(ClaferFeature.class, missingFeaturesSecurity.getFeature("Enum").getClass());

		ClaferModel missingFeaturesPerformance = claferModel.getMissingFeatures(performanceFeature);
		assertEquals(1, missingFeaturesPerformance.getClaferModel().size());
		assertEquals(ClaferFeature.class, missingFeaturesPerformance.getFeature("Enum").getClass());
	}

	@Test
	public final void testDoNotImplementPrimitiveTypes() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new ClaferProperty("number", "integer"));
		featureProperties.add(new ClaferProperty("characters", "string"));
		cfrFeature.setFeatureProperties(featureProperties);
		claferModel.add(cfrFeature);

		claferModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, "myInt", "integer"));
		claferModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, "myDouble", "double"));

		for (ClaferFeature refFeature : claferModel) {
			ClaferModel missingFeatures = claferModel.getMissingFeatures(refFeature);
			assertTrue(missingFeatures.getClaferModel().size() == 0);
		}
	}

	@Test
	public final void testNoDuplicateFeaturesImplemented() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
		claferModel.add(cfrFeature);

		cfrFeature.getFeatureProperties().add(new ClaferProperty("p1", "int"));
		cfrFeature.getFeatureProperties().add(new ClaferProperty("p2", "int"));
		cfrFeature.getFeatureProperties().add(new ClaferProperty("p3", "int"));

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
		ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new ClaferProperty("1", ""));
		featureProperties.add(new ClaferProperty("2", ""));
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
		 * Create Clafer feature abstract Algorithm securityLevel -> Security
		 */
		ClaferFeature algoFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		ArrayList<ClaferProperty> propertyList = new ArrayList<>();
		propertyList.add(new ClaferProperty("securityLevel", "Security"));
		algoFeature.setFeatureProperties(propertyList);

		// add feature to an empty list
		ClaferModel claferModel = new ClaferModel();
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
		ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.CONCRETE, "A", "B");
		ArrayList<ClaferProperty> propertiesA = new ArrayList<>();
		propertiesA.add(new ClaferProperty("1", "x"));
		propertiesA.add(new ClaferProperty("2", "y"));
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

		ArrayList<ClaferProperty> propertiesA = new ArrayList<>();

		ClaferProperty propertyA1 = new ClaferProperty("1", "x");
		ClaferProperty propertyA2 = new ClaferProperty("2", "y");
		propertiesA.add(propertyA1);
		propertiesA.add(propertyA2);
		featureA.setFeatureProperties(propertiesA);
		ClaferFeature featureB = new ClaferFeature(Constants.FeatureType.ABSTRACT, "B", "");

		ClaferFeature featureC = new ClaferFeature(Constants.FeatureType.CONCRETE, "C", "");

		// create the same properties as or A on purpose
		// they are stored in different objects and should not be found
		ArrayList<ClaferProperty> propertiesC = new ArrayList<>();
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
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature cfrFeatureA = new ClaferFeature(Constants.FeatureType.ABSTRACT, "A", "");
		ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		featureProperties.add(new ClaferProperty("size", "int"));
		cfrFeatureA.setFeatureProperties(featureProperties);
		claferModel.add(cfrFeatureA);

		if (!claferModel.toBinary(testFileFolder + "serializationTest.tmp")) {
			fail("Serialization failed");
		}

		ClaferModel modelFromBinaries = ClaferModel.createFromBinaries(testFileFolder + "serializationTest.tmp");
		assertEquals(1, modelFromBinaries.getClaferModel().size());
		assertEquals("A", modelFromBinaries.getClaferModel().get(0).getFeatureName());

		// remember for clean-up
		testFiles.add("serializationTest.tmp");
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// generate the paths and delete the files
		for (String filename : testFiles) {
			Path path = Paths.get(testFileFolder + filename);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}

	}

}
