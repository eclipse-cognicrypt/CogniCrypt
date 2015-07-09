package crossing.e1.featuremodel.clafer;

import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstRef;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import static crossing.e1.featuremodel.clafer.ClaferModelUtils.*;

public class TestClafer {
	public static void main(String[] args) {
		ClaferModel model = new ClaferModel(new ReadConfig().getClaferPath());

		System.out.println("--Testing getClafersByType-----");
		model.getClafersByType("Main").forEach(task -> {
			System.out.println("Task: " + task);
			model.getClaferProperties(task).forEach(property -> {
				AstRef referenceType = property.getRef();
				System.out.println("property: " + property);
				displayProperties(referenceType.getTargetType());
				// referenceType.getTargetType().getChildren().forEach(a ->
				// displayProperties(a));
				// System.out.println("has property: "+ property + " of type: "
				// + property.getRef() + " card: "+ property.getCard()));
				});
		});
		for (AstAbstractClafer clafer : model.getModel().getAbstracts()) {
			for (AstConcreteClafer childClafer : clafer.getChildren()) {
				System.out.println("name is "
						+ childClafer.getName()
						+ " card "
						+ childClafer.getCard()
						+ (childClafer.hasRef() ? "type is "
								+ childClafer.getRef().getTargetType()
								: (childClafer.hasChildren() ? " children are "
										+ childClafer.getChildren().toString()
										+ "" + childClafer.getCard() : "")));
				System.out.println();
			}
		}
		List<AstConstraint> const_ = model.getConstraints();
		AstClafer ram = const_.get(0).getContext();
		System.out.println(ram.hasRef());
	}

}
