package de.cognicrypt.codegenerator.primitive.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.jarpackager.IJarExportRunnable;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A class that generate the provider file 
 * 
 * @author Ahmed
 */

public class ProviderFile {

	public static int BUFFER_SIZE = 10240;
	private Manifest manifest;
	private String name;
	
	
	
	public ProviderFile(String name){
		this.name=name;
		this.manifest= new Manifest();
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
				while ((nRead = in.read(buffer, 0, buffer.length)) <= 0) { 
					out.write(buffer, 0, nRead);
				}
				in.close();
			}

			out.close();
			stream.close();
			System.out.println("Adding completed OK");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: " + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param owner
	 * 		  Name of the creator of the primitive
	 * @param classPaths
	 * 		  Paths of classes to add in the manifest		
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
	 * Get a class Object from a file
	 * @param ClassName
	 * 		  Path to the java file
	 * @return
	 * @throws Exception
	 */
	public Class loadClass(String ClassName, String ClassFolder) throws Exception {
		URLClassLoader loader = new URLClassLoader(new URL []{
			new URL("file://"+ClassFolder)
		});
		return loader.loadClass(ClassName);
//		JarFile jarFile = new JarFile(pathToJar);
//		Enumeration<JarEntry> e = jarFile.entries();
//
//		URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
//		URLClassLoader cl = URLClassLoader.newInstance(urls);
//
//		while (e.hasMoreElements()) {
//		    JarEntry je = e.nextElement();
//		    if(je.isDirectory() || !je.getName().endsWith(".class")){
//		        continue;
//		    }
//		    // -6 because of .class
//		    String className = je.getName().substring(0,je.getName().length()-6);
//		    className = className.replace('/', '.');
//		    Class c = cl.loadClass(className);

//		}
	}
	
	

	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	public Manifest getManifest() {
		return this.manifest;
	}
}
