package crossing.e1.configurator.codegeneration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

/**
 * A Code object contains java code source files. This files can be compiled during runtime with the method compile() and afterwards be executed by using the method run(...)
 */
public class CodeHandler {

	private File[] javaCodeFiles;
	private File[] classFiles;
	private boolean isCodeCompiled = false;

	/**
	 * constructor
	 * 
	 * @param javaCodeFiles
	 *        Array of file objects that include java code
	 */
	public CodeHandler(File[] javaCodeFiles) {
		this.javaCodeFiles = javaCodeFiles;
		classFiles = new File[javaCodeFiles.length];
	}

	/**
	 * compiles the java code files that are included in javaCodeFiles
	 * 
	 * @return Array of generated class files.
	 * 
	 * @throws Exception
	 *         If the compilation process was not successful an exception is thrown.
	 */
	public File[] compile() throws Exception {
		// setup compiler
		JavaCompiler compiler = new EclipseCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaCodeFiles));

		// start compilation process
		boolean state = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

		if (state) { // if the compilation process was successful return list of
						// class files
			for (int i = 0; i < javaCodeFiles.length; i++) {
				String path = javaCodeFiles[i].getAbsolutePath();
				path = path.substring(0, path.lastIndexOf(".")) + ".class";

				classFiles[i] = new File(path);
			}

			isCodeCompiled = true;
			return classFiles;

		} else { // if the compilation failed throw exception
			isCodeCompiled = false;
			throw new Exception("compilation failed!");
		}
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
	 * @throws Exception
	 *         An exception is throwen if<br>
	 *         - the given java code cannot be compiled<br>
	 *         - the compiled class files cannot be found<br>
	 *         - the given class cannot be loaded
	 * 
	 */
	public boolean run(String clazz, String method, Class<?>[] parameterTypes, Object[] args) throws Exception {
		// check if source code was compiled
		if (!isCodeCompiled) {
			try { // compile source code
				this.compile();
			} catch (Exception e) {
				throw new Exception("Invoke method run not possible because compilation of the java code failed.");
			}
		}

		// get urls of class file paths
		URL[] urls = new URL[classFiles.length];

		for (int i = 0; i < classFiles.length; i++) {
			// get path to class file
			String path = classFiles[i].getAbsoluteFile().toString();
			path = path.substring(0, path.lastIndexOf("\\") + 1);

			urls[i] = new File(path).toURI().toURL();
		}

		// initialize class loader
		URLClassLoader urlClassLoader = new URLClassLoader(urls);

		// load class
		Class<?> loadedClass = urlClassLoader.loadClass(clazz);
		urlClassLoader.close();

		// invoke method
		try {
			loadedClass.getMethod(method, parameterTypes).invoke(loadedClass.newInstance(), args);
		} catch (Exception e) {
			System.err.println("Exception is occured during method execution.");
			System.err.println(e.getCause());
		}

		return true;
	}

}
