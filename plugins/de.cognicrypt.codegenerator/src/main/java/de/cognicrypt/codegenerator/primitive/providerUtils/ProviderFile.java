/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.providerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A class that generate the provider file
 * 
 * @author Ahmed
 */

public class ProviderFile {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public ProviderFile() {

	}

	/**
	 * Archive files with option to exclude the main containing folder
	 * 
	 * @param fileToZip
	 * @param zipFile
	 * @param excludeContainingFolder
	 * @throws IOException
	 */
	public void zipProject(String fileToZip, File zipFile, boolean excludeContainingFolder) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		File srcFile = new File(fileToZip);
		if (excludeContainingFolder && srcFile.isDirectory()) {
			for (String fileName : srcFile.list()) {
				addToZip("", fileToZip + "/" + fileName, zipOut);
			}
		} else {
			addToZip("", fileToZip, zipOut);
		}

		zipOut.flush();
		zipOut.close();

		System.out.println("Successfully created " + zipFile.getName());
	}

	static private void addToZip(String path, String srcFile, ZipOutputStream zipOut) throws IOException {
		File file = new File(srcFile);
		String filePath = "".equals(path) ? file.getName() : path + "/" + file.getName();
		if (file.isDirectory()) {
			for (String fileName : file.list()) {
				addToZip(filePath, srcFile + "/" + fileName, zipOut);
			}
		} else {
			zipOut.putNextEntry(new ZipEntry(filePath));
			FileInputStream in = new FileInputStream(srcFile);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}
		}
	}

}
