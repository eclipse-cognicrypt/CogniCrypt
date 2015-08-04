package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.scope.Scope;
import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstBoolExpr;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
import org.clafer.objective.Objective;
import org.clafer.collection.Triple;
import org.clafer.instance.InstanceClafer;

import crossing.e1.featuremodel.clafer.ClaferModel;

/*
 * Class responsible for generating instances 
 * for a given clafer.
 * @author Ram
 *
 */

public class InstanceGenerator {

	private ClaferSolver solver;
	private Scope scope;
	private List<InstanceClafer> instances;
	private Map<String, InstanceClafer> instance;
	private Triple<AstModel, Scope, Objective[]> triple;
	private int noOfInstances;
	String taskName = "";

	public List<InstanceClafer> generateInstances(ClaferModel clafModel,
			Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		System.out.println("Instance generator called");
		if (map.isEmpty())
			return null;
		this.instances = new ArrayList<InstanceClafer>();
		this.instance = new HashMap<String, InstanceClafer>();
		clafModel.setModel(clafModel.getModelNoCon());
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();
		AstModel model = clafModel.getModelNoCon();
		try {
			AstConcreteClafer m = model
					.addChild("Main")
					.addChild("MAINTASK")
					.refTo(StringLableMapper.getTaskLables().get(getTaskName()));

			for (AstConcreteClafer main : m.getRef().getTargetType()
					.getChildren()) {
				for (ArrayList<AstConcreteClafer> claf : map.keySet()) {
					if (claf.get(0).equals(main)) {
						int operator = map.get(claf).get(0);
						int value = map.get(claf).get(1);
						System.out.println("Constraints before addition "
								+ main.getConstraints());
						System.out.println("MAIN =>"+main.getClass()+" property"+claf.get(1).getClass());
						if (operator == 1)
							main.addConstraint(equal(
									join(joinRef($this()), joinRef(claf.get(1))),
									constant(value)));
						if (operator == 2)
							main.addConstraint(lessThanEqual(
									join(joinRef($this()), joinRef(claf.get(1))),
									constant(value)));
						if (operator == 3)
							main.addConstraint(greaterThanEqual(
									join(joinRef($this()), joinRef(claf.get(1))),
									constant(value)));

						System.out.println("Constraints after addition "
								+ main.getConstraints());
					}
				}
			}

			solver = ClaferCompiler.compile(model, scope.toScope());
			while (solver.find()) {

				InstanceClafer instance = solver.instance().getTopClafers()[solver
						.instance().getTopClafers().length - 1];

				instances.add(instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public void resetInstances() {
		instance = null;
	}

	public void getInstanceMapping() {
		for (InstanceClafer inst : instances) {
			String key = getInstanceMapping(inst);
			instance.put(key, inst);
		}

	}

	public String displayInstanceValues(InstanceClafer inst, String value) {
		try {
			if (inst.hasChildren()) {
				for (InstanceClafer in : inst.getChildren()) {
					value += displayInstanceValues(in, "");
				}

			} else if (inst.hasRef()
					&& (inst.getType().isPrimitive() != true)
					&& (inst.getRef().getClass().toString().contains("Integer") == false)
					&& (inst.getRef().getClass().toString().contains("String") == false)
					&& (inst.getRef().getClass().toString().contains("Boolean") == false)) {
				value += displayInstanceValues((InstanceClafer) inst.getRef(),
						"");
			} else {
				if (inst.hasRef())
					return (inst.getType().getName() + "\t\t"
							+ inst.getRef().toString().replace("\"", "") + "\n");
				else
					return (inst.getType().getName() + "\n");

			}
		} catch (Exception E) {
			E.printStackTrace();
		}
		return value;
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
		return null;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
