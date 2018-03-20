package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.Utils;

public class ClaferCompiler {

	public static String getClaferExecutable() {
		// TODO test on different operating systems
		String osString = System.getProperty("os.name");

		if (osString.contains("Linux")) {
			return Utils.getResourceFromWithin("src/main/resources/bin/clafer-linux").getAbsolutePath();
		} else if (osString.contains("mac")) {
			return Utils.getResourceFromWithin("src/main/resources/bin/clafer-macos").getAbsolutePath();
		} else if (osString.contains("Windows 7") || osString.contains("Windows 8") || osString.contains("Windows 10")) {
			return Utils.getResourceFromWithin("src/main/resources/bin/clafer-windows.exe").getAbsolutePath();
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

			// print compilation output to command line
			InputStream processStdOutput = compilerProcess.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(processStdOutput));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

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
