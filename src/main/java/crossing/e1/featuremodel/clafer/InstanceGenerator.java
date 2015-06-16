package crossing.e1.featuremodel.clafer;

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
		Triple<AstModel, Scope, Objective[]> triple = clafModel.getTriple(); 
		intermediateScope=new ScopeWrapper();
		intermediateScope.setScopes(triple.getSnd().getScoped(), triple.getSnd());
//		AstClafer algorithms=clafModel.getChild("PasswordStoring");
//		AstClafer performance=clafModel.getChild("performance");
//		AstClafer name=clafModel.getChild("name");
//		AstClafer outputSize=clafModel.getChild("c0_outputSize");
//		AstClafer status=clafModel.getChild("status");
//		intermediateScope.alterScope(performance, 20);
//		intermediateScope.alterScope(outputSize, 272);
//		intermediateScope.alterScope(algorithms, 14);
//		intermediateScope.alterScope(name, 18);
//		intermediateScope.alterScope(status, 16);
		 
		scope=intermediateScope.getScopeObject(triple.getFstSnd().getSnd(),intermediateScope.getScope());
		Scope.intLow(0);
		solver = ClaferCompiler.compile(triple.getFst(),scope.toScope() );
		
		while (solver.find()) {
						System.out.println(solver.instance());
		}
		System.out.println("Ther are Instances"+solver.allInstances().length+" for scope");
	}
	public Scope getScope() {
		return Check.notNull(scope);
	}
	public ScopeWrapper getWrapper(){
		return Check.notNull(this.intermediateScope);
	}
}
