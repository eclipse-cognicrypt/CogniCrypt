/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class ClaferFeatureTest {

	public static final String testFileFolder = "src/test/resources/taskintegrator/";

	@Test
	public final void testPropertyAmount() {
		final ArrayList<ClaferProperty> featureProperties = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			featureProperties.add(new ClaferProperty("featureProperty" + String.valueOf(i), "propertyType"));
		}
		final ClaferFeature claferFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "testFeature", "");
		claferFeature.setFeatureProperties(featureProperties);

		assertEquals(claferFeature.getFeatureProperties(), featureProperties);
	}

	@Test
	public final void testClaferFeatureToString() throws IOException {
		// file to test against
		final String expectedFilename = testFileFolder + "testFile1.cfr";

		// programmatically created Clafer feature
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		cfrFeature.getFeatureProperties().add(new ClaferProperty("securityLevel", "Security"));

		// generate file from ClaferFeature instance
		final String actualFilename = testFileFolder + "testFile1_tmp.cfr";
		final Path actualPath = Paths.get(actualFilename);

		final Charset charset = Charset.forName("UTF-8");
		final String s = cfrFeature.toString();
		try (BufferedWriter writer = Files.newBufferedWriter(actualPath, charset)) {
			writer.write(s, 0, s.length());
		}
		catch (final IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		// compare the files
		final Object[] expectedLines = Files.readAllLines(Paths.get(expectedFilename)).toArray();
		final Object[] actualLines = Files.readAllLines(Paths.get(actualFilename)).toArray();
		assertArrayEquals(expectedLines, actualLines);
	}

	@Test
	public final void testGetInheritedProperties() {
		final ClaferModel claferModel = new ClaferModel();
		final ClaferFeature featureA = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Z", "");

		final ArrayList<ClaferProperty> propertiesA = new ArrayList<ClaferProperty>();
		final ClaferProperty expectedProperty = new ClaferProperty("prop", "integer");
		propertiesA.add(expectedProperty);
		featureA.setFeatureProperties(propertiesA);

		final ClaferFeature featureB = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Y", "Z");
		final ClaferFeature featureC = new ClaferFeature(Constants.FeatureType.CONCRETE, "X", "Y");

		claferModel.add(featureA);
		claferModel.add(featureB);
		claferModel.add(featureC);

		final ArrayList<ClaferProperty> inheritedPropertiesC = featureC.getInheritedProperties(claferModel);
		assertTrue(inheritedPropertiesC.contains(expectedProperty));

		final ArrayList<ClaferProperty> inheritedPropertiesB = featureB.getInheritedProperties(claferModel);
		assertTrue(inheritedPropertiesB.contains(expectedProperty));

		final ArrayList<ClaferProperty> inheritedPropertiesA = featureA.getInheritedProperties(claferModel);
		assertTrue(inheritedPropertiesA.contains(expectedProperty));
	}

	@Test
	public final void testGetDependencies() {
		// test a single reference clafer
		final ClaferFeature refClafer = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Security", "Enum -> integer");
		final Set<String> dependencies = refClafer.getDependencies();
		assertEquals(2, dependencies.size());

		// test an instance of the reference clafer
		final ClaferFeature refClaferInstance = new ClaferFeature(Constants.FeatureType.CONCRETE, "Broken", "Security = 0");
		final Set<String> refClaferDeps = refClaferInstance.getDependencies();
		assertTrue(refClaferDeps.contains("Security"));

		// test a rather common clafer
		final ClaferFeature securityClafer = new ClaferFeature(Constants.FeatureType.CONCRETE, "Security", "Enum");
		final ArrayList<ClaferProperty> securityProperties = new ArrayList<>();
		securityProperties.add(new ClaferProperty("keysize", "integer"));
		securityProperties.add(new ClaferProperty("blocksize", "Blocksize"));
		securityClafer.setFeatureProperties(securityProperties);

		final Set<String> securityDeps = securityClafer.getDependencies();
		assertTrue(securityDeps.contains("Enum"));
		assertTrue(securityDeps.contains("integer"));
		assertTrue(securityDeps.contains("Blocksize"));
		assertEquals(3, securityDeps.size());
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// gather all files to be deleted
		final ArrayList<String> temporaryFiles = new ArrayList<>();
		temporaryFiles.add(testFileFolder + "testFile1_tmp.cfr");

		// generate the paths and delete the files if they exist
		for (final String filename : temporaryFiles) {
			final Path path = Paths.get(filename);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}

	}

	@Test
	public final void testHasProperties() {
		// test without properties
		final ClaferFeature cfrFeatureNoProperties = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		assertEquals(false, cfrFeatureNoProperties.hasProperties());

		// test with only empty properties
		final ClaferFeature cfrFeatureEmpty = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		final ArrayList<ClaferProperty> emptyProperties = new ArrayList<>();
		emptyProperties.add(new ClaferProperty("", ""));
		cfrFeatureEmpty.setFeatureProperties(emptyProperties);
		assertEquals(false, cfrFeatureEmpty.hasProperties());

		// test with properties
		final ClaferFeature cfrFeatureNonEmpty = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		final ArrayList<ClaferProperty> properties = new ArrayList<>();
		properties.add(new ClaferProperty("keysize", "integer"));
		cfrFeatureNonEmpty.setFeatureProperties(properties);
		assertEquals(true, cfrFeatureNonEmpty.hasProperties());
	}

	@Test
	public final void hasPropertySatisfying() {
		final Predicate<? super ClaferProperty> constraintIntType = (e -> e.getPropertyType().equals("integer"));
		final Predicate<? super ClaferProperty> constraintStringType = (e -> e.getPropertyType().equals("string"));

		// test without properties
		final ClaferFeature cfrFeatureNoProperties = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		assertEquals(false, cfrFeatureNoProperties.hasPropertiesSatisfying(constraintIntType));
		assertEquals(false, cfrFeatureNoProperties.hasPropertiesSatisfying(constraintStringType));

		// test with only empty properties
		final ClaferFeature cfrFeatureEmpty = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		final ArrayList<ClaferProperty> emptyProperties = new ArrayList<>();
		emptyProperties.add(new ClaferProperty("", ""));
		cfrFeatureEmpty.setFeatureProperties(emptyProperties);
		assertEquals(false, cfrFeatureEmpty.hasPropertiesSatisfying(constraintIntType));
		assertEquals(false, cfrFeatureEmpty.hasPropertiesSatisfying(constraintStringType));

		// test with two different constraints
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		final ArrayList<ClaferProperty> properties = new ArrayList<>();
		properties.add(new ClaferProperty("keysize", "integer"));
		properties.add(new ClaferProperty("rounds", "integer"));
		cfrFeature.setFeatureProperties(properties);

		assertEquals(true, cfrFeature.hasPropertiesSatisfying(constraintIntType));
		assertEquals(false, cfrFeature.hasPropertiesSatisfying(constraintStringType));
	}

	@Test
	public final void hasPropertyNeedle() {
		final String needleKeysize = "keysize";
		final String needleBlocksize = "blocksize";

		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", "");
		final ArrayList<ClaferProperty> properties = new ArrayList<>();
		properties.add(new ClaferProperty("keysize", "integer"));
		properties.add(new ClaferProperty("rounds", "integer"));
		cfrFeature.setFeatureProperties(properties);

		assertEquals(true, cfrFeature.hasProperty(needleKeysize));
		assertEquals(false, cfrFeature.hasProperty(needleBlocksize));
	}
}
