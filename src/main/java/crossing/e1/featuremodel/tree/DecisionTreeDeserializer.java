package crossing.e1.featuremodel.tree;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class DecisionTreeDeserializer implements JsonDeserializer<TreeNode> {

	@Override
	public TreeNode deserialize(JsonElement element, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return parseJsonObject(element.getAsJsonObject());
	}

	private TreeNode parseJsonObject(JsonObject obj) {
		if (obj.has("trueBranch")) { // BooleanCriteria.class
			return parseBooleanCritera(obj);
		} else if (obj.has("children")) { // NCritera.class
			return parseNCritera(obj);
		} else if (obj.has("value")) { // ValueDecision.class
			final String value = obj.get("value").getAsString();
			final DataStructure dataStruc = DataStructure.valueOf((obj.get("decision").getAsString()));
			return new ValueDecision(value, dataStruc);
		} else { // SimpleDecision.class
			final String value = obj.get("decision").getAsString();
			final DataStructure dataStruc = DataStructure.valueOf(value);
			return new SimpleDecision(dataStruc);
		}
	}

	private TreeNode parseBooleanCritera(JsonObject obj) {
		final String criteria = obj.get("criteria").getAsString();
		final JsonObject trueBranch = obj.get("trueBranch").getAsJsonObject();		
		final JsonObject falseBranch = obj.get("falseBranch").getAsJsonObject();
		return new BooleanCriteria(
				criteria,
				parseJsonObject(trueBranch),
				parseJsonObject(falseBranch));
	}
	
	private TreeNode parseNCritera(JsonObject obj) {
		final String criteria = obj.get("criteria").getAsString();
		final JsonArray children = obj.get("children").getAsJsonArray();
		TreeNode[] nodes = new TreeNode[children.size()];
		for(int i = 0; i < children.size(); i++){
			final JsonObject arrayElement = children.get(i).getAsJsonObject();
			nodes[i] = parseJsonObject(arrayElement);
		}
		
		return new NCriteria(criteria, nodes);
	}

}
