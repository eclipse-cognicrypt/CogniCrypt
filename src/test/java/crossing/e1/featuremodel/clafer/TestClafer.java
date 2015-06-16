package crossing.e1.featuremodel.clafer;
import org.clafer.ast.AstRef;
import crossing.e1.featuremodel.clafer.ClaferModel;
import static crossing.e1.featuremodel.clafer.ClaferModelUtils.*;
public class TestClafer {
	public static void main(String[] args) {
		ClaferModel model = new ClaferModel();
		
		System.out.println("--Testing getClafersByType-----");
		model.getClafersByType("c0_Task").forEach(task -> {
			System.out.println("Task: "+ task);
			model.getClaferProperties(task).forEach(property -> {
				AstRef referenceType = property.getRef();
				System.out.println("property: " + property);
				displayProperties(referenceType.getTargetType());
				//referenceType.getTargetType().getChildren().forEach(a -> displayProperties(a));
				//System.out.println("has property: "+ property + " of type: " + property.getRef() + " card: "+ property.getCard()));
			});
		});
		
		System.out.println("--Testing getChild-----");
		System.out.println(model.getChildByName("c0_Digest",model.getModel().getChildren()));
	}

}
