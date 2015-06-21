package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.*;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.scope.Scope;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
import org.clafer.objective.Objective;
import org.claferconfigurator.scope.ScopeWrapper;
import org.clafer.collection.Triple;
import org.clafer.instance.InstanceClafer;


import crossing.e1.featuremodel.clafer.ClaferModel;
/*
 * Class responsible for generating instances 
 * for a given clafer.
 * 
 * */
public class InstanceGenerator {

	private ClaferSolver solver;
	private Scope scope;
	//private ScopeWrapper intermediateScope;
	AstModel model;
	Triple<AstModel, Scope, Objective[]> triple;
	public InstanceGenerator( ClaferModel claferModel){
		generateInstances(claferModel,1,512);
			
	}
	
public void generateInstances(ClaferModel clafModel,int performanceValue,int keyLength){
		//intermediateScope.setScopes(triple.getSnd().getScoped(), triple.getSnd());
		this.model=clafModel.getModel();	
		this.triple=clafModel.getTriple();
		this.scope=triple.getSnd();
		AstConcreteClafer algorithms=(AstConcreteClafer) clafModel.getChild("PasswordStoring");
		AstConcreteClafer digestToUse1=algorithms.getChildren().get(0);
		//scope.intHigh(600);		
		AstConcreteClafer performance=	(AstConcreteClafer)clafModel.getChild("c0_Algorithm").getChildren().get(1);
		clafModel.addConstraint(algorithms,lessThan(joinRef(join(joinRef(join($this(), digestToUse1)),performance )), constant(performanceValue)));
		//scope=intermediateScope.getScopeObject(triple.getFstSnd().getSnd(),intermediateScope.getScope());
		solver = ClaferCompiler.compile(triple.getFst(),scope.toScope() );

		
		while (solver.find()) {
			InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length-1];
			
			for(InstanceClafer clafer:instance.getChildren()){
				System.out.println(clafer.getType().getName()+(clafer.getRef().getClass().getSimpleName().endsWith("InstanceClafer")==true
						? 
								((InstanceClafer)clafer.getRef()).getType()
						:
							clafer.toString())
							);
			}
						
		}
		System.out.println("Ther are "+solver.instanceCount()+" Instances  for scope");
	
}
	public Scope getScope() {
		return Check.notNull(scope);
	}
	public ScopeWrapper getWrapper(){
		return null;//Check.notNull(this.intermediateScope);
	}
}
