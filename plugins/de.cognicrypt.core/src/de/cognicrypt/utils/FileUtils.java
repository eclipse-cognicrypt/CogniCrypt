/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

/**
 * A helper class for files.
 */
public class FileUtils {

	/**
	 * Deletes file with given name.
	 *
	 * @param fileName Path to file to be deleted.
	 * @return <Code>True</Code>/<Code>False</Code> if deletion of file succeeded/failed.
	 */
	public static boolean deleteFile(final String fileName) {
		try {
			if (new File(fileName).exists()) {
				Files.delete(Paths.get(fileName));
			}
			return true;
		}
		catch (final IOException e) {
			Activator.getDefault().logError(e);
			return false;
		}
	}

	/**
	 * This method checks if a file contains a self defined String
	 *
	 * @param filePath
	 * @param s
	 * @return If the file contains the String <CODE>true</CODE> otherwise <CODE>false</CODE>
	 * @throws IOException
	 */
	public static boolean checkFileForString(final String filePath, final String searchString) throws IOException {
		final File file = new File(filePath);
		if (!(file.exists() && Files.isReadable(file.toPath()))) {
			return false;
		}

		return Files.readAllLines(Paths.get(filePath)).stream().anyMatch(line -> line.contains(searchString));
	}

	/**
	 * This method trims a file content
	 *
	 * @param filePath
	 * @throws IOException
	 */
	public static void trimFile(final String filePath) throws IOException {
		final File file = new File(filePath);
		if (!(file.exists() && Files.isReadable(file.toPath()))) {
			Activator.getDefault().logError("Output file" + filePath + " could not be trimmed. It does either not exist or is not readable.");
		}

		final String contentStringAlt = String.join(Constants.lineSeparator, Files.readAllLines(Paths.get(filePath))).trim();
		final FileWriter writer = new FileWriter(file);
		writer.write(contentStringAlt);
		writer.close();
	}

}
