/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.clafer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

import de.cognicrypt.codegenerator.utilities.CodeGenUtils;

/**
 * This class is responsible for generating Clafer for custom primitive.
 * 
 * @author Anusha and Taran
 *
 */
public class ClaferGenerator {

	/**
	 * copy the static part of the clafer model into the target location
	 * 
	 * @param source
	 * @param target
	 * @return {@link File} object of the target
	 */
	public static File copyClaferHeader(String source, String target) {
		InputStream input = null;
		OutputStream output = null;
		File finalClafer;
		finalClafer = (CodeGenUtils.getFinalClaferFile(target));
		try {
			input = new FileInputStream(CodeGenUtils.getResourceFromWithin(source));
			output = new FileOutputStream(finalClafer);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return finalClafer;

	}

	/**
	 * Appends userInput into finalClafer
	 * 
	 * @param userInput
	 * @param finalClafer
	 */
	public static void printClafer(LinkedHashMap<String, String> userInput, File finalClafer) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(finalClafer, true)); // the true will append the new data
			for (String key : userInput.keySet()) {
				if (key != null && key.equals("name")) {
					bw.write(userInput.get(key) + " : SymmetricBlockCipher" + "\r\n");
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
