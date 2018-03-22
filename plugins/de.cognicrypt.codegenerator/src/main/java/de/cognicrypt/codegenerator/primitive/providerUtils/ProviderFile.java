package de.cognicrypt.codegenerator.primitive.providerUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.cognicrypt.codegenerator.Constants;

/**
 * A class that generate the provider file
 * 
 * @author Ahmed
 */

public class ProviderFile {

	public static int BUFFER_SIZE = 10240;
	private Manifest manifest;
	private String name;

	public ProviderFile(String name) {
		this.name = name;
		this.manifest = new Manifest();
	}

	/**
	 * 
	 * @param archiveFile
	 *        The generated jar
	 * @param tobeJared
	 *        Files to add into the jar file
	 * 
	 */
	public void createJarArchive(File archiveFile, File[] tobeJared) {
		try {
			byte buffer[] = new byte[BUFFER_SIZE];
			// Open archive file
			FileOutputStream stream = new FileOutputStream(archiveFile);
			JarOutputStream out = new JarOutputStream(stream, this.manifest);

			for (int i = 0; i < tobeJared.length; i++) {
				if (tobeJared[i] == null || !tobeJared[i].exists() || tobeJared[i].isDirectory())
					continue;
				System.out.println("Adding " + tobeJared[i].getName());

				// Add archive entry
				JarEntry jarAdd = new JarEntry(tobeJared[i].getName());
				jarAdd.setTime(tobeJared[i].lastModified());
				out.putNextEntry(jarAdd);

				// Write file to archive
				FileInputStream in = new FileInputStream(tobeJared[i]);
				int nRead;
				while (true) {
					int nRead1 = in.read(buffer, 0, buffer.length);
					if (nRead1 <= 0)
						break;
					out.write(buffer, 0, nRead1);
				}
				in.close();
			}
				out.close();
				stream.close();
				System.out.println("Adding completed OK");
			}
		 catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: " + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param owner
	 *        Name of the creator of the primitive
	 * @param classPaths
	 *        Paths of classes to add in the manifest
	 */
	public void createManifest(String owner, String[] classPaths) {
		Attributes global = this.manifest.getMainAttributes();
		global.put(Attributes.Name.MANIFEST_VERSION, "1.0.0");
		global.put(Attributes.Name.SPECIFICATION_TITLE, "Custom Provider");
		global.put(Attributes.Name.CONTENT_TYPE, "JCE Provider");
		global.put(new Attributes.Name("Created-By"), owner);
		for (String classPath : classPaths) {
			global.put(new Attributes.Name("Name"), classPath);
		}
	}


	/**
	 * Compile files
	 * 
	 * @param files
	 */
	public void compileFile(File file) {
		System.setProperty("java.home", lastAddedJDK().getAbsolutePath());

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
		compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();
	}

	
	//Get the last JDK from Java folder in local c:
		private static File lastAddedJDK() {
			File fl = new File(Constants.JAVA_BIN);
			FileFilter fileFilter = new WildcardFileFilter("jdk*");
			File[] files = fl.listFiles(fileFilter);
			long lastMod = Long.MIN_VALUE;
			File lastUpdatedFile = null;
			for (File file : files) {
				if (file.lastModified() > lastMod) {
					lastUpdatedFile = file;
					lastMod = file.lastModified();
				}
			}
			return lastUpdatedFile;
		}
	
	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	public Manifest getManifest() {
		return this.manifest;
	}
}
