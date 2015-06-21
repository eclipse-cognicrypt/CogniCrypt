package crossing.e1.featuremodel.clafer;

import java.util.Map;

import org.clafer.ast.AstClafer;
import org.clafer.scope.Scope;
import org.claferconfigurator.scope.ScopeWrapper;

import crossing.e1.configurator.ReadConfig;
public class TestInstanceGenerator {

	public static void main(String[] args) {
		
			System.out.println(" Testing instance Generator method");
			InstanceGenerator instance=new InstanceGenerator(new ClaferModel(new ReadConfig().getClaferPath()));
			displayScope(instance.getScope(),instance.getWrapper());
			
	}
	static void displayScope(Scope scope,ScopeWrapper wrapper) {
		
		Map<AstClafer, Integer> x= wrapper.getScope();
		System.out.println("scopes"+"");
		for (AstClafer ast: x.keySet()){
			System.out.println(ast.getName()+" "+x.get(ast));
		}
		System.out.println("DefaultScope "+scope.getDefaultScope());
		System.out.println("Charhigh "+scope.getCharHigh());
		System.out.println("CharLow "+scope.getCharLow());
		System.out.println("IntHigh "+scope.getIntHigh());
		System.out.println("IntLow "+scope.getIntLow());
		System.out.println("MulHigh "+scope.getMulHigh());
		System.out.println("MulLow "+scope.getMulLow());
		System.out.println("StringLength "+scope.getStringLength());
		
		}

}
