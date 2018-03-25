package crossing.e1.primitiveintegration.test;
 
 import static org.junit.Assert.*;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Enumeration;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.providerUtils.ProviderFile;
import de.cognicrypt.codegenerator.utilities.Utils;
 
 public class ProviderFileWriterTest {
 
 	ProviderFile providerFile = new ProviderFile("test provider");
 	String dirJar = "src/test/resources/test.jar";
 	boolean elementExists=false;
 	
 
 
// 	@Test
//	public void compilteJavaFileTest() {
//		File testJavaFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "testJava.java");
//		providerFile.compileFile(testJavaFile);
//		File testClassFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "testJava.class");
//		assertTrue(testClassFile.exists());
//	}
 	
 	public boolean check(File[] files, String element) {
 		boolean elementExists=false;
 		for (int i = 0; i < files.length; i++  ) {
 			if (files[i].isFile()) {
 				if (files[i].getName().equals(element))
 					elementExists=true;
 			}
 		}
 
 		return elementExists;
 	}
 	
 	
 
 	
// 	public void createJarFileTest() {
// 		try {
// 			
// 			File jarFile = new File(dirJar);
// 			File folder = Utils.getResourceFromWithin("src/test/resources");
// 			File[] files = folder.listFiles();
//// 			providerFile.createJarArchive(jarFile, files);
// 
// 			JarFile jar = new JarFile(jarFile);
// 			final Enumeration<JarEntry> entries = jar.entries();
// 			while (entries.hasMoreElements()) {
// 			final JarEntry entry = entries.nextElement();
// 			final String entryName = entry.getName();
// 			elementExists=check(files, entryName);
// 			}
// 			assertEquals(elementExists, true);
// 			jar.close();
// 			jarFile.delete();
// 
// 		} catch (IOException e) {
// 			e.printStackTrace();
// 		}
//
// 	}
 	

 
 	
 
 }