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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.controllers.ClaferPatternEnumGenerator;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.core.Constants;

public class DefaultFeatureSetTest {

	public static final String binClaferFolder = Constants.CFR_BIN_FILE_DIRECTORY_PATH;
	public static final ArrayList<String> tmpFiles = new ArrayList<>();

	/**
	 * this test is mainly designed to programmatically create the binary file that contains the default set of features to be displayed in the Clafer model page of the task
	 * integration wizard
	 */
	@Test
	public void testFeatureSetCreation() {
		ClaferModel defaultFeatureSet = new ClaferModel();

		ClaferFeature enumFtr = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Enum", "");
		defaultFeatureSet.add(enumFtr);
		ClaferFeature algorithmFtr = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		defaultFeatureSet.add(algorithmFtr);

		ArrayList<String> securityLevels = new ArrayList<>();
		securityLevels.add("Broken");
		securityLevels.add("Weak");
		securityLevels.add("Medium");
		securityLevels.add("Strong");

		ClaferPatternEnumGenerator securityLevelGenerator = new ClaferPatternEnumGenerator("Security", true);
		defaultFeatureSet.add(securityLevelGenerator.getClaferModel(securityLevels));

		ArrayList<String> performanceLevels = new ArrayList<>();
		performanceLevels.add("VerySlow");
		performanceLevels.add("Slow");
		performanceLevels.add("Fast");
		performanceLevels.add("VeryFast");

		ClaferPatternEnumGenerator performanceLevelGenerator = new ClaferPatternEnumGenerator("Performance", true);
		defaultFeatureSet.add(performanceLevelGenerator.getClaferModel(performanceLevels));

		ClaferFeature taskFtr = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Task", "");
		taskFtr.addFeatureProperty(new ClaferProperty("description", "string"));

		defaultFeatureSet.add(taskFtr);

		String datFilename = binClaferFolder + Constants.DEFAULT_FEATURE_SET_FILE + Constants.CFR_BIN_EXTENSION;
		defaultFeatureSet.toBinary(datFilename);
		String cfrFilename = binClaferFolder + Constants.DEFAULT_FEATURE_SET_FILE + Constants.CFR_EXTENSION;
		defaultFeatureSet.toFile(cfrFilename);

		tmpFiles.add(binClaferFolder + Constants.DEFAULT_FEATURE_SET_FILE + Constants.JS_EXTENSION);

		assertTrue(ClaferModel.compile(cfrFilename));
		assertNotNull(ClaferModel.createFromBinaries(datFilename));
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// generate the paths and delete the files
		for (String filename : tmpFiles) {
			Path path = Paths.get(filename);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}

	}

}
