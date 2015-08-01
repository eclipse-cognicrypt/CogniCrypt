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

	public List<InstanceClafer> generateInstances(ClaferModel clafModel,
			Map<String, Integer> filters) {
		this.instances = new ArrayList<InstanceClafer>();
		this.instance = new HashMap<String, InstanceClafer>();
		clafModel.setModel(clafModel.getModelNoCon());
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();
			for (AstConcreteClafer claf : clafModel.getSuperClafer(clafModel.getModel())) {
				if (claf.getName().contains("performance")) {
					claf.getParent().addConstraint(
							lessThanEqual(joinRef(global(clafModel
									.getConstraintClafers().get(0))),
									constant(4)));
					System.out.println(claf.getParent().getName()
							+ "  VALUESSS "
							+ claf.getParent().getConstraints().toString());
					//claf.addConstraint(lessThanEqual($this(), constant(3)));
				}
				for (AstConstraint x : clafModel.getModel().getConstraints()) {
					System.out.println("VALUE" + x.toString());
				}
			}
		// clafModel.getModel().addConstraint(
		// lessThanEqual(joinRef(global(clafModel.getConstraintClafers()
		// .get(2))), constant(3000)));
		// clafModel.getModel().addConstraint(
		// lessThanEqual(joinRef(global(clafModel.getConstraintClafers()
		// .get(3))), constant(3000)));

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
		int i=0;
		for (InstanceClafer inst : instances) {
			System.out.println("Instances are "+ i++ +inst.toString());
			String key = getInstanceMapping(inst).trim();
			System.out.println("KEY = > "+key+" VALUE=> "+inst.toString());
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
		// TODO Auto-generated method stub
		return null;
	}

}
