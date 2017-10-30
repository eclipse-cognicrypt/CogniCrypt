/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
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
package de.cognicrypt.codegenerator.featuremodel.clafer;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.all;
import static org.clafer.ast.Asts.and;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.decl;
import static org.clafer.ast.Asts.equal;
import static org.clafer.ast.Asts.global;
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.greaterThanEqual;
import static org.clafer.ast.Asts.join;
import static org.clafer.ast.Asts.joinRef;
import static org.clafer.ast.Asts.lessThan;
import static org.clafer.ast.Asts.lessThanEqual;
import static org.clafer.ast.Asts.local;
import static org.clafer.ast.Asts.union;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstBoolExpr;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstDecl;
import org.clafer.ast.AstLocal;
import org.clafer.ast.AstModel;
import org.clafer.ast.AstSetExpr;
import org.clafer.cli.Utils;
import org.clafer.common.Check;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.instance.InstanceClafer;
import org.clafer.scope.Scope;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.wizard.advanced.PropertyWidget;

/**
 * Class responsible for generating generatedInstances for a given clafer.
 *
 */
public class InstanceGenerator {

	private ClaferSolver solver;
	private List<InstanceClafer> generatedInstances;
	private final Map<Long, InstanceClafer> uniqueInstances;
	private Map<String, InstanceClafer> displayNameToInstanceMap;
	private final ClaferModel claferModel;
	private String taskName;
	private String taskDescription;
	private final AstClafer taskClafer;

	public InstanceGenerator(final String path, final String taskName, final String taskDescription) {
		this.claferModel = new ClaferModel(path);
		this.displayNameToInstanceMap = new HashMap<String, InstanceClafer>();
		this.uniqueInstances = new HashMap<>();
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.taskClafer = Utils.getModelChildByName(this.claferModel.getModel(), taskName);
	}

