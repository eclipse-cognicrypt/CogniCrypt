package crossing.e1.featuremodel.clafer;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConstraint;

/**
 * @author Ram
 *
 */

public class ClaferModelUtils {

	public static String getDisplayName(AstClafer inputClafer) {
		return "";
	}

	public static void displayProperties(AstClafer inputClafer) {

		if (inputClafer != null) {
			inputClafer.getChildren().forEach(
					child -> System.out.println("attr: " + child));

			displayProperties(inputClafer.getSuperClafer());
		}
	}

	/*
	 * Method takes AstClafer as an input and returns a description of the
	 * clafer if exist, returns name of the clafer otherwise
	 */
	String getDescription(AstClafer inputClafer) {
		if (inputClafer.hasConstraints())
			for (AstConstraint child : inputClafer.getConstraints()) {
				String expr = child.getExpr().toString();
				if (expr.substring(0,
						((expr.indexOf('=') > 0) ? expr.indexOf('=') : 1))
						.contains("escription . ref")) {
					//return without Quotes,hence replaced the "" with empty
					return expr.substring(expr.indexOf('=') + 1, expr.length())
							.replace("\"","");
				}

			}

		return inputClafer.getName();

	}
}
