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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstBoolExpr;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstModel;
import org.clafer.cli.Utils;
import org.clafer.common.Check;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferSolver;
import org.clafer.compiler.ClaferUnsat;
import org.clafer.instance.InstanceClafer;
import org.clafer.scope.Scope;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Dependency;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.wizard.advanced.PropertyWidget;

/**
 * Class responsible for generating generatedInstances for a given clafer.
 *
 */
public class InstanceGenerator {

	private ClaferSolver solver;
	private List<InstanceClafer> generatedInstances;
	private Map<Long, InstanceClafer> uniqueInstances;
	private Map<String, InstanceClafer> displayNameToInstanceMap;
	private ClaferModel claferModel;
	private String taskName;
	private String taskDescription;
	private AstClafer taskClafer;
	//we basically create a new instance generator for each task we trigger it for so why not just have this task property map here to avoid references issues
	//private Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> taskPropertyMap;

	public InstanceGenerator(final String path, String taskName, String taskDescription) {
		this.claferModel = new ClaferModel(path);
		this.displayNameToInstanceMap = new HashMap<String, InstanceClafer>();
		this.uniqueInstances = new HashMap<Long, InstanceClafer>();
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		taskClafer = Utils.getModelChildByName(claferModel.getModel(), taskName);
	//	taskPropertyMap = new HashMap<AstConcreteClafer, ArrayList<AstConcreteClafer>>();
//		if(taskName != null && !taskName.isEmpty()){
//			fillTaskPropertyMap();
//		}
	}
	
//	private void fillTaskPropertyMap() {
//		if (taskClafer.hasChildren())
//			for (AstConcreteClafer childClafer : taskClafer.getChildren()) {				
//				ArrayList<AstConcreteClafer> propertiesList = new ArrayList<AstConcreteClafer>();
//				ClaferModelUtils.findClaferProperties(childClafer, propertiesList, null);
//				//addClaferProperties(childClafer, propertiesList);
//				taskPropertyMap.put(childClafer, propertiesList);
//				
//			}	
//	}
	

