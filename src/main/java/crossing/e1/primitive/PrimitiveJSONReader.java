package crossing.e1.primitive;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utils;

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