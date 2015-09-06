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
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.ast.AstSetExpr;
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
	ParseClafer parser = new ParseClafer();

	public List<InstanceClafer> generateInstances(ClaferModel clafModel,
			Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		// System.out.println("Instance generator called");
		if (map.isEmpty())
			return null;
		this.instances = new ArrayList<InstanceClafer>();
		this.instance = new HashMap<String, InstanceClafer>();
		clafModel.setModel(clafModel.getModel());
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();
		AstModel model = clafModel.getModel();
		try {

			AstConcreteClafer m = model
					.addChild("Main")
					.addChild("MAINTASK")
					.refTo(StringLableMapper.getTaskLables().get(getTaskName()));

			for (AstConcreteClafer main : m.getRef().getTargetType()
					.getChildren()) {
				for (ArrayList<AstConcreteClafer> claf : map.keySet()) {
					if (claf.get(0).getName().equals(main.getName())) {
						int operator = map.get(claf).get(0);
						int value = map.get(claf).get(1);
						AstConcreteClafer operand=null;
						parser.getClaferByName(main, claf.get(1).getName());
						if(!parser.isFlag())
						operand=parser.getClaferByName();
						if (operator == 1)
							main.addConstraint(equal(
									joinRef(join(joinRef($this()), operand)),
									constant(value)));
						if (operator == 2)
							main.addConstraint(lessThan(
									joinRef(join(joinRef($this()), operand)),
									constant(value)));
						if (operator == 3)
							main.addConstraint(greaterThan(
									joinRef(join(joinRef($this()), operand)),
									constant(value)));
						if (operator == 4)
							main.addConstraint(lessThanEqual(
									joinRef(join(joinRef($this()), operand)),
									constant(value)));
						if (operator == 5)
							main.addConstraint(greaterThanEqual(
									joinRef(join(joinRef($this()), operand)),
									constant(value)));
						if (operator == 6) {
							AstAbstractClafer operandGloabl=null;
							AstConcreteClafer operandValue=null;
							parser.getClaferByName(main, main.getRef()
									.getTargetType().getName());
							if (parser.isFlag()) {
								operandGloabl = parser
										.getAstAbstractClaferByName();
							}
							parser
							.getClaferByName(main, claf.get(2)
									.getName());
							if (!parser.isFlag()) {
								operandValue = parser
										.getClaferByName();
							}
							main.addConstraint(some(join(
									join(global(operandGloabl), operand),operandValue)));
						}
//						System.out.println("Constraints after addition "
//								+ main.getConstraints());
					}
				}

			}

			solver = ClaferCompiler.compile(model, scope);
			while (solver.find()) {
				InstanceClafer instance = solver.instance().getTopClafers()[solver
						.instance().getTopClafers().length - 1];

				instances.add(instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getInstanceMapping();
		setNoOfInstances(instance.keySet().size());
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
			if (inst.getType().getName().equals("Main") && key.length() > 0)
				instance.put(key, inst);
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
	public String displayInstanceValues(InstanceClafer inst, String value) {
		value="<Algorithm> \n";
		if (inst.hasChildren()) {
			for (InstanceClafer in : inst.getChildren()) {
				value+=displayInstanceXML(in, "");
			}
			}
		
		value+="</Algorithm>";
		return value;
	}
	public String displayInstanceXML(InstanceClafer inst, String value) {
		try {
			if (inst.hasChildren()) {
				for (InstanceClafer in : inst.getChildren()) {
					value += displayInstanceXML(in, "");
				}

			} else if (inst.hasRef()
					&& (inst.getType().isPrimitive() != true)
					&& (inst.getRef().getClass().toString().contains("Integer") == false)
					&& (inst.getRef().getClass().toString().contains("String") == false)
					&& (inst.getRef().getClass().toString().contains("Boolean") == false)) {
				value += displayInstanceXML((InstanceClafer) inst.getRef(),
						"");
			} else {
				if (inst.hasRef())
					return ("\t<"+parser.trim(inst.getType().getName()) + ">"
							+ inst.getRef().toString().replace("\"", "") +"</"+parser.trim(inst.getType().getName()) + ">\n");
				else
					return (parser.trim(inst.getType().getName()) + "\n");

			}
		} catch (Exception E) {
			E.printStackTrace();
		}
		return value;
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