	/**
	 *
	 * method used by both basic and advanced user operations to add constraints to clafers before instance generation
	 *
	 * @param taskAlgorithm
	 *        Higher-level Clafer
	 * @param algorithmProperty
	 *        Clafer on which constraint is being applied EX outPutLength=128 outPutLength is operan
	 * @param operator
	 *        Currently supported: Single - =, >,<,>=, <=; Multiple - ++, |
	 * @param value
	 *        Numeric or String value added as a constraint, EX outPutLength=128 here 128 is the value
	 */
	private void addConstraints(final AstClafer taskAlgorithm, final List<AstConcreteClafer> algorithmProperty, final String operator, final String value) {
		final AstConcreteClafer rightOperand = algorithmProperty.get(0);
		if (algorithmProperty.size() == 1) {
			try {
				final Integer valueAsInt = Integer.parseInt(value);
				if (rightOperand == null && "=".equals(operator)) {
					taskAlgorithm.addConstraint(equal(joinRef($this()), constant(valueAsInt)));
				} else {
					taskAlgorithm.addConstraint(getFunctionFromOperator(joinRef(join(joinRef($this()), rightOperand)), constant(valueAsInt), operator));
				}
			} catch (final NumberFormatException e) {
				if (operator.equals("=")) {
					if (rightOperand != null) {
						taskAlgorithm.addConstraint(equal(joinRef(join(joinRef($this()), rightOperand)), constant(value)));
					} else {
						final AstAbstractClafer taskClafer = (AstAbstractClafer) taskAlgorithm.getRef().getTargetType();
						for (final AstClafer subClafer : taskClafer.getSubs()) {
							if (subClafer.getName().endsWith(value)) {
								taskAlgorithm.addConstraint(equal(joinRef($this()), global(subClafer)));
								break;
							}
						}
					}
				} else {
					taskAlgorithm.getParent().addConstraint(getFunctionFromOperator(joinRef(joinRef(join(joinRef(join($this(), taskAlgorithm)), rightOperand))),
						joinRef(global(ClaferModelUtils.findClaferByName(taskAlgorithm.getParent().getParent(), "c0_" + value))), operator));
				}
			}

		} else if (operator.equals("++")) {
			final String[] claferNames = value.split(";");
			final int length = claferNames.length;
			AstSetExpr constraint = null;
			if (length == 1) {
				addConstraints(taskAlgorithm, algorithmProperty, "=", value);
			} else {
				for (int j = 0; j < length; j++) {
					if (j == 0) {
						constraint = global(ClaferModelUtils.findClaferByName(taskAlgorithm.getParent(), "c0_" + claferNames[j++]));
					}
					final AstClafer astC = ClaferModelUtils.findClaferByName(taskAlgorithm.getParent(), "c0_" + claferNames[j]);
					constraint = union(constraint, global(astC));
				}
			}
			taskAlgorithm.addConstraint(equal(joinRef(join($this(), rightOperand)), constraint));
		} else if (operator.equals("|")) {

			final String[] claferNames = value.split(";");
			//The constraint that is created looks like [all $consName : taskAlgorithm | $consName.algorithmProperty claferNames[0] && ...]
			final AstLocal tmpClafer = local("suite");
			final AstDecl decl = decl(tmpClafer, join($this(), taskAlgorithm));
			AstBoolExpr boolExp = null;
			final AstSetExpr operandLeftClafer = joinRef(join(joinRef(tmpClafer), rightOperand));
			if (claferNames.length == 1) {
				final String[] opSquare = claferNames[0].split(" ");
				final AstSetExpr operandRightClafer = joinRef(global(ClaferModelUtils.findClaferByName(rightOperand.getParent().getParent(), "c0_" + opSquare[1])));
				boolExp = getFunctionFromOperator(operandLeftClafer, operandRightClafer, opSquare[0]);
			} else {
				for (int i = 0; i < claferNames.length; i++) {
					if (i == 0) {
						final String[] opSquare = claferNames[i].split(" ");
						final AstSetExpr operandRightClafer = joinRef(
							global(ClaferModelUtils.findClaferByName(algorithmProperty.get(i++).getParent().getParent(), "c0_" + opSquare[1])));
						boolExp = getFunctionFromOperator(operandLeftClafer, operandRightClafer, operator);
					}
					final String[] opSquare = claferNames[i].split(" ");
					final AstSetExpr operandRightClafer = joinRef(global(ClaferModelUtils.findClaferByName(algorithmProperty.get(i).getParent().getParent(), "c0_" + opSquare[1])));
					final AstBoolExpr addExp = getFunctionFromOperator(operandLeftClafer, operandRightClafer, operator);
					boolExp = and(boolExp, addExp);
				}
			}
			taskAlgorithm.getParent().addConstraint(all(decl, boolExp));
		}
	}

	/**
	 * This method is to parse the map of clafers and apply their values as constraints before instance generation, used only in advanceduserMode
	 *
	 * @param taskClafer
	 * @param propertiesMap
	 */
	private void advancedModeHandler(final AstModel astModel, final AstClafer taskClafer, final List<PropertyWidget> constraints) {
		for (final PropertyWidget constraint : constraints) {
			if (constraint.isEnabled() && !constraint.isGroupConstraint()) { //not sure why we need this check but keeping it from Ram's code till we figure it out
				final String operator = constraint.getOperator();
				final String value = constraint.getValue();
				final AstConcreteClafer parent = (AstConcreteClafer) ClaferModelUtils.findClaferByName(taskClafer, constraint.getParentClafer().getName());
				final List<AstConcreteClafer> operand = new ArrayList<>();
				operand.add((AstConcreteClafer) ClaferModelUtils.findClaferByName(taskClafer, constraint.getChildClafer().getName()));
				if (operand != null && !ClaferModelUtils.isAbstract(operand.get(0))) {
					addConstraints(parent, operand, operator, value);
				}
			}
		}
	}

