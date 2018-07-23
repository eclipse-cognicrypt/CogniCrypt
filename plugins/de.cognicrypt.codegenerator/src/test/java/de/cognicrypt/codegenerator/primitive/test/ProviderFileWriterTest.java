/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.primitive.providerUtils.ProviderFile;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class ProviderFileWriterTest {

	boolean elementExists = false;
	File jarFile = new File(CodeGenUtils.getResourceFromWithin(Constants.testPrimitverFolder) + "jarTest.jar");
	ProviderFile providerFile;
	File folder;

	@Before
	public void setUp() throws IOException {
		providerFile = new ProviderFile();
		folder = CodeGenUtils.getResourceFromWithin(Constants.testPrimitverFolder);
		providerFile.zipProject(folder.getAbsolutePath(), jarFile, true);
	}

	@Test
	public void createJarFileTest() {
		try {

			File[] files = folder.listFiles();
			for (File file : files) {
				providerFile.zipProject(file.getAbsolutePath(), jarFile, true);

			}
			JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();
				final String entryName = entry.getName();
				elementExists = fileExists(files, entryName);
			}
			assertEquals(elementExists, true);
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@After
	public void deleteFile() {
		jarFile.delete();
	}

	private boolean fileExists(File[] files, String element) {
		boolean elementExists = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if (files[i].getName().equals(element))
					elementExists = true;
			}
		}

		return elementExists;
	}

}
