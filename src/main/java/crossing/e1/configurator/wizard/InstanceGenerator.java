package crossing.e1.configurator.wizard;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstModel;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.claferconfigurator.scope.ScopeWrapper;
import org.clafer.collection.Triple;
import crossing.e1.featuremodel.clafer.ClaferModel;
/*
 * Class responsible for generating instances 
 * for a given clafer.
 * */
public class InstanceGenerator {

	private ClaferSolver solver;
	private ClaferModel clafModel;
	
	public InstanceGenerator(){
		this.clafModel=new ClaferModel();
		AstClafer algorithms=clafModel.getChild("PasswordStoring");
		AstClafer performance=clafModel.getChild("performance");
		AstClafer name=clafModel.getChild("name");
		AstClafer outputSize=clafModel.getChild("c0_outputSize");
		AstClafer status=clafModel.getChild("status");
		Triple<AstModel, Scope, Objective[]> triple = clafModel.getTriple(); 
		ScopeWrapper scopes=new ScopeWrapper();
			scopes.setScopes(triple.getSnd().getScoped(), triple.getSnd());
			scopes.alterScope(performance, 20);
			scopes.alterScope(outputSize, 240);
			scopes.alterScope(algorithms, 14);
			scopes.alterScope(name, 25);
			scopes.alterScope(status, 27);
		Scope scope=new ScopeWrapper().getScopeObject(triple.getFstSnd().getSnd(),scopes.getScope());
		solver = ClaferCompiler.compile(triple.getFst(),scope.toScope() );
		System.out.println(" Ther eare "+solver.allInstances().length+" Instances for scope"+" ");
		scopes.displayScope(scope);
		while (solver.find()) {
			//Instances can be accessed here
			 }
}
}
