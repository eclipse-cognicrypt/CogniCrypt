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

<<<<<<< HEAD
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
=======
public class InstanceGenerationTest {

	public static void main(String[] args) {

		System.out.println(ClassLoader.getSystemResource("installation.js"));
>>>>>>> 1c62314e5087e23dc5f27d71304a8a0a77af6988
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
<<<<<<< HEAD
				//System.out.println(solver.instance());
=======
				// System.out.println(solver.instance());
>>>>>>> 1c62314e5087e23dc5f27d71304a8a0a77af6988
			}

			System.out.println("before: " + numOfInstances);

			List<AstConcreteClafer> timeClafers = getClafersByName(
<<<<<<< HEAD
					model.getChildren(), "c0_Time");
			
			System.out.println("time clafer: "+ timeClafers.size());

			timeClafers.forEach(clafer -> clafer.addConstraint(greaterThan(
					joinRef($this()), constant(6))));

			solver = ClaferCompiler.compile(model, scope.toScope());
			
=======
					model.getChildren(), "c0_student");

			System.out.println("time clafer: " + timeClafers.size());

			for (AstClafer clafer : timeClafers) {
				AstClafer b = clafer.getChildren().get(0);
				b.addConstraint(greaterThan(
						joinRef(join(joinRef($this()), b.getRef()
								.getTargetType().getChildren().get(0))),
						constant(5)));
				
				b.addConstraint(greaterThan(
						joinRef(join(joinRef($this()), b.getRef()
								.getTargetType().getChildren().get(1))),
						constant(5)));

			}
			solver = ClaferCompiler.compile(model, scope.toScope());
>>>>>>> 1c62314e5087e23dc5f27d71304a8a0a77af6988

			numOfInstances = 0;
			while (solver.find()) {
				numOfInstances++;
<<<<<<< HEAD
				//System.out.println(solver.instance());
=======
				// System.out.println(solver.instance());
>>>>>>> 1c62314e5087e23dc5f27d71304a8a0a77af6988
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
