/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.core.Constants;

/**
 * A Code object contains java code source files. This files can be compiled during runtime with the method compile() and afterwards be executed by using the method run(...)
 *
 * @author Florian Breitfelder
 * @author Stefan Krueger
 */
public class CodeHandler {

	private Set<GeneratorClass> javaClasses;
	private List<File> classFiles;
	private boolean isCodeCompiled = false;

	/**
	 * constructor
	 * 
	 * @param generatedClasses
	 *        Array of file objects that include java code
	 */
	public CodeHandler(Set<GeneratorClass> generatedClasses) {
		this.javaClasses = generatedClasses;
		classFiles = new ArrayList<File>();
	}

	/**
	 * Writes the stored source code to the disk.
	 * 
	 * @throws Exception
	 */
	public File writeToDisk(final String folderPath) throws Exception {

		File fileOnDisk = new File(folderPath);
		fileOnDisk.mkdirs();
		for (GeneratorClass toBeGeneratedClass : javaClasses) {
			String path = fileOnDisk.getAbsolutePath() + Constants.outerFileSeparator + toBeGeneratedClass.getClassName() + ".java";
			try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
				fileOutputStream.write(toBeGeneratedClass.toString().getBytes("UTF-8"));
			} catch (Exception e) {
				throw new Exception("Writing source code to file failed.");
			}
			toBeGeneratedClass.setSourceFile(new File(path));
		}

		return fileOnDisk;
	}

	/**
	 * compiles the java code files that are included in javaCodeFiles
	 * 
	 * @return Array of generated class files.
	 * 
	 * @throws CompilationFailedException
	 *         If the compilation process was not successful an exception is thrown.
	 */
	public List<File> compile() throws CompilationFailedException {
		// setup compiler
		//		JavaCompiler compiler = new EclipseCompiler();
		//		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		//		List<File> javaCodeFiles = javaClasses.stream().map(c -> c.getAssociatedJavaFile()).collect(Collectors.toList());
		//		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaCodeFiles);
		//
		//		// start compilation process
		//		boolean state = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
		//
		//		if (state) { // if the compilation process was successful return list of class files
		//			for (int i = 0; i < javaClasses.size(); i++) {
		//				String path = javaCodeFiles.get(i).getAbsolutePath();
		//				path = path.substring(0, path.lastIndexOf(".")) + ".class";
		//				classFiles.add(i, new File(path));
		//			}
		//
		//			isCodeCompiled = true;
		return classFiles;
		//
		//		} else { // if the compilation failed throw exception
		//			isCodeCompiled = false;
		//			throw new CompilationFailedException("Compilation failed!");
		//		}
	}

	/**
	 * Executes a method of a class file.
	 * 
	 * @param clazz
	 *        Class that includes the method that should be executed.
	 * 
	 * @param method
	 *        Name of method that should be executed.
	 * 
	 * @param parameterTypes
	 *        Parameter types of method signature.
	 * 
	 * @param args
	 *        Parameter values.
	 * 
	 * @return Returns true, if the given method could be executed, otherwise false.
	 * 
	 */
	public boolean run(String clazz, String method, Class<?>[] parameterTypes, Object[] args) {
		// check if source code was compiled
		if (!isCodeCompiled) {
			try { // compile source code
				this.compile();
			} catch (Exception exception) {
				Activator.getDefault().logError(exception);
				return false;
			}
		}

		// get urls of class file paths
		URL[] urls = new URL[classFiles.size()];

		for (int i = 0; i < classFiles.size(); i++) {
			// get path to class file
			String path = classFiles.get(i).getAbsoluteFile().toString();
			path = path.substring(0, path.lastIndexOf("\\") + 1);

			try {
				urls[i] = new File(path).toURI().toURL();
			} catch (MalformedURLException exception) {
				Activator.getDefault().logError(exception);
				return false;
			}
		}

		// initialise class loader
		URLClassLoader urlClassLoader = new URLClassLoader(urls);

		// load class
		Class<?> loadedClass;
		try {
			loadedClass = urlClassLoader.loadClass(clazz);
			urlClassLoader.close();
		} catch (ClassNotFoundException | IOException exception) {
			Activator.getDefault().logError(exception);
			return false;
		}

		// invoke method
		try {
			loadedClass.getMethod(method, parameterTypes).invoke(loadedClass.newInstance(), args);
		} catch (Exception e) {
			Activator.getDefault().logError(e, "Exception occured during method execution.");
			return false;
		}

		return true;
	}

}
