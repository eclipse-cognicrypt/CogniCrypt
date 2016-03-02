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

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Dependency;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.utilities.ReadConfig;

/**
 * Class responsible for generating generatedInstances for a given clafer.
 *
 */
public class InstanceGenerator {

	private static final String MAINTASK = "MAINTASK";
	private static final String INT_LOW = "INT_LOW";
	private static final String INT_HIGH = "INT_HIGH";
	private ClaferSolver solver;
	private List<InstanceClafer> generatedInstances;
	private final Map<Long, InstanceClafer> uniqueInstances;
	private Map<String, InstanceClafer> displayNameToInstanceMap;
	private final ClaferModel claferModel;
	private int noOfInstances;
	private String taskName = "";

	public InstanceGenerator(final String path) {
		this.claferModel = new ClaferModel(new ReadConfig().getPathFromConfig(path));// till copy constructor works
		this.displayNameToInstanceMap = new HashMap<String, InstanceClafer>();
		this.uniqueInstances = new HashMap<Long, InstanceClafer>();
	}

	/**
	 * this method is part of instance generation process , creates a mapping instance name and instance Object
	 */
	public void generateInstanceMapping() {
		for (final InstanceClafer inst : this.generatedInstances) {
			String key = getInstanceName(inst);
			if (inst.getType().getName().equals("Main") && key.length() > 0) {
				/**
				 * Check if any instance has same name , if yes add numerical values as suffix
				 *
				 */
				if (this.displayNameToInstanceMap.keySet().contains(key)) {
					int counter = 1;
					for (final String name : this.displayNameToInstanceMap.keySet()) {
						if (name.contains(key)) {
							counter++;
						}
					}
					/**
					 * There is no need to check if the counter value is not 1 , because this loop will be executed only
					 * if there is a match in name of an instances
					 */
					key = key + "(" + counter + ")";
				}
				this.displayNameToInstanceMap.put(key, inst);
			}
		}
		/**
		 * sort all the instances, to have an user friendly display
		 */
		final Map<String, InstanceClafer> treeMap = new TreeMap<>(this.displayNameToInstanceMap);
		this.displayNameToInstanceMap = treeMap;
	}

