package crossing.e1.primitive.clafer;
import java.util.HashSet;
import java.util.Set;
import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.IntType;
import static org.clafer.ast.Asts.Mandatory;
import static org.clafer.ast.Asts.Optional;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.equal;
import static org.clafer.ast.Asts.joinRef;
import static org.clafer.ast.Asts.newModel;
import org.clafer.ast.analysis.InsufficientScopeException;
import org.clafer.collection.Pair;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.instance.InstanceClafer;
import org.clafer.instance.InstanceModel;
import org.clafer.scope.Scope;
public class ClaferGenerator {
	public static void main(String args[]){
	

	  /**
     * <pre>
     * abstract SymmetricBlockCipher
     * SICS : SymmetricBlockCipher
     * 		
     *  
     * </pre>
     */
	   AstModel model = newModel();

       
       AstAbstractClafer cipher = model.addAbstract("SymmetricBlockCipher");
       AstAbstractClafer sics = model.addAbstract("SICS").extending(cipher);
       String value="SICS";
       final Integer valueAsInt = 12;
       sics.addChild("name").addConstraint(equal(joinRef($this()), constant(valueAsInt)));

       ClaferSolver solver = ClaferCompiler.compile(model, Scope.defaultScope(2));
      System.out.println(solver.allInstances());
	
	
	
   	
   	}
}
