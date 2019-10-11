/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.ClaferPatternEnumGenerator;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class DefaultFeatureSetTest {

	public static final String binClaferFolder = Constants.CFR_BIN_FILE_DIRECTORY_PATH;
	public static final ArrayList<String> tmpFiles = new ArrayList<>();

	/**
	 * this test is mainly designed to programmatically create the binary file that contains the default set of features to be displayed in the Clafer model page of the task
	 * integration wizard
	 */
	@Test
	public void testFeatureSetCreation() {
		final ClaferModel defaultFeatureSet = new ClaferModel();

		final ClaferFeature enumFtr = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Enum", "");
		defaultFeatureSet.add(enumFtr);
		final ClaferFeature algorithmFtr = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		defaultFeatureSet.add(algorithmFtr);

		final ArrayList<String> securityLevels = new ArrayList<>();
		securityLevels.add("Broken");
		securityLevels.add("Weak");
		securityLevels.add("Medium");
		securityLevels.add("Strong");

		final ClaferPatternEnumGenerator securityLevelGenerator = new ClaferPatternEnumGenerator("Security", true);
		defaultFeatureSet.add(securityLevelGenerator.getClaferModel(securityLevels));

		final ArrayList<String> performanceLevels = new ArrayList<>();
		performanceLevels.add("VerySlow");
		performanceLevels.add("Slow");
		performanceLevels.add("Fast");
		performanceLevels.add("VeryFast");

		final ClaferPatternEnumGenerator performanceLevelGenerator = new ClaferPatternEnumGenerator("Performance", true);
		defaultFeatureSet.add(performanceLevelGenerator.getClaferModel(performanceLevels));

		final ClaferFeature taskFtr = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Task", "");
		taskFtr.addFeatureProperty(new ClaferProperty("description", "string"));

		defaultFeatureSet.add(taskFtr);

		final String datFilename = binClaferFolder + Constants.DEFAULT_FEATURE_SET_FILE + Constants.CFR_BIN_EXTENSION;
		defaultFeatureSet.toBinary(datFilename);
		final String cfrFilename = binClaferFolder + Constants.DEFAULT_FEATURE_SET_FILE + Constants.CFR_EXTENSION;
		defaultFeatureSet.toFile(cfrFilename);

		tmpFiles.add(binClaferFolder + Constants.DEFAULT_FEATURE_SET_FILE + Constants.JS_EXTENSION);

		assertTrue(ClaferModel.compile(cfrFilename));
		assertNotNull(ClaferModel.createFromBinaries(datFilename));
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// generate the paths and delete the files
		for (final String filename : tmpFiles) {
			final Path path = Paths.get(filename);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}

	}

}
