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
import static org.clafer.ast.Asts.min;
import static org.clafer.ast.Asts.union;

import java.util.ArrayList;
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
	private Map<String, InstanceClafer> displayFirstNameToInstanceMap;
	private final ClaferModel claferModel;
	private String taskName;
	private String taskDescription;
	private final AstClafer taskClafer;
	private String algorithmName;
	private int algorithmCount;

	/**
	 * Constructor for Instance Generator
	 * 
	 * @param pathToModel
	 *        Absolute path to model
	 * @param nameOfTaskClafer
	 *        Task clafer
	 * @param taskDescription
	 *        Description of selected task
	 */
	public InstanceGenerator(final String pathToModel, final String nameOfTaskClafer, final String taskDescription) {
		this.claferModel = new ClaferModel(pathToModel);
		this.displayNameToInstanceMap = new HashMap<>();
		this.displayFirstNameToInstanceMap = new HashMap<>();
		this.uniqueInstances = new HashMap<>();
		this.taskName = nameOfTaskClafer;
		this.taskDescription = taskDescription;
		this.taskClafer = Utils.getModelChildByName(this.claferModel.getModel(), nameOfTaskClafer);
	}

	/**
	 * Method used by both basic and advanced user operations to add constraints to clafers before instance generation
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
						String[] operands = value.split("\\.");
						if (operands.length == 2) {
							final AstClafer operand = ClaferModelUtils.findClaferByName(taskAlgorithm.getParent(), operands[0]);
							this.taskClafer.addConstraint(equal(joinRef(join($this(), taskAlgorithm)),
								joinRef(joinRef(join(joinRef(join($this(), operand)), ClaferModelUtils.findClaferByName(operand, operands[1]))))));
						} else if (value.contains("min(")) {
							final AstSetExpr left = joinRef(join($this(), taskAlgorithm));
							AstSetExpr joined = null;
							for (final String operand : value.substring(4, value.length() - 1).split(",")) {
								operands = operand.split("\\.");
								for (int i = 0; i < operands.length; i++) {
									operands[i] = operands[i].trim();
								}
								final AstClafer operandClafer = ClaferModelUtils.findClaferByName(taskAlgorithm.getParent(), operands[0]);
								if (joined == null) {
									joined = joinRef(joinRef(join(joinRef(join($this(), operandClafer)), ClaferModelUtils.findClaferByName(operandClafer, operands[1]))));
								} else {
									joined = union(joined,
										joinRef(joinRef(join(joinRef(join($this(), operandClafer)), ClaferModelUtils.findClaferByName(operandClafer, operands[1])))));
								}
							}
							final AstSetExpr right = min(joined);
							this.taskClafer.addConstraint(equal(left, right));
						} else {
							final AstAbstractClafer taskClafer = (AstAbstractClafer) taskAlgorithm.getRef().getTargetType();
							for (final AstClafer subClafer : taskClafer.getSubs()) {
								if (subClafer.getName().endsWith(value)) {
									taskAlgorithm.addConstraint(equal(joinRef($this()), global(subClafer)));
									break;
								}
							}
						}
					}
				} else {
					taskAlgorithm.getParent().addConstraint(getFunctionFromOperator(joinRef(joinRef(join(joinRef(join($this(), taskAlgorithm)), rightOperand))),
						joinRef(global(ClaferModelUtils.findClaferByName(taskAlgorithm.getParent().getParent(), value))), operator));
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
						constraint = global(ClaferModelUtils.findClaferByName(taskAlgorithm.getParent(), claferNames[j++]));
					}
					final AstClafer astC = ClaferModelUtils.findClaferByName(taskAlgorithm.getParent(), claferNames[j]);
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
			if (claferNames.length == 1) {
				final AstSetExpr operandLeftClafer = joinRef(join(joinRef(tmpClafer), rightOperand));
				final String[] opSquare = claferNames[0].split(" ");
				final AstSetExpr operandRightClafer = joinRef(global(ClaferModelUtils.findClaferByName(rightOperand.getParent().getParent(), opSquare[1])));
				boolExp = getFunctionFromOperator(operandLeftClafer, operandRightClafer, opSquare[0]);
			} else {
				for (int i = 0; i < claferNames.length; i++) {
					if (i == 0) {
						final AstSetExpr operandLeftClafer = joinRef(joinRef(join(joinRef(tmpClafer), rightOperand)));
						final String[] opSquare = claferNames[i].split(" ");
						final AstSetExpr operandRightClafer = joinRef(global(ClaferModelUtils.findClaferByName(algorithmProperty.get(i++).getParent().getParent(), opSquare[1])));
						boolExp = getFunctionFromOperator(operandLeftClafer, operandRightClafer, opSquare[0]);
					}
					final String[] opSquare = claferNames[i].split(" ");
					final AstSetExpr operandLeftClafer = joinRef(join(joinRef(tmpClafer), algorithmProperty.get(i)));
					final AstSetExpr operandRightClafer = joinRef(global(ClaferModelUtils.findClaferByName(algorithmProperty.get(i).getParent().getParent(), opSquare[1])));
					final AstBoolExpr addExp = getFunctionFromOperator(operandLeftClafer, operandRightClafer, opSquare[0]);
					boolExp = and(boolExp, addExp);
				}
			}
			taskAlgorithm.getParent().addConstraint(all(decl, boolExp));
		}
	}

	private void advancedModeHandler(final AstModel astModel, final AstClafer taskClafer, final List<PropertyWidget> constraints) {
		for (final PropertyWidget constraint : constraints) {
			if (constraint.isEnabled() && !constraint.isGroupConstraint()) { //not sure why we need this check but keeping it from Ram's code till we figure it out
				final String operator = constraint.getOperator();
				final String value = constraint.getValue();
				final AstConcreteClafer parent = (AstConcreteClafer) ClaferModelUtils.findClaferByName(taskClafer, constraint.getParentClafer().getName());
				final List<AstConcreteClafer> operand = new ArrayList<>();
				operand.add((AstConcreteClafer) ClaferModelUtils.findClaferByName(taskClafer, constraint.getChildClafer().getName()));
				if (operand != null && ClaferModelUtils.isConcrete(operand.get(0))) {
					addConstraints(parent, operand, operator, value);
				}
			}
		}
	}

	private void basicModeHandler(final AstModel astModel, final AstClafer taskClafer, final HashMap<Question, Answer> qAMap) {
		for (final Entry<Question, Answer> entry : qAMap.entrySet()) {
			final Answer answer = entry.getValue();
			if (answer.getClaferDependencies() != null) {
				for (final ClaferDependency claferDependency : answer.getClaferDependencies()) {
					if ("->".equals(claferDependency.getOperator())) {
						ClaferModelUtils.createClafer(taskClafer, claferDependency.getAlgorithm(), claferDependency.getValue());
					} else {
						final AstClafer algorithmClafer = ClaferModelUtils.findClaferByName(taskClafer, claferDependency.getAlgorithm());
						final List<AstConcreteClafer> propertyClafer = new ArrayList<>();
						final String operand = claferDependency.getOperand();
						if (operand != null && operand.contains(";")) {
							for (final String name : operand.split(";")) {
								propertyClafer.add((AstConcreteClafer) ClaferModelUtils.findClaferByName(algorithmClafer.getParent(), name));
							}
						} else {
							propertyClafer.add((AstConcreteClafer) ClaferModelUtils.findClaferByName(algorithmClafer.getParent(), operand));
						}
						addConstraints(algorithmClafer, propertyClafer, claferDependency.getOperator(), claferDependency.getValue());
					}
				}
			}
		}
	}

	private void generateInstanceMapping() {
		this.displayNameToInstanceMap.clear();
		// sort all the instances, to have an user friendly display
		try {
			this.generatedInstances.sort(new ClaferComparator());
		} catch (final Exception ex) {
			Activator.getDefault().logError("Instances not sorted by security level. Be cautious");
		}
		for (final InstanceClafer sortedInst : this.generatedInstances) {

			String key = getInstanceName(sortedInst);
			if (key.isEmpty()) {
				key = sortedInst.getChildren()[0].getRef().toString();
				this.displayNameToInstanceMap.remove(key, sortedInst);
			}
			if (sortedInst.getType().getName().equals(this.taskName) && key.length() > 0) {
				// Check if any instance has same name , if yes add numerical values as suffix
				int counter = 1;
				String copyKey = key;
				while (this.displayNameToInstanceMap.containsKey(copyKey)) {
					copyKey = key + "(" + String.format("%02d", ++counter) + ")";
					setAlgorithmCount(counter);
				}

				this.displayNameToInstanceMap.put(copyKey, sortedInst);
				setAlgorithmName(key);

			}
		}
		this.displayNameToInstanceMap = new TreeMap<>(this.displayNameToInstanceMap);
	}

	/**
	 * Method to generate instances for basic user.
	 *
	 * @param questAnswerMap
	 *        Map mapping questions to user-given answers.
	 * @return List of generated Instance
	 */
	public List<InstanceClafer> generateInstances(final HashMap<Question, Answer> questAnswerMap) {
		final AstModel astModel = this.claferModel.getModel();
		try {
			basicModeHandler(astModel, this.taskClafer, questAnswerMap);

			this.solver = ClaferCompiler.compile(astModel,
				this.claferModel.getScope().toBuilder()
					//.defaultScope(Integer.parseInt(new ReadConfig().getValue(DEFAULT_SCOPE)))
					.intHigh(Constants.INT_HIGH).intLow(Constants.INT_LOW));

			int redundantCounter = 0;
			while (this.solver.find()) {
				if (this.solver.instance().getTopClafers().length > 0) {
					InstanceClafer[] topClafers = this.solver.instance().getTopClafers();
					InstanceClafer taskInstance = null;

					for (InstanceClafer instanceClafer : topClafers) {
						if (instanceClafer.getType().equals(this.taskClafer)) {
							taskInstance = instanceClafer;
							break;
						}
					}

					if (taskClafer != null) {

						final long hashValueOfInstance = getHashValueOfInstance(taskInstance);

						if (this.uniqueInstances.containsKey(hashValueOfInstance)) {
							if (++redundantCounter > 1000) {
								break;
							}
						} else {
							this.uniqueInstances.put(hashValueOfInstance, taskInstance);
							redundantCounter = 0;
						}
						if (this.uniqueInstances.size() > 100) {
							break;
						}

					}
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
	 * Method to generate instances in an advanced user mode.
	 * 
	 * @param constraints
	 *        List of constraints set by the user
	 * @return List of generated Instance
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
		switch (operator) {
			case "=":
				return equal(operandLeftClafer, operandRightClafer);
			case "<":
				return lessThan(operandLeftClafer, operandRightClafer);
			case ">":
				return greaterThan(operandLeftClafer, operandRightClafer);
			case "<=":
				return lessThanEqual(operandLeftClafer, operandRightClafer);
			case ">=":
				return greaterThanEqual(operandLeftClafer, operandRightClafer);
			default:
				return null;
		}
	}

	/**
	 * Returns the hash value of the instance passed as an argument
	 *
	 * @see InstanceClaferHash#hashCode() 
	 */
	private long getHashValueOfInstance(final InstanceClafer inst) {
		int hash = 37;
		for (final InstanceClafer child : inst.getChildren()) {
			hash *= new InstanceClaferHash(child).hashCode();
		}

		return hash;
	}

	private String getInstanceName(final InstanceClafer inst) {
		String currentInstanceName = "";
		try {
			if (inst.hasChildren()) {
				for (final InstanceClafer childClafer : inst.getChildren()) {
					if (currentInstanceName.length() > 0) {
						final String childInstanceName = getInstanceName(childClafer);
						if (childInstanceName.length() > 0) {
							currentInstanceName += "+" + childInstanceName;
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
	 * get list of generated instances, sorted by security, if possible
	 * 
	 * @return {@link List}<{@link InstanceClafer}> of generated instances
	 */
	public List<InstanceClafer> getGeneratedInstances() {
		return generatedInstances;
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
	 * gives the instances, key field for First Instance .
	 *
	 * @return
	 */
	public Map<String, InstanceClafer> getFirstInstance() {
		return this.displayFirstNameToInstanceMap;
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
		this.displayFirstNameToInstanceMap = null;
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

	public void setAlgorithmName(final String algorithmName) {
		this.algorithmName = algorithmName;

	}

	public String getAlgorithmName() {
		return this.algorithmName;

	}

	public void setAlgorithmCount(final int algorithmCount) {
		this.algorithmCount = algorithmCount;

	}

	public int getAlgorithmCount() {
		return this.algorithmCount;

	}
}