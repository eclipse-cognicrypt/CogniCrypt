package de.cognicrypt.codegenerator.primitive.types;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.utilities.Utils;

public class PrimitiveJSONReader {

	private volatile static List<Primitive> primitives;

	public static List<Primitive> getPrimitiveTypes() {
		if (primitives == null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(Constants.jsonPrimitiveTypesFile)));
				final Gson gson = new Gson();
				primitives = gson.fromJson(reader, new TypeToken<List<Primitive>>() {}.getType());

			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			}
		}
		return primitives;
	}

}