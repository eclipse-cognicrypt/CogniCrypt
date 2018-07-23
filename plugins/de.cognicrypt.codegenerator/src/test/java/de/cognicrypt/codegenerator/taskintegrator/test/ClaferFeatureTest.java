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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.AfterClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.core.Constants;

public class ClaferFeatureTest {

	public static final String testFileFolder = "src/test/resources/taskintegrator/";

	@Test
	public final void testPropertyAmount() {
		ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			featureProperties.add(new ClaferProperty("featureProperty" + String.valueOf(i), "propertyType"));
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
		cfrFeature.getFeatureProperties().add(new ClaferProperty("securityLevel", "Security"));

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
	public final void testGetInheritedProperties() {
		ClaferModel claferModel = new ClaferModel();
		ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Z", "");

		ArrayList<ClaferProperty> propertiesA = new ArrayList<ClaferProperty>();
		ClaferProperty expectedProperty = new ClaferProperty("prop", "integer");
		propertiesA.add(expectedProperty);
		featureA.setFeatureProperties(propertiesA);

		ClaferFeature featureB = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Y", "Z");
		ClaferFeature featureC = new ClaferFeature(Constants.FeatureType.CONCRETE, "X", "Y");

		claferModel.add(featureA);
		claferModel.add(featureB);
		claferModel.add(featureC);

		ArrayList<ClaferProperty> inheritedPropertiesC = featureC.getInheritedProperties(claferModel);
		assertTrue(inheritedPropertiesC.contains(expectedProperty));

		ArrayList<ClaferProperty> inheritedPropertiesB = featureB.getInheritedProperties(claferModel);
		assertTrue(inheritedPropertiesB.contains(expectedProperty));

		ArrayList<ClaferProperty> inheritedPropertiesA = featureA.getInheritedProperties(claferModel);
		assertTrue(inheritedPropertiesA.contains(expectedProperty));
	}

	@Test
	public final void testGetDependencies() {
		// test a single reference clafer
		ClaferFeature refClafer = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Security", "Enum -> integer");
		Set<String> dependencies = refClafer.getDependencies();
		assertEquals(2, dependencies.size());

		// test an instance of the reference clafer
		ClaferFeature refClaferInstance = new ClaferFeature(Constants.FeatureType.CONCRETE, "Broken", "Security = 0");
		Set<String> refClaferDeps = refClaferInstance.getDependencies();
		assertTrue(refClaferDeps.contains("Security"));

		// test a rather common clafer
		ClaferFeature securityClafer = new ClaferFeature(Constants.FeatureType.CONCRETE, "Security", "Enum");
		ArrayList<ClaferProperty> securityProperties = new ArrayList<>();
		securityProperties.add(new ClaferProperty("keysize", "integer"));
		securityProperties.add(new ClaferProperty("blocksize", "Blocksize"));
		securityClafer.setFeatureProperties(securityProperties);

		Set<String> securityDeps = securityClafer.getDependencies();
		assertTrue(securityDeps.contains("Enum"));
		assertTrue(securityDeps.contains("integer"));
		assertTrue(securityDeps.contains("Blocksize"));
		assertEquals(3, securityDeps.size());
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// gather all files to be deleted
		ArrayList<String> temporaryFiles = new ArrayList<>();
		temporaryFiles.add(testFileFolder + "testFile1_tmp.cfr");

		// generate the paths and delete the files if they exist
		for (String filename : temporaryFiles) {
			Path path = Paths.get(filename);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}

	}

	@Test
	public final void testHasProperties() {
		// test without properties
		ClaferFeature cfrFeatureNoProperties = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		assertEquals(false, cfrFeatureNoProperties.hasProperties());

		// test with only empty properties
		ClaferFeature cfrFeatureEmpty = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		ArrayList<ClaferProperty> emptyProperties = new ArrayList<>();
		emptyProperties.add(new ClaferProperty("", ""));
		cfrFeatureEmpty.setFeatureProperties(emptyProperties);
		assertEquals(false, cfrFeatureEmpty.hasProperties());

		// test with properties
		ClaferFeature cfrFeatureNonEmpty = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		ArrayList<ClaferProperty> properties = new ArrayList<>();
		properties.add(new ClaferProperty("keysize", "integer"));
		cfrFeatureNonEmpty.setFeatureProperties(properties);
		assertEquals(true, cfrFeatureNonEmpty.hasProperties());
	}

	@Test
	public final void hasPropertySatisfying() {
		Predicate<? super ClaferProperty> constraintIntType = (e -> e.getPropertyType().equals("integer"));
		Predicate<? super ClaferProperty> constraintStringType = (e -> e.getPropertyType().equals("string"));

		// test without properties
		ClaferFeature cfrFeatureNoProperties = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		assertEquals(false, cfrFeatureNoProperties.hasPropertiesSatisfying(constraintIntType));
		assertEquals(false, cfrFeatureNoProperties.hasPropertiesSatisfying(constraintStringType));

		// test with only empty properties
		ClaferFeature cfrFeatureEmpty = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		ArrayList<ClaferProperty> emptyProperties = new ArrayList<>();
		emptyProperties.add(new ClaferProperty("", ""));
		cfrFeatureEmpty.setFeatureProperties(emptyProperties);
		assertEquals(false, cfrFeatureEmpty.hasPropertiesSatisfying(constraintIntType));
		assertEquals(false, cfrFeatureEmpty.hasPropertiesSatisfying(constraintStringType));

		// test with two different constraints
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		ArrayList<ClaferProperty> properties = new ArrayList<>();
		properties.add(new ClaferProperty("keysize", "integer"));
		properties.add(new ClaferProperty("rounds", "integer"));
		cfrFeature.setFeatureProperties(properties);

		assertEquals(true, cfrFeature.hasPropertiesSatisfying(constraintIntType));
		assertEquals(false, cfrFeature.hasPropertiesSatisfying(constraintStringType));
	}

	@Test
	public final void hasPropertyNeedle() {
		String needleKeysize = "keysize";
		String needleBlocksize = "blocksize";

		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		ArrayList<ClaferProperty> properties = new ArrayList<>();
		properties.add(new ClaferProperty("keysize", "integer"));
		properties.add(new ClaferProperty("rounds", "integer"));
		cfrFeature.setFeatureProperties(properties);

		assertEquals(true, cfrFeature.hasProperty(needleKeysize));
		assertEquals(false, cfrFeature.hasProperty(needleBlocksize));
	}
}
