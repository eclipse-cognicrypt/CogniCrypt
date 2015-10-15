package crossing.e1.featuremodel.clafer;
/**
 * 
 */


import java.util.List;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.instance.InstanceModel;

import crossing.e1.featuremodel.clafer.ClaferModel;

public class TestDynamicModelUpdater {
	public static void main(String[] args) {
		ClaferModel model = new ClaferModel("test.cfr");
		List<AstConcreteClafer> people=null;
//		List<AstConcreteClafer> people = claferModel.getChildrenByName("person", claferModel
//				.getModel().getChildren());
		System.out.println("size:" + people.size());
		for (AstConcreteClafer clafer : people) {
			System.out.println(clafer);
		}
		ClaferSolver solver;
		solver = ClaferCompiler.compile(model.getModel(),model.getScope() );
		while (solver.find()) {
			System.out.println(solver.instance());
		}
		System.out.println("total children "+model.getModel().getChildren().size());
		System.out.println("No of instances before child "+solver.instanceCount());
		System.out.println("AFTER Adding child");
		AstConcreteClafer installation = model.getModel().addChild("Trudy")
				.addChild("name").withCard(1, 1).refTo(people.get(0).getRef().getTargetType());
				
		solver = ClaferCompiler.compile(model.getModel(),model.getScope());
		while (solver.find()) {
			InstanceModel instance= solver.instance();
			System.out.println(instance);
		}
		System.out.println("No of instances after child "+solver.instanceCount());
	
		System.out.println("total children "+model.getModel().getChildren().size());
		
	}
}