package de.cognicrypt.codegenerator.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;

/**
 * A helper class which writes string content to a file and it will be saved under project directory
 */
public class FileHelper {

	public static int deleteFile(final String fileName) {
		try {
			final File f = new File(fileName);
			if (f.exists()) {
				Files.delete(Paths.get(fileName));
			}
		} catch (final IOException e) {
			Activator.getDefault().logError(e);
			return 1;
		}
		return 0;
	}
	
	/**
	 * This method checks if a file contains a self defined String
	 * @param filePath 
	 * @param s 
	 * @return If the file contains the String <CODE>true</CODE> otherwise <CODE>false</CODE>
	 * @throws IOException
	 */
	public static boolean checkFileForString(final String filePath, String s) throws IOException {

		final File f = new File(filePath);
		if (!(f.exists() && Files.isReadable(f.toPath()))) {
			return false;
		}

		final List<String> content = Files.readAllLines(Paths.get(filePath));
		final StringBuilder contentBuilder = new StringBuilder();
		for (final String el : content) {
			contentBuilder.append(el);
			contentBuilder.append(Constants.lineSeparator);
		}
		final String contentString = contentBuilder.toString();
		return contentString.contains(s);
	}

	
	/**
	 * This method trims a file content
	 * @param filePath
	 * @throws IOException
	 */
	public static void trimFile(final String filePath) throws IOException {
		final File f = new File(filePath);
		if (!(f.exists() && Files.isReadable(f.toPath()))) {
			System.out.println("wrong filepath");
		}

		final List<String> content = Files.readAllLines(Paths.get(filePath));
		final StringBuilder contentBuilder = new StringBuilder();
		for (final String el : content) {
			contentBuilder.append(el);
			contentBuilder.append(Constants.lineSeparator);
		}
		final String contentString = contentBuilder.toString().trim();
		FileWriter writer = new FileWriter(f);
		writer.write(contentString);
		writer.close();
	}

}
