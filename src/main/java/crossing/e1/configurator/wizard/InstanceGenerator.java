package crossing.e1.configurator.wizard;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.IntType;
import static org.clafer.ast.Asts.Mandatory;
import static org.clafer.ast.Asts.Optional;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.joinRef;
import static org.clafer.ast.Asts.newModel;

import java.util.List;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstModel;
import org.clafer.ast.AstUtil;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferOption;
import org.clafer.compiler.ClaferSolver;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.clafer.scope.ScopeBuilder;

import crossing.e1.featuremodel.clafer.ClaferModel;

public class InstanceGenerator {


InstanceGenerator(){
	
	AstModel model= new ClaferModel().getModel();
	ClaferModel clafModel=new ClaferModel();
	AstConcreteClafer algorithms=clafModel.getChild("PasswordStoring");
	AstConcreteClafer performance=clafModel.getChild("performance");
	AstConcreteClafer name=clafModel.getChild("name");
	AstConcreteClafer outputSize=clafModel.getChild("outputSize");
	AstConcreteClafer status=clafModel.getChild("status");
	ScopeBuilder scope= Scope.defaultScope(64).adjustDefaultScope(10).intHigh(1).intLow(-1)
			;
	
	ClaferOption obj = ClaferOption.Optimized;
	System.out.println("here we are"+scope.toString());
	//scope.setScope(clafer, scope)
	ClaferSolver solver = ClaferCompiler.compile(model,scope);
		        //.setScope(status, 1).setScope(ok, 1).setScope(bad, 1).setScope(time, 1)
		        // Set the scope of every Clafer to 1. The code above could be replaced with
		        // "Scope.defaultScope(1)".
		        //.intLow(-16).intHigh(16));
		        // intLow is the "suggested" lowest integer for solving. intHigh is the "suggested"
		        // highest integer.
		    // find will return true when the solver finds another instance.
		    while (solver.find()) {
		        // Print the solution in a format similar to ClaferIG.
		        System.out.println(solver.instance());
		    }
		    
}
	
	
}
