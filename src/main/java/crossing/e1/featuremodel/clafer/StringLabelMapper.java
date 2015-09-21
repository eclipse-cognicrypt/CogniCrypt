/**
 * 
 */
package crossing.e1.featuremodel.clafer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstConcreteClafer;

/**
 * @author Ram
 *
 */
public class StringLabelMapper {
	private static Map<String, AstConcreteClafer> task = null;
	private static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> properties = null;
	private static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> groupProperties = null;

	private StringLabelMapper() {

	}

	public static Map<String, AstConcreteClafer> getTaskLabels() {
		if (task == null) {
			task = new HashMap<String, AstConcreteClafer>();
		}
		return task;
	}

	public static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> getPropertyLabels() {
		if (properties == null) {
			properties = new HashMap<AstConcreteClafer, ArrayList<AstConcreteClafer>>();
		}
		return properties;
	}

	public static void resetProperties() {
		properties = null;
	}

	public static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> getGroupProperties() {
		if (groupProperties == null) {
			groupProperties = new HashMap<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>>();
		}
		return groupProperties;
	}

	public static void resetGroupProperties() {
		StringLabelMapper.groupProperties = null;
	}

}
