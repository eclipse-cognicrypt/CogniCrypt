package crossing.e1.featuremodel.clafer.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.clafer.ast.*;
import org.clafer.collection.Triple;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;

import static org.clafer.ast.Asts.*;

//Installation
//xor Status
//    Ok
//    Bad
//Time -> integer
//    [this > 2]
public class InstanceGenerationTest {

	public static void main(String[] args) {
		
		System.out.println(ClassLoader.getSystemResource(
				"installation.js"));
		File filename = new File(ClassLoader.getSystemResource(
				"installation.js").getFile());

		Triple<AstModel, Scope, Objective[]> triple;
		try {
			triple = Javascript.readModel(filename, Javascript.newEngine());

			AstModel model = triple.getFst();
			Scope scope = triple.getSnd();
			ClaferSolver solver = ClaferCompiler.compile(model, scope);

			int numOfInstances = 0;
			while (solver.find()) {
				numOfInstances++;
				//System.out.println(solver.instance());
			}

			System.out.println("before: " + numOfInstances);

			List<AstConcreteClafer> timeClafers = getClafersByName(
					model.getChildren(), "c0_Time");
			
			System.out.println("time clafer: "+ timeClafers.size());

			timeClafers.forEach(clafer -> clafer.addConstraint(greaterThan(
					joinRef($this()), constant(6))));

			solver = ClaferCompiler.compile(model, scope.toScope());
			

			numOfInstances = 0;
			while (solver.find()) {
				numOfInstances++;
				//System.out.println(solver.instance());
			}

			System.out.println("after: " + numOfInstances);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<AstConcreteClafer> getClafersByName(
			List<AstConcreteClafer> children, String name) {

		List<AstConcreteClafer> childrenOfInterest = children.stream()
				.filter(child -> child.getName().equals(name))
				.collect(Collectors.toList());

		children.forEach(child -> childrenOfInterest.addAll(getClafersByName(
				child.getChildren(), name)));

		return childrenOfInterest;
	}
}
