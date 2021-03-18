package de.cognicrypt.integrator.task.plugintests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import junit.framework.AssertionFailedError;

public class ImportExportTest {

	IntegratorModel im;
	
	String pathPrefix = "src/test/Templates/";
	String iconLocation = "src/test/CoffeeAPI/coffee.png";
	
	String tempDirectory = "src/test/Temp";
	String exportReference = "src/test/ExportReference";
	
	String zipLocation = Constants.ECLIPSE_LOC_EXPORT_DIR + "/Test.zip";
	
	String guidedModeMultiReference = "src/test/MultiCopyReference";
	String guidedModeMultiZipLocation = "src/test/MultiCopyReference/ExportableTasks/Test.zip";
	
	@Before
	public void setupTest() {
		try {
			FileUtils.deleteDirectory(new File(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR));
		} catch (IOException e1) {
			fail("Directory should be deleted without errors");
		}
		
		IntegratorModel.resetInstance();
		im = IntegratorModel.getInstance();
	}
	
	@Test
	public void testExport() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");
			
			im.setGuidedModeChosen(true);
			im.setImportModeChosen(false);
			
			im.setLocationOfIconFile(new File(iconLocation));
			
			im.addQuestion();
			im.addAnswer(0);
			im.addAnswer(0);
			
		} catch (Exception e) {
			fail("Templates, question and answers should be added without errors");
		}
		
		try {
			im.copyTask();
			
			File tempDir = new File(tempDirectory);
			File exportReferenceDir = new File(exportReference);
			
			FileUtilities.unzipFile(zipLocation, tempDir);
			
			try {
				TestHelpers.checkDirectoriesAreEqual(Paths.get(tempDir.getAbsolutePath()), Paths.get(exportReferenceDir.getAbsolutePath()));
			} catch (IOException e) {
				fail("File operations should not fail");
			} catch (AssertionFailedError e) {
				fail(e.getMessage());
			}
		} catch (ErrorMessageException e) {
			fail("Export should be successful but got " + e.getMessage());
		}
	}
	
	@Test
	public void testImport() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");

			im.setLocationOfImportFile(new File(guidedModeMultiZipLocation));

			im.setImportModeChosen(true);
		} catch (Exception e) {
			fail("Templates, question and answers should be added without errors");
		}
		
		try {
			im.copyTask();

			try {
				TestHelpers.checkDirectoriesAreEqual(Paths.get(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR), Paths.get(new File(guidedModeMultiReference).getAbsolutePath()));
			} catch (IOException e) {
				fail("File operations should not fail");
			} catch (AssertionFailedError e) {
				fail(e.getMessage());
			}
		} catch (ErrorMessageException e) {
			fail("Import should be successful but got " + e.getMessage());
		}
	}
}
