package de.cognicrypt.codegenerator.taskintegrator.controllers;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;

public class ClaferCompiler {

	public static String getClaferExecutable() {
		// TODO test on different operating systems
		String osString = System.getProperty("os.name");

		if (osString.contains("Linux")) {
			return CodeGenUtils.getResourceFromWithin("src/main/resources/bin/clafer-linux").getAbsolutePath();
		} else if (osString.contains("Mac OS X")) {
			return CodeGenUtils.getResourceFromWithin("src/main/resources/bin/clafer-macos").getAbsolutePath();
		} else if (osString.contains("Windows 7") || osString.contains("Windows 8") || osString.contains("Windows 10")) {
			return CodeGenUtils.getResourceFromWithin("src/main/resources/bin/clafer-windows.exe").getAbsolutePath();
		}
		
		// rely on the PATH variable if full path cannot be found
		return "clafer";
	}

	public static boolean execute(String filename) {
		String claferExectuable = getClaferExecutable();

		// try compilation
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(claferExectuable, "-k", "-m", "choco", filename);
			processBuilder.redirectErrorStream(true);
			Process compilerProcess = processBuilder.start();

			compilerProcess.waitFor();

			if (compilerProcess.exitValue() != 0) {
				return false;
			}

		} catch (Exception ex) {
			Activator.getDefault().logError(ex);
			return false;
		}

		return true;
	}

}
