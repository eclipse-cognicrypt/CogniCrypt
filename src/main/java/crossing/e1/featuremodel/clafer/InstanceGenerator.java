package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.*;

import java.util.ArrayList;
import java.util.List;

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
	private List<InstanceClafer> instances;
	AstModel model;
	Triple<AstModel, Scope, Objective[]> triple;
	private int noOfInstances;

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public List<InstanceClafer> generateInstances(ClaferModel clafModel,
			int performanceValue, int keyLength) {
		this.instances = new ArrayList<InstanceClafer>();
		clafModel.setModel(clafModel.getModelNoCon());
		this.model = clafModel.getModel();
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();
		AstConcreteClafer algorithms = (AstConcreteClafer) clafModel
				.getChild("Main");
		AstConcreteClafer digestToUse = algorithms.getChildren().get(1);
		AstConcreteClafer performance = (AstConcreteClafer) clafModel
				.getChild("c0_Algorithm").getChildren().get(1);
		clafModel.addConstraint(
				algorithms,
				lessThan(
						joinRef(join(joinRef(join($this(), digestToUse)),
								performance)), constant(performanceValue)),
				clafModel);
		solver = ClaferCompiler.compile(clafModel.getModel(), scope.toScope());
		while (solver.find()) {
			System.out
					.println("===="
							+ performanceValue
							+ "==========================================================");
			InstanceClafer instance = solver.instance().getTopClafers()[solver
					.instance().getTopClafers().length - 1];
			instances.add(instance);
			for (InstanceClafer clafer : instance.getChildren()) {

				System.out
						.println(clafer.getType().getName()
								+ " => "
								+ (clafer.getRef().getClass().getSimpleName()
										.endsWith("InstanceClafer") == true ? ((InstanceClafer) clafer
										.getRef())
										.getType()
										.toString()
										.substring(
												((InstanceClafer) clafer
														.getRef()).getType()
														.toString()
														.indexOf('_') + 1,
												((InstanceClafer) clafer
														.getRef()).getType()
														.toString().length())
										: clafer.toString()));
			}
		}
		
		setInstances(instances);
		setNoOfInstances(solver.instanceCount());
		System.out.println("there are " + getNoOfInstances() + " instances");
		return instances;

	}

	public Scope getScope() {
		return Check.notNull(scope);
	}

	public List<InstanceClafer> getInstances() {
		return Check.notNull(instances);
	}

	public void setInstances(List<InstanceClafer> instances) {
		this.instances = instances;
	}

	public ScopeWrapper getWrapper() {
		return null;// Check.notNull(this.intermediateScope);
	}
}
