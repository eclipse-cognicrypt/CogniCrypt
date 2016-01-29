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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstModel;
import org.clafer.collection.Triple;
import org.clafer.common.Check;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;

public class ClaferModel {

	private String modelName;
	Triple<AstModel, Scope, Objective[]> triple;
	private Map<String, AstConcreteClafer> constraintClafers;
	// private ParseClafer pClafer = new ParseClafer();
	ArrayList<AstConcreteClafer> propertiesList;
	HashMap<AstConcreteClafer, AstConcreteClafer> childClaferList;

	/**
	 * Constructor for claferModel which takes absolute path for the js as parm
	 * 
	 * @param path
	 */
	public ClaferModel(String path) {
		loadModel(path);
		propertiesList = new ArrayList<AstConcreteClafer>();
	}

	/**
	 * returns the scope of the model
	 * 
	 * @return
	 */
	public Scope getScope() {
		return triple.getSnd();
	}

	/**
	 * Initializes the model and also list the task list Tasks lists are those
	 * who extends Task
	 * 
	 * @param path
	 */
	private void loadModel(String path) {
		try {
			File filename = new File(path);

			triple = Javascript.readModel(filename, Javascript.newEngine());

			this.setModelName("Cyrptography Task Configurator");
			setTaskList(triple.getFst());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * list the task list, Tasks lists are those who extends Abstract clafer
	 * Task
	 * 
	 * @param model
	 */
	public void setTaskList(AstModel model) {
		String key = "";
		for (AstAbstractClafer object : model.getAbstracts()) {
			// Find all the abstract first and select only the abstact with name Task
			if (object.getName().contains("Task") == true) {
				for (AstClafer clafer : object.getSubs()) { // get all clafers which are derived from "Task" 
					for (AstConstraint constraint : clafer.getConstraints()) {
						// Check Task description , and put that as Key 
						if (constraint.getExpr().toString().contains("description . ref")) {
							key = constraint.getExpr().toString()
									.substring(constraint.getExpr().toString().indexOf("=") + 1,
											constraint.getExpr().toString().length())
									.trim().replace("\"", "");
						}
					}
					/**
					 * construct a map of tasks, key is a clafer description and
					 * value is actual clafer Key will be used in Wizard, as an
					 * input for taskList combo box
					 */
					PropertiesMapperUtil.getTaskLabelsMap().put(key, (AstConcreteClafer) clafer);

				}
			}
		}
	}
	
	/**
	 * list the Enums
	 * 
	 * @param model
	 */
	public void setEnumList(AstModel model) {
		String key = "";
		for (AstAbstractClafer object : model.getAbstracts()) {
			if (object.getName().contains("Enum") == true) {
				for (AstClafer clafer : object.getSubs()) {
					
				
					/**
					 * construct a map of tasks, key is a clafer description and
					 * value is actual clafer Key will be used in Wizard, as an
					 * input for taskList combo box
					 */
					//PropertiesMapperUtil.getTaskLabelsMap().put(key, (AstConcreteClafer) clafer);

				}
			}
		}
	}
	/**
	 * set a model name
	 * 
	 * @param modelName
	 */
	public void setModelName(String modelName) {

		this.modelName = modelName;

	}

	/**
	 * 
	 * @return
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * returns the astModel from the clafer list
	 * 
	 * @return
	 */
	public AstModel getModel() {
		return triple.getFst();
	}

	/**
	 * returns the clafer constraint list
	 * 
	 * @return
	 */
	public Map<String, AstConcreteClafer> getConstraintClafers() {
		return Check.notNull(constraintClafers);
	}

	/**
	 * Method provides map of clafer with a desired name.
	 * 
	 * Ex: Search clafer with name performance throughout the model
	 * 
	 * @param name
	 * @return
	 */
	public HashMap<AstConcreteClafer, AstConcreteClafer> getChildrenListbyName(String name) {
		childClaferList = new HashMap<>();
		for (AstClafer child : this.getModel().getChildren()) {
			setChildrenList(child, child, name);
		}
		return childClaferList;
	}

	/**
	 * Initializes the children of a given clafer , used in testing also invoked
	 * by getChildrenListByName(). Retention is a HashMap data structure where
	 * key is parent clafer and inputclafer is recursive parameter clafer.
	 * parentClafer remains constant throughout the iteration. When the name of
	 * the clafer matches with the string being passed, map entry is constructed
	 * as <parentclafer,matchedClafer>
	 * 
	 * @param parentClafer
	 * @param inputClafer
	 * @param name
	 */
	private void setChildrenList(AstClafer parentClafer, AstClafer inputClafer, String name) {
		try {
			if (inputClafer.getName().contains(name)) {
				childClaferList.put((AstConcreteClafer) parentClafer, (AstConcreteClafer) inputClafer);

			}
			if (inputClafer.hasChildren()) {
				for (AstConcreteClafer in : inputClafer.getChildren())
					setChildrenList(parentClafer, in, name);
			}
			if (inputClafer.hasRef()) {
				if (inputClafer.getRef().getTargetType().isPrimitive() == false) {
					setChildrenList(parentClafer, inputClafer.getRef().getTargetType(), name);
				}
			}
			if (inputClafer.getSuperClafer() != null)
				setChildrenList(parentClafer, inputClafer.getSuperClafer(), name);
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	/**
	 * Method performs same functionality as of the above method, written for
	 * Abstract clafer
	 * 
	 * @param parentClafer
	 * @param inputClafer
	 * @param claferName
	 */
	private void setChildrenList(AstClafer parentClafer, AstAbstractClafer inputClafer, String claferName) {

		try {
			if (inputClafer.hasChildren()) {
				for (AstConcreteClafer childClafer : inputClafer.getChildren())
					setChildrenList(parentClafer, childClafer, claferName);
			}
			if (inputClafer.hasRef())
				setChildrenList(parentClafer, inputClafer.getRef().getTargetType(), claferName);

			if (inputClafer.getSuperClafer() != null)
				setChildrenList(parentClafer, inputClafer.getSuperClafer(), claferName);

		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	//
	// /**
	// * @param astConcreteClafer
	// */
	// public void getPrimitive(AstConcreteClafer astConcreteClafer) {
	// pClafer.getPrimitive(astConcreteClafer);
	//
	// }
	/**
	 * Recursive method to list subclafers of a clafer
	 * 
	 * @param inputClafer
	 */
	public void addClaferProperties(AstClafer inputClafer) {
		try {
			if (inputClafer.hasChildren()) {
				if (inputClafer.getGroupCard().getLow() >= 1) {
					propertiesList.add((AstConcreteClafer) inputClafer);
				} else
					for (AstConcreteClafer childClafer : inputClafer.getChildren())
						addClaferProperties(childClafer);
			}
			if (inputClafer.hasRef()) {
				if (inputClafer.getRef().getTargetType().isPrimitive() == true
						&& (inputClafer.getRef().getTargetType().getName().contains("string") == false)) {
					propertiesList.add((AstConcreteClafer) inputClafer);

				} else if (inputClafer.getRef().getTargetType().isPrimitive() == false) {
					addClaferProperties(inputClafer.getRef().getTargetType());
				}
			}
			if (inputClafer.getSuperClafer() != null)
				addClaferProperties(inputClafer.getSuperClafer());
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	/**
	 * Recursive method to list properties or subclafres of an Abstract clafer
	 * 
	 * @param inputClafer
	 */
	public void addClaferProperties(AstAbstractClafer inputClafer) {

		try {
			if (inputClafer.hasChildren()) {
				for (AstConcreteClafer in : inputClafer.getChildren())
					addClaferProperties(in);
			}
			if (inputClafer.hasRef())
				addClaferProperties(inputClafer.getRef().getTargetType());

			if (inputClafer.getSuperClafer() != null)
				addClaferProperties(inputClafer.getSuperClafer());

		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	/**
	 * Method which creates the list of subclafers in a given task , It omits
	 * String type If a subclafers refers to another clafer then search is
	 * performed recursively to list all the subclafers
	 * 
	 * 	PasswordStoring : Task
	  		[Description = "Password Storing"] 
	  		digestToUse ->Digest ? 
	  		kdaToUse -> KeyDerivationAlgorithm ?
	  	abstract Digest : Algorithm 
	  		outputSize -> integer
	  	abstract KeyDerivationAlgorithm : Algorithm
	 	
	 	abstract Algorithm 
	 		name -> string 
	 		performance -> integer
	 
	 * if input clafer is passwordStoring  then PropertiesMapperUtil.getPropertiesMap()
	 * will be
	 * 
	 * <digestToUse,<outputSize,performance>> ,<kdaToUse,<performance>>
	 * 
	 * Note : String types are ignored
	 * 
	 * 
	 * @param inputClafer
	 */
	public void createClaferPropertiesMap(AstConcreteClafer inputClafer) {
		if (inputClafer.hasChildren())
			for (AstConcreteClafer childClafer : inputClafer.getChildren()) {
				propertiesList = new ArrayList<AstConcreteClafer>();
				addClaferProperties(childClafer);
				PropertiesMapperUtil.getPropertiesMap().put(childClafer, propertiesList);
			}
	}

}
