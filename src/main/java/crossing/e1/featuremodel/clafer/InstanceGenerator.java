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
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.greaterThanEqual;
import static org.clafer.ast.Asts.join;
import static org.clafer.ast.Asts.joinRef;
import static org.clafer.ast.Asts.lessThan;
import static org.clafer.ast.Asts.lessThanEqual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.instance.InstanceClafer;
import org.clafer.scope.Scope;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.xml.export.Answer;
import crossing.e1.xml.export.Dependency;
import crossing.e1.xml.export.Question;

/**
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

	public InstanceGenerator(String path) {
		claferModel = new ClaferModel(new ReadConfig().getPathFromConfig(path));// till
																				// copy
																				// constructor
																				// works

		this.displayNameToInstanceMap = new HashMap<String, InstanceClafer>();
		this.uniqueInstances = new HashMap<Long, InstanceClafer>();
	}

	/**
	 * Method to Generate instances for basic user. Argument is a map of
	 * property(clafer) name and their values
	 * 
	 * @param map
	 * @return
	 */
	public List<InstanceClafer> generateInstances(HashMap<Question, Answer> map) {
		AstModel model = claferModel.getModel();
		try {
			AstConcreteClafer taskName = PropertiesMapperUtil.getTaskLabelsMap().get(getTaskName());
			AstConcreteClafer main = model.addChild("Main").addChild("MAINTASK").refTo(taskName);
			basicModeHandler(main, map);
			solver = ClaferCompiler.compile(model, claferModel.getScope().toBuilder()
					.intHigh(Integer.parseInt(new ReadConfig().getValue("INT_HIGH")))
					.intLow(Integer.parseInt(new ReadConfig().getValue("INT_LOW"))));
			while (solver.find()) {
				InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length
						- 1];
				uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.generatedInstances = new ArrayList<InstanceClafer>(uniqueInstances.values());
		generateInstanceMapping();
		setNoOfInstances(displayNameToInstanceMap.keySet().size());
		return generatedInstances;
	}

	/**
	 * Method to generate instances in an advanced user mode, takes map with
	 * claer and their values as parameterF
	 * 
	 * @param map
	 * @return
	 */
	public List<InstanceClafer> generateInstancesAdvancedUserMode(
			Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		

		AstModel model = claferModel.getModel();
		try {

			AstConcreteClafer m = model.addChild("Main").addChild("MAINTASK")
					.refTo(PropertiesMapperUtil.getTaskLabelsMap().get(getTaskName()));
			advancedModeHandler(m, map);

			solver = ClaferCompiler.compile(model, claferModel.getScope().toBuilder()
					.intHigh(Integer.parseInt(new ReadConfig().getValue("INT_HIGH")))
					.intLow(Integer.parseInt(new ReadConfig().getValue("INT_LOW"))));
			while (solver.find()) {
				InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length
						- 1];

				uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.generatedInstances = new ArrayList<InstanceClafer>(uniqueInstances.values());
		generateInstanceMapping();
		setNoOfInstances(displayNameToInstanceMap.keySet().size());
		return generatedInstances;

	}

	/**
	 * This method is to parse the map of clafers and apply their values as
	 * constraints before instance generation, used only in advanceduserMode
	 * 
	 * @param m
	 * @param map
	 */
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

	/**
	 * BasicModeHandler will take <Question, answer> map as a parameter where
	 * the key of the map is a question, answer is the selected answer for a
	 * given question each answer has been further iterated to apply associated
	 * dependencies
	 */
	// FIXME include group operator
	void basicModeHandler(AstConcreteClafer inputClafer, HashMap<Question, Answer> qAMap) {
		Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> popertiesMap = PropertiesMapperUtil.getPropertiesMap();

		for (AstConcreteClafer childOfMainClfer : inputClafer.getRef().getTargetType().getChildren()) {
			for (AstConcreteClafer propertyOfaClafer : popertiesMap.keySet()) {
				if (childOfMainClfer.getName().equals(propertyOfaClafer.getName())) {
					for (AstConcreteClafer property : popertiesMap.get(propertyOfaClafer)) {
						for (Question question : qAMap.keySet())
							if (qAMap.get(question).hasDependencies()) {
								for (Dependency dependency : qAMap.get(question).getDependencies()) {
									if (property.getName().contains(dependency.getRefClafer())) {
										addConstraints(Integer.parseInt(dependency.getOperator()), propertyOfaClafer,
												Integer.parseInt(dependency.getValue()), property, null);
									}
								}
							}

					}
				}
			}
		}
	}

	/**
	 * method used by both basic and advanced user operations to add constraints
	 * to clafers before instance generation
	 * 
	 * operator is the numeric value which indicates > < >= <= == operations
	 * 
	 * main is the higher level clafer ,usually task choose by user
	 * 
	 * value is the numeric or string value which will be added as a
	 * constraints, EX outPutLength=128 here 128 is the value
	 * 
	 * operand is the clafer on which constraint is being applied EX
	 * outPutLength=128 outPutLength is operand here
	 * 
	 * claf is a clafer used only with XOR
	 * 
	 * @param operator
	 * @param main
	 * @param value
	 * @param operand
	 * @param claf
	 */
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
			// AstAbstractClafer operandGloabl = null;
			// AstConcreteClafer operandValue = null;
			// AstClafer claferByName = ClaferModelUtils.findClaferByName(main,
			// main.getRef().getTargetType().getName());//
			// .getClaferByName(main,
			// // main.getRef().getTargetType().getName());
			// if (ClaferModelUtils.isAbstract(claferByName)) {
			// operandGloabl = (AstAbstractClafer) claferByName;
			// }
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

	/**
	 * scope of the model
	 * 
	 * @return
	 */
	public Scope getScope() {
		return Check.notNull(claferModel.getScope());
	}

	/**
	 * gives the instances, key field being the name in String format .
	 * 
	 * @return
	 */
	public Map<String, InstanceClafer> getInstances() {
		return displayNameToInstanceMap;
	}

	/**
	 * method to set instanceCount to 0
	 */
	public void resetInstances() {
		displayNameToInstanceMap = null;
	}

	/**
	 * this method is part of instance generation process , creates a mapping
	 * instance name and instance Object
	 */
	public void generateInstanceMapping() {

		for (InstanceClafer inst : generatedInstances) {
			String key = getInstanceName(inst);
			if (inst.getType().getName().equals("Main") && key.length() > 0) {
				/**
				 * Check if any instance has same name , if yes add numerical
				 * values as suffix
				 * 
				 */
				if (displayNameToInstanceMap.keySet().contains(key)) {
					int counter = 1;
					for (String name : displayNameToInstanceMap.keySet()) {
						if (name.contains(key)) {
							counter++;
						}
					}
					/**
					 * There is no need to check if the counter value is not 1 ,
					 * because this loop will be executed only if there is a
					 * match in name of an instances
					 */
					key = key + "(" + counter + ")";
				}
				displayNameToInstanceMap.put(key, inst);
			}
		}
		/**
		 *  sort all the instances, to have an user friendly display 
		 */
		Map<String, InstanceClafer>treeMap = new TreeMap<>(displayNameToInstanceMap);
		displayNameToInstanceMap=treeMap;

	}

	/**
	 * Returns the hash value of the instance passed as an argument
	 * 
	 * @param inst
	 * @return
	 */
	private long getHashValueOfInstance(InstanceClafer inst) {

		InstanceClafer sub = null;
		if (inst.hasChildren())
			sub = (InstanceClafer) inst.getChildren()[0].getRef();

		if (sub != null) {
			return new InstanceClaferHash(sub).hashCode();
		}

		return 0;

	}

	/**
	 * Used by instanceMapping method to find the instance name
	 * 
	 * @param inst
	 * @return
	 */
	public String getInstanceName(InstanceClafer inst) {
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

	/**
	 * Returns number of instances of the task
	 * 
	 * @return
	 */
	public int getNoOfInstances() {
		return noOfInstances;
	}

	/**
	 * once the instances are generated, this method is invoked to set number of
	 * instances
	 * 
	 * @param noOfInstances
	 */
	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	/**
	 * Provides the name of the task chosen by user
	 * 
	 * @return
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * to Set task name
	 * 
	 * @param taskName
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}