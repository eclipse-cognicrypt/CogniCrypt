/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.cognicrypt.integrator.primitive.Activator;

public class PrimitiveJSONReader {

	private volatile static List<Primitive> primitives;

	public static List<Primitive> getPrimitiveTypes(final File file) {
		if (primitives == null) {
			try {
				final BufferedReader reader = new BufferedReader(new FileReader(file));
				final Gson gson = new Gson();
				primitives = gson.fromJson(reader, new TypeToken<List<Primitive>>() {}.getType());

			}
			catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			}
		}
		return primitives;
	}

}
