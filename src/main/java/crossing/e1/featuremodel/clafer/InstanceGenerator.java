package crossing.e1.featuremodel.clafer;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
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
 * 
 * */
public class InstanceGenerator {

	private ClaferSolver solver;
	private ClaferModel clafModel;
	private Scope scope;
	private ScopeWrapper intermediateScope;
	public InstanceGenerator(){
		this.clafModel=new ClaferModel();
		this.intermediateScope=new ScopeWrapper();
		AstClafer algorithms=clafModel.getChild("PasswordStoring");
		AstClafer performance=clafModel.getChild("performance");
		AstClafer name=clafModel.getChild("name");
		AstClafer outputSize=clafModel.getChild("c0_outputSize");
		AstClafer status=clafModel.getChild("status");
		Triple<AstModel, Scope, Objective[]> triple = clafModel.getTriple(); 
		intermediateScope=new ScopeWrapper();
		intermediateScope.setScopes(triple.getSnd().getScoped(), triple.getSnd());
		intermediateScope.alterScope(performance, 20);
		intermediateScope.alterScope(outputSize, 240);
		intermediateScope.alterScope(algorithms, 14);
		intermediateScope.alterScope(name, 25);
		intermediateScope.alterScope(status, 27);
		scope=intermediateScope.getScopeObject(triple.getFstSnd().getSnd(),intermediateScope.getScope());
		solver = ClaferCompiler.compile(triple.getFst(),scope.toScope() );
		System.out.println(" Ther eare "+solver.allInstances().length+" Instances for scope"+" ");
		while (solver.find()) {
			//Instances can be accessed here
			 }
	}
	public Scope getScope() {
		return Check.notNull(scope);
	}
	public ScopeWrapper getWrapper(){
		return Check.notNull(this.intermediateScope);
	}
}
