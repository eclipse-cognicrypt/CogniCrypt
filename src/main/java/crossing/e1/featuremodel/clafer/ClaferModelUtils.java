package crossing.e1.featuremodel.clafer;

import org.clafer.ast.AstClafer;


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
}
