package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.scope.Scope;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
import org.clafer.objective.Objective;
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
	private Map<String, InstanceClafer> instance;
	private Triple<AstModel, Scope, Objective[]> triple;
	private int noOfInstances;

	public List<InstanceClafer> generateInstances(ClaferModel clafModel,
			Map<String, Integer> filters) {
		this.instances = new ArrayList<InstanceClafer>();
		this.instance = new HashMap<String, InstanceClafer>();
		clafModel.setModel(clafModel.getModelNoCon());
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();

		clafModel.getConstraintClafers().get(0)
				.addConstraint(lessThanEqual(joinRef($this()), constant(5)));
		// System.out.println(clafModel.getConstraintClafers().get(0).getConstraints().toString());
		solver = ClaferCompiler.compile(clafModel.getModel(), scope.toScope());
		while (solver.find()) {

			InstanceClafer instance = solver.instance().getTopClafers()[solver
					.instance().getTopClafers().length - 1];
			instances.add(instance);
		}
		getInstanceMapping();
		setNoOfInstances(solver.instanceCount());
		return instances;

	}

	public Scope getScope() {
		return Check.notNull(scope);
	}

	public Map<String, InstanceClafer> getInstances() {
		return Check.notNull(instance);
	}

	public void getInstanceMapping() {
		for (InstanceClafer inst : instances) {
			String key = getInstanceMapping(inst).trim();
			instance.put(key, inst);
		}

	}

	public void displayInstanceValues(InstanceClafer inst) {
		try {
			if (inst.hasChildren()) {
				for (InstanceClafer in : inst.getChildren())
					displayInstanceValues(in);

			} else if (inst.hasRef()
					&& (inst.getType().isPrimitive() != true)
					&& (inst.getRef().getClass().toString().contains("Integer") == false)
					&& (inst.getRef().getClass().toString().contains("String") == false)
					&& (inst.getRef().getClass().toString().contains("Boolean") == false)) {
				displayInstanceValues((InstanceClafer) inst.getRef());
			} else {
				if (inst.hasRef())
					System.out.println(inst.getType().getName() + " ==> "
							+ inst.getRef().toString().replace("\"", ""));
				else
					System.out.println(inst.getType().getName());

			}
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	String getInstanceMapping(InstanceClafer inst) {
		String val = "";
		try {
			if (inst.hasChildren()) {
				for (InstanceClafer in : inst.getChildren())
					if (val.length() > 0) {
						String x = getInstanceMapping(in);
						if (x.length() > 0)
							val = val + "+" + x;
					} else
						val = getInstanceMapping(in);
			} else if (inst.hasRef()
					&& (inst.getType().isPrimitive() != true)
					&& (inst.getRef().getClass().toString().contains("Integer") == false)
					&& (inst.getRef().getClass().toString().contains("String") == false)
					&& (inst.getRef().getClass().toString().contains("Boolean") == false)) {
				val += getInstanceMapping((InstanceClafer) inst.getRef());
			} else {
				if (inst.getType().getName().contains("_name")
						&& inst.getRef().getClass().toString()
								.contains("String")) {
					return inst.getRef().toString().replace("\"", "");
				}
			}
		} catch (Exception E) {
			E.printStackTrace();
		}
		return val;
	}

	public InstanceClafer getInstances(String b) {
		return Check.notNull(this.instance.get(b));
	}

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public Object getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

}