	/**
	 * this method is part of instance generation process , creates a mapping instance name and instance Object
	 */
	public void generateInstanceMapping() {
		for (InstanceClafer inst : this.generatedInstances) {
			String key = getInstanceName(inst);
			if (inst.getType().getName().equals(taskName) && key.length() > 0) {
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
					 * There is no need to check if the counter value is not 1 , because this loop will be executed only if there is a match in name of an instances
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
		final AstModel astModel = claferModel.getModel();
		try {
			basicModeHandler(astModel, taskClafer, map);
			
			solver = ClaferCompiler.compile(astModel,
				claferModel.getScope().toBuilder()
					//.defaultScope(Integer.parseInt(new ReadConfig().getValue(DEFAULT_SCOPE)))
					.intHigh(Constants.INT_HIGH).intLow(Constants.INT_LOW));

			while (this.solver.find()) {
				final InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length - 1];
				uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}
	
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		generatedInstances = new ArrayList<InstanceClafer>(uniqueInstances.values());
		generateInstanceMapping();
		return generatedInstances;
	}

	/**
	 * Method to generate instances in an advanced user mode, takes map with claer and their values as parameterF
	 *
	 * @param propertiesMap
	 * @return
	 */
	public List<InstanceClafer> generateInstancesAdvancedUserMode(final List<PropertyWidget> constraints) {
		final AstModel model = claferModel.getModel();
		try {
	
			//PropertiesMapperUtil.getTaskLabelsMap().get(getTaskDescription());
			advancedModeHandler(model, taskClafer, constraints);
			
			// TODO Need to be uncommented after fix
			// addGroupProperties(tempTask, constraints);
			solver = ClaferCompiler.compile(model, claferModel.getScope().toBuilder().intHigh(Constants.INT_HIGH).intLow(Constants.INT_LOW));
			while (solver.find()) {
				InstanceClafer instance = solver.instance().getTopClafers()[solver.instance().getTopClafers().length - 1];
				uniqueInstances.put(getHashValueOfInstance(instance), instance);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			Activator.getDefault().logError(e);
		}
		generatedInstances = new ArrayList<InstanceClafer>(uniqueInstances.values());
		generateInstanceMapping();
		//setNoOfInstances(displayNameToInstanceMap.keySet().size());
		return generatedInstances;
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
	 * method used by both basic and advanced user operations to add constraints to clafers before instance generation
	 *
	 * operator is the numeric value which indicates > < >= <= == operations
	 *
	 * main is the higher level clafer ,usually task choose by user
	 *
	 * value is the numeric or string value which will be added as a constraints, EX outPutLength=128 here 128 is the value
	 *
	 * operand is the clafer on which constraint is being applied EX outPutLength=128 outPutLength is operand here
	 *
	 * claf is a clafer used only with XOR
	 *
	 * @param operator
	 * @param childClafer
	 * @param value
	 * @param operand
	 * @param claf
	 */
	void addConstraints(AstClafer taskAlgorithm, AstConcreteClafer algorithmProperty, String operator, int value) {
		if (operator.equals("=")) {
			System.out.println("adding equal");
			taskAlgorithm.addConstraint(equal(joinRef(join(joinRef($this()), algorithmProperty)), constant(value)));
		} else if (operator.equals("<")) {
			taskAlgorithm.addConstraint(lessThan(joinRef(join(joinRef($this()), algorithmProperty)), constant(value)));
		} else if (operator.equals(">")) {
			taskAlgorithm.addConstraint(greaterThan(joinRef(join(joinRef($this()), algorithmProperty)), constant(value)));
		} else if (operator.equals("<=")) {
			taskAlgorithm.addConstraint(lessThanEqual(joinRef(join(joinRef($this()), algorithmProperty)), constant(value)));
		} else if (operator.equals(">=")) {
			taskAlgorithm.addConstraint(greaterThanEqual(joinRef(join(joinRef($this()), algorithmProperty)), constant(value)));			
		}
	}


	/**
	 * This method is to parse the map of clafers and apply their values as constraints before instance generation, used only in advanceduserMode
	 *
	 * @param taskClafer
	 * @param propertiesMap
	 */
	void advancedModeHandler(AstModel astModel, AstClafer taskClafer, final List<PropertyWidget> constraints) {
		for (AstConcreteClafer taskAlgorithm : taskClafer.getChildren()) {
			for (PropertyWidget constraint : constraints) {
				if (!constraint.isGroupConstraint()){
					if (constraint.getParentClafer().getName().equals(taskAlgorithm.getName())) {
						final String operator = constraint.getOperator();
						final int value = constraint.getValue();
						final AstConcreteClafer operand = (AstConcreteClafer) ClaferModelUtils.findClaferByName(taskAlgorithm, constraint.getChildClafer().getName());
						if (operand != null && !ClaferModelUtils.isAbstract(operand)) {							
							addConstraints(taskAlgorithm, operand, operator, value);
						}
					}
				}
			}
		}
	}

	/**
	 * BasicModeHandler will take <Question, answer> map as a parameter where the key of the map is a question, answer is the selected answer for a given question each answer has been further iterated to apply associated dependencies
	 */
	// FIXME include group operator
	void basicModeHandler(AstModel astModel, AstClafer taskClafer, final HashMap<Question, Answer> qAMap) {		
		for (Question question : qAMap.keySet()){
			Answer answer = qAMap.get(question);
			for (Dependency dependency : answer.getDependencies()){
				AstClafer algorithmClafer = ClaferModelUtils.findClaferByName(taskClafer, "c0_" + dependency.getAlgorithm());
				AstConcreteClafer propertyClafer = (AstConcreteClafer) ClaferModelUtils.findClaferByName(algorithmClafer, "c0_" + dependency.getOperand());				
				addConstraints(algorithmClafer, propertyClafer, dependency.getOperator(), Integer.parseInt(dependency.getValue()));				
			}
		}
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
		return uniqueInstances.size();
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
	 * to Set task name
	 *
	 * @param taskName
	 */
	public void setTaskName(final String taskName) {
		this.taskName = taskName;
	}
	
	

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
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
		if (inst.hasChildren()) {
			final InstanceClafer sub = (InstanceClafer) inst.getChildren()[0].getRef();
			if (sub != null) {
				return new InstanceClaferHash(sub).hashCode();
			}
		}
		return 0;
	}
}