	/**
	 * BasicModeHandler will take <Question, answer> map as a parameter where the key of the map is a question, answer is the selected answer for a given question each answer has
	 * been further iterated to apply associated dependencies
	 */
	// FIXME include group operator
	private void basicModeHandler(final AstModel astModel, final AstClafer taskClafer, final HashMap<Question, Answer> qAMap) {
		for (final Entry<Question, Answer> entry : qAMap.entrySet()) {
			Answer answer = entry.getValue();
			if (answer.getClaferDependencies() != null) {
				for (final ClaferDependency claferDependency : answer.getClaferDependencies()) {
					if ("->".equals(claferDependency.getOperator())) {
						ClaferModelUtils.createClafer(taskClafer, claferDependency.getAlgorithm(), claferDependency.getValue());
					} else {
						final AstClafer algorithmClafer = ClaferModelUtils.findClaferByName(taskClafer, "c0_" + claferDependency.getAlgorithm());
						final List<AstConcreteClafer> propertyClafer = new ArrayList<>();
						final String operand = claferDependency.getOperand();
						if (operand != null && operand.contains(";")) {
							for (final String name : operand.split(";")) {
								propertyClafer.add((AstConcreteClafer) ClaferModelUtils.findClaferByName(algorithmClafer, "c0_" + name));
							}
						} else {
							propertyClafer.add((AstConcreteClafer) ClaferModelUtils.findClaferByName(algorithmClafer, "c0_" + operand));
						}
						addConstraints(algorithmClafer, propertyClafer, claferDependency.getOperator(), claferDependency.getValue());
					}
				}
			}
		}
	}

