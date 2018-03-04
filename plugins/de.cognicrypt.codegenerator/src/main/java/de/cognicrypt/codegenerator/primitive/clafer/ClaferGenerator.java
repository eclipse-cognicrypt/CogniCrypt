package de.cognicrypt.codegenerator.primitive.clafer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

/**
 * @author Anusha and Taran
 *
 */
public abstract class ClaferGenerator {

	/**
	 * Write the User Input into new File
	 *
	 * @return
	 */

	public static void printClafer(LinkedHashMap<String, String> userInput) {
		BufferedWriter bw = null;

		try {

			bw = new BufferedWriter(new FileWriter(filename, true)); // the true will append the new data

			// TODO Auto-generated method stub
			for (String key : userInput.keySet()) {
				bw.write("[" + key + " = " + userInput.get(key) + "]" + "\r\n");// appends the string to the file
				System.out.println("[" + key + " = " + userInput.get(key) + "]" + "\r\n");
			}
			bw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

	//	File Path

	private static String src = "C:\\Users\\singh\\git\\CogniCrypt-CPTaran\\plugins\\de.cognicrypt.codegenerator\\src\\main\\resources\\ClaferModel\\ClaferHeader.cfr";
	private static String filename = "C:\\Users\\singh\\git\\CogniCrypt-CPTaran\\plugins\\de.cognicrypt.codegenerator\\src\\main\\resources\\ClaferModel\\FinalClafer.cfr";

	//	Copy the Static Part into New created file

	public static File copyBaseFile() {

		File dest = new File(filename);
		InputStream input = null;
		OutputStream output = null;

		try {
			input = new FileInputStream(src);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;

			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				input.close();
				output.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return dest;
	}
}
