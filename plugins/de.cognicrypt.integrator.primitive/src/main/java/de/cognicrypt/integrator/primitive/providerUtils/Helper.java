/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.providerUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

/**
 * This class support the provider generation by copying the source from a java file.
 *
 * @author Ahmed
 *
 */
public class Helper {

	private String readFileLineByLine(final String filePath) {
		final StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public LinkedHashMap<String, String> getSourceCode(final File folder) {
		final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		final File[] listOfFiles = (folder).listFiles();
		for (final File file : listOfFiles) {
			if (file.getName().endsWith(".java")) {
				final String className = file.getName();
				final String sourceCode = readFileLineByLine(file.getAbsolutePath());
				map.put(className, sourceCode);

			}
		}
		return map;
	}
}
