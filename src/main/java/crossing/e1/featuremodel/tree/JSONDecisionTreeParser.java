package crossing.e1.featuremodel.tree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.utilities.Utils;

/**
 * 
 * 
 * @note INTERNAL USE ONLY!
 * 
 * @author Michael Reif
 */
class JSONDecisionTreeParser {

	public static TreeModel parseDecisionTree(final String filePath) {

		TreeModel decisionTree = null;

		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		gsonBuilder.registerTypeAdapter(TreeNode.class, new DecisionTreeDeserializer());

		final Gson gson = gsonBuilder.create();

		try (BufferedReader reader = new BufferedReader(new FileReader(Utils.getAbsolutePath(filePath)))) {

			decisionTree = gson.fromJson(reader, TreeModel.class);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		} catch (final IOException e) {
			Activator.getDefault().logError(e);
		}

		return decisionTree;
	}
}
