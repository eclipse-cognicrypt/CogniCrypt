/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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

package crossing.e1.featuremodel.clafer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstModel;
import org.clafer.common.Check;
import org.clafer.javascript.Javascript;
import org.clafer.javascript.JavascriptFile;
import org.clafer.scope.Scope;

import crossing.e1.configurator.Activator;

/**
 * @author Ram Kamath
 *
 */
public class ClaferModel {

	private String modelName;
	private JavascriptFile jsFile;
	private Map<String, AstConcreteClafer> constraintClafers;
	private HashMap<AstConcreteClafer, AstConcreteClafer> childClaferList;

	/**
	 * Constructor for claferModel which takes absolute path for the js as parm
	 *
	 * @param path
	 */
	public ClaferModel(final File path) {
		loadModel(path);
	}

	public ClaferModel(final String path) {
		loadModel(new File(path));
	}

	/**
	 * Method provides map of clafer with a desired name.
	 *
	 * Ex: Search clafer with name performance throughout the model
	 *
	 * @param name
	 * @return
	 */
	public HashMap<AstConcreteClafer, AstConcreteClafer> getChildrenListbyName(final String name) {
		this.childClaferList = new HashMap<>();
		for (final AstClafer child : getModel().getChildren()) {
			setChildrenList(child, child, name);
		}
		return this.childClaferList;
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
		return this.jsFile.getModel();
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
		return this.jsFile.getScope();
	}

	/**
	 * Initializes the model and also list the task list Tasks lists are those who extends Task
	 *
	 * @param path
	 */
	private void loadModel(final File file) {
		try {
			this.jsFile = Javascript.readModel(file, Javascript.newEngine());

			setModelName("Cyrptography Task Configurator");
			final AstModel astModel = getModel();
			setEnumList(astModel);

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
	private void setChildrenList(final AstClafer parentClafer, final AstAbstractClafer inputClafer, final String claferName) {

		try {
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
					setChildrenList(parentClafer, childClafer, claferName);
				}
			}
			if (inputClafer.hasRef()) {
				setChildrenList(parentClafer, inputClafer.getRef().getTargetType(), claferName);
			}

			if (inputClafer.getSuperClafer() != null) {
				setChildrenList(parentClafer, inputClafer.getSuperClafer(), claferName);
			}

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * Initializes the children of a given clafer , used in testing also invoked by getChildrenListByName(). Retention is a HashMap data structure where key is parent clafer and
	 * inputclafer is recursive parameter clafer. parentClafer remains constant throughout the iteration. When the name of the clafer matches with the string being passed, map
	 * entry is constructed as <parentclafer,matchedClafer>
	 *
	 * @param parentClafer
	 * @param inputClafer
	 * @param name
	 */
	private void setChildrenList(final AstClafer parentClafer, final AstClafer inputClafer, final String name) {
		try {
			if (inputClafer.getName().contains(name)) {
				this.childClaferList.put((AstConcreteClafer) parentClafer, (AstConcreteClafer) inputClafer);

			}
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer in : inputClafer.getChildren()) {
					setChildrenList(parentClafer, in, name);
				}
			}
			if (inputClafer.hasRef()) {
				if (inputClafer.getRef().getTargetType().isPrimitive() == false) {
					setChildrenList(parentClafer, inputClafer.getRef().getTargetType(), name);
				}
			}
			if (inputClafer.getSuperClafer() != null) {
				setChildrenList(parentClafer, inputClafer.getSuperClafer(), name);
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * list the Enums
	 *
	 * @param model
	 */
	public void setEnumList(final AstModel model) {
		PropertiesMapperUtil.resetEnumMap();
		for (final AstAbstractClafer object : model.getAbstracts()) {
			if (object.getName().contains("Enum") == true) {
				for (final AstClafer clafer : object.getSubs()) {
					/**
					 * construct a map of tasks, key is a clafer description and value is actual clafer. Key will be used in Wizard, as an input for taskList combo box
					 */
					PropertiesMapperUtil.getenumMap().put((AstAbstractClafer) clafer, ((AstAbstractClafer) clafer).getSubs());

				}
			}
		}
	}

	/**
	 * set a model name
	 *
	 * @param modelName
	 */
	public void setModelName(final String modelName) {

		this.modelName = modelName;

	}

}
