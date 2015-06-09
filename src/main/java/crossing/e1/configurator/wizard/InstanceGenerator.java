package crossing.e1.configurator.wizard;
import java.util.HashMap;
import java.util.Map;


import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferOption;
import org.clafer.compiler.ClaferSolver;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.clafer.scope.ScopeBuilder;
import org.clafer.collection.Triple;

import crossing.e1.featuremodel.clafer.ClaferModel;

public class InstanceGenerator {


public InstanceGenerator(){
	
	AstModel model= new ClaferModel().getModel();
	ClaferModel clafModel=new ClaferModel();
	AstConcreteClafer algorithms=clafModel.getChild("PasswordStoring");
	AstConcreteClafer performance=clafModel.getChild("performance");
	AstConcreteClafer name=clafModel.getChild("name");
	AstConcreteClafer outputSize=clafModel.getChild("c0_outputSize");
	AstConcreteClafer status=clafModel.getChild("status");
//	ScopeBuilder scope= Scope.intHigh(600).defaultScope(20);
//	
//	System.out.println("status  solver "+scope.toScope().getScope(performance));
//	Map<AstClafer, Integer> map = new HashMap<AstClafer, Integer>();
//	map.put(status, 16);
//	map.put(outputSize, 1);
//	map.put(name, 16);
//	map.put(performance, 18);
//	map.put(algorithms, 1);
//	
//	clafModel.getTriple();
	Triple<AstModel, Scope, Objective[]> triple = clafModel.getTriple(); 
	Scope scope=triple.getSnd();
	scope.intHigh(512);
	scope.intHigh(128);
	ClaferSolver solver = ClaferCompiler.compile(triple.getFst(),scope );
	
	
	System.out.println(solver.find()+" status  solver ");
	solver.instance();
	;
	
	while (solver.find()) {
		      
		        System.out.println(solver.instance());
		    }
}
	
	
}