	/**
	 * this method is part of instance generation process , creates a mapping instance name and instance Object
	 */
	private void generateInstanceMapping() {
		this.displayNameToInstanceMap.clear();
		/**
		 * sort all the instances, to have an user friendly display
		 */
		try {
			Collections.sort(this.generatedInstances, new Comparator<InstanceClafer>() {

				@Override
				public int compare(InstanceClafer left, InstanceClafer right) {
					return -Integer.compare(getSecurityLevel(left), getSecurityLevel(right));
				}

				private Integer getSecurityLevel(InstanceClafer instance) {
					for (InstanceClafer innerInst : instance.getChildren()) {
						if (innerInst.getType().getName().contains("security")) {
							Object level = innerInst.getRef();
							if (level instanceof Integer) {
								return (Integer) level;
							}
						}
					}
					return -1;
				}

			});
		} catch (Exception ex) {
			Activator.getDefault().logError("Instances not sorted by security level. Be cautious");
		}
		for (InstanceClafer sortedInst : this.generatedInstances) {
			String key = getInstanceName(sortedInst);
			if (key.isEmpty()) {
				key = sortedInst.getChildren()[0].getRef().toString();
			}
			if (sortedInst.getType().getName().equals(this.taskName) && key.length() > 0) {
				/**
				 * Check if any instance has same name , if yes add numerical values as suffix
				 *
				 */
				int counter = 1;
				String copyKey = key;
				while (displayNameToInstanceMap.containsKey(copyKey)) {
					copyKey = key + "(" + String.format("%02d", ++counter) + ")";
				}

				this.displayNameToInstanceMap.put(copyKey, sortedInst);
			}
		}
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
		final AstModel astModel = this.claferModel.getModel();
		try {
			basicModeHandler(astModel, this.taskClafer, map);

			this.solver = ClaferCompiler.compile(astModel,
				this.claferModel.getScope().toBuilder()
					//.defaultScope(Integer.parseInt(new ReadConfig().getValue(DEFAULT_SCOPE)))
					.intHigh(Constants.INT_HIGH).intLow(Constants.INT_LOW));

			int redundantCounter = 0;
			while (this.solver.find()) {
				final InstanceClafer instance = this.solver.instance().getTopClafers()[this.solver.instance().getTopClafers().length - 1];
				final long hashValueOfInstance = getHashValueOfInstance(instance);

				if (this.uniqueInstances.containsKey(hashValueOfInstance)) {
					if (++redundantCounter > 1000) {
						break;
					}
				} else {
					this.uniqueInstances.put(hashValueOfInstance, instance);
					redundantCounter = 0;
				}
				if (this.uniqueInstances.size() > 100) {
					break;
				}
			}

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		this.generatedInstances = new ArrayList<>(this.uniqueInstances.values());
		generateInstanceMapping();
		return this.generatedInstances;
	}

	/**
	 * Method to generate instances in an advanced user mode, takes map with claer and their values as parameterF
	 *
	 * @param propertiesMap
	 * @return
	 */
	public List<InstanceClafer> generateInstancesAdvancedUserMode(final List<PropertyWidget> constraints) {
		final AstModel model = this.claferModel.getModel();
		try {

			//PropertiesMapperUtil.getTaskLabelsMap().get(getTaskDescription());
			advancedModeHandler(model, this.taskClafer, constraints);

			// TODO Need to be uncommented after fix
			// addGroupProperties(tempTask, constraints);
			this.solver = ClaferCompiler.compile(model, this.claferModel.getScope().toBuilder().intHigh(Constants.INT_HIGH).intLow(Constants.INT_LOW));
			while (this.solver.find()) {
				final InstanceClafer instance = this.solver.instance().getTopClafers()[this.solver.instance().getTopClafers().length - 1];
				this.uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		this.generatedInstances = new ArrayList<>(this.uniqueInstances.values());
		generateInstanceMapping();

		return this.generatedInstances;
	}

	private AstBoolExpr getFunctionFromOperator(final AstSetExpr operandLeftClafer, final AstSetExpr operandRightClafer, final String operator) {
		if (operator.equals("=")) {
			return equal(operandLeftClafer, operandRightClafer);
		} else if (operator.equals("<")) {
			return lessThan(operandLeftClafer, operandRightClafer);
		} else if (operator.equals(">")) {
			return greaterThan(operandLeftClafer, operandRightClafer);
		} else if (operator.equals("<=")) {
			return lessThanEqual(operandLeftClafer, operandRightClafer);
		} else if (operator.equals(">=")) {
			return greaterThanEqual(operandLeftClafer, operandRightClafer);
		}
		return null;
	}

	/**
	 * Returns the hash value of the instance passed as an argument
	 *
	 * @param inst
	 * @return
	 */
	private long getHashValueOfInstance(final InstanceClafer inst) {
		// TODO: Why child at position 0, why is 0 returned if there is no
		// child?
		int hash = 37;
		for (final InstanceClafer child : inst.getChildren()) {
			hash *= new InstanceClaferHash(child).hashCode();
		}

		return hash;
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
			} else if (inst.hasRef() && !inst.getType().isPrimitive() && !inst.getRef().getClass().toString().contains(Constants.INTEGER) && !inst.getRef().getClass().toString()
				.contains(Constants.STRING) && !inst.getRef().getClass().toString().contains(Constants.BOOLEAN)) {
				currentInstanceName += getInstanceName((InstanceClafer) inst.getRef());
			} else if (inst.getType().getName().contains("_name") && inst.getRef().getClass().toString().contains(Constants.STRING)) {
				return inst.getRef().toString().replace("\"", "");
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
		return this.uniqueInstances.size();
	}

	/**
	 * scope of the model
	 *
	 * @return
	 */
	public Scope getScope() {
		return Check.notNull(this.claferModel.getScope());
	}

	public String getTaskDescription() {
		return this.taskDescription;
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

	public void setTaskDescription(final String taskDescription) {
		this.taskDescription = taskDescription;
	}

	/**
	 * to Set task name
	 *
	 * @param taskName
	 */
	public void setTaskName(final String taskName) {
		this.taskName = taskName;
	}
}