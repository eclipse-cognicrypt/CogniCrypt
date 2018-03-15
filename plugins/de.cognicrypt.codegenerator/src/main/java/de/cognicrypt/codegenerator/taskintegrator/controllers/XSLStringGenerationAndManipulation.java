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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class XSLStringGenerationAndManipulation {

	/**
	 * 
	 * @param filePath
	 * @param existingText
	 * @param selected
	 * @return
	 */
	public static String generateXSLStringFromPath(String filePath, String existingText, Point selected, String stringToAdd) {
		StringBuilder dataFromFile = new StringBuilder();

		String xslStringBeforeAddingText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n<xsl:output method=\"text\"/>\n<xsl:template match=\"/\">\npackage <xsl:value-of select=\"//task/Package\"/>; \n<xsl:apply-templates select=\"//Import\"/>\n";
		String xslStringAfterAddingText = "\n</xsl:template>\n<xsl:template match=\"Import\">\nimport <xsl:value-of select=\".\"/>;\n</xsl:template>\n</xsl:stylesheet>";

		if (filePath != null) {
			Path path = Paths.get(filePath);
			if (path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA") || path.getFileName().toString().endsWith(".txt") || path
				.getFileName().toString().endsWith(".TXT")) {

				// Check if the XSL tags have already been created. If yes then add the text at the cursor location.
				if (existingText.contains("<?xml version=") || existingText.contains("<xsl:stylesheet xmlns:xsl=")) {
					dataFromFile.append(existingText.substring(0, selected.x));
					dataFromFile.append("\n");
					dataFromFile.append("<xsl:result-document href=\"\">");
					dataFromFile.append("\n");
					appendTextFromFileToStringBuilder(dataFromFile, filePath);
					dataFromFile.append("\n");
					dataFromFile.append("</xsl:result-document>");
					dataFromFile.append("\n");
					dataFromFile.append(existingText.substring(selected.y, existingText.length()));

				} else {
					dataFromFile.append(xslStringBeforeAddingText);
					appendTextFromFileToStringBuilder(dataFromFile, filePath);
					dataFromFile.append(xslStringAfterAddingText);
				}

			} else {
				appendTextFromFileToStringBuilder(dataFromFile, filePath);
			}
		} else {
			dataFromFile.append(xslStringBeforeAddingText);
			dataFromFile.append(stringToAdd);
			dataFromFile.append(xslStringAfterAddingText);
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

	/**
	 * Set the colors to all the {@link XmlRegion}.
	 * 
	 * @param regions
	 *        the List of {@link XmlRegion}
	 * @return returns the {@link StyleRange} for the given code.
	 */
	public static List<StyleRange> computeStyleForXMLRegions(List<XmlRegion> regions) {
		List<StyleRange> styleRanges = new ArrayList<StyleRange>();
		for (XmlRegion xr : regions) {

			// The style itself depends on the region type
			// In this example, we use colors from the system
			StyleRange sr = new StyleRange();
			switch (xr.getXmlRegionType()) {
				case MARKUP:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
					break;

				case ATTRIBUTE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
					break;

				case ATTRIBUTE_VALUE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
					break;

				case MARKUP_VALUE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
					break;
				case COMMENT:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
					break;
				case INSTRUCTION:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
					break;
				case CDATA:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
					break;
				case WHITESPACE:
					break;
				default:
					break;
			}

			// Define the position and limit
			sr.start = xr.getStart();
			sr.length = xr.getEnd() - xr.getStart();
			styleRanges.add(sr);
		}

		return styleRanges;
	}

}
