package de.cognicrypt.integrator.task.plugintests;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import junit.framework.AssertionFailedError;

public class TestHelpers {
	static void checkDirectoriesAreEqual(Path one, Path other) throws IOException {
		Files.walkFileTree(one, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				FileVisitResult result = super.visitFile(file, attrs);

				// get the relative file name from path "one"
				Path relativize = one.relativize(file);

				if(relativize.toFile().getAbsolutePath().contains(".zip"))
					return result;

				// construct the path for the counterpart file in "other"
				Path fileInOther = other.resolve(relativize);

				byte[] otherBytes = Files.readAllBytes(fileInOther);
				byte[] theseBytes = Files.readAllBytes(file);
				if (!Arrays.equals(otherBytes, theseBytes)) {
					throw new AssertionFailedError(file + " is not equal to " + fileInOther);
				}  
				return result;
			}
		});
	}
}
