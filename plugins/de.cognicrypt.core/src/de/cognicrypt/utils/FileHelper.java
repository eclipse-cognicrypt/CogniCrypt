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
public class FileHelper {

	/**
	 * Deletes file with given name.
	 * 
	 * @param fileName
	 *        Path to file to be deleted.
	 * @return <Code>True</Code>/<Code>False</Code> if deletion of file succeeded/failed.
	 */
	public static boolean deleteFile(final String fileName) {
		try {
			final File f = new File(fileName);
			if (f.exists()) {
				Files.delete(Paths.get(fileName));
			}
		} catch (final IOException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		return true;
	}

	/**
	 * This method checks if a file contains a self defined String
	 * 
	 * @param filePath
	 * @param s
	 * @return If the file contains the String <CODE>true</CODE> otherwise <CODE>false</CODE>
	 * @throws IOException
	 */
	public static boolean checkFileForString(final String filePath, String searchString) throws IOException {
		final File f = new File(filePath);
		if (!(f.exists() && Files.isReadable(f.toPath()))) {
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
		final File f = new File(filePath);
		if (!(f.exists() && Files.isReadable(f.toPath()))) {
			System.out.println("wrong filepath");
		}

		final String contentStringAlt = String.join(Constants.lineSeparator, Files.readAllLines(Paths.get(filePath))).trim();
		FileWriter writer = new FileWriter(f);
		writer.write(contentStringAlt);
		writer.close();
	}

}
