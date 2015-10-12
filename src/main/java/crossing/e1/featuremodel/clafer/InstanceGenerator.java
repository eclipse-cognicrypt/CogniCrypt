/**
 * Copyright 2015 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Ram Kamath
 *
 */
package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.equal;
import static org.clafer.ast.Asts.global;
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.greaterThanEqual;
import static org.clafer.ast.Asts.join;
import static org.clafer.ast.Asts.joinRef;
import static org.clafer.ast.Asts.lessThan;
import static org.clafer.ast.Asts.lessThanEqual;
import static org.clafer.ast.Asts.some;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.collection.Triple;
import org.clafer.common.Check;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.instance.InstanceClafer;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.xml.export.Answer;
import crossing.e1.xml.export.Dependency;

/*
 * Class responsible for generating instances 
 * for a given clafer.
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
			HashMap<String, Answer> map, boolean isadvanced) {
		if (map.isEmpty())
			return null;
		clafModel = new ClaferModel(new ReadConfig().getPath("claferPath"));
		this.instances = new ArrayList<InstanceClafer>();
		this.instance = new HashMap<String, InstanceClafer>();
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();
		AstModel model = clafModel.getModel();
		try {

			AstConcreteClafer main = model
					.addChild("Main")
					.addChild("MAINTASK")
					.refTo(StringLabelMapper.getTaskLabels().get(getTaskName()));
			basicModeHandler(main, map);
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

	public List<InstanceClafer> generateInstances(ClaferModel clafModel,
			Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map,
			boolean isadvanced) {
		if (map.isEmpty())
			return null;
		clafModel = new ClaferModel(new ReadConfig().getPath("claferPath"));
		this.instances = new ArrayList<InstanceClafer>();
		this.instance = new HashMap<String, InstanceClafer>();
		this.triple = clafModel.getTriple();
		this.scope = triple.getSnd();
		AstModel model = clafModel.getModel();
		try {

			AstConcreteClafer m = model
					.addChild("Main")
					.addChild("MAINTASK")
					.refTo(StringLabelMapper.getTaskLabels().get(getTaskName()));
			if (isadvanced)
				advancedModeHandler(m, map);

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

	void advancedModeHandler(AstConcreteClafer m,
			Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		for (AstConcreteClafer main : m.getRef().getTargetType().getChildren()) {
			for (ArrayList<AstConcreteClafer> claf : map.keySet()) {
				if (claf.get(0).getName().equals(main.getName())) {
					int operator = map.get(claf).get(0);
					int value = map.get(claf).get(1);
					AstConcreteClafer operand = null;
					parser.getClaferByName(main, claf.get(1).getName());
					if (!parser.isFlag())
						operand = parser.getClaferByName();
					addConstraints(operator, main, value, operand, claf.get(1));

				}
			}

		}

	}

	/*
	 * basicModeHandler will take <String, answer> map as a parameter where the
	 * key of the map is a clafer name associated with the question answer is
	 * the selected answer for a given question each answer has been further
	 * iterated to apply associated dependencies
	 */
	// FIXME include group operator
	void basicModeHandler(AstConcreteClafer m, HashMap<String, Answer> map) {
		Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> b = StringLabelMapper
				.getPropertyLabels();

		for (AstConcreteClafer main : m.getRef().getTargetType().getChildren()) {
			for (AstConcreteClafer ast : StringLabelMapper.getPropertyLabels()
					.keySet()) {
				if (main.getName().equals(ast.getName())) {
					ArrayList<AstConcreteClafer> propertiesList = b.get(ast);
					for (AstConcreteClafer property : propertiesList) {
						for (String name : map.keySet())
							if (property.getName().contains(name)) {
								Answer ans = map.get(name);
								addConstraints(
										Integer.parseInt(ans.getOperator()),
										ast, Integer.parseInt(ans.getRef()),
										property, null);
							}
						for (String name : map.keySet())
							if (map.get(name).hasDependencies()) {
								for (Dependency dependency : map.get(name)
										.getDependencies()) {
									if (property.getName().contains(
											dependency.getRefClafer())) {
										addConstraints(
												Integer.parseInt(dependency
														.getOperator()), ast,
												Integer.parseInt(dependency
														.getValue()), property,
												null);
									}
								}
							}

					}
				}
			}
		}
	}

	void addConstraints(int operator, AstConcreteClafer main, int value,
			AstConcreteClafer operand, AstConcreteClafer claf) {
		if (operator == 1)
			main.addConstraint(equal(joinRef(join(joinRef($this()), operand)),
					constant(value)));
		if (operator == 2)
			main.addConstraint(lessThan(
					joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 3)
			main.addConstraint(greaterThan(
					joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 4)
			main.addConstraint(lessThanEqual(
					joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 5)
			main.addConstraint(greaterThanEqual(
					joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 6) {
			AstAbstractClafer operandGloabl = null;
			AstConcreteClafer operandValue = null;
			parser.getClaferByName(main, main.getRef().getTargetType()
					.getName());
			if (parser.isFlag()) {
				operandGloabl = parser.getAstAbstractClaferByName();
			}
			parser.getClaferByName(main, claf.getName());
			if (!parser.isFlag()) {
				operandValue = parser.getClaferByName();
			}
			main.addConstraint(some(join(join(global(operandGloabl), operand),
					operandValue)));
		}
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