package crossing.e1.featuremodel.clafer;

import java.util.List;
import java.util.stream.Collectors;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;

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
