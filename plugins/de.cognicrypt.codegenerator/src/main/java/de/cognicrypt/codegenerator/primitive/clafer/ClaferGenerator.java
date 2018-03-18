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

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.utilities.Utils;

/**
 * @author Anusha and Taran
 *
 */
public abstract class ClaferGenerator {

	//	Copy the Static Part into New created file
	public static File copyClaferHeader() {
		InputStream input = null;
		OutputStream output = null;
		File finalClafer = null;
		try {
			finalClafer = (Utils.getFinalClaferFile(Constants.claferFooter));
			input = new FileInputStream(Utils.getResourceFromWithin(Constants.claferHeader));
			output = new FileOutputStream(finalClafer);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (Exception e) {
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
		return finalClafer;
	}

	/**
	 * Write the User Input into new File
	 *
	 * @return
	 */

	public static void printClafer(LinkedHashMap<String, String> userInput, File finalClafer) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(finalClafer, true)); // the true will append the new data
			// TODO Auto-generated method stub
			for (String key : userInput.keySet()) {
				if (key != null && key.equals("name")) {
					bw.write(userInput.get(key) + " : SymmetricCipher" + "\r\n");
				}
				bw.write("\t" + "[" + key + " = " + userInput.get(key) + "]" + "\r\n"); // appends the string to the file
				System.out.println("\t" + "[" + key + " = " + userInput.get(key) + "]" + "\r\n");
			}
			bw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
}
