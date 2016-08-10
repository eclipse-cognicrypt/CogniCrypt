package crossing.e1.featuremodel.tree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import crossing.e1.configurator.utilities.Utilities;

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

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		gsonBuilder.registerTypeAdapter(TreeNode.class, new DecisionTreeDeserializer());

		final Gson gson = gsonBuilder.create();

		try (BufferedReader reader = new BufferedReader(new FileReader(Utilities.getAbsolutePath(filePath)))) {

			decisionTree = gson.fromJson(reader, TreeModel.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return decisionTree;
	}
}
