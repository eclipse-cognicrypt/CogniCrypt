package de.cognicrypt.codegenerator.primitive.providerUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private Manifest manifest;
	private String name;

	public ProviderFile(String name) {
		this.name = name;
		this.manifest = new Manifest();
	}

	/**
	 * Archive files with option to exclude the main containing folder
	 * 
	 * @param fileToZip
	 * @param zipFile
	 * @param excludeContainingFolder
	 * @throws IOException
	 */
	public void zipFile(String fileToZip, File zipFile, boolean excludeContainingFolder) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		File srcFile = new File(fileToZip);
		if (excludeContainingFolder && srcFile.isDirectory()) {
			for (String fileName : srcFile.list()) {
				addToZip("", fileToZip + "/" + fileName, zipOut);
			}
		} else {
			addToZip("", fileToZip, zipOut);
		}

		zipOut.flush();
		zipOut.close();

		System.out.println("Successfully created " + zipFile.getName());
	}

	static private void addToZip(String path, String srcFile, ZipOutputStream zipOut) throws IOException {
		File file = new File(srcFile);
		String filePath = "".equals(path) ? file.getName() : path + "/" + file.getName();
		if (file.isDirectory()) {
			for (String fileName : file.list()) {
				addToZip(filePath, srcFile + "/" + fileName, zipOut);
			}
		} else {
			zipOut.putNextEntry(new ZipEntry(filePath));
			FileInputStream in = new FileInputStream(srcFile);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}
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


}
