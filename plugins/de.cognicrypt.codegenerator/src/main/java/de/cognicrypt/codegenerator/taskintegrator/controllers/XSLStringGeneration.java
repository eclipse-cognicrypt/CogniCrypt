/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.graphics.Point;

public class XSLStringGeneration {

	/**
	 * 
	 * @param filePath
	 * @param existingText
	 * @param selected
	 * @return
	 */
	public static String generateXSLStringFromPath(String filePath, String existingText, Point selected, String stringToAdd) {
		StringBuilder dataFromFile = new StringBuilder();

		Path path = Paths.get(filePath);

		if (path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA") || path.getFileName().toString().endsWith(".txt") || path
			.getFileName().toString().endsWith(".TXT")) {

			// Check if the XSL tags have already been created. If yes then add the text at the cursor location.
			if (existingText.contains("<?xml version=") || existingText.contains("<xsl:stylesheet xmlns:xsl=")) {
				dataFromFile.append(existingText.substring(0, selected.x));
				dataFromFile.append("\n");
				dataFromFile.append("<xsl:result-document href=\"\">");
				dataFromFile.append("\n");
				if (filePath == null) {
					dataFromFile.append(stringToAdd);
				} else {
					appendTextFromFileToStringBuilder(dataFromFile, filePath);
				}
				dataFromFile.append("\n");
				dataFromFile.append(existingText.substring(selected.y, existingText.length()));

			} else {
				dataFromFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				dataFromFile.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n");
				dataFromFile.append("<xsl:output method=\"text\"/>\n");
				dataFromFile.append("<xsl:template match=\"/\">\n");
				dataFromFile.append("package <xsl:value-of select=\"//task/Package\"/>; \n");
				dataFromFile.append("<xsl:apply-templates select=\"//Import\"/>\n");

				if (filePath == null) {
					dataFromFile.append(stringToAdd);
				} else {
					appendTextFromFileToStringBuilder(dataFromFile, filePath);
				}


				dataFromFile.append("\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("<xsl:template match=\"Import\">\n");
				dataFromFile.append("import <xsl:value-of select=\".\"/>;\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("</xsl:stylesheet>");
			}

		} else {
			appendTextFromFileToStringBuilder(dataFromFile, filePath);
		}

		return dataFromFile.toString();
	}

	/**
	 * Read the data from the file and add it to the StringBuilder.
	 * 
	 * @param dataFromFile
	 *        the StringBuilder.
	 * @param filePath
	 *        the location of the file to be read.
	 */
	private static void appendTextFromFileToStringBuilder(StringBuilder dataFromFile, String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String line = br.readLine();

			while (line != null) {
				dataFromFile.append(line);
				dataFromFile.append(System.lineSeparator());
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
