package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants.OperatingSystem;
import de.cognicrypt.codegenerator.utilities.Utils;

public class ClaferCompiler {

	public static OperatingSystem getOS() {
		String osString = System.getProperty("os.name");

		if (osString.contains("Linux")) {
			return OperatingSystem.linux;
		} else if (osString.contains("windows")) {
			return OperatingSystem.windows;
		} else if (osString.contains("mac")) {
			return OperatingSystem.macos;
		}

		return OperatingSystem.unknown;
	}

	public static String getClaferExecutable() {
		// TODO test on different operating systems
		if (getOS() == OperatingSystem.linux) {
			return Utils.getResourceFromWithin("src/main/resources/bin/clafer-linux").getAbsolutePath();
		} else if (getOS() == OperatingSystem.macos) {
			return Utils.getResourceFromWithin("src/main/resources/bin/clafer-macos").getAbsolutePath();
		} else if (getOS() == OperatingSystem.windows) {
			return Utils.getResourceFromWithin("src/main/resources/bin/clafer-windows").getAbsolutePath();
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
