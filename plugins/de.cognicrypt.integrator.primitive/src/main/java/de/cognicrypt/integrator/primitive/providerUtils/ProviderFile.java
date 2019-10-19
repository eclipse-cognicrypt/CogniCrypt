/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.providerUtils;

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
	public void zipProject(final String fileToZip, final File zipFile, final boolean excludeContainingFolder) throws IOException {
		final ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		final File srcFile = new File(fileToZip);
		if (excludeContainingFolder && srcFile.isDirectory()) {
			for (final String fileName : srcFile.list()) {
				addToZip("", fileToZip + "/" + fileName, zipOut);
			}
		} else {
			addToZip("", fileToZip, zipOut);
		}

		zipOut.flush();
		zipOut.close();

		System.out.println("Successfully created " + zipFile.getName());
	}

	static private void addToZip(final String path, final String srcFile, final ZipOutputStream zipOut) throws IOException {
		final File file = new File(srcFile);
		final String filePath = "".equals(path) ? file.getName() : path + "/" + file.getName();
		if (file.isDirectory()) {
			for (final String fileName : file.list()) {
				addToZip(filePath, srcFile + "/" + fileName, zipOut);
			}
		} else {
			zipOut.putNextEntry(new ZipEntry(filePath));
			final FileInputStream in = new FileInputStream(srcFile);

			final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}
		}
	}

}