	/**
	 * Method to Generate instances for basic user. Argument is a map of property(clafer) name and their values
	 *
	 * @param map
	 * @return
	 */
	public List<InstanceClafer> generateInstances(final HashMap<Question, Answer> map) {
		final AstModel model = this.claferModel.getModel();
		try {
			final AstConcreteClafer taskName = PropertiesMapperUtil.getTaskLabelsMap().get(getTaskName());
			final AstConcreteClafer main = model.addChild("Main").addChild(MAINTASK).refTo(taskName);
			basicModeHandler(main, map);
			this.solver = ClaferCompiler.compile(model,
					this.claferModel.getScope().toBuilder().intHigh(Integer.parseInt(new ReadConfig().getValue(INT_HIGH)))
							.intLow(Integer.parseInt(new ReadConfig().getValue(INT_LOW))));
			while (this.solver.find()) {
				final InstanceClafer instance = this.solver.instance().getTopClafers()[this.solver.instance().getTopClafers().length - 1];
				this.uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		this.generatedInstances = new ArrayList<InstanceClafer>(this.uniqueInstances.values());
		generateInstanceMapping();
		setNoOfInstances(this.displayNameToInstanceMap.keySet().size());
		return this.generatedInstances;
	}

	/**
	 * Method to generate instances in an advanced user mode, takes map with claer and their values as parameterF
	 *
	 * @param map
	 * @return
	 */
	public List<InstanceClafer> generateInstancesAdvancedUserMode(final Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {

		final AstModel model = this.claferModel.getModel();
		try {

			final AstConcreteClafer m = model.addChild("Main").addChild(MAINTASK)
					.refTo(PropertiesMapperUtil.getTaskLabelsMap().get(getTaskName()));
			advancedModeHandler(m, map);

			this.solver = ClaferCompiler.compile(model,
					this.claferModel.getScope().toBuilder().intHigh(Integer.parseInt(new ReadConfig().getValue(INT_HIGH)))
							.intLow(Integer.parseInt(new ReadConfig().getValue(INT_LOW))));
			while (this.solver.find()) {
				final InstanceClafer instance = this.solver.instance().getTopClafers()[this.solver.instance().getTopClafers().length - 1];

				this.uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		this.generatedInstances = new ArrayList<InstanceClafer>(this.uniqueInstances.values());
		generateInstanceMapping();
		setNoOfInstances(this.displayNameToInstanceMap.keySet().size());
		return this.generatedInstances;

	}

	/**
	 * Used by instanceMapping method to find the instance name
	 *
	 * @param inst
	 * @return
	 */
	public String getInstanceName(final InstanceClafer inst) {
		String currentInstanceName = "";
		try {
			if (inst.hasChildren()) {
				for (final InstanceClafer childClafer : inst.getChildren()) {
					if (currentInstanceName.length() > 0) {
						final String childInstanceName = getInstanceName(childClafer);
						if (childInstanceName.length() > 0) {
							currentInstanceName = currentInstanceName + "+" + childInstanceName;
						}
					} else {
						currentInstanceName = getInstanceName(childClafer);
					}
				}
			} else if (inst.hasRef() && !inst.getType().isPrimitive() && !inst.getRef().getClass().toString()
					.contains(Constants.INTEGER) && !inst.getRef().getClass().toString()
							.contains(Constants.STRING) && !inst.getRef().getClass().toString().contains(Constants.BOOLEAN)) {
				currentInstanceName += getInstanceName((InstanceClafer) inst.getRef());
			} else {
				if (inst.getType().getName().contains("_name") && inst.getRef().getClass().toString().contains(Constants.STRING)) {
					return inst.getRef().toString().replace("\"", "");
				}
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		return currentInstanceName;
	}

	/**
	 * gives the instances, key field being the name in String format .
	 *
	 * @return
	 */
	public Map<String, InstanceClafer> getInstances() {
		return this.displayNameToInstanceMap;
	}

	/**
	 * Returns number of instances of the task
	 *
	 * @return
	 */
	public int getNoOfInstances() {
		return this.noOfInstances;
	}

	/**
	 * scope of the model
	 *
	 * @return
	 */
	public Scope getScope() {
		return Check.notNull(this.claferModel.getScope());
	}

	/**
	 * Provides the name of the task chosen by user
	 *
	 * @return
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/**
	 * method to set instanceCount to 0
	 */
	public void resetInstances() {
		this.displayNameToInstanceMap = null;
	}

	/**
	 * once the instances are generated, this method is invoked to set number of instances
	 *
	 * @param noOfInstances
	 */
	public void setNoOfInstances(final int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	/**
	 * to Set task name
	 *
	 * @param taskName
	 */
	public void setTaskName(final String taskName) {
		this.taskName = taskName;
	}

	/**
	 * Returns the hash value of the instance passed as an argument
	 *
	 * @param inst
	 * @return
	 */
	private long getHashValueOfInstance(final InstanceClafer inst) {
		// TODO: Why child at position 0, why is 0 returned if there is no child?
		if (inst.hasChildren()) {
			final InstanceClafer sub = (InstanceClafer) inst.getChildren()[0].getRef();
			if (sub != null) {
				return new InstanceClaferHash(sub).hashCode();
			}
		}
		return 0;
	}

	/**
	 * method used by both basic and advanced user operations to add constraints to clafers before instance generation
	 *
	 * operator is the numeric value which indicates > < >= <= == operations
	 *
	 * main is the higher level clafer ,usually task choose by user
	 *
	 * value is the numeric or string value which will be added as a constraints, EX outPutLength=128 here 128 is the
	 * value
	 *
	 * operand is the clafer on which constraint is being applied EX outPutLength=128 outPutLength is operand here
	 *
	 * claf is a clafer used only with XOR
	 *
	 * @param operator
	 * @param main
	 * @param value
	 * @param operand
	 * @param claf
	 */
	void addConstraints(final int operator, final AstConcreteClafer main, final int value, final AstConcreteClafer operand,
			final AstConcreteClafer claf) {
		if (operator == 1) {
			main.addConstraint(equal(joinRef(join(joinRef($this()), operand)), constant(value)));
		} else if (operator == 2) {
			main.addConstraint(lessThan(joinRef(join(joinRef($this()), operand)), constant(value)));
		} else if (operator == 3) {
			main.addConstraint(greaterThan(joinRef(join(joinRef($this()), operand)), constant(value)));
		} else if (operator == 4) {
			main.addConstraint(lessThanEqual(joinRef(join(joinRef($this()), operand)), constant(value)));
		} else if (operator == 5) {
			main.addConstraint(greaterThanEqual(joinRef(join(joinRef($this()), operand)), constant(value)));
		} else if (operator == 6) {
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
	 * This method is to parse the map of clafers and apply their values as constraints before instance generation, used
	 * only in advanceduserMode
	 *
	 * @param m
	 * @param map
	 */
	void advancedModeHandler(final AstConcreteClafer m, final Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> map) {
		for (final AstConcreteClafer main : m.getRef().getTargetType().getChildren()) {
			for (final ArrayList<AstConcreteClafer> claf : map.keySet()) {
				if (claf.get(0).getName().equals(main.getName())) {
					final int operator = map.get(claf).get(0);
					final int value = map.get(claf).get(1);
					final AstConcreteClafer operand = (AstConcreteClafer) ClaferModelUtils.findClaferByName(main, claf.get(1).getName());
					if (operand != null && !ClaferModelUtils.isAbstract(operand)) {
						addConstraints(operator, main, value, operand, claf.get(1));
					}
				}
			}
		}
	}

	/**
	 * BasicModeHandler will take <Question, answer> map as a parameter where the key of the map is a question, answer
	 * is the selected answer for a given question each answer has been further iterated to apply associated
	 * dependencies
	 */
	// FIXME include group operator
	void basicModeHandler(final AstConcreteClafer inputClafer, final HashMap<Question, Answer> qAMap) {
		final Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> popertiesMap = PropertiesMapperUtil.getPropertiesMap();
		for (final AstConcreteClafer childOfMainClfer : inputClafer.getRef().getTargetType().getChildren()) {
			for (final AstConcreteClafer propertyOfaClafer : popertiesMap.keySet()) {
				if (childOfMainClfer.getName().equals(propertyOfaClafer.getName())) {
					for (final AstConcreteClafer property : popertiesMap.get(propertyOfaClafer)) {
						for (final Question question : qAMap.keySet()) {
							if (qAMap.get(question).hasDependencies()) {
								for (final Dependency dependency : qAMap.get(question).getDependencies()) {
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
	}

}