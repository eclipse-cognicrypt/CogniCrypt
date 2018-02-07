package de.cognicrypt.codegenerator.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.cognicrypt.codegenerator.Activator;

/**
 * A helper class for files.
 */
public class FileHelper {

	/**
	 * Deletes file with given name.
	 * @param fileName Path to file to be deleted.
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

}
