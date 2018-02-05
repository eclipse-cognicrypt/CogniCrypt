package de.cognicrypt.codegenerator.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.cognicrypt.codegenerator.Activator;

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

}
