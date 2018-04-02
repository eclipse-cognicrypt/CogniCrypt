package crossing.e1.primitiveintegration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.providerUtils.ProviderFile;
import de.cognicrypt.codegenerator.utilities.Utils;

public class ProviderFileWriterTest {

	boolean elementExists = false;
	File jarFile = new File(Utils.getResourceFromWithin(Constants.testPrimitverFolder) + "jarTest.jar");
	ProviderFile providerFile;
	File folder;

	@Before
	public void setUp() throws IOException {
		providerFile = new ProviderFile("test provider");
		folder = Utils.getResourceFromWithin(Constants.testPrimitverFolder);
		providerFile.zipFile(folder.getAbsolutePath(), jarFile, true);
	}

	@Test
	public void createJarFileTest() {
		try {

			File[] files = folder.listFiles();
			for(File file: files) {
					providerFile.zipFile(file.getAbsolutePath(),jarFile, true);
				
			}
			JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();
				final String entryName = entry.getName();
				elementExists = fileExists(files, entryName);
			}
			assertEquals(elementExists, true);
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void compilteJavaFileTest() {
		File testJavaFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "testJava.java");
		providerFile.compileFile(testJavaFile);
		File testClassFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "testJava.class");
		assertTrue(testClassFile.exists());
	}

	@After
	public void deleteFile() {
		jarFile.delete();
	}

	private boolean fileExists(File[] files, String element) {
		boolean elementExists = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if (files[i].getName().equals(element))
					elementExists = true;
			}
		}

		return elementExists;
	}

}