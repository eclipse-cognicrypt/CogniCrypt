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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
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
 * Class responsible for generating generatedInstances 
 * for a given clafer.
 *
 */

public class InstanceGenerator {

	private ClaferSolver solver;
	private List<InstanceClafer> generatedInstances;
	Map<Long, InstanceClafer> uniqueInstances;
	private Map<String, InstanceClafer> displayNameToInstanceMap;
	private ClaferModel claferModel;
	private int noOfInstances;
	String taskName = "";

	public InstanceGenerator() {
		claferModel = new ClaferModel(new ReadConfig().getPath("claferPath"));// till
																				// copy
																				// constructor
																				// works

		this.displayNameToInstanceMap = new HashMap<String, InstanceClafer>();
		this.uniqueInstances = new HashMap<Long, InstanceClafer>();
	}

	public List<InstanceClafer> generateInstances(HashMap<String, Answer> map) {
		if (map.isEmpty())
			return null;
		AstModel model = claferModel.getModel();
		try {

			AstConcreteClafer main = model.addChild("Main").addChild("MAINTASK")
					.refTo(PropertiesMapperUtil.getTaskLabelsMap().get(getTaskName()));
			basicModeHandler(main, map);
			solver = ClaferCompiler.compile(model, claferModel.getScope());
			while (solver.find()) {
				InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length
						- 1];
				uniqueInstances.put(getHashValue(instance), instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.generatedInstances = new ArrayList<InstanceClafer>(uniqueInstances.values());
		generateInstanceMapping();
		setNoOfInstances(displayNameToInstanceMap.keySet().size());
		return generatedInstances;
	}

	public List<InstanceClafer> generateInstancesAdvancedUserMode(
			Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		if (map.isEmpty())
			return null;

		AstModel model = claferModel.getModel();
		try {

			AstConcreteClafer m = model.addChild("Main").addChild("MAINTASK")
					.refTo(PropertiesMapperUtil.getTaskLabelsMap().get(getTaskName()));
			advancedModeHandler(m, map);

			solver = ClaferCompiler.compile(model, claferModel.getScope());
			while (solver.find()) {
				InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length
						- 1];

				uniqueInstances.put(getHashValue(instance), instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.generatedInstances = new ArrayList<InstanceClafer>(uniqueInstances.values());
		generateInstanceMapping();
		setNoOfInstances(displayNameToInstanceMap.keySet().size());
		return generatedInstances;

	}

	void advancedModeHandler(AstConcreteClafer m, Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		for (AstConcreteClafer main : m.getRef().getTargetType().getChildren()) {
			for (ArrayList<AstConcreteClafer> claf : map.keySet()) {
				if (claf.get(0).getName().equals(main.getName())) {
					int operator = map.get(claf).get(0);
					int value = map.get(claf).get(1);
					AstConcreteClafer operand = (AstConcreteClafer) ClaferModelUtils.findClaferByName(main,
							claf.get(1).getName());
					if (operand != null && !ClaferModelUtils.isAbstract(operand))
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
	void basicModeHandler(AstConcreteClafer inputClafer, HashMap<String, Answer> qAMap) {
		Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> b = PropertiesMapperUtil.getPropertiesMap();

		for (AstConcreteClafer main : inputClafer.getRef().getTargetType().getChildren()) {
			for (AstConcreteClafer ast : PropertiesMapperUtil.getPropertiesMap().keySet()) {
				if (main.getName().equals(ast.getName())) {
					ArrayList<AstConcreteClafer> propertiesList = b.get(ast);
					for (AstConcreteClafer property : propertiesList) {
						for (String question : qAMap.keySet())
							if (property.getName().contains(question)) {
								Answer answer = qAMap.get(question);
								addConstraints(Integer.parseInt(answer.getOperator()), ast,
										Integer.parseInt(answer.getRef()), property, null);
							}
						for (String name : qAMap.keySet())
							if (qAMap.get(name).hasDependencies()) {
								for (Dependency dependency : qAMap.get(name).getDependencies()) {
									if (property.getName().contains(dependency.getRefClafer())) {
										addConstraints(Integer.parseInt(dependency.getOperator()), ast,
												Integer.parseInt(dependency.getValue()), property, null);
									}
								}
							}

					}
				}
			}
		}
	}

	void addConstraints(int operator, AstConcreteClafer main, int value, AstConcreteClafer operand,
			AstConcreteClafer claf) {
		if (operator == 1)
			main.addConstraint(equal(joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 2)
			main.addConstraint(lessThan(joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 3)
			main.addConstraint(greaterThan(joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 4)
			main.addConstraint(lessThanEqual(joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 5)
			main.addConstraint(greaterThanEqual(joinRef(join(joinRef($this()), operand)), constant(value)));
		if (operator == 6) {
			AstAbstractClafer operandGloabl = null;
			AstConcreteClafer operandValue = null;
			AstClafer claferByName = ClaferModelUtils.findClaferByName(main, main.getRef().getTargetType().getName());// .getClaferByName(main,
																														// main.getRef().getTargetType().getName());
			if (ClaferModelUtils.isAbstract(claferByName)) {
				operandGloabl = (AstAbstractClafer) claferByName;
			}
			// TODO: fix xor behavior.. how do we get the operandValue??
			// parser.getClaferByName(main, claf.getName());
			// if (!parser.isFlag()) {
			// operandValue = parser.getClaferByName();
			// }
			// main.addConstraint(some(join(join(global(operandGloabl),
			// operand),
			// operandValue)));
		}
	}

	public Scope getScope() {
		return Check.notNull(claferModel.getScope());
	}

	public Map<String, InstanceClafer> getInstances() {
		return displayNameToInstanceMap;
	}

	public void resetInstances() {
		displayNameToInstanceMap = null;
	}

	public void generateInstanceMapping() {

		for (InstanceClafer inst : generatedInstances) {
			String key = getInstanceName(inst);
			if (inst.getType().getName().equals("Main") && key.length() > 0)
				displayNameToInstanceMap.put(key, inst);
		}

	}

	private long getHashValue(InstanceClafer inst) {

		InstanceClafer sub = null;
		if (inst.hasChildren())
			sub = (InstanceClafer) inst.getChildren()[0].getRef();

		if (sub != null) {
			return new InstanceClaferHash(sub).hashCode();
		}

		return 0;

	}

	String getInstanceName(InstanceClafer inst) {
		String val = "";
		try {
			if (inst.hasChildren()) {
				for (InstanceClafer in : inst.getChildren())
					if (val.length() > 0) {
						String x = getInstanceName(in);
						if (x.length() > 0)
							val = val + "+" + x;
					} else
						val = getInstanceName(in);
			} else if (inst.hasRef() && (inst.getType().isPrimitive() != true)
					&& (inst.getRef().getClass().toString().contains("Integer") == false)
					&& (inst.getRef().getClass().toString().contains("String") == false)
					&& (inst.getRef().getClass().toString().contains("Boolean") == false)) {
				val += getInstanceName((InstanceClafer) inst.getRef());
			} else {
				if (inst.getType().getName().contains("_name")
						&& inst.getRef().getClass().toString().contains("String")) {
					return inst.getRef().toString().replace("\"", "");
				}
			}
		} catch (Exception E) {
			E.printStackTrace();
		}
		return val;
	}

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}