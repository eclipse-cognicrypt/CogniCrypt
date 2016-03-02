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

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.utilities.Labels;

public class ClaferModel {

	private String modelName;
	private Triple<AstModel, Scope, Objective[]> triple;
	private Map<String, AstConcreteClafer> constraintClafers;
	private ArrayList<AstConcreteClafer> propertiesList;

	/**
	 * Constructor for claferModel which takes absolute path for the js as parm
	 *
	 * @param path
	 */
	public ClaferModel(final String path) {
		loadModel(path);
		this.propertiesList = new ArrayList<AstConcreteClafer>();
	}

	/**
	 * Recursive method to list properties or subclafres of an Abstract clafer
	 *
	 * @param inputClafer
	 */
	public void addClaferProperties(final AstAbstractClafer inputClafer) {
		try {
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer in : inputClafer.getChildren()) {
					addClaferProperties(in);
				}
			}
			if (inputClafer.hasRef()) {
				addClaferProperties(inputClafer.getRef().getTargetType());
			}

			if (inputClafer.getSuperClafer() != null) {
				addClaferProperties(inputClafer.getSuperClafer());
			}

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * Recursive method to list subclafers of a clafer
	 *
	 * @param inputClafer
	 */
	public void addClaferProperties(final AstClafer inputClafer) {
		try {
			if (inputClafer.hasChildren()) {
				if (inputClafer.getGroupCard().getLow() >= 1) {
					this.propertiesList.add((AstConcreteClafer) inputClafer);
				} else {
					for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
						addClaferProperties(childClafer);
					}
				}
			}
			if (inputClafer.hasRef()) {
				if (inputClafer.getRef().getTargetType()
						.isPrimitive() && !inputClafer.getRef().getTargetType().getName().contains("string")) {
					this.propertiesList.add((AstConcreteClafer) inputClafer);

				} else if (inputClafer.getRef().getTargetType().isPrimitive() == false) {
					addClaferProperties(inputClafer.getRef().getTargetType());
				}
			}
			if (inputClafer.getSuperClafer() != null) {
				addClaferProperties(inputClafer.getSuperClafer());
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * Method which creates the list of subclafers in a given task , It omits String type If a subclafers refers to
	 * another clafer then search is performed recursively to list all the subclafers
	 *
	 * PasswordStoring : Task [Description = "Password Storing"] digestToUse ->Digest ? kdaToUse ->
	 * KeyDerivationAlgorithm ? abstract Digest : Algorithm outputSize -> integer abstract KeyDerivationAlgorithm :
	 * Algorithm
	 *
	 * abstract Algorithm name -> string performance -> integer
	 *
	 * if input clafer is passwordStoring then PropertiesMapperUtil.getPropertiesMap() will be
	 *
	 * <digestToUse,<outputSize,performance>> ,<kdaToUse,<performance>>
	 *
	 * Note : String types are ignored
	 *
	 *
	 * @param inputClafer
	 */
	public void createClaferPropertiesMap(final AstConcreteClafer inputClafer) {
		for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
			this.propertiesList = new ArrayList<AstConcreteClafer>();
			addClaferProperties(childClafer);
			PropertiesMapperUtil.getPropertiesMap().put(childClafer, this.propertiesList);
		}
	}

	// /**
	// * list the Enums
	// *
	// * @param model
	// */
	// public void setEnumList(AstModel model) {
	// String key = "";
	// for (AstAbstractClafer object : model.getAbstracts()) {
	// if (object.getName().contains("Enum") == true) {
	// for (AstClafer clafer : object.getSubs()) {
	//
	// /**
	// * construct a map of tasks, key is a clafer description and value is actual clafer Key will be used
	// * in Wizard, as an input for taskList combo box
	// */
	// // PropertiesMapperUtil.getTaskLabelsMap().put(key, (AstConcreteClafer) clafer);
	//
	// }
	// }
	// }
	// }

	/**
	 * Method provides map of clafer with a desired name.
	 *
	 * Ex: Search clafer with name performance throughout the model
	 *
	 * @param name
	 * @return
	 */
	public HashMap<AstConcreteClafer, AstConcreteClafer> getChildrenListbyName(final String name) {
		final HashMap<AstConcreteClafer, AstConcreteClafer> childClaferList = new HashMap<>();
		for (final AstClafer child : getModel().getChildren()) {
			setChildrenList(childClaferList, child, child, name);
		}
		return childClaferList;
	}

	/**
	 * returns the clafer constraint list
	 *
	 * @return
	 */
	public Map<String, AstConcreteClafer> getConstraintClafers() {
		return Check.notNull(this.constraintClafers);
	}

	/**
	 * returns the astModel from the clafer list
	 *
	 * @return
	 */
	public AstModel getModel() {
		return this.triple.getFst();
	}

	/**
	 *
	 * @return
	 */
	public String getModelName() {
		return this.modelName;
	}

	/**
	 * returns the scope of the model
	 *
	 * @return
	 */
	public Scope getScope() {
		return this.triple.getSnd();
	}

	/**
	 * set a model name
	 *
	 * @param modelName
	 */
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	/**
	 * list the task list, Tasks lists are those who extends Abstract clafer Task
	 *
	 * @param model
	 */
	public void setTaskList(final AstModel model) {
		String key = "";
		for (final AstAbstractClafer object : model.getAbstracts()) {
			// Find all the abstract first and select only the abstact with name Task
			if (object.getName().contains(Labels.TASK)) {
				for (final AstClafer clafer : object.getSubs()) { // get all clafers which are derived from "Task"
					for (final AstConstraint constraint : clafer.getConstraints()) {
						// Check Task description , and put that as Key
						if (constraint.getExpr().toString().contains("description . ref")) {
							key = constraint.getExpr().toString()
									.substring(constraint.getExpr().toString().indexOf("=") + 1, constraint.getExpr().toString().length())
									.trim().replace("\"", "");
						}
					}
					/**
					 * construct a map of tasks, key is a clafer description and value is actual clafer Key will be used
					 * in Wizard, as an input for taskList combo box
					 */
					PropertiesMapperUtil.getTaskLabelsMap().put(key, (AstConcreteClafer) clafer);
				}
			}
		}
	}

	/**
	 * Initializes the model and also list the task list Tasks lists are those who extends Task
	 *
	 * @param path
	 */
	private void loadModel(final String path) {
		try {
			final File filename = new File(path);
			this.triple = Javascript.readModel(filename, Javascript.newEngine());
			setModelName("Cryptography Task Configurator");
			setTaskList(this.triple.getFst());
		} catch (final IOException e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * Method performs same functionality as of the above method, written for Abstract clafer
	 *
	 * @param parentClafer
	 * @param inputClafer
	 * @param claferName
	 */
	private void setChildrenList(final HashMap<AstConcreteClafer, AstConcreteClafer> childClaferList, final AstClafer parentClafer,
			final AstAbstractClafer inputClafer, final String claferName) {
		try {
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
					setChildrenList(childClaferList, parentClafer, childClafer, claferName);
				}
			}
			if (inputClafer.hasRef()) {
				setChildrenList(childClaferList, parentClafer, inputClafer.getRef().getTargetType(), claferName);
			}
			if (inputClafer.getSuperClafer() != null) {
				setChildrenList(childClaferList, parentClafer, inputClafer.getSuperClafer(), claferName);
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * Initializes the children of a given clafer , used in testing also invoked by getChildrenListByName(). Retention
	 * is a HashMap data structure where key is parent clafer and inputclafer is recursive parameter clafer.
	 * parentClafer remains constant throughout the iteration. When the name of the clafer matches with the string being
	 * passed, map entry is constructed as <parentclafer,matchedClafer>
	 *
	 * @param parentClafer
	 * @param inputClafer
	 * @param name
	 */
	private void setChildrenList(final HashMap<AstConcreteClafer, AstConcreteClafer> childClaferList, final AstClafer parentClafer,
			final AstClafer inputClafer, final String name) {
		try {
			if (inputClafer.getName().contains(name)) {
				childClaferList.put((AstConcreteClafer) parentClafer, (AstConcreteClafer) inputClafer);
			}
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer in : inputClafer.getChildren()) {
					setChildrenList(childClaferList, parentClafer, in, name);
				}
			}
			if (inputClafer.hasRef()) {
				if (inputClafer.getRef().getTargetType().isPrimitive() == false) {
					setChildrenList(childClaferList, parentClafer, inputClafer.getRef().getTargetType(), name);
				}
			}
			if (inputClafer.getSuperClafer() != null) {
				setChildrenList(childClaferList, parentClafer, inputClafer.getSuperClafer(), name);
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

}
