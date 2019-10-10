/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.test;

import static org.junit.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import org.junit.Before;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.primitive.providerUtils.Helper;
import de.cognicrypt.utils.Utils;

public class HelperTest {

	static File file = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "testJava.java");
	Helper helper = new Helper();
	int nbLinesInFile = 0;
	int nbLinesInCode = 0;

	@Before
	public void countLines() throws IOException {
		final InputStream is = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
		try {
			final byte[] c = new byte[1024];
			int readChars = 0;

			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++this.nbLinesInFile;
					}
				}
			}

		}
		finally {
			is.close();
		}
	}

	@Test
	public void compareSourceCode() {
		final LinkedHashMap<String, String> map = this.helper.getSourceCode(Utils.getResourceFromWithin(Constants.testPrimitverFolder));
		for (final String key : map.keySet()) {
			this.nbLinesInCode = countLinesInString(map.get(key));
		}
		assertEquals(this.nbLinesInCode, this.nbLinesInFile);

	}

	private static int countLinesInString(final String str) {
		final String[] lines = str.split("\r\n|\r|\n");
		return lines.length;
	}
}
