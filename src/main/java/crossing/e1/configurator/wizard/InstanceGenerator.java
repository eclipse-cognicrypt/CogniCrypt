package crossing.e1.configurator.wizard;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.clafer.collection.Triple;
import crossing.e1.featuremodel.clafer.ClaferModel;
/*
 * Class responsible for generating instances 
 * for a given clafer
 * */
public class InstanceGenerator {

	private ClaferSolver solver;
	private AstModel model;
	private ClaferModel clafModel;
public InstanceGenerator(){
	
	this.model= new ClaferModel().getModel();
	this.clafModel=new ClaferModel();
	AstConcreteClafer algorithms=clafModel.getChild("PasswordStoring");
	AstConcreteClafer performance=clafModel.getChild("performance");
	AstConcreteClafer name=clafModel.getChild("name");
	AstConcreteClafer outputSize=clafModel.getChild("c0_outputSize");
	AstConcreteClafer status=clafModel.getChild("status");

	Triple<AstModel, Scope, Objective[]> triple = clafModel.getTriple(); 
	Scope scope=Check.notNull(triple.getSnd());
	Scope.intHigh(512);
	Scope.intHigh(128);
	solver = ClaferCompiler.compile(triple.getFst(),scope );
	while (solver.find()) {
				System.out.println(solver.instance());
		    }
}
}
