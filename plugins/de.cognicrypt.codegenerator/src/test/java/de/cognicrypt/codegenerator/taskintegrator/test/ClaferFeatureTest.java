package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.clafer.javascript.Javascript;
import org.clafer.javascript.JavascriptFile;
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

		assertEquals(claferFeature.getfeatureProperties(), featureProperties);
	}
	
	public static boolean filesEqual(String expectedFilename, String actualFilename) throws IOException {
		Path expectedFilePath = Paths.get(expectedFilename);
		Path actualFilePath = Paths.get(actualFilename);

		byte[] expectedBytes = Files.readAllBytes(expectedFilePath);
		byte[] actualBytes = Files.readAllBytes(actualFilePath);
		
		return Arrays.equals(expectedBytes, actualBytes);
	}

	@Test
	public final void testClaferFeatureToString() throws IOException {
		// file to test against
		String expectedFilename = testFileFolder + "testFile1.cfr";

		// programmatically created Clafer feature
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		cfrFeature.getfeatureProperties().add(new FeatureProperty("securityLevel", "Security"));

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
		assertTrue(filesEqual(expectedFilename, actualFilename));
	}
	
	@Test
	public final void testWriteCFRFile() {
		fail("Not yet implemented");
	}

	@Test
	public final void testImplementMissingFeatures() {
		fail("Not yet implemented");
	}

	@Test
	public final void testSolveClaferFeature() throws IOException {
		String fileName = testFileFolder + "testFile2.cfr";
		File inputFile;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("clafer", "-k", "-m", "choco", fileName);
			processBuilder.redirectErrorStream(true);
			Process compilerProcess = processBuilder.start();

			InputStream is = compilerProcess.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			compilerProcess.waitFor();
			
			if (compilerProcess.exitValue() != 0) {
				System.out.println("Clafer compilation error: make sure your model is correct. Aborting...");
			}
		} catch (Exception e) {
			System.out.println("Abnormal Clafer compiler termination. Aborting...");
			e.printStackTrace();
		}

		// replace the extension to .js
		int extPos = fileName.lastIndexOf(".");
		if (extPos != -1) {
			fileName = fileName.substring(0, extPos) + ".js";
		}

		// change the inputFile to the resulting .js file
		inputFile = new File(fileName);

		// run the different modes
		JavascriptFile javascriptFile = null;
		try {
			System.out.println("=========== Parsing+Typechecking " + fileName + "  =============");
			javascriptFile = Javascript.readModel(inputFile);
		} catch (Exception e) {
			System.out.println("Unhandled compilation error occured. Please report this problem.");
			System.out.println(e.getMessage());
		}

		fail("Not yet implemented");
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
